package form;

import org.hibernate.validator.constraints.Length

public class BasicForm{

	@Length(min=3)
	String name

	@Length(min=3)
	String surname
	
	
}