package org.otherobjects.cms.controllers;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
public class TestController extends MultiActionController
{
//    @Resource
//    private DaoService daoService;
//
//    @Resource
//    private TypeService typeService;

    @Resource
    private OoResourceLoader ooResourceLoader;

    @SuppressWarnings("unchecked")
    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        System.err.println("TEST");

        // Load data file
//        logger.debug("Running setup scripts.");
//        Binding binding = new Binding();
//        binding.setProperty("daoService", daoService);
//        binding.setProperty("typeService", typeService);
//        GroovyShell shell = new GroovyShell(binding);
//        String script = IOUtils.toString(new FileInputStream("data-transfer/import.groovy"));
//        shell.evaluate(script);

        
        OoResource resource = ooResourceLoader.getResource("/site/templates/blocks/client.ftl");

        String string = IOUtils.toString(resource.getInputStream());
        
        
        return null;
    }
 
}
