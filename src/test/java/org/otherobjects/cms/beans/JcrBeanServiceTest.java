package org.otherobjects.cms.beans;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public class JcrBeanServiceTest extends BaseJcrTestCase {
	
	private TypeService typeService;
	private JcrBeanService beanService;
	
	private DynaNode dynaNode;
	
	private Date now;
	
	public void testCreateCustomDynaNodeBean() throws Exception
	{
		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
		try{
			dynaNodeBean.getClass().getMethod("getTestString", null);
			dynaNodeBean.getClass().getMethod("getTestText", null);
			dynaNodeBean.getClass().getMethod("getTestDate", null);
			dynaNodeBean.getClass().getMethod("getTestTime", null);
			dynaNodeBean.getClass().getMethod("getTestTimestamp", null);
			dynaNodeBean.getClass().getMethod("getTestNumber", null);
			dynaNodeBean.getClass().getMethod("getTestDecimal", null);
			dynaNodeBean.getClass().getMethod("getTestBoolean", null);
		}
		catch(NoSuchMethodException e)
		{
			fail("Methods should exist");
		}
	}
	
	public void testCopyDynamicProperties() throws Exception
	{
		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
		beanService.copyDynamicProperties(dynaNode, dynaNodeBean);
		
		assertEquals(dynaNode.get("testString"), PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
		assertEquals(dynaNode.get("testText"), PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
		assertEquals(dynaNode.get("testDate"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
		assertEquals(dynaNode.get("testTime"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
		assertEquals(dynaNode.get("testTimestamp"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
		assertEquals(dynaNode.get("testNumber"), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
		assertEquals(dynaNode.get("testDecimal"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
		assertEquals(dynaNode.get("testBoolean"), PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
		
	}
	
	public void testCopyBeanProperties()  throws Exception
	{
		dynaNode = new DynaNode(typeService.getType("org.otherobjects.Dyna.jcr.TestObject"));
		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
		
		PropertyUtils.setNestedProperty(dynaNodeBean, "testString", "testString");
		PropertyUtils.setNestedProperty(dynaNodeBean, "testText", "testText");
		PropertyUtils.setNestedProperty(dynaNodeBean, "testDate", now);
		PropertyUtils.setNestedProperty(dynaNodeBean, "testTime", now);
		PropertyUtils.setNestedProperty(dynaNodeBean, "testTimestamp", now);
		PropertyUtils.setNestedProperty(dynaNodeBean, "testNumber", new Long(1));
		PropertyUtils.setNestedProperty(dynaNodeBean, "testDecimal", new BigDecimal(1.0));
		PropertyUtils.setNestedProperty(dynaNodeBean, "testBoolean", Boolean.FALSE);
		dynaNodeBean.setPath("/test");
		dynaNodeBean.setCode("testNode");
		
		beanService.copyBeanProperties(dynaNodeBean, dynaNode);
		
		assertEquals(dynaNode.get("testString"), PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
		assertEquals(dynaNode.get("testText"), PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
		assertEquals(dynaNode.get("testDate"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
		assertEquals(dynaNode.get("testTime"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
		assertEquals(dynaNode.get("testTimestamp"), PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
		assertEquals(dynaNode.get("testNumber"), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
		assertEquals(dynaNode.get("testDecimal"), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
		assertEquals(dynaNode.get("testBoolean"), PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
		
	}
	
	
	
	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		setupTypesService(typeService);
		beanService = new JcrBeanService();
		beanService.setTypeService(typeService);
		dynaNode = new DynaNode(typeService.getType("org.otherobjects.Dyna.jcr.TestObject"));
		now = new Date();
		populateDynNode(dynaNode);
	}

	private void populateDynNode(DynaNode dynaNode2) {
		dynaNode.set("testString", "testString");
		dynaNode.set("testText", "testText");
		dynaNode.set("testDate", now);
		dynaNode.set("testTime", now);
		dynaNode.set("testTimestamp", now);
		dynaNode.set("testNumber", new Long(1));
		dynaNode.set("testDecimal", new BigDecimal(1.0));
		dynaNode.set("testBoolean", Boolean.FALSE);
		dynaNode.setPath("/test");
		dynaNode.setCode("testNode");
	}

	@Override
	protected void onTearDown() throws Exception {
		super.onTearDown();
		typeService.unregisterType("org.otherobjects.Dyna.jcr.TestObject");
        typeService.unregisterType("org.otherobjects.Dyna.jcr.TestReferenceObject");
        typeService.unregisterType("org.otherobjects.Dyna.jcr.TestComponentObject");
        dynaNode = null;
        now = null;
	}

	private void setupTypesService(TypeService typeService)
    {
        TypeDef td = new TypeDef("org.otherobjects.Dyna.jcr.TestObject");
        td.addProperty(new PropertyDef("testString", "string", null, null));
        td.addProperty(new PropertyDef("testText", "text", null, null));
        td.addProperty(new PropertyDef("testDate", "date", null, null));
        td.addProperty(new PropertyDef("testTime", "time", null, null));
        td.addProperty(new PropertyDef("testTimestamp", "timestamp", null, null));
        td.addProperty(new PropertyDef("testNumber", "number", null, null));
        td.addProperty(new PropertyDef("testDecimal", "decimal", null, null));
        td.addProperty(new PropertyDef("testBoolean", "boolean", null, null));
//        td.addProperty(new PropertyDef("testReference", "reference", "org.otherobjects.Dyna.jcr.TestReferenceObject", null));
//        td.addProperty(new PropertyDef("testComponent", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
//        td.addProperty(new PropertyDef("testStringsList", "string", null, "list"));
//        td.addProperty(new PropertyDef("testComponentsList", "component", null, "list"));
//        td.addProperty(new PropertyDef("testReferencesList", "reference", null, "list"));
        typeService.registerType(td);

//        TypeDef td2 = new TypeDef("org.otherobjects.Dyna.jcr.TestReferenceObject");
//        td2.addProperty(new PropertyDef("name", "string", null, null));
//        typeService.registerType(td2);
//
//        TypeDef td3 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObject");
//        td3.addProperty(new PropertyDef("name", "string", null, null));
//        td3.addProperty(new PropertyDef("component", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
//        typeService.registerType(td3);

    }

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}
	
	
}
