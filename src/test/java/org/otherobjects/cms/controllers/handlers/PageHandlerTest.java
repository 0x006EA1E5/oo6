package org.otherobjects.cms.controllers.handlers;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.model.SitePage;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.ModelAndView;

public class PageHandlerTest extends BaseJcrTestCase {
	
	private DaoService daoService;
	private TypeService typeService;
	
	
	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}

	public void setDaoService(DaoService daoService) {
		this.daoService = daoService;
	}

	@Override
	protected void onSetUp() throws Exception {
		super.onSetUp();
		anoymousLogin();
	}

	@Override
	protected void onTearDown() throws Exception {
		super.onTearDown();
		logout();
	}

	public void testHandleRequest() throws Exception
	{
		MockHttpServletRequest  request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		
		PageHandler pageHandler = new PageHandler();
		pageHandler.setDaoService(this.daoService);
		
		DynaNode template = (DynaNode) Class.forName("org.otherobjects.cms.model.Template").newInstance();
		DynaNode templateLayout = (DynaNode) Class.forName("org.otherobjects.cms.model.TemplateLayout").newInstance();
		
		PropertyUtils.setProperty(templateLayout, "code", "TEST");
		PropertyUtils.setProperty(template, "layout", templateLayout);
		
		SitePage resource = new SitePage();
		resource.setTemplate(template);
		
		TypeDefImpl resourceTypeDef = new TypeDefImpl("SitePage");
		resourceTypeDef.setClassName(SitePage.class.getName());
		resourceTypeDef.setSuperClassName(DynaNode.class.getName());
		resourceTypeDef.addProperty(new PropertyDefImpl("template","reference","org.otherobjects.cms.model.Template",null));

		resource.setTypeDef(resourceTypeDef);
		
		ModelAndView mav = pageHandler.handleRequest(resource, request, response);
		
		assertEquals("/site.resources/templates/layouts/TEST", mav.getViewName());
		
		assertEquals(resource, mav.getModelMap().get("resourceObject"));
		
		
	}
}
