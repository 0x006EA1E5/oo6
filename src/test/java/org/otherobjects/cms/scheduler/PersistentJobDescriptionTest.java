package org.otherobjects.cms.scheduler;

import junit.framework.TestCase;

public class PersistentJobDescriptionTest extends TestCase {
	
	public void testBuildCronExpression() throws Exception
	{
		PersistentJobDescription pjd = new PersistentJobDescription();
		pjd.setSecond("0");
		pjd.setMinute("*");
		pjd.setHour("*");
		pjd.setMonth("8");
		
		assertEquals("0 * * * 8 *", pjd.buildCronExpression());
	}
}
