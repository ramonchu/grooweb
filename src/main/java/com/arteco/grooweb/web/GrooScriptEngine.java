package com.arteco.grooweb.web;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ScriptException;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.inject.Injector;

@Singleton
public class GrooScriptEngine {

	@Inject
	private Injector injector;
	
	private boolean development = true;
	
	public GroovyScriptEngine gse;
	public Validator validator;
	public ObjectMapper mapper;

	public GrooScriptEngine() {
		// shell = new GroovyShell(this.getClass().getClassLoader());
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		mapper = new ObjectMapper();
	}

	public void serve(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String[] path = resolveMapping(request, response);
		if (path == null) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		Class<?> clazz = resolveClass(request, path);
		Method method = resolveMethod(path, clazz);

		Class<?>[] paramsClass = method.getParameterTypes();
		Object[] paramsValues = new Object[paramsClass.length];
		int i = 0;
		int ierrors = -1;
		Object validable = null;
		GrooModel model = new GrooModel();
		for (Class<?> paramClass : paramsClass) {
			if (paramClass.isAssignableFrom(HttpServletRequest.class))
				paramsValues[i] = request;
			else if (paramClass.isAssignableFrom(HttpServletResponse.class))
				paramsValues[i] = response;
			else if (paramClass.isAssignableFrom(HttpSession.class))
				paramsValues[i] = request.getSession();
			else if (paramClass.isAssignableFrom(Validator.class))
				paramsValues[i] = validator;
			else if (paramClass.isAssignableFrom(GrooModel.class)) {
				paramsValues[i] = model;
			} else if (paramClass.isAssignableFrom(GrooErrors.class)) {
				ierrors = i;
			} else {
				paramsValues[i] = paramClass.newInstance();
				if (paramsValues[i] instanceof GrooValidable) {
					validable = paramsValues[i];
				}
			}
			i++;
		}
		GrooErrors gerrors = null;
		if (ierrors >= 0 && validable != null) {
			BeanUtils.populate(validable, request.getParameterMap());
			paramsValues[ierrors] = gerrors = new GrooErrors(validator.validate(validable));
		}

		
		Object controller = injector.getInstance(clazz);
		Object obj = clazz.getMethod(method.getName(), paramsClass).invoke(controller, paramsValues);
		resolveOutput(request, response, model, obj, gerrors);

	}

	private void resolveOutput(HttpServletRequest request, HttpServletResponse response, GrooModel model, Object obj,
			GrooErrors gerrors) throws Exception {
		if (obj instanceof String) {
			String view = (String) obj;
			if (model != null) {
				copyModel(request, model);
			}
			if (gerrors != null) {
				request.setAttribute("errors", gerrors);
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

	private Class<?> resolveClass(HttpServletRequest request, String[] path) throws MalformedURLException,
			ClassNotFoundException {
		if (gse == null || development) {
			initializeGse(request);
		}
		Class<?> clazz = gse.getGroovyClassLoader().loadClass(path[0]);
		return clazz;
	}

	private String[] resolveMapping(HttpServletRequest request, HttpServletResponse response) throws Exception,
			ScriptException {
		String uri = request.getRequestURI();
		Binding binding = new Binding();

		if (gse == null|| development) {
			initializeGse(request);
		}
		@SuppressWarnings("unchecked")
		Map<String, String> map = (Map<String, String>) gse.run("conf/Mappings.groovy", binding);
		String result = map.get(uri);
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
}
