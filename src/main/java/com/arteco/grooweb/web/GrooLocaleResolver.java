package com.arteco.grooweb.web;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

public class GrooLocaleResolver {

	public Locale getLocale(HttpServletRequest request) {
		return request.getLocale();
	}

}
