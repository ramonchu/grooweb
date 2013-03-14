package com.arteco.grooweb.web;

import groovy.util.GroovyScriptEngine;

import java.net.URL;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.codehaus.jackson.map.ObjectMapper;

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

	private Validator validator;
	private ObjectMapper mapper;
	private GrooLocaleResolver localeResolver;

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
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
		mapper = new ObjectMapper();
		localeResolver = new GrooLocaleResolver();

		Module grooModule = null;
		try {
			URL urlBase = servletContext.getResource("/WEB-INF/groovy/conf/");
			engine = new GroovyScriptEngine(new URL[] { urlBase });
			@SuppressWarnings("unchecked")
			Class<? extends GrooAppModule> grooModuleClass = engine.loadScriptByName("GrooModule.groovy");
			GrooAppModule grooAppModule = grooModuleClass.newInstance();
			grooAppModule.configureMapper(mapper);
			grooModule = grooAppModule;
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
				bind(Validator.class).toInstance(validator);
				bind(ObjectMapper.class).toInstance(mapper);
				bind(GrooLocaleResolver.class).toInstance(localeResolver);
				serve("*.html", "*.json").with(GrooServlet.class);
			}
		}).with(grooModule);
	}
}
