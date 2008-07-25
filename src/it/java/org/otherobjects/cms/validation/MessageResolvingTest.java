package org.otherobjects.cms.validation;

import org.otherobjects.cms.test.BaseSharedContextTestCase;

//@ContextConfiguration(locations={"classpath:/org/otherobjects/cms/bootstrap/shared-test-context.xml"})
public class MessageResolvingTest extends BaseSharedContextTestCase
{
    public void testResolveErrorCode()
    {
        //FIXME: broken test
//        Errors errors = new BeanPropertyBindingResult(new DummyBean(), "dummy");
//
//        errors.rejectValue("name", "field.required");
//        System.out.println(super.applicationContext.getMessage(errors.getFieldError("name").getCode(), null, Locale.GERMANY));
//
//        assertEquals("Dieses Feld ist obligatorisch", super.applicationContext.getMessage(errors.getFieldError("name").getCode(), null, Locale.GERMANY));
    }

    class DummyBean
    {
        private String name;

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
