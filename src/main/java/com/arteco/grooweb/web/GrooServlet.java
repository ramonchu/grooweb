package com.arteco.grooweb.web;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Singleton
public class GrooServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Inject
	private GrooScriptEngine gse;

	@PostConstruct
	public void init() {

	}

	@Override
	public void service(ServletRequest req, ServletResponse res) throws IOException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			gse.serve(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
