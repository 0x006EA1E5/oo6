package org.otherobjects.cms.beans;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.model.DynaNode;

import junit.framework.TestCase;

public class CglibClassloadingTest extends TestCase {
	
	public void testTwoBeansSameClassname() 
	{
		Object bean1 = getDynaNodeBean();
		Object bean2 = getDynaNodeBean();
		
		
		String genClassName = bean1.getClass().getName();
		System.out.println("Name: " + genClassName);
		assertEquals(bean1.getClass().getName(), bean2.getClass().getName());
		
		
		try {
			Class clazz = Class.forName(genClassName);
			Object bean3 = clazz.newInstance();
			PropertyUtils.setSimpleProperty(bean3, "testString", "This is a test");
		} catch (ClassNotFoundException e) {
			fail("We should find this class as we just added it to the classloader");
		} catch (InstantiationException e) {
			fail();
		} catch (IllegalAccessException e) {
			fail();
		} catch (InvocationTargetException e) {
			fail();
		} catch (NoSuchMethodException e) {
			fail();
		}
		
	
	}
	
	private Object getDynaNodeBean()
	{
		BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(DynaNode.class);
        
        beanGenerator.addProperty("testString", String.class);
        beanGenerator.addProperty("testLong", Long.class);
        beanGenerator.addProperty("testDate", Date.class);
        return beanGenerator.create();
	}
	
	private Object getDynaNodeBean2()
	{
		BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(DynaNode.class);
        
        beanGenerator.addProperty("testString", String.class);
        beanGenerator.addProperty("testDate", Date.class);
        beanGenerator.addProperty("testLong", Long.class);
        beanGenerator.addProperty("testInt", Integer.class);
        
        return beanGenerator.create();
	}
			
}
