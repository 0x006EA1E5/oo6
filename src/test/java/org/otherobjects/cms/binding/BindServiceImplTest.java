package org.otherobjects.cms.binding;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.beans.BaseBeanServiceTest;
import org.otherobjects.cms.bind.BindServiceImpl;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

public class BindServiceImplTest extends BaseBeanServiceTest {
	
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
		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
		beanService.copyDynamicProperties(dynaNode, dynaNodeBean);
		
		BindingResult errors = bindService.bind(dynaNodeBean, getRequest());
		assertTrue(errors.getErrorCount() == 0);
		
		assertEquals("testString1", PropertyUtils.getNestedProperty(dynaNodeBean, "testString"));
		assertEquals("testText1", PropertyUtils.getNestedProperty(dynaNodeBean, "testText"));
		assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNodeBean, "testDate"));
		assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNodeBean, "testTime"));
		assertEquals(testDate, PropertyUtils.getNestedProperty(dynaNodeBean, "testTimestamp"));
		assertEquals(new Long(7), PropertyUtils.getNestedProperty(dynaNodeBean, "testNumber"));
		assertEquals(new BigDecimal(2.7, new MathContext(2, RoundingMode.HALF_UP)), PropertyUtils.getNestedProperty(dynaNodeBean, "testDecimal"));
		assertEquals(Boolean.FALSE, PropertyUtils.getNestedProperty(dynaNodeBean, "testBoolean"));
		
	}
	
	public void testBindError()
	{
		DynaNode dynaNodeBean = beanService.createCustomDynaNodeBean(dynaNode);
		beanService.copyDynamicProperties(dynaNode, dynaNodeBean);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("testNumber", "abc"); // the wrong one
		
		request.addParameter("testString", "testString1");
		request.addParameter("testText", "testText1");
		request.addParameter("testDate", "01/01/99");
		request.addParameter("testTime", "01/01/99");
		request.addParameter("testTimestamp", "01/01/99");
		request.addParameter("testDecimal", "2.7");
		request.addParameter("testBoolean", "false");
		
		BindingResult errors = bindService.bind(dynaNodeBean, request);
		
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
