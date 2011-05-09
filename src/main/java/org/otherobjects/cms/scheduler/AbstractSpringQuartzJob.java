/*
 * This file is part of the OTHERobjects Content Management System.
 * 
 * Copyright 2007-2009 OTHER works Limited.
 * 
 * OTHERobjects is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OTHERobjects is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OTHERobjects.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.otherobjects.cms.scheduler;

import org.otherobjects.cms.model.UserDao;
import org.otherobjects.cms.security.SecurityUtil;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.UnableToInterruptJobException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Abstract class to be extended by all Jobs that need to be run by the OTHERobjects quartz scheduler.
 * Configures proper acegi security context setup.
 * 
 * @author joerg
 */
public abstract class AbstractSpringQuartzJob implements Job, InterruptableJob
{
    private final Logger logger = LoggerFactory.getLogger(AbstractSpringQuartzJob.class);
    
    private boolean interrupted = false;
    
    /* 
     * Needs to correspond with whatever was specified for the applicationContextSchedulerContextKey property
     * in the application context defining org.springframework.scheduling.quartz.SchedulerFactoryBean. 
     */
    private static final String APPLICATION_CONTEXT_KEY = "applicationContext";

    public static final String USER_NAME_KEY = "jobUsername";

    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException
    {
        try
        {
            SecurityUtil.setupAuthenticationForNamedUser(getUserDao(jobExecutionContext), (String) jobExecutionContext.getJobDetail().getJobDataMap().get(USER_NAME_KEY));
            executeJob(jobExecutionContext);
        }
        catch (Exception e)
        {
            throw new JobExecutionException("Couldn't execute Job: ", e);
        }
        finally
        {
            SecurityContextHolder.clearContext();
        }
    }

    public abstract void executeJob(JobExecutionContext jobExecutionContext) throws JobExecutionException;

    protected ApplicationContext getApplicationContext(JobExecutionContext context) throws Exception
    {
        ApplicationContext appCtx = null;
        appCtx = (ApplicationContext) context.getScheduler().getContext().get(APPLICATION_CONTEXT_KEY);
        if (appCtx == null)
        {
            throw new JobExecutionException("No application context available in scheduler context for key \"" + APPLICATION_CONTEXT_KEY + "\"");
        }
        return appCtx;
    }

    protected UserDao getUserDao(JobExecutionContext jobExecutionContext) throws Exception
    {
        return (UserDao) getApplicationContext(jobExecutionContext).getBean("userDao");
    }
    

    /**
     * Called when job is interupted. Sets interrupted flag to true.
     */
    public void interrupt() throws UnableToInterruptJobException
    {
        logger.info("Interupting job: " + this.toString());
        this.interrupted = true;
    }
    
    
    /**
     * Returns true if this job has been interupted.
     * 
     * @return
     */
    public boolean isInterupted()
    {
        return this.interrupted;
    }
}
