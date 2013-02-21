package com.arteco.grooweb.web;

public class GrooPopul<T> {

	private GrooErrors errors;
	private T value;

	public GrooPopul(T form, GrooErrors validate) {
		this.value = form;
		this.errors = validate;
	}

	public GrooErrors getErrors() {
		return errors;
	}

	public T getValue() {
		return value;
	}

	public boolean hasError() {
		return !errors.isEmpty();
	}

}
