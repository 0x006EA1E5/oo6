package org.otherobjects.cms.validation;

import java.util.Locale;

import org.springframework.test.AbstractSingleSpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

public class MessageResolvingTest extends AbstractSingleSpringContextTests {
	
	
	
	protected String[] getConfigLocations() {
        return new String[] {
                "file:src/test/resources/applicationContext-resources.xml",
                "file:src/test/resources/applicationContext-messages.xml",
            };
    }
	
	
	public void testResolveErrorCode()
	{
		Errors errors = new BeanPropertyBindingResult(new DummyBean(), "dummy");
		
		errors.rejectValue("name", "field.required");
		System.out.println(getApplicationContext().getMessage(errors.getFieldError("name").getCode(), null, Locale.GERMANY));
		
		assertEquals("Dieses Feld ist obligatorisch", getApplicationContext().getMessage(errors.getFieldError("name").getCode(), null, Locale.GERMANY));
		
	}
	
	class DummyBean{
		String name;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
	}
	
}
