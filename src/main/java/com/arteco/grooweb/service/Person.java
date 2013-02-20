package com.arteco.grooweb.service;

import org.hibernate.validator.constraints.Length;

public class Person {
	@Length(min = 3)
	String name;

	@Length(min = 3)
	String surname;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

}
