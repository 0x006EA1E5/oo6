package org.otherobjects.cms.controllers.handlers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.Script;
import org.otherobjects.cms.model.ScriptResource;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * TODO Add multiple lanugaue support.
 * 
 * @author rich
 *
 */
public class ScriptHandler implements ResourceHandler, ApplicationContextAware
{
    private DaoService daoService;
    private ApplicationContext applicationContext;

    public ModelAndView handleRequest(CmsNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Assert.notNull(resourceObject, "resourceObject must not be null");
        Assert.isInstanceOf(ScriptResource.class, resourceObject, "resourceObject must be a ScriptResource not: " + resourceObject.getClass().getName());

        ScriptResource scriptResource = (ScriptResource) resourceObject;
        Script script = scriptResource.getScript();

        String code = null;
        if (!StringUtils.isEmpty(script.getScriptCode()))
        {
            code = script.getScriptCode();
        }
        else if (!StringUtils.isEmpty(script.getScriptPath()))
        {
            code = FileUtils.readFileToString(new File(script.getScriptPath()));
        }
        else
        {
            // TODO Add waring if both are specified
            // TODO Should this error be in Scipt.isValid() 
            throw new OtherObjectsException("You must provide either script code or a scripth path in: " + script.getJcrPath());
        }
        
        // Execute script
        runScript(code, request, response);
        return null;
    }

    protected void runScript(String code, HttpServletRequest request, HttpServletResponse response) throws IOException
    {
        Binding binding = new Binding();
        binding.setVariable("daoService", this.daoService);
        binding.setVariable("request", request);
        binding.setVariable("response", response);
        binding.setVariable("applicationContext", applicationContext);
        GroovyShell shell = new GroovyShell(binding);
        shell.evaluate(code);
    }
    
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }
}
