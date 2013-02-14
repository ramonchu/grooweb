package form;

import org.hibernate.validator.constraints.Length

import com.arteco.grooweb.web.GrooValidable;

class BasicForm implements GrooValidable{

	@Length(min=3)
	String name

	@Length(min=3)
	String surname
}