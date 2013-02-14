package com.arteco.grooweb.service;

import javax.inject.Singleton;

@Singleton
public class PersonServiceImpl implements PersonService{

	@Override
	public String sayHello() {
		return "Hello!";
	}

}
