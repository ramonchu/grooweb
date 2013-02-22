package controller;

import javax.inject.Inject

import com.arteco.grooweb.service.Person
import com.arteco.grooweb.service.PersonService
import com.arteco.grooweb.web.GrooController
import com.arteco.grooweb.web.GrooModel
import com.arteco.grooweb.web.GrooMap

import form.BasicForm


public class BasicController extends GrooController{


	@Inject
	private PersonService personService;
	

	class JsonObj{
		String name ="R";
		String surname ="A";
		String email ="A@b.com";
		String url ="http://agasfasdf.com";
	};

	@GrooMap("/index.html")
	public String index(){
		return "index.jsp";
	}

	@GrooMap("/prueba.html")
	public String prueba(){
		model.put("msg", "Hello world");
		return "prueba.jsp";
	}


	@GrooMap("/prueba.json")
	public Object pruebaJson(){
		return new JsonObj();
	}

	@GrooMap("/redirect.html")
	public String redirect(){
		return "redirect:/prueba.html";
	}

	@GrooMap("/parameters.html")
	public String parameters(){
		model.put("id", getParam("id"));
		model.put("from", getParam(Date.class,"from"));
		model.put("to", getParam(Date.class,"to"));
		return "parameters.jsp";
	}
	
	
	@GrooMap("/form.html")
	public String form(){
		return "form.jsp";
	}

	@GrooMap("/formUpdate.html")
	public String formUpdate(){
		def popul = populidate(Person.class);
		if(!popul.hasError()){
			//update changes in service layer, and do redirect after post
			return "formOk.jsp";
		}else{
			//show de  html form with errors
			model.put("form", popul.getValue());
			model.put("errors", popul.getErrors());
		}
		return "form.jsp";
	}
	
	
	@GrooMap("/groovyform.html")
	public String groovyForm(){
		def popul = populidate(BasicForm.class);
		if(!popul.hasError()){
			//update changes in service layer, and do redirect after post
			return "formOk.jsp";
		}else{
			//show de  html form with errors
			model.put("form", popul.getValue());
			model.put("errors", popul.getErrors());
		}
		return "form.jsp";
	}
	
	@GrooMap("/customValidator.html")
	public String customValidator(){
		def popul = populidate(BasicForm.class);
		
		BasicForm form = popul.getValue();
		String name = form.getName();
		if(name==null || !name.contains("a")){
			popul.rejectField("name", "the name not contains 'a'");
		}
		
		if(!popul.hasError()){
			//update changes in service layer, and do redirect after post
			return "redirect:/customValidator.html?msg=saved";
		}else{
			//show de  html form with errors
			model.put("form", popul.getValue());
			model.put("errors", popul.getErrors());
		}
		return "form.jsp";
	}
	
	

}
