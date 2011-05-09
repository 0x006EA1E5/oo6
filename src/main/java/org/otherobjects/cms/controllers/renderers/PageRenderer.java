package org.otherobjects.cms.controllers.renderers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.model.TemplateMapping;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

public class PageRenderer implements ResourceRenderer
{
    //    private NavigatorService navigatorService;
    //    private SiteNavigatorService siteNavigatorService;
    @Resource
	private DaoService daoService;
    
    @Resource
    private UniversalJcrDao universalJcrDao;

    public ModelAndView handleRequest(CmsNode o, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        BaseNode resourceObject = (BaseNode) o;

        // Determine template to use
        Template template = determineTemplate(resourceObject);
        if (template == null)
        {
            ModelAndView mv = new ModelAndView("/otherobjects/templates/hud/error-handling/oo-500-create-template");
            mv.addObject("resourceObject", resourceObject);
            String templateName = resourceObject.getTypeDef().getLabel();
            mv.addObject("templateName", templateName);
            // FIXME Bring this into a generic place
            mv.addObject("templateCode", templateName.toLowerCase().replaceAll("\\s", ""));
            mv.addObject("templates", getTemplates());
            mv.addObject("layouts", getLayouts());
            return mv;
        }

        // Return page and context
        TemplateLayout layout = template.getLayout();
        Assert.notNull(layout, "No layout defined for template: " + template.getLabel());

        ModelAndView view = new ModelAndView("/site/templates/layouts/" + layout.getCode().replaceAll("\\.html", "") + "");

        view.addObject("resourceObject", resourceObject);

        view.addObject("ooTemplate", template);

        // TODO Would be good to have a flag to enable this for testing
        //         ActionUtils actionUtils = new ActionUtils(request, response, null, null);
        //         actionUtils.flashWarning("Hey guys! How is it going?");

        return view;
    }

    private Object getTemplates()
    {
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        return universalJcrDao.getAllByType(Template.class);
    }

    private Object getLayouts()
    {
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        return universalJcrDao.getAllByType(TemplateLayout.class);
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
     * @param resourceObject
     * determines which template to render with the resource object
     * @return Template
     */
    private Template determineTemplate(BaseNode resourceObject)
    {
        Template template = null;

        // FIXME This needs to be more generic -- or at least tied to SitePage
        if (resourceObject.hasProperty("data.publishingOptions") && resourceObject.getPropertyValue("data.publishingOptions") != null)
        {
            template = (Template) resourceObject.getPropertyValue("data.publishingOptions.template");
            if (template != null)
                return template;
        }

        if (resourceObject.hasProperty("publishingOptions") && resourceObject.getPropertyValue("publishingOptions") != null)
        {
            template = (Template) resourceObject.getPropertyValue("publishingOptions.template");
            if (template != null)
                return template;
        }

        //check for template in parent folder 
        String typeName = resourceObject.getTypeDef().getName();
        SiteFolder folder = (SiteFolder) resourceObject.getParentNode(universalJcrDao);
        Template t = getTemplateFromFolder(typeName, folder);
        if (t != null)
            return t;
        return getTemplateByName(typeName);
    }

    /**
     * @param resourceObject
     * @return Template
     * 
     */
    private Template getTemplateByName(String typeName)
    {
        Template template;
        String templateCode = "";
        if (typeName.contains("."))
            templateCode = StringUtils.substringAfterLast(typeName, ".").toLowerCase();
        else
            templateCode = typeName.toLowerCase();
        template = (Template) universalJcrDao.getByPath("/designer/templates/" + templateCode);
        return template;
    }

    /**
     * @param resourceObject
     * @param folder
     */
    private Template getTemplateFromFolder(String typeName, SiteFolder folder)
    {
        TemplateMapping tm = folder.getTemplateMapping(typeName);
        if (tm != null)
            return tm.getTemplate();
        else
        {
            if (folder.getParentFolder(universalJcrDao) == null)
                return null;
            else
                return getTemplateFromFolder(typeName, folder.getParentFolder(universalJcrDao));
        }
    }

}
