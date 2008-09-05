package org.otherobjects.cms.controllers.site;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.validation.ValidatorService;
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
        try
        {
            Resource scriptResource = ooResourceLoader.getResource("/site/actions/"+actionName+".groovy");
            Binding binding = new Binding();
            binding.setVariable("daoService", this.daoService);
            binding.setVariable("jcr", this.daoService.getDao("BaseNode"));
            binding.setVariable("request", request);
            binding.setVariable("response", response);
            BindService bindService = (BindService) getApplicationContext().getBean("bindService");
            binding.setVariable("bindService", bindService);
            ValidatorService validatorSevice = (ValidatorService) getApplicationContext().getBean("validatorService");
            binding.setVariable("validatorService", validatorSevice);
            binding.setVariable("action", new ActionUtils(request, response, bindService, validatorSevice));
            binding.setVariable("applicationContext", getApplicationContext());
            GroovyShell shell = new GroovyShell(binding);
            shell.evaluate(scriptResource.getInputStream(), actionName);
            return binding;
        }
        catch (Exception e)
        {
            logger.error("Error processing acion: " + actionName, e);
            throw e;
        }
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
