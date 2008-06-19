package org.otherobjects.cms.controllers;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.HtmlLogger;
import org.otherobjects.cms.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ScriptController
{
    private final Logger logger = LoggerFactory.getLogger(ScriptController.class);

    @Resource
    private DaoService daoService;

    @Resource
    private TypeService typeService;

    @Resource
    private OoResourceLoader ooResourceLoader;

    @RequestMapping("/**/run/**")
    public ModelAndView run(HttpServletRequest request, HttpServletResponse response) throws Throwable
    {
        // TODO need regexp to check scriptname is valid java class name

        String scriptName = RequestUtils.getId(request) + ".groovy";
        String scriptPath = "/site/scripts/" + scriptName;
        response.getWriter().print("<html><body>");
        try
        {
            // Load data file
            logger.info("Running script: {}", scriptName);
            Binding binding = new Binding();
            binding.setProperty("daoService", daoService);
            binding.setProperty("typeService", typeService);
            binding.setProperty("logger", new HtmlLogger(response.getWriter()));
            GroovyShell shell = new GroovyShell(binding);
            OoResource resource = ooResourceLoader.getResource(scriptPath);

            shell.evaluate(resource.getInputStream(), scriptName);
        }
        catch (Exception e)
        {
            e = (Exception) sanitize(e);
            int line = findLineNumber(e, scriptName);
            String message = e.getMessage() != null ? e.getMessage() : e.getClass().getName();
            OtherObjectsException otherObjectsException = new OtherObjectsException("Error on line " + line + " of " + scriptPath + ": " + message);
            otherObjectsException.setStackTrace(e.getStackTrace());
            //            throw otherObjectsException;

            response.getWriter().print("<p>Error on line " + line + " of " + scriptPath + ": " + message + " </p>");
        }

        response.getWriter().print("</body></html>");
        return null;
    }

    /**
     * From GrailsUtil.
     * 
     * @param t
     * @return
     */
    public static Throwable sanitize(Throwable t)
    {
        StackTraceElement[] trace = t.getStackTrace();
        List<StackTraceElement> newTrace = new ArrayList<StackTraceElement>();
        for (StackTraceElement stackTraceElement : trace)
        {
            if (isApplicationClass(stackTraceElement.getClassName()))
            {
                newTrace.add(stackTraceElement);
            }
        }

        // Only trim the trace if there was some application trace on the stack
        // if not we will just skip sanitizing and leave it as is
        if (newTrace.size() > 0)
        {
            // We don't want to lose anything, so log it
            //STACK_LOG.error("Sanitizing stacktrace:", t);
            StackTraceElement[] clean = new StackTraceElement[newTrace.size()];
            newTrace.toArray(clean);
            t.setStackTrace(clean);
        }
        return t;
    }

    public static int findLineNumber(Throwable t, String fileName)
    {
        StackTraceElement[] trace = t.getStackTrace();
        for (StackTraceElement stackTraceElement : trace)
        {
            if (stackTraceElement.getFileName() != null && stackTraceElement.getFileName().equals(fileName))
                return stackTraceElement.getLineNumber();
        }
        return -1;
    }

    private static final String[] GRAILS_PACKAGES = new String[]{"org.codehaus.groovy.", "groovy.", "org.mortbay.", "sun.", "java.lang.reflect.", "org.springframework.", "com.opensymphony.",
            "org.hibernate.", "javax.servlet."};

    public static boolean isApplicationClass(String className)
    {
        for (int i = 0; i < GRAILS_PACKAGES.length; i++)
        {
            String grailsPackage = GRAILS_PACKAGES[i];
            if (className.startsWith(grailsPackage))
            {
                return false;
            }
        }
        return true;
    }
}
