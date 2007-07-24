package org.otherobjects.cms.beans;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public abstract class BaseBeanServiceTest extends BaseJcrTestCase {

	protected TypeService typeService;
	protected JcrBeanService jcrBeanService;
	protected DynaNode dynaNode;
	protected Date now;

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		setupTypesService(typeService);
		dynaNode = jcrBeanService.createCustomDynaNodeBean("org.otherobjects.Dyna.jcr.TestObject");
		now = new Date();
		populateDynNode(dynaNode);
	}

	protected void populateDynNode(DynaNode dynaNode) throws Exception {
		PropertyUtils.setNestedProperty(dynaNode,"testString", "testString");
		PropertyUtils.setNestedProperty(dynaNode,"testText", "testText");
		PropertyUtils.setNestedProperty(dynaNode,"testDate", now);
		PropertyUtils.setNestedProperty(dynaNode,"testTime", now);
		PropertyUtils.setNestedProperty(dynaNode,"testTimestamp", now);
		PropertyUtils.setNestedProperty(dynaNode,"testNumber", new Long(1));
		PropertyUtils.setNestedProperty(dynaNode,"testDecimal", new BigDecimal(1.0));
		PropertyUtils.setNestedProperty(dynaNode,"testBoolean", Boolean.FALSE);
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

	protected void setupTypesService(TypeService typeService) {
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
	        
	        //FIXME we need to deal with the reference and component types as well at some point
	
	    }

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public void setJcrBeanService(JcrBeanService jcrBeanService) {
		this.jcrBeanService = jcrBeanService;
	}
	
	

}
