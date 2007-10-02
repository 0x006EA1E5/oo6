package org.otherobjects.cms.controllers.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.workbench.NavigatorService;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class PageHandler implements ResourceHandler
{
    private NavigatorService navigatorService;
    private DaoService daoService;
    
    public ModelAndView handleRequest(DynaNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // Update session counter
        HttpSession session = request.getSession();
        Integer counter = (Integer) session.getAttribute("counter");
        if (counter == null)
            counter = 0;
        session.setAttribute("counter", ++counter);

        // Determine template to use
        DynaNode template = determineTemplate(resourceObject);
        Assert.notNull(template, "No template found for type: " + resourceObject.getTypeDef().getName());

        // Return page and context
        ModelAndView view = new ModelAndView("/site.resources/templates/layouts/" + template.get("layout.code") + "");
        view.addObject("counter", counter);
        view.addObject("resourceObject", resourceObject);
        view.addObject("sessionId", session.getId());
        view.addObject("template", template);
        view.addObject("navigatorService", this.navigatorService);
        view.addObject("cmsImageTool", new CmsImageTool());
        return view;
    }
    
    /**
     * FIXME Classes for TemplateDao and Template?
     * @param resourceObject
     * @return
     */
    private DynaNode determineTemplate(DynaNode resourceObject)
    {
        if (resourceObject.hasProperty("template") && resourceObject.get("template") != null)
            return (DynaNode) resourceObject.get("template");
        DynaNodeDao dynaNodeDao = (DynaNodeDao) this.daoService.getDao(DynaNode.class);
        DynaNode template = dynaNodeDao.getByPath("/designer/templates/" + resourceObject.getTypeDef().getName());
        return template;
    }

    public void setNavigatorService(NavigatorService navigatorService)
    {
        this.navigatorService = navigatorService;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
