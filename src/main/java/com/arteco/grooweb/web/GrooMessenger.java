package com.arteco.grooweb.web;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang.StringUtils;

public class GrooMessenger {

	private Map<Locale, Properties> resources = new HashMap<Locale, Properties>();

	public String interpolate(Locale locale, String key) {
		return interpolate(locale, key, null);
	}

	public String interpolate(Locale locale, String key, Object[] vals) {
		Properties res = resources.get(locale);
		if (res == null) {
			try {
				res = new Properties();
				res.load(this.getClass().getResourceAsStream("/messages_" + locale.getLanguage() + ".properties"));
				resources.put(locale, res);
			} catch (Exception e) {
				throw new IllegalArgumentException(e.getMessage(), e);
			}
		}
		String result = res.getProperty(key);
		if (result == null) {
			throw new IllegalArgumentException("Can't find the key [" + key + "] for locale " + locale);
		}
		if (vals != null && vals.length > 0) {
			int i = 0;
			for (Object obj : vals) {
				result = StringUtils.replace(result, "{" + i + "}", ConvertUtils.convert(obj));
				i++;
			}
		}
		return result;
	}

}
