package org.otherobjects.cms.beans;

import org.apache.commons.beanutils.PropertyUtils;

import groovy.lang.GroovyClassLoader;
import junit.framework.TestCase;

public class GroovyBeanTest extends TestCase {
	
	public void testGroovyBean() throws Exception
	{
		GroovyClassLoader gcl = new GroovyClassLoader(getClass().getClassLoader());
		Class article = gcl.parseClass("package org.otherobjects.cms.model;" +
						"class Article extends org.otherobjects.cms.model.DynaNode {" +
							"String testString;" +
							"}");
		
		Object articleInstance = article.newInstance();
		
		System.out.println(articleInstance.getClass());
		
		PropertyUtils.setSimpleProperty(articleInstance, "testString", "testStringValue");
		
		Object articleInstance2 = Class.forName("org.otherobjects.cms.model.Article");
		
		PropertyUtils.setSimpleProperty(articleInstance, "testString", "testStringValue");
		
	}
}
