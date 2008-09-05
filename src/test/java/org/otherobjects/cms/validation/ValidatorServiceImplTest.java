package org.otherobjects.cms.validation;

import junit.framework.TestCase;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.binding.TestObject;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.springframework.validation.Validator;

public class ValidatorServiceImplTest extends TestCase
{
    private TypeServiceImpl typeService = new TypeServiceImpl();;

    @Override
    protected void setUp() throws Exception
    {
        //setup type service for Template, TemplateRegion TemplateBlock
        super.setUp();
                // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("TestDynaNode");
        td.addProperty(new PropertyDefImpl("testString", "string", null, null, true, true));

        // Add Bean type
        TypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        typeService.registerType(typeDefBuilder.getTypeDef(TestObject.class));

        ((TypeServiceImpl) typeService).reset();
    
        SingletonBeanLocator.registerTestBean("typeService", typeService);
    }

    public void testGetValidator()
    {
        ValidatorServiceImpl validatorService = new ValidatorServiceImpl();
        TypeDefConfiguredValidator validator = new TypeDefConfiguredValidator();
        validator.setTypeService(typeService);
        
        validatorService.setDefaultValidator(validator);

        Validator v1 = validatorService.getValidator(TestObject.class);
        assertNotNull(v1);

        Validator v2 = validatorService.getValidator(DynaNode.class);
        assertNotNull(v2);
    }

}
