package com.arteco.grooweb.web;

import java.util.Locale;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

public abstract class GrooController {

	protected Validator validator;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected GrooModel model;
	protected GrooMessenger messenger;
	protected GrooLocaleResolver localeResolver;
	protected PersistenceManager persistenceManager;

	public void init() {
	};

	public GrooErrors validate(Object obj) {
		return new GrooErrors(validator.validate(obj));
	}

	public void populate(Object obj) {
		try {
			BeanUtils.populate(obj, request.getParameterMap());
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

	}

	public <T> GrooPopul<T> populidate(Class<T> formClass) {
		try {
			T form = formClass.newInstance();
			populate(form);
			GrooPopul<T> result = new GrooPopul<T>(form, validate(form));
			return result;
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
	}

	public String getParam(String name) {
		return request.getParameter(name);
	}

	@SuppressWarnings("unchecked")
	public <T> T getParam(Class<T> clazz, String name) {
		return (T) ConvertUtils.convert(request.getParameter(name), clazz);
	}

	public String interpolate(Locale locale, String key, Object[] vals) {
		return messenger.interpolate(locale, key, vals);
	}

	public String interpolate(Locale locale, String key) {
		return interpolate(locale, key, null);

	}

	public String interpolate(String key, Object[] vals) {
		return interpolate(localeResolver.getLocale(request), key, vals);
	}

	public String interpolate(String key) {
		return interpolate(localeResolver.getLocale(request), key, null);
	}
}
