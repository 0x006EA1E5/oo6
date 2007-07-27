package org.otherobjects.cms.beans;

import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;

import java.io.File;

import junit.framework.TestCase;

import org.apache.commons.beanutils.PropertyUtils;

public class GroovyBeanTest extends TestCase {
	
	public void testGroovyBean() throws Exception
	{
		GroovyClassLoader gcl = new GroovyClassLoader();
//		Class article = gcl.parseClass("package org.otherobjects.cms.model;" +
//						"class Article extends org.otherobjects.cms.model.DynaNode {" +
//							"String testString;" +
//							"}");
		
		Class article = gcl.parseClass(new GroovyCodeSource(new File("./src/test/resources/article.groovy")), true);
		
		Object articleInstance = article.newInstance();
		
		System.out.println(articleInstance.getClass().getName());
		
		PropertyUtils.setSimpleProperty(articleInstance, "testString", "testStringValue");
		
		Object articleInstance2 = gcl.loadClass(articleInstance.getClass().getName());
		
//		Object articleInstance3 = Class.forName(articleInstance.getClass().getName());
//		
//		PropertyUtils.setSimpleProperty(articleInstance, "testString", "testStringValue");
		
	}
}
