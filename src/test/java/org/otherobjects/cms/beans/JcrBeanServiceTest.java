//package org.otherobjects.cms.beans;
//
//import junit.framework.TestCase;
//
//import org.otherobjects.cms.model.DynaNode;
//import org.otherobjects.cms.types.TypeServiceImpl;
//import org.otherobjects.cms.types.PropertyDefImpl;
//import org.otherobjects.cms.types.TypeDefImpl;
//
//public class JcrBeanServiceTest extends TestCase
//{
//    private TypeDefImpl td;
//    private TypeServiceImpl typeService;
//    private JcrBeanService jcrBeanService;
//
//    @Override
//    protected void setUp() throws Exception
//    {
//        typeService = new TypeServiceImpl();
//        typeService.reset();
//        td = new TypeDefImpl("org.otherobjects.cms.test.TestObject");
//        td.addProperty(new PropertyDefImpl("testString", "string", null, null));
//        td.addProperty(new PropertyDefImpl("testText", "text", null, null));
//        td.addProperty(new PropertyDefImpl("testDate", "date", null, null));
//        td.addProperty(new PropertyDefImpl("testTime", "time", null, null));
//        td.addProperty(new PropertyDefImpl("testTimestamp", "timestamp", null, null));
//        td.addProperty(new PropertyDefImpl("testNumber", "number", null, null));
//        td.addProperty(new PropertyDefImpl("testDecimal", "decimal", null, null));
//        td.addProperty(new PropertyDefImpl("testBoolean", "boolean", null, null));
//        td.addProperty(new PropertyDefImpl("testReference", "reference", "org.otherobjects.cms.test.TestObject", null));
//        td.addProperty(new PropertyDefImpl("testStringsList", "string", null, "list"));
//        td.setLabelProperty("testString");
//        typeService.registerType(td);
//
//        jcrBeanService = new JcrBeanService();
//    }
//
//    public void testCreateCustomDynaNodeBean() throws Exception
//    {
//        jcrBeanService.createCustomDynaNodeClass(td);
//
//        DynaNode dynaNode = (DynaNode) Class.forName("org.otherobjects.cms.test.TestObject").newInstance();
//
//        // Check methods exist
//        dynaNode.getClass().getMethod("getTestString");
//        dynaNode.getClass().getMethod("getTestText");
//        dynaNode.getClass().getMethod("getTestDate");
//        dynaNode.getClass().getMethod("getTestTime");
//        dynaNode.getClass().getMethod("getTestTimestamp");
//        dynaNode.getClass().getMethod("getTestNumber");
//        dynaNode.getClass().getMethod("getTestDecimal");
//        dynaNode.getClass().getMethod("getTestBoolean");
//
//    }
//
//    //    public void setTypeService(TypeService typeService)
//    //    {
//    //        this.typeService = typeService;
//    //    }
//
//    //	public void testCopyDynamicProperties() throws Exception
//    //	{
//    //		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
//    //		beanService.copyDynamicProperties(dynaNode, dynaNodeBean);
//    //		
//    //		assertEquals(dynaNode.get("testString"), PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
//    //		assertEquals(dynaNode.get("testText"), PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
//    //		assertEquals(dynaNode.get("testDate"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
//    //		assertEquals(dynaNode.get("testTime"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
//    //		assertEquals(dynaNode.get("testTimestamp"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
//    //		assertEquals(dynaNode.get("testNumber"), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
//    //		assertEquals(dynaNode.get("testDecimal"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
//    //		assertEquals(dynaNode.get("testBoolean"), PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
//    //		
//    //	}
//
//    //	public void testCopyBeanProperties()  throws Exception
//    //	{
//    //		dynaNode = new DynaNode(typeService.getType("org.otherobjects.Dyna.jcr.TestObject"));
//    //		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(typeService.getType("org.otherobjects.Dyna.jcr.TestObject"));
//    //		
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testString", "testString");
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testText", "testText");
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testDate", now);
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testTime", now);
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testTimestamp", now);
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testNumber", new Long(1));
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testDecimal", new BigDecimal(1.0));
//    //		PropertyUtils.setNestedProperty(dynaNodeBean, "testBoolean", Boolean.FALSE);
//    //		dynaNodeBean.setPath("/test");
//    //		dynaNodeBean.setCode("testNode");
//    //		
//    //		beanService.copyBeanProperties(dynaNodeBean, dynaNode);
//    //		
//    //		assertEquals(dynaNode.get("testString"), PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
//    //		assertEquals(dynaNode.get("testText"), PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
//    //		assertEquals(dynaNode.get("testDate"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
//    //		assertEquals(dynaNode.get("testTime"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
//    //		assertEquals(dynaNode.get("testTimestamp"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
//    //		assertEquals(dynaNode.get("testNumber"), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
//    //		assertEquals(dynaNode.get("testDecimal"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
//    //		assertEquals(dynaNode.get("testBoolean"), PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
//    //		
//    //	}
//
//}
