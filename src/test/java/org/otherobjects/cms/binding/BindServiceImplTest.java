package org.otherobjects.cms.binding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.beans.BaseDynaNodeTest;
import org.otherobjects.cms.binding.BindServiceImpl;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BindServiceImplTest extends BaseDynaNodeTest {
	
	private String dateFormat = "dd/MM/yy";
	Date testDate;
	private BindServiceImpl bindService;
	
	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		bindService = new BindServiceImpl();
		bindService.setDateFormat(dateFormat);
		testDate = new SimpleDateFormat(dateFormat).parse("01/01/99");
	}

	public void testBindValues() throws Exception
	{
				
		BindingResult errors = bindService.bind(dynaNode, getRequest());
		assertTrue(errors.getErrorCount() == 0);
		
		assertEquals("testString1", PropertyUtils.getNestedProperty(dynaNode, "testString"));
		assertEquals("testText1", PropertyUtils.getNestedProperty(dynaNode, "testText"));
		assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNode, "testDate"));
		assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNode, "testTime"));
		assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNode, "testTimestamp"));
		assertEquals(new Long(7), PropertyUtils.getNestedProperty(dynaNode, "testNumber"));
		assertEquals(new BigDecimal(2.7, new MathContext(2, RoundingMode.HALF_UP)), PropertyUtils.getNestedProperty(dynaNode, "testDecimal"));
		assertEquals(Boolean.FALSE, PropertyUtils.getNestedProperty(dynaNode, "testBoolean"));
		
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
	
	
	public HttpServletRequest getRequest()
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
		return request;
	}
}
