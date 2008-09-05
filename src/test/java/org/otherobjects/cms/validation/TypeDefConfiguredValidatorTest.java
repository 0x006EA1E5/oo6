package org.otherobjects.cms.validation;

import junit.framework.TestCase;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

@SuppressWarnings("unchecked")
public class TypeDefConfiguredValidatorTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();

    @Override
    protected void setUp() throws Exception
    {
        //setup type service for Template, TemplateRegion TemplateBlock
        super.setUp();
        SingletonBeanLocator.registerTestBean("typeService", typeService);

        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("TestDynaNode");
        td.addProperty(new PropertyDefImpl("testString", "string", null, null, true, true));
        PropertyDefImpl testTextProperty = new PropertyDefImpl("testText", "text", null, null, false, true);
        testTextProperty.setSize(10);
        td.addProperty(testTextProperty);
        
        PropertyDefImpl testNumberProperty = new PropertyDefImpl("testNumber", "text", null, null, false, true);
        testNumberProperty.setValang("{? : 5<=? AND ?<10 : 'Field is out of range' : 'field.out.of.range' }");
        td.addProperty(testNumberProperty);
        
        PropertyDefImpl testRegexpProperty = new PropertyDefImpl("testRegexp", "text", null, null, false, true);
        testRegexpProperty.setValang("{? : ? is not null and matches('[ABC]\\\\w{3,4}',?) is true: 'Field does not match pattern' : 'field.pattern.does.not.match' }");
        td.addProperty(testRegexpProperty);
        
        PropertyDefImpl carRegexpProperty = new PropertyDefImpl("carReg", "text", null, null, false, true);
        carRegexpProperty.setValang("{? : ? is not null and matches('\\\\w{2}\\\\d{2}\\\\s?\\\\w{3}', ?) is true: 'Not a valid car registration' : 'field.pattern.does.not.match' }");
        td.addProperty(carRegexpProperty);
        
        td.addProperty(new PropertyDefImpl("testDate", "date", null, null, false, true));
        typeService.registerType(td);

        ((TypeServiceImpl) typeService).reset();

    }

    
    public void testValidate()
    {
        DynaNode t = new DynaNode("TestDynaNode");
        t.getData().put("testString", null); // Required property
        t.getData().put("testText", "12345678901234567890123456789012345678901234567890x"); // Size 50 property
        t.getData().put("testNumber", 4); // Range: 5 <= ? 10
        t.getData().put("testRegexp", "Abc"); // Match regexp FAIL
        t.getData().put("carReg", "LN52 ABC"); // Match regexp PASS
        
            
        TypeDefConfiguredValidator validator = new TypeDefConfiguredValidator();
        validator.setTypeService(typeService);
        
        Errors errors = new BeanPropertyBindingResult(t, "");
        validator.validate(t, errors);
        System.out.println(errors);
        assertEquals("field.required", errors.getFieldError("data[testString]").getCode());
        assertEquals("field.value.too.long", errors.getFieldError("data[testText]").getCode());
        assertEquals("field.out.of.range", errors.getFieldError("data[testNumber]").getCode());
        assertEquals("field.pattern.does.not.match", errors.getFieldError("data[testRegexp]").getCode());
        assertNull(errors.getFieldError("data[carReg]"));
    }

}
