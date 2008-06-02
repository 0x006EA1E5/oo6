package org.otherobjects.cms.jcr.dynamic;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.apache.commons.beanutils.BeanUtils;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;

/**
 * TODO All simple properties
 * TODO Components/References
 * TODO Collections
 * TODO Definitnon changes
 * TODO Validation
 * TODO Binding
 * 
 * @author rich
 */
public class DynaNodeBindingTest extends TestCase
{
    public void testWrite() throws IllegalAccessException, InvocationTargetException
    {
//        TypeDefImpl articleTypeDef = new TypeDefImpl();
//        //articleTypeDef.setClassName("");
//        articleTypeDef.setName("test.Article");
//        articleTypeDef.addProperty(new PropertyDefImpl("title", "string", null, null, true));
//        articleTypeDef.addProperty(new PropertyDefImpl("content", "text", null, null));
//        articleTypeDef.setLabelProperty("title");
//
//        // Type must be registered with type service
//        registerType(articleTypeDef);

        DynaNode a1 = new DynaNode();
        a1.setPath("/");
        a1.set("title", "A1 title");

        BeanUtils.setProperty(a1, "title", "new-title");
            
//        assertEquals("new-title", a1.get("title"));
    }
}
