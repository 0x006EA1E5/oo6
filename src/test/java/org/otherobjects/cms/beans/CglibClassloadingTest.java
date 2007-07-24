package org.otherobjects.cms.beans;

import java.util.Date;

import net.sf.cglib.beans.BeanGenerator;

import org.otherobjects.cms.model.DynaNode;

import junit.framework.TestCase;

public class CglibClassloadingTest extends TestCase {
	
	public void testTwoBeansSameClassname()
	{
		Object bean1 = getDynaNodeBean();
		Object bean2 = getDynaNodeBean();
		
		System.out.println("Name: " + bean1.getClass().getName());
		assertEquals(bean1.getClass().getName(), bean2.getClass().getName());
	
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
