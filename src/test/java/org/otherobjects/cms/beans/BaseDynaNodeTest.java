package org.otherobjects.cms.beans;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;

public abstract class BaseDynaNodeTest extends BaseJcrTestCase
{

    public static final String TEST_TYPE_NAME = "org.otherobjects.Dyna.jcr.TestObject";
    public static final String TEST_REFERENCE_TYPE_NAME = "org.otherobjects.Dyna.jcr.TestReferenceObject";

    protected TypeServiceImpl typeService;
    protected DaoService daoService;
    protected DynaNodeDao dynaNodeDao;
    protected DynaNode dynaNode;
    protected Date now;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.dynaNodeDao = (DynaNodeDao) getApplicationContext().getBean("dynaNodeDao");
        setupTypesService(this.typeService);
        this.dynaNode = this.dynaNodeDao.create(TEST_TYPE_NAME);
        this.now = new Date();
        populateDynNode(this.dynaNode);
    }

    protected void populateDynNode(DynaNode dynaNode) throws Exception
    {
        PropertyUtils.setNestedProperty(dynaNode, "testString", "testString");
        PropertyUtils.setNestedProperty(dynaNode, "testText", "testText");
        PropertyUtils.setNestedProperty(dynaNode, "testDate", this.now);
        PropertyUtils.setNestedProperty(dynaNode, "testTime", this.now);
        PropertyUtils.setNestedProperty(dynaNode, "testTimestamp", this.now);
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
        this.typeService.unregisterType(TEST_TYPE_NAME);
        this.typeService.unregisterType("org.otherobjects.Dyna.jcr.TestReferenceObject");
        this.typeService.unregisterType("org.otherobjects.Dyna.jcr.TestComponentObject");
        this.dynaNode = null;
        this.now = null;
    }

    protected void setupTypesService(TypeServiceImpl typeService)
    {
        TypeDef td2 = new TypeDef("org.otherobjects.Dyna.jcr.TestReferenceObject");
        td2.addProperty(new PropertyDef("name", "string", null, null));
        td2.setLabelProperty("name");
        typeService.registerType(td2);

        TypeDef td3 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObject");
        td3.addProperty(new PropertyDef("name", "string", null, null));
        td3.addProperty(new PropertyDef("requiredString", "string", null, null, true));
        // FIXME Nestsed components
        // td3.addProperty(new PropertyDef("component", "component", "org.otherobjects.Dyna.jcr.TestComponentObject", null));
        td3.setLabelProperty("name");
        typeService.registerType(td3);

        TypeDef td4 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObjectWithSimpleList");
        td4.addProperty(new PropertyDef("name", "string", null, null));
        td4.addProperty(new PropertyDef("testSimpleList", "list", null, "string"));
        td4.setLabelProperty("name");
        typeService.registerType(td4);

        TypeDef td5 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObjectWithComponentList");
        td5.addProperty(new PropertyDef("name", "string", null, null));
        td5.addProperty(new PropertyDef("testComponentsList2", "list", "org.otherobjects.Dyna.jcr.TestComponentObjectWithSimpleList", "component"));
        td5.setLabelProperty("name");
        typeService.registerType(td5);

        TypeDef td6 = new TypeDef("org.otherobjects.Dyna.jcr.TestSimpleSubComponent");
        td6.addProperty(new PropertyDef("name", "string", null, null));
        td6.addProperty(new PropertyDef("reference", "reference", "org.otherobjects.Dyna.jcr.TestReferenceObject", null));
        td6.setLabelProperty("name");
        typeService.registerType(td6);

        TypeDef td7 = new TypeDef("org.otherobjects.Dyna.jcr.TestComponentObjectWithSubComponent");
        td7.addProperty(new PropertyDef("name", "string", null, null));
        td7.addProperty(new PropertyDef("subComponent", "component", "org.otherobjects.Dyna.jcr.TestSimpleSubComponent", null));
        td7.setLabelProperty("name");
        typeService.registerType(td7);

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
        td.addProperty(new PropertyDef("testReference", "reference", "org.otherobjects.Dyna.jcr.TestReferenceObject", null));
        td.addProperty(new PropertyDef("testReference2", "reference", "org.otherobjects.Dyna.jcr.TestReferenceObject", null, true));
        td.addProperty(new PropertyDef("testStringsList", "list", null, "string"));
        td.addProperty(new PropertyDef("testComponentsList", "list", "org.otherobjects.Dyna.jcr.TestComponentObject", "component"));
        td.addProperty(new PropertyDef("testReferencesList", "list", "org.otherobjects.Dyna.jcr.TestReferenceObject", "reference"));
        td.addProperty(new PropertyDef("testDeepComponentsList", "list", "org.otherobjects.Dyna.jcr.TestComponentObjectWithComponentList", "component"));
        td.addProperty(new PropertyDef("testDeepComponent", "component", "org.otherobjects.Dyna.jcr.TestComponentObjectWithSubComponent", null));
        td.setLabelProperty("testString");
        typeService.registerType(td);

        typeService.generateClasses();

    }

    protected DynaNode createReference(String name)
    {
        DynaNode r = this.dynaNodeDao.create("org.otherobjects.Dyna.jcr.TestReferenceObject");
        r.setJcrPath("/" + name + ".html");
        r.set("name", name + " Name");
        this.dynaNodeDao.save(r);
        r = this.dynaNodeDao.getByPath("/" + name + ".html");
        return r;
    }

    protected DynaNode createComponent(String name)
    {
        DynaNode c = this.dynaNodeDao.create("org.otherobjects.Dyna.jcr.TestComponentObject");
        c.setCode(name);
        c.set("name", name + " Name");
        return c;
    }

    public void setTypeService(TypeServiceImpl typeService)
    {
        this.typeService = typeService;
    }

    public DaoService getDaoService()
    {
        return this.daoService;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

}
