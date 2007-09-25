package org.otherobjects.cms.scheduler;

import org.springframework.test.AbstractSingleSpringContextTests;

public class QuartzSchedulerConfigurationBeanTest extends AbstractSingleSpringContextTests {

	@Override
	protected String[] getConfigLocations() {
		//return new String[]{"file:src/main/resources/otherobjects-resources/config/applicationContext-scheduler.xml"};
		return new String[]{"file:src/test/resources/applicationContext-scheduler.xml"};
	}
	
	public void testDummy() throws InterruptedException
	{
		System.out.println("Scheduler test running");
		
	}
	
	
}
