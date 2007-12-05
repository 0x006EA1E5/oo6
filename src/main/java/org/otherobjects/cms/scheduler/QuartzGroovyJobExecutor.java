package org.otherobjects.cms.scheduler;

import java.io.IOException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;
import org.otherobjects.cms.util.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

/**
 * Helper class to allow for groovy scripts to be executed by quartz scheduled jobs.
 * 
 * @author joerg
 *
 */
public class QuartzGroovyJobExecutor extends AbstractSpringQuartzJob
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String GROOVY_SCRIPT_KEY = "scriptsource";
    public static final String GROOVY_SCRIPT_RESOURCE_KEY = "script-resource";

    @Override
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {

        String script = (String) context.getJobDetail().getJobDataMap().get(GROOVY_SCRIPT_KEY);
        Resource scriptResource = (Resource) context.getJobDetail().getJobDataMap().get(GROOVY_SCRIPT_RESOURCE_KEY);

        Binding binding = new Binding();

        try
        {
            binding.setVariable("appCtx", getApplicationContext(context));
        }
        catch (Exception e)
        {
            logger.warn("Problems making applicationContext available to groovy script");
        }
        if (StringUtils.isNotBlank(script))
            executeScript(binding, script);
        else if (scriptResource != null)
        {
            executeScript(binding, scriptResource);
        }
    }

    protected Binding executeScript(Binding binding, Object script) throws JobExecutionException
    {
        GroovyShell shell = new GroovyShell(binding);
        try
        {
            if (script instanceof String)
                shell.evaluate((String) script);
            else if (script instanceof Resource)
                shell.evaluate(((Resource) script).getInputStream());
            else
                throw new JobExecutionException("script is neither provided as a string nor as a resource - can't execute!");

            return binding;
        }
        catch (CompilationFailedException e)
        {
            throw new JobExecutionException("Couldn't compile script", e);
        }
        catch (IOException e)
        {
            throw new JobExecutionException("Couldn't read script resource", e);
        }
    }

}
