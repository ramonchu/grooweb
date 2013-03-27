Grooweb
=======

Quick maven start up dynamic webapp, including simple web framework, with dynamic controllers (and views) 
based on groovy. It includes JSR 303 Validations, CDI with Guice, and Json with Jackson Mapper.


Resources included in the webapp:

  * Html5 Boilerplate
  * Twitter Bootstrap 2.3.0
  * Jquery 1.9.1
  * Modernizer 2.6.2
  * JSR-303 Validation (Hibernate-validator)
  * JSR-299 Contexts and Dependency Injection, with Google Guice
  * Groovy Controllers, with save and test.
  * Ajax Ready, with Json output with Jackson Mapper
  * Jade template engine. Se more on https://github.com/neuland/jade4j


Look the sample opening the following files and directories:

  * /src/main/webapp/WEB-INF/groovy/conf/GrooModule.groovy   
              => Where the Injectable beans must be declared
                          
  * /src/main/webapp/WEB-INF/groovy/controller/BasicController.groovy
              => Sample Controller, with several actions

  * /src/main/webapp/WEB-INF/groovy/form/BasicForm.groovy
              => Sample JSR 303 Form

  * /src/main/webapp/WEB-INF/jsp
              => All views protected from URL direct accesing


Usage 
=====

Simply, clone the repo and launch mvn jetty:run


Sample
======

Sample method in one controller class:

```java
 @GrooMap(value="/form.html",method=GrooRequestMethod.POST)
 public String formUpdate(){
	 def popul = populidate(BasicForm.class);
		if(!popul.hasError()){
		 //update changes in service layer, and do redirect after post
			return "formOk.jsp";
		}else{
			//show de  html form with errors
			model.put("urlToPost", "/form.html");
			model.put("form", popul.getValue());
			model.put("errors", popul.getErrors());
			return "form.jsp";
		}
	}
```

You have to put the annotation @GrooMap into public method to handle request. 
If the result is instance of String, the framework redirects to standar jsp page, but
if the method returns another object, it'll be serialized into json stream.


Injection
=========

It's possible inject 'static' services with @Inject, served by google guice.

```java
class MyController extends GrooController{
 
 @Inject
 private MyService myservice;
 
 @GrooMap(value="/anypage.html",method=GrooRequestMethod.GET)
 public String anypage(){
  myservice.doAnyLogic();
  return "anypage.jsp";
 }
}
```


Security
========

If you want protect methods of any controller, add @GrooRole("<PROTECTED_ROLE>") before the 
target methods

```java
class MyController extends GrooController{
 
 @Inject
 private MyService myservice;
 
 @GrooRole("ADMIN")
 @GrooMap(value="/anypage.html",method=GrooRequestMethod.GET)
 public String anypage(){
  myservice.doAnyLogic();
  return "anypage.jsp";
	}
}
```


Your contribution is welcomed!
