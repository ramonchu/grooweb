package com.arteco.grooweb.web;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ScriptException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Injector;

@Singleton
public class GrooServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private GroovyScriptEngine gse;

	@Inject
	private Injector injector;

	private boolean development = true;

	private Map<String, String> mapURLs;

	public Validator validator;
	public ObjectMapper mapper;
	public GrooMessenger messenger;

	@Override
	@PostConstruct
	public void init() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		mapper = new ObjectMapper();
		ConvertUtils.register(new GrooDateConverter(), Date.class);
		development = "true".equals(System.getProperty("grooweb.devel"));

		Logger.getLogger(this.getClass().getName()).log(
				Level.INFO,
				"\n================================================\n\tGrooweb is in "
						+ ((development) ? "DEVELOPMENT" : "PRODUCTION")
						+ " mode\n================================================");
	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws IOException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;

			String[] path = resolveMapping(request, response);
			if (path == null) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			Class<? extends GrooController> clazz = resolveClass(request, path);
			Method method = resolveMethod(path, clazz);

			Class<?>[] paramsClass = method.getParameterTypes();
			Object[] paramsValues = new Object[paramsClass.length];
			int i = 0;
			GrooModel model = new GrooModel();
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

			GrooController controller = injector.getInstance(clazz);
			controller.request = request;
			controller.response = response;
			controller.validator = validator;
			controller.model = model;
			controller.messenger = getMessenger();

			Object obj = clazz.getMethod(method.getName(), paramsClass).invoke(controller, paramsValues);
			resolveOutput(request, response, model, obj);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private GrooMessenger getMessenger() {
		if (messenger == null || development) {
			messenger = new GrooMessenger();
		}
		return messenger;
	}

	private void resolveOutput(HttpServletRequest request, HttpServletResponse response, GrooModel model, Object obj)
			throws Exception {
		if (obj instanceof String) {
			String view = (String) obj;
			if (model != null) {
				copyModel(request, model);
			}
			if (StringUtils.startsWith(view, "redirect:")) {
				response.sendRedirect(StringUtils.substringAfter(view, ":"));
			} else {
				request.getRequestDispatcher("/WEB-INF/jsp/" + view).forward(request, response);
			}
		} else {
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

	private Class<? extends GrooController> resolveClass(HttpServletRequest request, String[] path)
			throws MalformedURLException, ClassNotFoundException {
		if (gse == null || development) {
			initializeGse(request);
		}
		@SuppressWarnings("unchecked")
		Class<? extends GrooController> clazz = (Class<? extends GrooController>) gse.getGroovyClassLoader().loadClass(
				path[0]);
		return clazz;
	}

	private String[] resolveMapping(HttpServletRequest request, HttpServletResponse response) throws Exception,
			ScriptException {
		String uri = request.getRequestURI();
		Binding binding = new Binding();
		binding.setVariable("grooweb", this);

		if (gse == null || development) {
			initializeGse(request);
		}
		if (mapURLs == null || mapURLs.isEmpty() || development) {
			gse.run("conf/Mappings.groovy", binding);
		}
		String result = mapURLs.get(uri);
		if (result == null) {
			return null;
		}
		String[] path = StringUtils.split(result, ":");
		return path;
	}

	private void initializeGse(HttpServletRequest request) throws MalformedURLException {
		String scriptsRoot = request.getSession().getServletContext().getRealPath("/WEB-INF/groovy");
		gse = new GroovyScriptEngine(new URL[] { new File(scriptsRoot).toURI().toURL() });
	}

	public void registerController(Class<? extends GrooController> controllerClazz) {
		if (mapURLs == null) {
			mapURLs = new HashMap<String, String>();
		}
		Method[] methods = controllerClazz.getDeclaredMethods();
		for (Method m : methods) {
			if (m.isAnnotationPresent(GrooMap.class)) {
				GrooMap ann = m.getAnnotation(GrooMap.class);
				mapURLs.put(ann.value(), controllerClazz.getName() + ":" + m.getName());
//				System.out.println("Controller " + ann.value() + " >>> " + controllerClazz.getName() + ":"
//						+ m.getName());
			}
		}
	}
}
