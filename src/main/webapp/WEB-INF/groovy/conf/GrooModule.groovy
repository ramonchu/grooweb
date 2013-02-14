package conf;

import com.arteco.grooweb.service.PersonService
import com.arteco.grooweb.service.PersonServiceImpl
import com.google.inject.AbstractModule

public class GrooModule extends AbstractModule{
	
	@Override
	protected void configure() {
		bind(PersonService.class).toInstance(new PersonServiceImpl());
	}
}