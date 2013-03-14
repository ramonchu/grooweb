package com.arteco.grooweb.web;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;

public class GrooMessenger {

	private Map<Locale, Properties> maps = new HashMap<>();
	private boolean development;

	public GrooMessenger(boolean development) {
		this.development = development;
	}

	public String interpolate(Locale locale, String key, Object[] vals) {
		Validate.notNull(locale);
		Validate.notNull(key);
		Properties prop = getMessages(locale);
		String template = prop.getProperty(key);
		if (vals != null) {
			int i = 0;
			for (Object obj : vals) {
				template = StringUtils.replace(template, "{" + i + "}", ConvertUtils.convert(obj));
				i++;
			}
		}
		return template;
	}

	private synchronized Properties getMessages(Locale locale) {
		Properties prop = maps.get(locale);
		if (prop == null || development) {
			prop = new Properties();
			maps.put(locale, prop);
			try {
				String path = "/messages_" + locale.getLanguage() + ".properties";
				InputStream is = this.getClass().getResourceAsStream(path);
				if (is == null) {
					throw new IllegalArgumentException("No se puede encontrar " + path);
				}

				prop.load(is);
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		return prop;
	}

}
