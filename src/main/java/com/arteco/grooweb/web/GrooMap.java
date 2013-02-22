package com.arteco.grooweb.web;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface GrooMap {
	GrooRequestMethod[] method() default GrooRequestMethod.GET;

	String value();
}
