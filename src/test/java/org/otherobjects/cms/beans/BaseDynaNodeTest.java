package org.otherobjects.cms.beans;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public abstract class BaseDynaNodeTest extends BaseJcrTestCase
{

    public static final String TEST_TYPE_NAME = "org.otherobjects.Dyna.jcr.TestObject";

    protected TypeService typeService;
    protected DynaNodeDao dynaNodeDao;
    protected DynaNode dynaNode;
    protected Date now;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        setupTypesService(typeService);
        dynaNode = dynaNodeDao.create(TEST_TYPE_NAME);
        now = new Date();
        populateDynNode(dynaNode);
    }

    protected void populateDynNode(DynaNode dynaNode) throws Exception
    {
        PropertyUtils.setNestedProperty(dynaNode, "testString", "testString");
        PropertyUtils.setNestedProperty(dynaNode, "testText", "testText");
        PropertyUtils.setNestedProperty(dynaNode, "testDate", now);
        PropertyUtils.setNestedProperty(dynaNode, "testTime", now);
        PropertyUtils.setNestedProperty(dynaNode, "testTimestamp", now);
        PropertyUtils.setNestedProperty(dynaNode, "testNumber", new Long(1));
        PropertyUtils.setNestedProperty(dynaNode, "testDecimal", new BigDecimal(1.0));
        PropertyUtils.setNestedProperty(dynaNode, "testBoolean", Boolean.FALSE);
        dynaNode.setPath("/test");
        dynaNode.setCode("testNode");
    }

    @Override
    protected void onTearDown() throws Exception
    {
        super.onTearDown();
        typeService.unregisterType(TEST_TYPE_NAME);
        typeService.unregisterType("org.otherobjects.Dyna.jcr.TestReferenceObject");
        typeService.unregisterType("org.otherobjects.Dyna.jcr.TestComponentObject");
        dynaNode = null;
        now = null;
    }

    protected void setupTypesService(TypeService typeService)
    {
        //            TypeDef td2 = new TypeDef("org.otherobjects.Dyna.jcr.TestReferenceObject");
        //            td2.addProperty(new PropertyDef("name", "string", null, null));
        //            typeService.registerType(td2);

        TypeDef td3 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObject");
        td3.addProperty(new PropertyDef("name", "string", null, null));
        td3.addProperty(new PropertyDef("requiredString", "string", null, null, true));
        //            td3.addProperty(new PropertyDef("component", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
        td3.setLabelProperty("name");
        typeService.registerType(td3);

        TypeDef td = new TypeDef("org.otherobjects.Dyna.jcr.TestObject");
        td.addProperty(new PropertyDef("testString", "string", null, null));
        td.addProperty(new PropertyDef("testText", "text", null, null));
        td.addProperty(new PropertyDef("testDate", "date", null, null));
        td.addProperty(new PropertyDef("testTime", "time", null, null));
        td.addProperty(new PropertyDef("testTimestamp", "timestamp", null, null));
        td.addProperty(new PropertyDef("testNumber", "number", null, null));
        td.addProperty(new PropertyDef("testDecimal", "decimal", null, null));
        td.addProperty(new PropertyDef("testBoolean", "boolean", null, null));
        td.addProperty(new PropertyDef("testComponent", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
        td.setLabelProperty("testString");
        //            td.addProperty(new PropertyDef("testReference", "reference", "org.otherobjects.Dyna.jcr.TestReferenceObject", null));
        //            td.addProperty(new PropertyDef("testStringsList", "string", null, "list"));
        //            td.addProperty(new PropertyDef("testComponentsList", "component", null, "list"));
        //            td.addProperty(new PropertyDef("testReferencesList", "reference", null, "list"));
        typeService.registerType(td);

    }

    //  protected DynaNode createReference(String name)
    //  {
    //      DynaNode r = dynaNodeDao.create("org.otherobjects.Dyna.jcr.TestReferenceObject");
    //      r.setJcrPath("/" + name + ".html");
    //      r.set("name", name + " Name");
    //      dynaNodeDao.save(r);
    //      r = (DynaNode) dynaNodeDao.getByPath("/" + name + ".html");
    //      return r;
    //  }

    protected DynaNode createComponent(String name)
    {
        DynaNode c = dynaNodeDao.create("org.otherobjects.Dyna.jcr.TestComponentObject");
        c.set("name", name + " Name");
        return c;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

}
