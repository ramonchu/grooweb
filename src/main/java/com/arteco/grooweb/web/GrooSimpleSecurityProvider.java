package com.arteco.grooweb.web;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public class GrooSimpleSecurityProvider implements GrooSecurityProvider {

	private static final String KEY_SESSION = "grooRoles";

	@Override
	@SuppressWarnings("unchecked")
	public Set<String> getUserRoles(HttpServletRequest request) {
		Set<String> userRoles = (Set<String>) request.getSession().getAttribute(KEY_SESSION);
		if (userRoles == null) {
			userRoles = new HashSet<String>();
			request.getSession().setAttribute(KEY_SESSION, userRoles);
		}
		return userRoles;
	}

	@Override
	public void setUserRoles(HttpServletRequest request, Set<String> userRoles) {
		request.getSession().setAttribute(KEY_SESSION, userRoles);

	}

}
