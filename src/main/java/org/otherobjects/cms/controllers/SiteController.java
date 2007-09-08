package org.otherobjects.cms.controllers;import groovy.lang.Binding;import groovy.lang.GroovyShell;import java.io.File;import java.io.IOException;import java.util.List;import javax.servlet.ServletException;import javax.servlet.http.HttpServletRequest;import javax.servlet.http.HttpServletResponse;import javax.servlet.http.HttpSession;import org.apache.commons.io.FileUtils;import org.otherobjects.cms.dao.DaoService;import org.otherobjects.cms.dao.DynaNodeDao;import org.otherobjects.cms.dao.UserDao;import org.otherobjects.cms.model.DynaNode;import org.otherobjects.cms.tools.CmsImageTool;import org.otherobjects.cms.workbench.NavigatorService;import org.slf4j.Logger;import org.slf4j.LoggerFactory;import org.springframework.util.Assert;import org.springframework.web.servlet.ModelAndView;import org.springframework.web.servlet.mvc.Controller;public class SiteController implements Controller{    private final Logger logger = LoggerFactory.getLogger(SiteController.class);    private NavigatorService navigatorService;    private DaoService daoService;    private UserDao userDao;    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException    {        //        runScript();        // Make sure folders end with slash         String path = request.getPathInfo();        this.logger.info("Requested resource: {}", path);        if (path == null)            path = "/";        else if (path.length() > 1 && !path.contains(".") && !path.endsWith("/"))            path = path + "/";        DynaNodeDao dynaNodeDao = (DynaNodeDao) this.daoService.getDao(DynaNode.class);        DynaNode resourceObject = null;        if (path.contains("."))        {            // Page requested            resourceObject = dynaNodeDao.getByPath("/site" + path);        }        else        {            // Folder requested. If this folder has it's own template then render that.            resourceObject = dynaNodeDao.getByPath("/site" + path);            DynaNode folderTemplate = determineTemplate(resourceObject);            if (folderTemplate == null)            {                resourceObject = null;                // Folder requested. Get first item that isn't a folder.                List<DynaNode> contents = dynaNodeDao.getAllByPath("/site" + path);                for (DynaNode n : contents)                {                    if (!n.isFolder())                    {                        resourceObject = n;                        break;                    }                }                Assert.notNull(resourceObject, "No resources in this folder.");            }        }        // Update session counter        HttpSession session = request.getSession();        Integer counter = (Integer) session.getAttribute("counter");        if (counter == null)            counter = 0;        session.setAttribute("counter", ++counter);        // Determine template to use        DynaNode template = determineTemplate(resourceObject);        Assert.notNull(template, "No template found for type: " + resourceObject.getTypeDef().getName());        // Return page and context        ModelAndView view = new ModelAndView("/site.resources/templates/layouts/" + template.get("layout.code") + "");        view.addObject("counter", counter);        view.addObject("resourceObject", resourceObject);        view.addObject("sessionId", session.getId());        //view.addObject("daoService", daoService); // now injected by VelocityView        view.addObject("template", template);        view.addObject("navigatorService", this.navigatorService);        view.addObject("cmsImageTool", new CmsImageTool());        view.addObject("userDao", this.userDao);        return view;    }    /**     * FIXME Classes for TemplateDao and Template?     * @param resourceObject     * @return     */    private DynaNode determineTemplate(DynaNode resourceObject)    {        if (resourceObject.hasProperty("template") && resourceObject.get("template") != null)            return (DynaNode) resourceObject.get("template");        DynaNodeDao dynaNodeDao = (DynaNodeDao) this.daoService.getDao(DynaNode.class);        DynaNode template = dynaNodeDao.getByPath("/designer/templates/" + resourceObject.getTypeDef().getName());        return template;    }    protected void runScript() throws IOException    {        Binding binding = new Binding();        String script = FileUtils.readFileToString(new File("test.groovy"));        binding.setVariable("daoService", this.daoService);        GroovyShell shell = new GroovyShell(binding);        shell.evaluate(script);    }    public UserDao getUserDao()    {        return this.userDao;    }    public void setUserDao(UserDao userDao)    {        this.userDao = userDao;    }    public DaoService getDaoService()    {        return this.daoService;    }    public void setDaoService(DaoService daoService)    {        this.daoService = daoService;    }    public NavigatorService getNavigatorService()    {        return this.navigatorService;    }    public void setNavigatorService(NavigatorService navigatorService)    {        this.navigatorService = navigatorService;    }}