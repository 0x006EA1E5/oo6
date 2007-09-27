package org.otherobjects.cms.scheduler;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to allow for groovy scripts to be executed by quartz scheduled jobs.
 * 
 * @author joerg
 *
 */
public class QuartzGroovyJobExecutor extends AbstractSpringQuartzJob {
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final String GROOVY_SCRIPT_KEY = "scriptsource";
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		String script = (String) context.getJobDetail().getJobDataMap().get(GROOVY_SCRIPT_KEY);
		
		Binding binding = new Binding();
		
		try {
			binding.setVariable("appCtx", getApplicationContext(context));
		} catch (Exception e) {
			logger.warn("Problems making applicationContext available to groovy script");
		}
		executeScript(binding, script);
	}
	
	protected Binding executeScript(Binding binding, String script) throws JobExecutionException
	{
		GroovyShell shell = new GroovyShell(binding);
		try{
			shell.evaluate(script);
			return binding;
		}
		catch(CompilationFailedException e)
		{
			throw new JobExecutionException("Couldn't compile script", e);
		}
	}

}
