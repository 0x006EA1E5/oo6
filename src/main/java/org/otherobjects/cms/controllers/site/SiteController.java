package org.otherobjects.cms.controllers.site;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.controllers.renderers.ResourceRenderer;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.SiteFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Site controller handles all site page requests. For custom application functionality
 * you will need to create and map your own controllers.
 * 
 * @author rich
 */
@Controller
@RequestMapping(value="/")
public class SiteController extends AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(SiteController.class);

    @Resource
    private DaoService daoService;

    @Resource
    private Map<String, ResourceRenderer> renderers = new HashMap<String, ResourceRenderer>();

    @RequestMapping(value="**")
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return handleRequestInternal(request, response);
    }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        // Make sure folders end with slash
        String path = request.getPathInfo();

        if (StringUtils.isEmpty(path))
            path = "/";
        else if (path.length() > 1 && !path.contains(".") && !path.endsWith("/"))
            path = path + "/";

        this.logger.info("Requested resource: {}", path);

        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        BaseNode resourceObject = null;
        String newCode = StringUtils.substringAfterLast(path, "/");        
        if (path.contains("."))
        {
            // Page requested
            resourceObject = universalJcrDao.getByPath("/site" + path);
        }
        else
        {
            // Folder requested. If this folder has it's own template then
            // render that.
            resourceObject = universalJcrDao.getByPath("/site" + path);

            if (resourceObject instanceof SiteFolder)
            {
                SiteFolder folder = (SiteFolder) resourceObject;
                String defaultPage = StringUtils.isNotBlank(folder.getDefaultPage()) ? folder.getDefaultPage() : "index.html";
                newCode = defaultPage;
                resourceObject = universalJcrDao.getByPath(folder.getJcrPath() + "/" + defaultPage);

//                if (resourceObject == null)
//                {
//                    ModelAndView mv = new ModelAndView("/site/templates/pages/404");
//                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
//                    mv.addObject("requestedPath", path);
//                    mv.addObject("folder", folder);
//                    return mv;
//                }
            }
        }

        // Handle page not found
        if (resourceObject == null)
        {
            // Determine folder
            path = StringUtils.substringBeforeLast(path, "/");
            Object folder = universalJcrDao.getByPath("/site" + path);

            //FIXME Add Security check here
            ModelAndView mv = new ModelAndView("/otherobjects/templates/hud/error-handling/oo-404-create");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mv.addObject("requestedPath", path);
            mv.addObject("folder", folder);
            mv.addObject("newCode", newCode);
            return mv;

            //throw new NotFoundException("No resource at: " + path);
        }

        // Pass control to renderer
        ResourceRenderer handler = renderers.get(resourceObject.getClass().getName());
        if (handler == null)
            handler = renderers.get("pageRenderer");
        return handler.handleRequest(resourceObject, request, response);
    }

    public void setRenderers(Map<String, ResourceRenderer> renderers)
    {
        this.renderers = renderers;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
