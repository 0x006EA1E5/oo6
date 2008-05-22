package org.otherobjects.cms.validation;

import java.util.Locale;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@ContextConfiguration
public class MessageResolvingTest extends AbstractJUnit38SpringContextTests
{
    public void testResolveErrorCode()
    {
        Errors errors = new BeanPropertyBindingResult(new DummyBean(), "dummy");

        errors.rejectValue("name", "field.required");
        System.out.println(super.applicationContext.getMessage(errors.getFieldError("name").getCode(), null, Locale.GERMANY));

        assertEquals("Dieses Feld ist obligatorisch", super.applicationContext.getMessage(errors.getFieldError("name").getCode(), null, Locale.GERMANY));
    }

    class DummyBean
    {
        String name;

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }
    }
}
