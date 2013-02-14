package com.arteco.grooweb.web;

import groovy.util.GroovyScriptEngine;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.util.Modules;

public class GrooServletContexListener extends GuiceServletContextListener {

	private GroovyScriptEngine engine;
	private ServletContext servletContext;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		this.servletContext = servletContextEvent.getServletContext();
		super.contextInitialized(servletContextEvent);
	}

	@Override
	protected Injector getInjector() {
		return Guice.createInjector(servletModule());
	}

	private Module servletModule() {
		Module grooModule = null;
		try {
			URL urlBase = servletContext.getResource("/WEB-INF/groovy/conf/");
			engine = new GroovyScriptEngine(new URL[] { urlBase });
			grooModule = (Module) engine.loadScriptByName("GrooModule.groovy").newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			grooModule = new AbstractModule() {

				@Override
				protected void configure() {
				}

			};
		}

		return Modules.override(new ServletModule() {

			@Override
			protected void configureServlets() {
				bind(GrooScriptEngine.class);
				serve("*.html", "*.json").with(GrooServlet.class);
			}
		}).with(grooModule);
	}
}
