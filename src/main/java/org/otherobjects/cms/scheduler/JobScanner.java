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

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.tools.FormatTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ClassUtils;

/**
 * Helper class which injects site @ScheduledJob annotated components into a ScheduledJobProvider.
 * 
 * @author rcastro
 */
public class JobScanner implements ApplicationContextAware
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private ScheduledJobProvider scheduledJobProvider;

    private ApplicationContext applicationContext;

    /**
     * Scans for and adds @ScheduledJob annotated components into the injected scheduledJobProvider.
     */
    @PostConstruct
    public void addScheduledJobs()
    {
        //get all Job beans
        try
        {
            for (String beanName : this.applicationContext.getBeanNamesForType(ClassUtils.forName("org.otherobjects.cms.scheduler.AbstractSpringQuartzJob", null), false, false))
            {

                Object job = this.applicationContext.getBean(beanName);
                ScheduledJob annotation = job.getClass().getAnnotation(ScheduledJob.class);
                if (annotation != null)
                {
                    String name = StringUtils.isNotEmpty(annotation.label()) ? annotation.label() : FormatTool.generateName(job.getClass().getName());
                    scheduledJobProvider.addJob(name, job);
                }
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.error("Class not for job found. ");
        }

        scheduledJobProvider.schedule();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
}
