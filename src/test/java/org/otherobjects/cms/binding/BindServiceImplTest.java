package org.otherobjects.cms.binding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.beans.BaseDynaNodeTest;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BindServiceImplTest extends BaseDynaNodeTest
{

    private final String dateFormat = "dd/MM/yy";
    Date testDate;
    private BindServiceImpl bindService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.bindService = new BindServiceImpl();
        this.bindService.setDateFormat(this.dateFormat);
        this.bindService.setDaoService(this.daoService);
        this.testDate = new SimpleDateFormat(this.dateFormat).parse("01/01/99");
    }

    public void testBindValues() throws Exception
    {
        BindingResult errors = this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), getRequest());

        assertTrue(errors.getErrorCount() == 0);

        // Simple properties
        assertEquals("testString1", PropertyUtils.getNestedProperty(this.dynaNode, "testString"));
        assertEquals("testText1", PropertyUtils.getNestedProperty(this.dynaNode, "testText"));
        assertEquals(this.testDate, PropertyUtils.getNestedProperty(this.dynaNode, "testDate"));
        assertEquals(this.testDate, PropertyUtils.getNestedProperty(this.dynaNode, "testTime"));
        assertEquals(this.testDate, PropertyUtils.getNestedProperty(this.dynaNode, "testTimestamp"));
        assertEquals(new Long(7), PropertyUtils.getNestedProperty(this.dynaNode, "testNumber"));
        assertEquals(new BigDecimal(2.7, new MathContext(2, RoundingMode.HALF_UP)), PropertyUtils.getNestedProperty(this.dynaNode, "testDecimal"));
        assertEquals(Boolean.FALSE, PropertyUtils.getNestedProperty(this.dynaNode, "testBoolean"));

        /// Component
        assertEquals("myName", PropertyUtils.getNestedProperty(this.dynaNode, "testComponent.name"));

        // Reference
        assertEquals("TR1 Name", PropertyUtils.getNestedProperty(this.dynaNode, "testReference.name"));
    }

    @SuppressWarnings("unchecked")
    public void testBindList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testStringsList[0]", "testString1");

        this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), request);

        assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(this.dynaNode, "testStringsList").getClass()));

        List testStringList = (List) PropertyUtils.getSimpleProperty(this.dynaNode, "testStringsList");

        assertEquals("testString1", testStringList.get(0));

    }

    public void testBindComponentList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addParameter("testComponentsList[0].name", "testName");

        this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), request);

        assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(this.dynaNode, "testComponentsList").getClass()));

        assertTrue(DynaNode.class.isAssignableFrom(PropertyUtils.getIndexedProperty(this.dynaNode, "testComponentsList", 0).getClass()));

        assertEquals("testName", PropertyUtils.getSimpleProperty(PropertyUtils.getIndexedProperty(this.dynaNode, "testComponentsList", 0), "name"));

    }

    public void testBindReferenceList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addParameter("testReferencesList[2]", "ae9a48f6-8e4e-4bad-8954-3baea6ed416f");

        this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), request);

        assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(this.dynaNode, "testReferencesList").getClass()));
        // FIXME This needs fixing
        System.err.println("************** SKIPPING " + getClass().getName() + ".testBindReferenceList ********************");
        //        assertTrue(DynaNode.class.isAssignableFrom(PropertyUtils.getIndexedProperty(this.dynaNode, "testReferencesList", 2).getClass()));
    }

    public void testDeepNestList() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();

        request.addParameter("testDeepComponentsList[0].testComponentsList2[1].testSimpleList[0]", "testDeepName2");
        request.addParameter("testDeepComponentsList[0].testComponentsList2[0].testSimpleList[0]", "testDeepName");
        request.addParameter("testDeepComponentsList[0].testComponentsList2[2].testSimpleList[0]", "testDeepName3");

        this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), request);

        Object deepComponentListEntry = PropertyUtils.getIndexedProperty(this.dynaNode, "testDeepComponentsList", 0);
        Object firstLevelWithComponentsList = PropertyUtils.getIndexedProperty(deepComponentListEntry, "testComponentsList2", 2);
        Object simpleListEntry = PropertyUtils.getIndexedProperty(firstLevelWithComponentsList, "testSimpleList", 0);

        assertEquals("testDeepName3", simpleListEntry);

    }

    public void testDeepNestComponent() throws Exception
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testDeepComponent.subComponent.reference", "ae9a48f6-8e4e-4bad-8954-3baea6ed416f");
        request.addParameter("testDeepComponent.subComponent.name", "testSubName");
        request.addParameter("testDeepComponent.name", "testSuperName");
        this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), request);

        assertEquals("testSuperName", PropertyUtils.getNestedProperty(this.dynaNode, "testDeepComponent.name"));
        assertEquals("testSubName", PropertyUtils.getNestedProperty(this.dynaNode, "testDeepComponent.subComponent.name"));

        // FIXME Deep references are broken
        //assertTrue(DynaNode.class.isAssignableFrom(PropertyUtils.getNestedProperty(dynaNode, "testDeepComponent.subComponent.reference").getClass()));
    }

    public void testBindError()
    {

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testNumber", "abc"); // the wrong one

        request.addParameter("testString", "testString1");
        request.addParameter("testText", "testText1");
        request.addParameter("testDate", "01/01/99");
        request.addParameter("testTime", "01/01/99");
        request.addParameter("testTimestamp", "01/01/99");
        request.addParameter("testDecimal", "2.7");
        request.addParameter("testBoolean", "false");

        BindingResult errors = this.bindService.bind(this.dynaNode, this.dynaNode.getTypeDef(), request);

        assertTrue(errors.getErrorCount() == 1);

        FieldError numberFieldError = errors.getFieldError("testNumber");
        assertTrue(numberFieldError.getCode().equals("typeMismatch"));
    }

    public MockHttpServletRequest getRequest()
    {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("testString", "testString1");
        request.addParameter("testText", "testText1");
        request.addParameter("testDate", "01/01/99");
        request.addParameter("testTime", "01/01/99");
        request.addParameter("testTimestamp", "01/01/99");
        request.addParameter("testNumber", "7");
        request.addParameter("testDecimal", "2.7");
        request.addParameter("testBoolean", "false");
        request.addParameter("testComponent.name", "myName");

        adminLogin();
        DynaNode tr1 = createReference("TR1");
        this.dynaNodeDao.save(tr1);
        request.addParameter("testReference", tr1.getId());
        logout();
        return request;
    }

    //    public void testCreateSubObjects()
    //    {     
    //        // Check that only component (not reference) sub objects are created
    //        DynaNode node = dynaNodeDao.create(BaseDynaNodeTest.TEST_TYPE_NAME);
    //        assertNull(node.get("testComponent"));
    //        bindService.createSubObjects(node, "testComponent.name");
    //        assertNotNull(node.get("testComponent"));
    //
    //        assertNull(node.get("testReference"));
    //        bindService.createSubObjects(node, "testReference");
    //        assertNull(node.get("testReference"));
    //       
    //        // Make sure it has the type set
    //        assertNotNull(((DynaNode) node.get("testComponent")).getOoType());
    //        
    //        // Check it is correct class
    //        assertEquals(node.getTypeDef().getProperty("testComponent").getRelatedTypeDef().getClassName(), node.get("testComponent").getClass().getName());
    //    }
}
