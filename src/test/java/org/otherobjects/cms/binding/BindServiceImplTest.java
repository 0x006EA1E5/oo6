package org.otherobjects.cms.binding;

import java.lang.reflect.InvocationTargetException;
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

    private String dateFormat = "dd/MM/yy";
    Date testDate;
    private BindServiceImpl bindService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        bindService = new BindServiceImpl();
        bindService.setDateFormat(dateFormat);
        bindService.setDynaNodeDao(dynaNodeDao);
        testDate = new SimpleDateFormat(dateFormat).parse("01/01/99");
    }

    public void testBindValues() throws Exception
    {

        BindingResult errors = bindService.bind(dynaNode, getRequest());
        assertTrue(errors.getErrorCount() == 0);

        // Simple properties
        assertEquals("testString1", PropertyUtils.getNestedProperty(dynaNode, "testString"));
        assertEquals("testText1", PropertyUtils.getNestedProperty(dynaNode, "testText"));
        assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNode, "testDate"));
        assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNode, "testTime"));
        assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNode, "testTimestamp"));
        assertEquals(new Long(7), PropertyUtils.getNestedProperty(dynaNode, "testNumber"));
        assertEquals(new BigDecimal(2.7, new MathContext(2, RoundingMode.HALF_UP)), PropertyUtils.getNestedProperty(dynaNode, "testDecimal"));
        assertEquals(Boolean.FALSE, PropertyUtils.getNestedProperty(dynaNode, "testBoolean"));

        /// Component
        assertEquals("myName", PropertyUtils.getNestedProperty(dynaNode, "testComponent.name"));
        
        // Reference
        assertEquals("TR1 Name", PropertyUtils.getNestedProperty(dynaNode, "testReference.name"));
    }
    
    public void testBindList() throws Exception
    {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addParameter("testStringsList[0]", "testString1");
    	
    	BindingResult errors = bindService.bind(dynaNode, request);
    	
    	assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(dynaNode, "testStringsList").getClass()));
    	
    	List testStringList = (List) PropertyUtils.getSimpleProperty(dynaNode, "testStringsList");
    	
    	assertEquals("testString1", testStringList.get(0));
    	
    }
    
    public void testBindComponentList() throws Exception
    {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	
    	request.addParameter("testComponentsList[0].name", "testName");
    	
    	BindingResult errors = bindService.bind(dynaNode, request);
    	
    	assertTrue(List.class.isAssignableFrom(PropertyUtils.getSimpleProperty(dynaNode, "testComponentsList").getClass()));
    	
    	assertTrue(DynaNode.class.isAssignableFrom(PropertyUtils.getIndexedProperty(dynaNode, "testComponentsList", 0).getClass()));
    	
    	assertEquals("testName", PropertyUtils.getSimpleProperty(PropertyUtils.getIndexedProperty(dynaNode, "testComponentsList", 0), "name"));
    	
    	
    }
    
    
    public void testDeepNestList()  throws Exception
    {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	
    	request.addParameter("testDeepComponentsList[0].testComponentsList2[1].testSimpleList[0]", "testDeepName2");
    	request.addParameter("testDeepComponentsList[0].testComponentsList2[0].testSimpleList[0]", "testDeepName");
    	request.addParameter("testDeepComponentsList[0].testComponentsList2[2].testSimpleList[0]", "testDeepName3");
    	
    	BindingResult errors = bindService.bind(dynaNode, request);
    	
    	Object deepComponentListEntry = PropertyUtils.getIndexedProperty(dynaNode,"testDeepComponentsList", 0);
    	Object firstLevelWithComponentsList = PropertyUtils.getIndexedProperty(deepComponentListEntry,"testComponentsList2", 2);
    	Object simpleListEntry = PropertyUtils.getIndexedProperty(firstLevelWithComponentsList,"testSimpleList", 0);
    	
    	assertEquals("testDeepName3", simpleListEntry);
    	
    }
    
    public void testDeepNestComponent() throws Exception
    {
    	MockHttpServletRequest request = new MockHttpServletRequest();
    	request.addParameter("testDeepComponent.subComponent.name", "testSubName");
    	request.addParameter("testDeepComponent.name", "testSuperName");
    	BindingResult errors = bindService.bind(dynaNode, request);
    	
    	assertEquals("testSuperName", PropertyUtils.getNestedProperty(dynaNode, "testDeepComponent.name"));
    	assertEquals("testSubName", PropertyUtils.getNestedProperty(dynaNode, "testDeepComponent.subComponent.name"));
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

        BindingResult errors = bindService.bind(dynaNode, request);

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

        DynaNode tr1 = createReference("TR1");
        dynaNodeDao.save(tr1);
        request.addParameter("testReference", tr1.getId());
        
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
