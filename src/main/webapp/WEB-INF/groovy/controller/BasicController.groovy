package controller;

import javax.inject.Inject;

import com.arteco.grooweb.web.GrooErrors;
import com.arteco.grooweb.web.GrooModel;
import com.arteco.grooweb.service.PersonService;
import form.BasicForm;


public class BasicController{
	
	
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
	
	public String prueba(GrooModel model){
		model.put("cont", 1);
		return "prueba.jsp";
	}
	
	
	public Object pruebaJson(GrooModel model){
		
		return new JsonObj();
	}
	
	public String redirect(){
		return "redirect:/prueba.html";
	}
	
	
	public String form(){
		return "form.jsp";
	}
	
	public String formUpdate(GrooModel model, BasicForm form, GrooErrors errors){
		if(errors.isEmpty()){
			//update changes in service layer, and do redirect after post
			println "service says " + personService.sayHello();
			return "redirect:/form.html?msg=saved";
		}else{
			//show de  html form with errors
			model.put("form", form);
		}
		return "form.jsp";
	}
	

}