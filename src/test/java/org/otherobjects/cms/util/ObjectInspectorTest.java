package org.otherobjects.cms.util;

import junit.framework.TestCase;

import org.otherobjects.cms.beans.DummyBean;

public class ObjectInspectorTest extends TestCase {

	DummyBean testBean;
	static final String lipsum = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas id mi a ligula mollis fermentum. Integer eu sem ac ligula bibendum tincidunt. Mauris iaculis tempor sem. Nulla risus lectus, malesuada vitae, eleifend eu, elementum auctor, tortor. Cras id nibh at odio ullamcorper accumsan. Morbi non urna eu augue consequat dictum. Curabitur augue massa, molestie eu, ornare vel, blandit vitae, massa. Phasellus nec neque eget neque congue aliquam. Donec rhoncus. Mauris posuere, augue sit amet porta viverra, purus libero placerat tortor, vel pretium odio nisi at augue. Nullam vestibulum mi scelerisque dolor. Nullam non orci eu dui iaculis volutpat. Suspendisse egestas accumsan leo. Quisque molestie neque. Suspendisse ornare, eros vel venenatis pharetra, felis massa consequat tortor, et aliquam ipsum urna ac metus.";
	//static final String lipsum = "Lorem ipsum";
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		testBean = new DummyBean();
		
		testBean.setFirstName("Test");
		testBean.setLastName("Tester");
		testBean.setDescription(lipsum);
		testBean.setActive(true);
		testBean.setId(new Long(1));
		testBean.setEmailAddresses(new String[]{"email1@test.com","email2@test.com"});
		
		DummyBean childBean1 = new DummyBean();
		childBean1.setFirstName("Test");
		childBean1.setLastName("Tester");
		childBean1.setDescription(lipsum);
		childBean1.setActive(false);
		childBean1.setId(new Long(1));
		childBean1.setEmailAddresses(new String[]{"child1a@test.com","child1b@test.com"});
		
		DummyBean childBean2 = new DummyBean();
		childBean2.setFirstName("Test");
		childBean2.setLastName("Tester");
		childBean2.setDescription(lipsum);
		childBean2.setActive(true);
		childBean2.setId(new Long(1));
		childBean2.setEmailAddresses(new String[]{"child2a@test.com","child2b@test.com","child2c@test.com"});
		
		testBean.addChildDummyBean(childBean1);
		testBean.addChildDummyBean(childBean2);
		
	}

	public void testToString()
	{
		System.out.println(ObjectInspector.toString(testBean, true));
	}
	
	public void testToHtml()
	{
		System.out.println(ObjectInspector.toHtml(testBean));
	}
}
