package org.otherobjects.cms.binding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.binding.BindServiceImplNG.ListProps;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.MockDaoService;
import org.otherobjects.cms.dao.MockGenericDao;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefBuilder;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

/**
 * We should really mock the Jcr stuff in this to make it a better unit test...
 * 
 * @author rich
 */
public class BindServiceImplNGTest extends TestCase
{
    private final String dateFormat = "dd MM yyyy";
    private final String timeFormat = "hh:mm";
    private final String timestampFormat = dateFormat + " " + timeFormat;

    private BindServiceImplNG bindService;
    protected Date now = new Date();
    private Date testDate;
    private TypeService typeService = new TypeServiceImpl();
    private TypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
    private DaoService daoService;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        this.bindService = new BindServiceImplNG();
        //set date/time formats
        PropertyDefImpl.setDateFormat("dd MM yyyy");
        PropertyDefImpl.setTimeFormat("hh:mm");
        PropertyDefImpl.setTimestampFormat("dd MM yyyy hh:mm");

        //this.bindService.setDaoService(this.daoService);
        SingletonBeanLocator.registerTestBean("typeService", typeService);

        typeService.registerType(typeDefBuilder.getTypeDef(TestObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TestReferenceObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TestComponentObject.class));
        ((TypeServiceImpl) typeService).reset();
        testDate = new SimpleDateFormat(this.dateFormat).parse("01 01 1999");
        this.daoService = new MockDaoService(new MockGenericDao(createTestReferenceObject("TR1")));
        this.bindService.setDaoService(this.daoService);
    }

    public void testBindValues() throws Exception
    {
        TestObject o = new TestObject();
        HttpServletRequest request = getRequest();
        BindingResult errors = this.bindService.bind(o, o.getTypeDef(), request);

        assertTrue(errors.getErrorCount() == 0);

        // Simple properties
        assertEquals("testString1", PropertyUtils.getNestedProperty(o, "testString"));
        assertEquals("testText1", PropertyUtils.getNestedProperty(o, "testText"));
        assertEquals(new SimpleDateFormat(dateFormat).parse(request.getParameter("testDate")), PropertyUtils.getNestedProperty(o, "testDate"));
        assertEquals(new SimpleDateFormat(timeFormat).parse(request.getParameter("testTime")), PropertyUtils.getNestedProperty(o, "testTime"));
        assertEquals(new SimpleDateFormat(timestampFormat).parse(request.getParameter("testTimestamp")), PropertyUtils.getNestedProperty(o, "testTimestamp"));
        assertEquals(new Long(7), PropertyUtils.getNestedProperty(o, "testNumber"));
        assertEquals(new BigDecimal(2.7, new MathContext(2, RoundingMode.HALF_UP)), PropertyUtils.getNestedProperty(o, "testDecimal"));
        assertEquals(Boolean.FALSE, PropertyUtils.getNestedProperty(o, "testBoolean"));
        // TODO Make sure all supported properties are tested here

        // Component
        assertEquals("myName", PropertyUtils.getNestedProperty(o, "testComponent.name"));

        // Reference
        assertEquals("TR1 Name", PropertyUtils.getNestedProperty(o, "testReference.name"));
    }

    public void xtestCalcListProps()
    {
        BindServiceImplNG bs = new BindServiceImplNG();

        Map<String, String> params = new HashMap<String, String>();

        params.put("test.list[1].prop1", "");
        params.put("test.list[0].prop3", "");
        params.put("test.list[8].test", "");
        params.put("test.list[3].list[2].name", "");
        params.put("test.list[6].prop4", "");
        params.put("test.list[10].prop2", "");
        params.put("test.list[2].prop1.address1", "");
        params.put("test.list[4].prop1.address2", "");
        params.put("test.list[7].prop1.moreTest", "");
        params.put("test.list[5].prop1.anotherList[0]", "");

        ListProps props = bs.calcListProps("test.list", params);

        assertEquals(10, props.getUsedIndices().size());
        assertEquals(11, props.getRequiredSize());

        params.put("test.list[5].prop1.list2[1]", "");

        ListProps props2 = bs.calcListProps("test.list[5].prop1.list2", params);

        assertEquals(1, props2.getUsedIndices().size());
        assertEquals(2, props2.getRequiredSize());

    }

    public void testTopLevelDynaNode() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.addParameter("testDate", "01 01 1999");

        TypeDefImpl typeDef = new TypeDefImpl();
        typeDef.addProperty(new PropertyDefImpl("testDate", "date", null, null));
        typeService.registerType(typeDef);
        ((TypeServiceImpl) typeService).reset();

        BindServiceImplNG bs = new BindServiceImplNG();
        DynaNode dn = new DynaNode();

        BindingResult errors = bs.bind(dn, typeDef, req);

        assertTrue(errors.getErrorCount() == 0);
        assertEquals(new SimpleDateFormat("dd MM yyyy").parse("01 01 1999"), dn.get("testDate"));
    }

    public void testNestedExistingDynaNode() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.addParameter("testNode.testDate", "01 01 1999");
        req.addParameter("name", "testBeanName");

        TypeDefImpl typeDef = new TypeDefImpl("org.otherobjects.cms.binding.BeanWithDynaNodeProperty");
        typeDef.addProperty(new PropertyDefImpl("name", "string", null, null));
        typeDef.addProperty(new PropertyDefImpl("testNode", "component", "testDynaNode", null));

        TypeDefImpl dynaType = new TypeDefImpl("testDynaNode");
        dynaType.addProperty(new PropertyDefImpl("testDate", "date", null, null));

        typeService.registerType(typeDef);
        typeService.registerType(dynaType);

        ((TypeServiceImpl) typeService).reset();

        BindServiceImplNG bs = new BindServiceImplNG();
        BeanWithDynaNodeProperty bean = new BeanWithDynaNodeProperty();
        //DynaNode testNode = new DynaNode();
        bean.setTestNode(new DynaNode("testDynaNode"));

        BindingResult errors = bs.bind(bean, typeDef, req);

        assertTrue(errors.getErrorCount() == 0);
        assertEquals("testBeanName", bean.getName());
        assertEquals(new SimpleDateFormat("dd MM yyyy").parse("01 01 1999"), bean.getTestNode().get("testDate"));
    }

    public void testNestedNonExistingDynaNode() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();

        req.addParameter("testNode.testDate", "01 01 1999");
        req.addParameter("name", "testBeanName");

        TypeDefImpl typeDef = new TypeDefImpl("org.otherobjects.cms.binding.BeanWithDynaNodeProperty");
        typeDef.addProperty(new PropertyDefImpl("name", "string", null, null));
        typeDef.addProperty(new PropertyDefImpl("testNode", "component", "testDynaNode", null));

        TypeDefImpl dynaType = new TypeDefImpl("testDynaNode");
        dynaType.addProperty(new PropertyDefImpl("testDate", "date", null, null));

        typeService.registerType(typeDef);
        typeService.registerType(dynaType);

        ((TypeServiceImpl) typeService).reset();

        BindServiceImplNG bs = new BindServiceImplNG();
        BeanWithDynaNodeProperty bean = new BeanWithDynaNodeProperty();

        BindingResult errors = bs.bind(bean, typeDef, req);

        assertTrue(errors.getErrorCount() == 0);
        assertEquals("testBeanName", bean.getName());
        assertEquals(new SimpleDateFormat("dd MM yyyy").parse("01 01 1999"), bean.getTestNode().get("testDate"));
    }

    @SuppressWarnings("unchecked")
    public void xtestBindList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testStringsList[0]", "testString1");

        TestObject o = new TestObject();
        this.bindService.bind(o, o.getTypeDef(), request);

        assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(o, "testStringsList").getClass()));
        List testStringList = (List) PropertyUtils.getSimpleProperty(o, "testStringsList");
        assertEquals("testString1", testStringList.get(0));
    }

    public void xtestBindComponentList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testComponentsList[0].name", "testName");

        TestObject o = new TestObject();
        this.bindService.bind(o, o.getTypeDef(), request);

        assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(o, "testComponentsList").getClass()));
        assertTrue(BaseNode.class.isAssignableFrom(PropertyUtils.getIndexedProperty(o, "testComponentsList", 0).getClass()));
        assertEquals("testName", PropertyUtils.getSimpleProperty(PropertyUtils.getIndexedProperty(o, "testComponentsList", 0), "name"));
    }

    public void xtestBindReferenceList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testReferencesList[2]", "ae9a48f6-8e4e-4bad-8954-3baea6ed416f");

        TestObject o = new TestObject();
        this.bindService.bind(o, o.getTypeDef(), request);

        assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(o, "testReferencesList").getClass()));
        // FIXME This needs fixing
        System.err.println("************** SKIPPING " + getClass().getName() + ".testBindReferenceList ********************");
        //        assertTrue(BaseNode.class.isAssignableFrom(PropertyUtils.getIndexedProperty(o, "testReferencesList", 2).getClass()));
    }

    public void xtestDeepNestList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testComponentsList[0].componentsList[1].stringsList[0]", "testDeepName2");
        request.addParameter("testComponentsList[0].componentsList[0].stringsList[0]", "testDeepName");
        request.addParameter("testComponentsList[0].componentsList[2].stringsList[0]", "testDeepName3");

        TestObject o = new TestObject();
        this.bindService.bind(o, o.getTypeDef(), request);

        Object deepComponentListEntry = PropertyUtils.getIndexedProperty(o, "testComponentsList", 0);
        Object firstLevelWithComponentsList = PropertyUtils.getIndexedProperty(deepComponentListEntry, "componentsList", 2);
        Object simpleListEntry = PropertyUtils.getIndexedProperty(firstLevelWithComponentsList, "stringsList", 0);

        assertEquals("testDeepName3", simpleListEntry);

    }

    //    public void xtestDeepNestComponent() throws Exception
    //    {
    //        TestReferenceObject r1 = createAndSaveReference("R1");
    //
    //        MockHttpServletRequest request = new MockHttpServletRequest();
    //        request.addParameter("testComponent.component.reference", r1.getId());
    //        request.addParameter("testComponent.component.name", "testSubName");
    //        request.addParameter("testComponent.name", "testSuperName");
    //
    //        TestObject o = new TestObject();
    //        this.bindService.bind(o, o.getTypeDef(), request);
    //
    //        assertEquals("testSuperName", PropertyUtils.getNestedProperty(o, "testComponent.name"));
    //        assertEquals("testSubName", PropertyUtils.getNestedProperty(o, "testComponent.component.name"));
    //
    //        // FIXME Deep references are broken
    //        //assertTrue(BaseNode.class.isAssignableFrom(PropertyUtils.getNestedProperty(baseNode, "testDeepComponent.subComponent.reference").getClass()));
    //    }

    public void xtestBindError()
    {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testNumber", "abc"); // the wrong one
        request.addParameter("testString", "testString1");
        request.addParameter("testText", "testText1");
        request.addParameter("testDate", "01 01 1999");
        request.addParameter("testTime", "12:00:00");
        request.addParameter("testTimestamp", "01 01 1999 12:00:00");
        request.addParameter("testDecimal", "2.7");
        request.addParameter("testBoolean", "false");

        TestObject o = new TestObject();
        BindingResult errors = this.bindService.bind(o, o.getTypeDef(), request);

        assertTrue(errors.getErrorCount() == 1);

        FieldError numberFieldError = errors.getFieldError("testNumber");
        assertTrue(numberFieldError.getCode().equals("typeMismatch"));
    }

    private MockHttpServletRequest getRequest()
    {
        // adminLogin();
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testString", "testString1");
        request.addParameter("testText", "testText1");
        request.addParameter("testDate", "01 01 1999");
        request.addParameter("testTime", "12:00");
        request.addParameter("testTimestamp", "01 01 1999 12:00");
        request.addParameter("testNumber", "7");
        request.addParameter("testDecimal", "2.7");
        request.addParameter("testBoolean", "false");
        request.addParameter("testComponent.name", "myName");

        //        BaseNode tr1 = createAndSaveReference("TR1");
        request.addParameter("testReference", "ae9a48f6-8e4e-4bad-8954-3baea6ed416f");
        //  logout();
        return request;
    }

    protected TestObject createTestObject()
    {
        TestObject o = new TestObject();
        o.setTestString("testString");
        o.setTestText("testText");
        //        o.setTestDate(this.now);
        //        o.setTestTime(this.now);
        //        o.setTestTimestamp(this.now);
        o.setTestNumber(new Long(1));
        o.setTestDecimal(new BigDecimal(1.0));
        o.setTestBoolean(Boolean.FALSE);
        o.setPath("/test");
        o.setCode("testNode");
        return o;
    }

    protected TestReferenceObject createTestReferenceObject(String name)
    {
        TestReferenceObject r = new TestReferenceObject();
        r.setJcrPath("/" + name + ".html");
        r.setName(name + " Name");
        r.setId("ae9a48f6-8e4e-4bad-8954-3baea6ed416f");
        return r;
    }
    //
    //    protected TestComponentObject createComponent(String name)
    //    {
    //        TestComponentObject c = new TestComponentObject();
    //        c.setJcrPath("/" + name + ".html");
    //        c.setName(name + " Name");
    //        return (TestComponentObject) universalJcrDao.save(c);
    //    }
}
