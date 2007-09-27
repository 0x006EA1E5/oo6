package org.otherobjects.cms.scheduler;

import org.acegisecurity.context.SecurityContextHolder;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.security.SecurityTool;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.context.ApplicationContext;

/**
 * Abstract class to be extended by all Jobs that need to be run by the OTHERobjects quartz scheduler. Enforces proper acegi security context setup.
 * @author joerg
 *
 */
abstract class AbstractSpringQuartzJob implements Job {

	private static final String APPLICATION_CONTEXT_KEY = "applicationContext"; // needs to correspond with whatever was specified for the applicationContextSchedulerContextKey property
																				// in the application context defining org.springframework.scheduling.quartz.SchedulerFactoryBean
	
	public static final String USERID_KEY = "jobUsername";

	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
	{
		try{
			SecurityTool.setupAuthenticationForNamesUser(getUserDao(jobExecutionContext), (String) jobExecutionContext.getJobDetail().getJobDataMap().get(USERID_KEY));
			executeJob(jobExecutionContext);
		} catch (Exception e) {
			throw new JobExecutionException("Couldn't execute Job: ", e);
		}
		finally
		{
			SecurityContextHolder.clearContext();
		}
	}
	
	public abstract void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException;
	
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
	
	protected UserDao getUserDao(JobExecutionContext jobExecutionContext) throws Exception
	{
		return (UserDao) getApplicationContext(jobExecutionContext).getBean("userDao");
	}
}
