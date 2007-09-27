package org.otherobjects.cms.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

abstract class AbstractSpringQuartzJob implements Job {

	private static final String APPLICATION_CONTEXT_KEY = "applicationContext"; // needs to correspond with whatever was specified for the applicationContextSchedulerContextKey property
																				// in the application context defining org.springframework.scheduling.quartz.SchedulerFactoryBean

	public abstract void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException;
	
	protected ApplicationContext getApplicationContext(JobExecutionContext context )
	throws Exception {
		ApplicationContext appCtx = null;
		appCtx = (ApplicationContext)context.getScheduler().getContext().get(APPLICATION_CONTEXT_KEY);
		if (appCtx == null) {
			throw new JobExecutionException(
					"No application context available in scheduler context for key \"" + APPLICATION_CONTEXT_KEY + "\"");
		}
		return appCtx;
	}
}
