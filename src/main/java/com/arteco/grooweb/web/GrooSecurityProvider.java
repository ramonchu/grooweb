package com.arteco.grooweb.web;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

public interface GrooSecurityProvider {

	Set<String> getUserRoles(HttpServletRequest request);
	void setUserRoles(HttpServletRequest request, Set<String> roles);
}
