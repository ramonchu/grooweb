package com.arteco.grooweb.web;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.beanutils.Converter;

public class GrooDateConverter implements Converter {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	@SuppressWarnings("unchecked")
	public Object convert(@SuppressWarnings("rawtypes") Class type, Object value) {
		try {
			if (type.isAssignableFrom(Date.class)) {
				return sdf.parse((String) value);
			}
			if (type.isAssignableFrom(String.class)) {
				return sdf.format((Date) value);
			} else
				throw new IllegalArgumentException("can't convert " + value + " to" + type);
		} catch (ParseException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}

	}

}
