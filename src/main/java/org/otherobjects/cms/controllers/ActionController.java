package org.otherobjects.cms.controllers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.scripting.ScriptResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ActionController extends AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(ActionController.class);
    private DaoService daoService;

    private ScriptResourceResolver actionScriptResolver;

    public void setActionScriptResolver(ScriptResourceResolver actionScriptResolver)
    {
        this.actionScriptResolver = actionScriptResolver;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String actionName = request.getPathInfo();
        actionName = StringUtils.substringAfterLast(actionName, "/");
        this.logger.info("Firing action: {}", actionName);

        Binding binding = runScript(actionName, request, response);

        String target = (String) binding.getVariable("target");
        response.sendRedirect(target);
        return null;
    }

    protected Binding runScript(String actionName, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Resource scriptResource = actionScriptResolver.resolveScriptName(actionName);

        Binding binding = new Binding();
        binding.setVariable("daoService", this.daoService);
        binding.setVariable("request", request);
        binding.setVariable("response", response);
        binding.setVariable("applicationContext", getApplicationContext());
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(scriptResource.getInputStream(), actionName);
        return binding;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
