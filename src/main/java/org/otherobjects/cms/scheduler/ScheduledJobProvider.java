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

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.tools.FormatTool;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Adds the provided site jobs to the active scheduler.
 * 
 * @author rcastro
 */
public class ScheduledJobProvider 
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private Scheduler scheduler;
    private Map<String, Object> jobs = new HashMap<String, Object>();

    // used for testing
    public Scheduler getScheduler()
    {
        return scheduler;
    }

    public Map<String, Object> getJobs()
    {

        return jobs;
    }

    /**
     * Schedules the provided jobs with using the corresponding cron expressions.
     */
    public void schedule()
    {
        // inject found jobs into scheduler
        try
        {
            Map<String, Object> map = getJobs();

            for (Object job : map.values())
            {

                ScheduledJob annotation = job.getClass().getAnnotation(ScheduledJob.class);
                String cron = StringUtils.isNotEmpty(annotation.cronExpression()) ? annotation.cronExpression() : null;
                String label = StringUtils.isNotEmpty(annotation.label()) ? annotation.label() : FormatTool.generateName(job.getClass().getName());
                JobDetail jobDetail = new JobDetail(label, null, job.getClass());
                //get the username from the annotation 
                String username = StringUtils.isNotEmpty(annotation.username()) ? annotation.username() : null;
                jobDetail.getJobDataMap().put(AbstractSpringQuartzJob.USER_NAME_KEY, username);
                Trigger ranchTrigger = null;
                if (cron == null)
               {
                    ranchTrigger = new SimpleTrigger(label, null);
                }
                else
                {
                    try
                    {
                        ranchTrigger = new CronTrigger(label, null, cron);
                    }
                    catch (ParseException e)
                    {
                        logger.error("Error with cron exception: " + cron + " job " + label);

                    }
                }
                scheduler.scheduleJob(jobDetail, ranchTrigger);

            }

        }
        catch (SchedulerException ee)
         {
            logger.error("Error scheduling jobs");
         }
    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler; 
    }

    public void setJobs(Map<String, Object> jobs)
    {
        this.jobs = jobs;
    }

    public void addJob(String name, Object job)
    {
        this.jobs.put(name, job);
    }
    

}
