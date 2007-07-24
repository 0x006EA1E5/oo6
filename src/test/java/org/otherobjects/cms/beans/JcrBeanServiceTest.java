package org.otherobjects.cms.beans;


public class JcrBeanServiceTest extends BaseBeanServiceTest {
	
	public void testCreateCustomDynaNodeBean() throws Exception
	{
		
		try{
			dynaNode.getClass().getMethod("getTestString");
			dynaNode.getClass().getMethod("getTestText");
			dynaNode.getClass().getMethod("getTestDate");
			dynaNode.getClass().getMethod("getTestTime");
			dynaNode.getClass().getMethod("getTestTimestamp");
			dynaNode.getClass().getMethod("getTestNumber");
			dynaNode.getClass().getMethod("getTestDecimal");
			dynaNode.getClass().getMethod("getTestBoolean");
		}
		catch(NoSuchMethodException e)
		{
			fail("Methods should exist");
		}
	}
	
//	public void testCopyDynamicProperties() throws Exception
//	{
//		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
//		beanService.copyDynamicProperties(dynaNode, dynaNodeBean);
//		
//		assertEquals(dynaNode.get("testString"), PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
//		assertEquals(dynaNode.get("testText"), PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
//		assertEquals(dynaNode.get("testDate"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
//		assertEquals(dynaNode.get("testTime"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
//		assertEquals(dynaNode.get("testTimestamp"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
//		assertEquals(dynaNode.get("testNumber"), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
//		assertEquals(dynaNode.get("testDecimal"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
//		assertEquals(dynaNode.get("testBoolean"), PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
//		
//	}
	
//	public void testCopyBeanProperties()  throws Exception
//	{
//		dynaNode = new DynaNode(typeService.getType("org.otherobjects.Dyna.jcr.TestObject"));
//		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(typeService.getType("org.otherobjects.Dyna.jcr.TestObject"));
//		
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testString", "testString");
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testText", "testText");
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testDate", now);
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testTime", now);
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testTimestamp", now);
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testNumber", new Long(1));
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testDecimal", new BigDecimal(1.0));
//		PropertyUtils.setNestedProperty(dynaNodeBean, "testBoolean", Boolean.FALSE);
//		dynaNodeBean.setPath("/test");
//		dynaNodeBean.setCode("testNode");
//		
//		beanService.copyBeanProperties(dynaNodeBean, dynaNode);
//		
//		assertEquals(dynaNode.get("testString"), PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
//		assertEquals(dynaNode.get("testText"), PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
//		assertEquals(dynaNode.get("testDate"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
//		assertEquals(dynaNode.get("testTime"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
//		assertEquals(dynaNode.get("testTimestamp"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
//		assertEquals(dynaNode.get("testNumber"), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
//		assertEquals(dynaNode.get("testDecimal"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
//		assertEquals(dynaNode.get("testBoolean"), PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
//		
//	}
	
	
}
