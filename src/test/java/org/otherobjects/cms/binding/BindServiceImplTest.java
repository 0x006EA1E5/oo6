package org.otherobjects.cms.binding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

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

    public void testCreateSubObjects()
    {     
        // Check that only component (not reference) sub objects are created
        DynaNode node = dynaNodeDao.create(BaseDynaNodeTest.TEST_TYPE_NAME);
        assertNull(node.get("testComponent"));
        bindService.createSubObjects(node, "testComponent.name");
        assertNotNull(node.get("testComponent"));

        assertNull(node.get("testReference"));
        bindService.createSubObjects(node, "testReference");
        assertNull(node.get("testReference"));
       
        // Make sure it has the type set
        assertNotNull(((DynaNode) node.get("testComponent")).getOoType());
        
        // Check it is correct class
        assertEquals(node.getTypeDef().getProperty("testComponent").getRelatedTypeDef().getClassName(), node.get("testComponent").getClass().getName());
    }
}
