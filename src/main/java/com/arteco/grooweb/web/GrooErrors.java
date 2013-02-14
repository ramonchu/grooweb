package com.arteco.grooweb.web;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

public class GrooErrors extends HashMap<String, Set<String>> {

	private static final long serialVersionUID = 1L;

	public GrooErrors(Set<ConstraintViolation<Object>> errors) {
		for (ConstraintViolation<Object> cv : errors) {
			String key = cv.getPropertyPath().toString();
			Set<String> set = this.get(key);
			if(set==null){
				set = new HashSet<String>();
				this.put(key, set);
			}
			set.add(cv.getMessage());
		}
	}

}
