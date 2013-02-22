package controller;

import javax.inject.Inject

import com.arteco.grooweb.service.Person
import com.arteco.grooweb.service.PersonService
import com.arteco.grooweb.web.GrooController
import com.arteco.grooweb.web.GrooErrors
import com.arteco.grooweb.web.GrooModel


public class BasicController extends GrooController{


	@Inject
	private PersonService personService;


	class JsonObj{
		String name ="R";
		String surname ="A";
		String email ="A@b.com";
		String url ="http://agasfasdf.com";
	};

	public String index(){
		return "index.jsp";
	}

	public String prueba(){
		model.put("msg", "Hello world");
		return "prueba.jsp";
	}


	public Object pruebaJson(){
		return new JsonObj();
	}

	public String redirect(){
		return "redirect:/prueba.html";
	}


	public String form(){
		return "form.jsp";
	}

	public String parameters(){
		model.put("id", getParam("id"));
		model.put("from", getParam(Date.class,"from"));
		model.put("to", getParam(Date.class,"to"));
		return "parameters.jsp";
	}

	public String formUpdate(){
		def popul = populidate(Person.class);
		if(!popul.hasError()){
			//update changes in service layer, and do redirect after post
			return "redirect:/form.html?msg=saved";
		}else{
			//show de  html form with errors
			model.put("form", popul.getValue());
			model.put("errors", popul.getErrors());
		}
		return "form.jsp";
	}
	
	
	public String groovyForm(){
		def popul = populidate(form.BasicForm.class);
		if(!popul.hasError()){
			//update changes in service layer, and do redirect after post
			return "redirect:/form.html?msg=saved";
		}else{
			//show de  html form with errors
			model.put("form", popul.getValue());
			model.put("errors", popul.getErrors());
		}
		return "form.jsp";
	}
}