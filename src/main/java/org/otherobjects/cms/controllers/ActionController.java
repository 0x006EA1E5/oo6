package org.otherobjects.cms.controllers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.dao.DaoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class ActionController extends AbstractController
{
    private final Logger logger = LoggerFactory.getLogger(ActionController.class);
    private DaoService daoService;

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String actionName = request.getPathInfo();
        actionName = StringUtils.substringAfterLast(actionName, "/");
        this.logger.info("Firing action: {}", actionName);

        String actionPath = "/site.resources/scripts/actions/" + actionName + ".script";
        this.logger.debug("Running script from file: {}", actionPath);

        Binding binding = runScript(actionPath, request, response);

        String target = (String) binding.getVariable("target");
        response.sendRedirect(target);
        return null;
    }

    protected Binding runScript(String path, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        // FIXME We should have a ScriptResolver!
        String code = IOUtils.toString(getClass().getResourceAsStream(path));

        Binding binding = new Binding();
        binding.setVariable("daoService", this.daoService);
        binding.setVariable("request", request);
        binding.setVariable("response", response);
        binding.setVariable("applicationContext", getApplicationContext());
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(code);
        return binding;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
