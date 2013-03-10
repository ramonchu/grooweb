package com.arteco.grooweb.web;

import java.util.Properties;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

public class GrooPersistence {
	public GrooPersistence() {
		Properties properties = new Properties();
		properties.setProperty("javax.jdo.PersistenceManagerFactoryClass", "org.datanucleus.jdo.JDOPersistenceManagerFactory");
		properties.setProperty("javax.jdo.option.ConnectionDriverName", "com.mysql.jdbc.Driver");
		properties.setProperty("javax.jdo.option.ConnectionURL", "jdbc:mysql://localhost/myDB");
		properties.setProperty("javax.jdo.option.ConnectionUserName", "login");
		properties.setProperty("javax.jdo.option.ConnectionPassword", "password");
		PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory(properties);
		
	}
}
