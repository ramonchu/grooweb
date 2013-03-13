package com.arteco.grooweb.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class GrooPopul<T> {

	private GrooErrors errors;
	private T value;
	private GrooMessenger messenger;
	private GrooLocaleResolver localeResolver;
	private HttpServletRequest request;

	public GrooPopul(T form, GrooErrors validate, GrooMessenger messenger, GrooLocaleResolver localeResolver, HttpServletRequest request) {
		this.value = form;
		this.errors = validate;
		this.messenger = messenger;
		this.localeResolver = localeResolver;
		this.request = request;
	}

	public GrooErrors getErrors() {
		return errors;
	}

	public T getForm() {
		return value;
	}

	public boolean hasErrors() {
		return !errors.isEmpty();
	}

	public void rejectValue(String path, String keyMsg, Object... vals) {
		Locale locale = localeResolver.getLocale(request);
		String interpolated = messenger.interpolate(locale, keyMsg, vals);
		errors.rejectField(path, interpolated);
	}

}
