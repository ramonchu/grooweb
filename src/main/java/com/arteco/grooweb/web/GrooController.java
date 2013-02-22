package com.arteco.grooweb.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Validator;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;

public class GrooController {

	protected Validator validator;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected GrooModel model;
	protected GrooMessenger messenger;

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
}
