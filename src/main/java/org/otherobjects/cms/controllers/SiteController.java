package org.otherobjects.cms.controllers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsInsufficientAuthorityException;
import org.otherobjects.cms.controllers.renderers.ResourceRenderer;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.security.SecurityUtil;
import org.otherobjects.cms.site.NavigationService;
import org.otherobjects.cms.tools.SecurityTool;
import org.otherobjects.cms.util.JcrPathTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Site controller handles all site page requests. For custom application
 * functionality you will need to create and map your own controllers.
 * 
 * @author rich
 */
public class SiteController extends AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(SiteController.class);

    private DaoService daoService;

    private NavigationService navigationService;

    private Map<String, ResourceRenderer> renderers = new HashMap<String, ResourceRenderer>();

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

                // if (resourceObject == null)
                // {
                // ModelAndView mv = new
                // ModelAndView("/site/templates/pages/404");
                // response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                // mv.addObject("requestedPath", path);
                // mv.addObject("folder", folder);
                // return mv;
                // }
            }
        }

        // Handle page not found
        if (resourceObject == null)
        {
            // Determine folder
            path = StringUtils.substringBeforeLast(path, "/");
            Object folder = universalJcrDao.getByPath("/site" + path);

            // FIXME Add Security check here
            // ModelAndView mv = new
            // ModelAndView("/otherobjects/templates/hud/error-handling/oo-404-create");
            ModelAndView mv = new ModelAndView("/site/templates/pages/404");
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mv.addObject("requestedPath", path);
            mv.addObject("folder", folder);
            mv.addObject("newCode", newCode);
            return mv;

            // throw new NotFoundException("No resource at: " + path);
        }

        // Handle access forbidden
        try
        {
            accessAllowed(resourceObject);
        }
        catch (OtherObjectsInsufficientAuthorityException e)
        {
            Object forbiddenFolder = universalJcrDao.getByPath("/site" + e.getMessage());
            String notAuthorizedResource = null;
            if (forbiddenFolder instanceof SiteFolder)
                notAuthorizedResource = ((SiteFolder) forbiddenFolder).getNotAuthorizedResource();

            ModelAndView mv;
            if (StringUtils.isNotBlank(notAuthorizedResource))
            {
                if (notAuthorizedResource.startsWith("redirect:"))
                {
                    return new ModelAndView(notAuthorizedResource);
                }
                else
                {
                    mv = new ModelAndView("/site" + notAuthorizedResource);
                }
            }
            else
            {
                mv = new ModelAndView("/site/templates/pages/403");
            }
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            mv.addObject("requestedPath", path);
            mv.addObject("forbiddenFolder", forbiddenFolder);
            return mv;
        }

        // Pass control to renderer
        ResourceRenderer handler = renderers.get(resourceObject.getClass().getName());
        if (handler == null)
            handler = renderers.get("*");
        return handler.handleRequest(resourceObject, request, response);
    }

    private void accessAllowed(BaseNode resourceObject)
    {
        SecurityTool securityTool = new SecurityTool();
        logger.debug("checking access to path: {}", resourceObject.getJcrPath());
        for (Iterator<JcrPathTool.PathItem> it = JcrPathTool.walkUpPath(resourceObject.getJcrPath().substring("/site".length())); it.hasNext();)
        {
            JcrPathTool.PathItem pi = it.next();
            if (pi.isFolder())
            {
                List<String> requiredRoles = navigationService.getRolesForPath(pi.getPathUpToHere());
                String reqRoles = StringUtils.join(requiredRoles, ",");
                if (requiredRoles != null && !securityTool.authorize("", reqRoles, ""))
                {
                    logger.debug(String.format("Trying to access path [%s] which requires any of the following role(s): [%s] - current user has only following role(s): [%s]", pi.getPathUpToHere(),
                            reqRoles, SecurityUtil.getCurrentAuthoritiesAsString()));
                    throw new OtherObjectsInsufficientAuthorityException(pi.getPathUpToHere());
                }
            }
        }
    }

    public void setRenderers(Map<String, ResourceRenderer> renderers)
    {
        this.renderers = renderers;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public void setNavigationService(NavigationService navigationService)
    {
        this.navigationService = navigationService;
    }

}
