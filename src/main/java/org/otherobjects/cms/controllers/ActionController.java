package org.otherobjects.cms.controllers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.util.ActionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ActionController extends AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(ActionController.class);
    private DaoService daoService;
    
    private OoResourceLoader ooResourceLoader;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String actionName = request.getPathInfo();
        actionName = StringUtils.substringAfterLast(actionName, "/");
        this.logger.info("Firing action: {}", actionName);

        runScript(actionName, request, response);

        // Return null because actions MUST redirect to a URL
        return null;
    }

    protected Binding runScript(String actionName, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Resource scriptResource = ooResourceLoader.getResource("/site/actions/"+actionName+".groovy");
        Binding binding = new Binding();
        binding.setVariable("daoService", this.daoService);
        binding.setVariable("jcr", this.daoService.getDao("BaseNode"));
        binding.setVariable("request", request);
        binding.setVariable("response", response);
        binding.setVariable("bindService", getApplicationContext().getBean("bindService"));
        binding.setVariable("action", new ActionUtils(request, response));
        binding.setVariable("applicationContext", getApplicationContext());
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(scriptResource.getInputStream(), actionName);
        return binding;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public void setOoResourceLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }
}
