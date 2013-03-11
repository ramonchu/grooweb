package com.arteco.grooweb.web;

import java.util.Properties;

import javax.jdo.JDOEnhancer;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.apache.commons.lang.StringUtils;

public class GrooPersistence {

	private PersistenceManagerFactory pmf;
	private Properties props;
	private PersistenceManager pm;

	public GrooPersistence() {
		try {
			Properties props = new Properties();
			props.load(this.getClass().getResourceAsStream("/datanucleus.properties"));
			pmf = JDOHelper.getPersistenceManagerFactory(props);

			JDOEnhancer enhancer = JDOHelper.getEnhancer();
			enhancer.setVerbose(true);

			for (Object entry : props.keySet()) {
				if (entry instanceof String) {
					String key = (String) entry;
					if (StringUtils.startsWith(key, "datanucleus.entity")) {
						enhancer.addClasses(props.getProperty(key));
					}
				}

			}

			enhancer.enhance();
			pm = pmf.getPersistenceManager();
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

	}

	public PersistenceManager getPersistenceManager() {
		return pm;
	}

	public Properties getProperties() {
		return props;
	}
}
