package com.arteco.grooweb.web;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validator;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Injector;

@Singleton
public class GrooServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String GROOVY_PATH = "/WEB-INF/groovy";

	@Inject
	private Injector injector;

	@Inject
	private Validator validator;

	@Inject
	private ObjectMapper mapper;

	@Inject
	private GrooLocaleResolver localeResolver;

	private boolean development;
	private GroovyScriptEngine gse;
	private File controllersBaseDir;
	private GrooRoutes routes;
	private GrooMessenger messenger;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

		ConvertUtils.register(new GrooDateConverter(), Date.class);
		development = "true".equals(System.getProperty("grooweb.devel"));

		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\n================================================\n\tGrooweb is in " + ((development) ? "DEVELOPMENT" : "PRODUCTION") + " mode" + "\n================================================");
		if (!development) {
			Logger.getLogger(this.getClass().getName()).log(Level.INFO, "\n================================================\n\tUse -Dgrooweb.devel=true for development mode" + "\n================================================");
		}
		try {
			String scriptsRoot = config.getServletContext().getRealPath(GROOVY_PATH);
			gse = new GroovyScriptEngine(new URL[] { new File(scriptsRoot).toURI().toURL() });
		} catch (Exception e) {
			e.printStackTrace();
		}
		controllersBaseDir = new File(config.getServletContext().getRealPath(GROOVY_PATH + "/controller"));
		routes = new GrooRoutes();

	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws IOException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		try {
			Map<String, String> mappings = getMappings(gse, request);
			String[] path = resolveMapping(gse, mappings, request, response);
			Class<? extends GrooController> controllerClass = resolveClass(gse, request, path[0]);
			Method method = resolveMethod(path, controllerClass);
			GrooModel model = new GrooModel();
			Class<?>[] paramsClass = method.getParameterTypes();
			Object[] paramsValues = fillArguments(request, response, model, paramsClass);
			GrooController controller = initializeController(request, response, controllerClass, model);
			Method callableMethod = controllerClass.getMethod(method.getName(), paramsClass);
			checkSecurity(controllerClass, callableMethod, request);
			Object obj = callableMethod.invoke(controller, paramsValues);
			resolveOutput(request, response, controller, model, obj);
		} catch (SecurityException e) {
			System.err.println(e.getMessage());
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		} catch (NoSuchMethodException e) {
			System.err.println(e.getMessage());
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unchecked")
	private void checkSecurity(Class<? extends GrooController> controllerClass, Method callableMethod, HttpServletRequest request) {
		Set<String> requiredRoles = new HashSet<String>();
		GrooRole groorole = controllerClass.getAnnotation(GrooRole.class);
		addRoles(groorole, requiredRoles);
		groorole = callableMethod.getAnnotation(GrooRole.class);
		addRoles(groorole, requiredRoles);

		Set<String> userRoles = (Set<String>) request.getSession().getAttribute("grooRoles");
		if (userRoles == null) {
			userRoles = new HashSet<String>();
			request.getSession().setAttribute("grooRoles", userRoles);
		}

		for (String requiredRole : requiredRoles) {
			if (userRoles.contains(requiredRole)) {
				return;
			}
		}
		if (requiredRoles.size() > 0) {
			throw new SecurityException("User is not in any allowed role " + requiredRoles);
		}
	}

	public void addRoles(GrooRole groorole, Set<String> requiredRoles) {
		if (groorole != null) {
			String[] values = groorole.value();
			if (values != null) {
				for (String val : values) {
					requiredRoles.add(val);
				}
			}
		}
	}

	private Map<String, String> getMappings(GroovyScriptEngine gse, HttpServletRequest request) throws ResourceException, ScriptException {
		Map<String, String> result = routes.getAllRoutes();
		if (result.isEmpty() || development) {
			Collection<File> files = FileUtils.listFiles(controllersBaseDir, new String[] { "groovy" }, true);
			for (File controllerFile : files) {
				long modtime = controllerFile.lastModified();
				Long l = routes.getLasModification(controllerFile);
				if (l == null || modtime > l) {
					Map<String, String> controllerUrls = reloadControllersMappings(gse, request, controllerFile);
					routes.updateRoutes(controllerFile, controllerUrls);
				}
			}
			result = routes.update(files);
		}
		return result;
	}

	private GrooController initializeController(HttpServletRequest request, HttpServletResponse response, Class<? extends GrooController> clazz, GrooModel model) {
		GrooController controller = injector.getInstance(clazz);
		controller.request = request;
		controller.response = response;
		controller.validator = validator;
		controller.model = model;
		controller.messenger = getMessenger();
		controller.localeResolver = localeResolver;
		controller.init();
		return controller;
	}

	private Object[] fillArguments(HttpServletRequest request, HttpServletResponse response, GrooModel model, Class<?>[] paramsClass) {
		int i = 0;
		Object[] paramsValues = new Object[paramsClass.length];
		for (Class<?> paramClass : paramsClass) {
			if (paramClass.isAssignableFrom(HttpServletRequest.class)) {
				paramsValues[i] = request;
			} else if (paramClass.isAssignableFrom(HttpServletResponse.class)) {
				paramsValues[i] = response;
			} else if (paramClass.isAssignableFrom(HttpSession.class)) {
				paramsValues[i] = request.getSession();
			} else if (paramClass.isAssignableFrom(Validator.class)) {
				paramsValues[i] = validator;
			} else if (paramClass.isAssignableFrom(GrooModel.class)) {
				paramsValues[i] = model;
			}
			i++;
		}
		return paramsValues;
	}

	private GrooMessenger getMessenger() {
		if (messenger == null || development) {
			messenger = new GrooMessenger(development);
		}
		return messenger;
	}

	private void resolveOutput(HttpServletRequest request, HttpServletResponse response, GrooController controller, GrooModel model, Object obj) throws Exception {
		request.setAttribute("grooMessenger", controller.messenger);
		request.setAttribute("grooLocaleResolver", controller.localeResolver);

		if (obj instanceof String) {
			String view = (String) obj;
			if (StringUtils.startsWith(view, "redirect:")) {
				response.sendRedirect(StringUtils.substringAfter(view, ":"));
			} else if (view.endsWith("jsp")) {
				copyModel(request, model);
				request.getRequestDispatcher("/WEB-INF/jsp/" + view).forward(request, response);
			} else {
				throw new IllegalArgumentException("Can't resolve view " + view);
			}
		} else {
			response.setCharacterEncoding("utf-8");
			response.setContentType("application/json");
			mapper.writeValue(response.getOutputStream(), obj);
		}
	}

	private void copyModel(HttpServletRequest request, GrooModel model) {
		for (Entry<String, Object> entry : model.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
	}

	private Method resolveMethod(String[] path, Class<?> clazz) {
		Method[] methods = clazz.getMethods();
		Method method = null;
		for (Method m : methods) {
			if (StringUtils.equals(path[1], m.getName())) {
				method = m;
				break;
			}
		}
		if (method == null) {
			throw new RuntimeException("no se ha encontrado el método a ejectuar");
		}
		return method;
	}

	private Class<? extends GrooController> resolveClass(GroovyScriptEngine gse, HttpServletRequest request, String groovyClassName) throws MalformedURLException, ClassNotFoundException {
		@SuppressWarnings("unchecked")
		Class<? extends GrooController> clazz = (Class<? extends GrooController>) gse.getGroovyClassLoader().loadClass(groovyClassName);
		return clazz;
	}

	private String[] resolveMapping(GroovyScriptEngine gse, Map<String, String> mappings, HttpServletRequest request, HttpServletResponse response) throws Exception, ScriptException {
		String uri = request.getRequestURI();
		Binding binding = new Binding();
		binding.setVariable("grooweb", this);

		String result = mappings.get(request.getMethod() + " " + uri);
		if (result == null) {
			throw new NoSuchMethodException("No se ha encontrado mapping para " + request.getMethod() + " " + uri);
		}
		String[] path = StringUtils.split(result, ":");
		return path;
	}

	private Map<String, String> reloadControllersMappings(GroovyScriptEngine gse, HttpServletRequest request, File f) throws ResourceException, ScriptException {
		Map<String, String> mapControllers = new HashMap<String, String>();
		// File baseDir = new File(request.getSession().getServletContext().getRealPath(GROOVY_PATH + "/controller"));
		// Collection<File> files = FileUtils.listFiles(baseDir, new String[] { "groovy" }, true);
		// for (File f : files) {
		String path = StringUtils.substringAfterLast(f.getAbsolutePath(), GROOVY_PATH + "/");
		@SuppressWarnings("unchecked")
		Class<? extends GrooController> groovyClazz = gse.loadScriptByName(path);
		registerController(mapControllers, groovyClazz);
		// }
		return mapControllers;
	}

	private void registerController(Map<String, String> mapControllers, Class<? extends GrooController> controllerClazz) {
		Method[] methods = controllerClazz.getDeclaredMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(GrooMap.class)) {
				GrooMap ann = m.getAnnotation(GrooMap.class);
				GrooRequestMethod[] reqMethods = ann.method();
				for (GrooRequestMethod reqme : reqMethods) {
					mapControllers.put(reqme.name() + " " + ann.value(), controllerClazz.getName() + ":" + m.getName());
				}
			}
		}
	}
}
