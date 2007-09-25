package org.otherobjects.cms.scheduler;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import org.codehaus.groovy.control.CompilationFailedException;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class QuartzGroovyJobExecutor implements Job {
	
	public static final String GROOVY_SCRIPT_KEY = "scriptsource";
	
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		
		String script = (String) context.getJobDetail().getJobDataMap().get(GROOVY_SCRIPT_KEY);
		
		Binding binding = new Binding();
		GroovyShell shell = new GroovyShell(binding);
		try{
			shell.evaluate(script);
		}
		catch(CompilationFailedException e)
		{
			throw new JobExecutionException("Couldn't compile script", e);
		}
	}

}
