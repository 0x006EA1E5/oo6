package org.otherobjects.cms.scheduler;

import groovy.lang.Binding;
import junit.framework.TestCase;

public class QuartzGroovyJobExecutorTest extends TestCase {
	
	public void testExecuteScript() throws Exception
	{
		QuartzGroovyJobExecutor qgje = new QuartzGroovyJobExecutor();
		Binding binding = new Binding();
		binding.setVariable("arg1", new Integer(2));
		qgje.executeScript(binding, "x = arg1 * 10");
		assertEquals(new Integer(20), binding.getVariable("x"));
		
	}
}
