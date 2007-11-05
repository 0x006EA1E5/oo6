package org.otherobjects.cms.controllers.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.site.SiteItem;
import org.otherobjects.cms.site.SiteNavigatorService;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.workbench.NavigatorService;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class PageHandler implements ResourceHandler
{
    private NavigatorService navigatorService;
    private SiteNavigatorService siteNavigatorService;
    private DaoService daoService;

    public ModelAndView handleRequest(CmsNode o, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        BaseNode resourceObject = (BaseNode) o;

        // Update session counter
        HttpSession session = request.getSession();
        Integer counter = (Integer) session.getAttribute("counter");
        if (counter == null)
            counter = 0;
        session.setAttribute("counter", ++counter);

        // Determine template to use
        BaseNode template = determineTemplate(resourceObject);
        Assert.notNull(template, "No template found for type: " + resourceObject.getTypeDef().getName());

        // Return page and context
        ModelAndView view = new ModelAndView("/site.resources/templates/layouts/" + template.get("layout.code") + "");
        view.addObject("counter", counter);
        view.addObject("resourceObject", resourceObject);
        view.addObject("sessionId", session.getId());
        view.addObject("template", template);
        view.addObject("navigatorService", this.navigatorService);
        view.addObject("siteNavigator", this.siteNavigatorService);
        if (SiteItem.class.isAssignableFrom(resourceObject.getClass())) //put the trail in the ctx if we are dealing with a SiteItem object
            view.addObject("trail", siteNavigatorService.getTrail((SiteItem) resourceObject));

        view.addObject("cmsImageTool", new CmsImageTool());
        return view;
    }

    /**
     * FIXME Classes for TemplateDao and Template?
     * @param resourceObject
     * @return
     */
    private BaseNode determineTemplate(BaseNode resourceObject)
    {
        if (resourceObject.hasProperty("template") && resourceObject.get("template") != null)
            return (BaseNode) resourceObject.get("template");
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        BaseNode template = universalJcrDao.getByPath("/designer/templates/" + resourceObject.getTypeDef().getName());
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

    public void setSiteNavigatorService(SiteNavigatorService siteNavigatorService)
    {
        this.siteNavigatorService = siteNavigatorService;
    }
}
