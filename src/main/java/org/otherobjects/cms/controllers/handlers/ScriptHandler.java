package org.otherobjects.cms.controllers.handlers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.web.servlet.ModelAndView;

public class ScriptHandler implements ResourceHandler
{
    private DaoService daoService;

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public ModelAndView handleRequest(DynaNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        if (request.getParameter("script") != null)
        {
            runScript(request, response);
        }
        return null;
    }

    protected void runScript(HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Binding binding = new Binding();
        String script = FileUtils.readFileToString(new File("scripts/" + request.getParameter("script") + ".groovy"));
        binding.setVariable("daoService", this.daoService);
        binding.setVariable("request", request);
        binding.setVariable("response", response);
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(script);
    }

}
