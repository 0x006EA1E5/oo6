package org.otherobjects.cms.controllers.renderers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.Comment;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.ModelAndView;

public class PageRenderer implements ResourceRenderer
{
    //    private NavigatorService navigatorService;
    //    private SiteNavigatorService siteNavigatorService;
    private DaoService daoService;

    public ModelAndView handleRequest(CmsNode o, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        BaseNode resourceObject = (BaseNode) o;

        // Determine template to use
        Template template = determineTemplate(resourceObject);
        Assert.notNull(template, "No template found for type: " + resourceObject.getTypeDef().getName());

        // Return page and context
        TemplateLayout layout = template.getLayout();
        Assert.notNull(layout, "No layout defined for template: " +template.getLabel());

        ModelAndView view = new ModelAndView("/site/templates/layouts/" + layout.getCode().replaceAll("\\.html", "") + "");

        view.addObject("resourceObject", resourceObject);

        view.addObject("ooTemplate", template);

        HttpSession session = request.getSession(false);
        if (session != null)
        {
            BindingResult fo = (BindingResult) session.getAttribute("errors");
            if (fo != null && fo.getModel() != null)
            {
                view.addObject("org.springframework.validation.BindingResult.formObject", fo);
            }
        }
        // FIXME Let's not add this stuff here
        view.addObject("formObject", new Comment());
        view.addObject("daoService", this.daoService);
        return view;
    }

    public DaoService getDaoService()
    {
        return daoService;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    /**
     * FIXME Classes for TemplateDao and Template?
     * @param resourceObject
     * @return
     */
    private Template determineTemplate(BaseNode resourceObject)
    {
        Template template = null;
        
        // FIXME This needs to be more generic -- or at least tied to SitePage
        if(resourceObject.getPropertyValue("data.publishingOptions") != null)
        {
            template = (Template) resourceObject.getPropertyValue("data.publishingOptions.template");
            if (template != null)
                return template;
        }

        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);

        String templateCode = "";
        if (resourceObject.getTypeDef().getName().contains("."))
            templateCode = StringUtils.substringAfterLast(resourceObject.getTypeDef().getName(), ".").toLowerCase();
        else
            templateCode = resourceObject.getTypeDef().getName().toLowerCase();

        template = (Template) universalJcrDao.getByPath("/designer/templates/" + templateCode);
        return template;
    }
}
