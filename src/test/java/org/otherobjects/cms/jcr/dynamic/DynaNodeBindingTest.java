package org.otherobjects.cms.jcr.dynamic;

import java.lang.reflect.InvocationTargetException;

import junit.framework.TestCase;

import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.DynaNodeBeanWrapperImpl;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.validation.DataBinder;

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
        TypeDefImpl articleTypeDef = new TypeDefImpl();
        articleTypeDef.setName("test.Article");
        articleTypeDef.addProperty(new PropertyDefImpl("title", "string", null, null, true));
        articleTypeDef.addProperty(new PropertyDefImpl("content", "text", null, null));
        articleTypeDef.setLabelProperty("title");


        
        
        DynaNode a1 = new DynaNode();
        a1.setPath("/");
        a1.set("title", "A1 title");

        DataBinder binder = new DataBinder(a1);
        MutablePropertyValues pvs = new MutablePropertyValues();
        pvs.addPropertyValue(new PropertyValue("title", "new-title"));
        binder.bind(pvs);
        
//        BeanWrapper bw = new DynaNodeBeanWrapperImpl(a1);
//        bw.setPropertyValue("title", "new-title");
        assertEquals("new-title", a1.get("title"));
    }
    
}
