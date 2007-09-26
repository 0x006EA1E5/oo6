package org.otherobjects.cms.scheduler;

import java.util.List;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.events.PublishEvent;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.TypeService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Simple bean that configures the built-in quartz scheduler by querying for all {@link PersistentJobDescription} objects under a certain
 * repository path. Configuration happens at application context startup. 
 * @author joerg
 *
 */
public class QuartzSchedulerConfigurationBean implements ApplicationListener
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Scheduler scheduler;
    private DynaNodeDao dynaNodeDao;
    private TypeService typeService;

    private void configure()
    {
        List<DynaNode> jobs = dynaNodeDao.getAllByPath("/scheduler");
        for (DynaNode node : jobs)
        {
            if (node instanceof PersistentJobDescription)
            {
                PersistentJobDescription jobDescription = (PersistentJobDescription) node;
                if (jobDescription.isValid())
                    scheduleJob(jobDescription);
            }
        }
    }

    public void onApplicationEvent(ApplicationEvent event)
    {
        //initialise when ctx is ready
        if (event instanceof ContextRefreshedEvent)
        {
            init();
        }
        //only interested in PublishEvents
        else if (event instanceof PublishEvent)
        {
            CmsNode cmsNode = ((PublishEvent) event).getCmsNode();
            if (cmsNode instanceof PersistentJobDescription)
            {
                PersistentJobDescription jobDescription = (PersistentJobDescription) cmsNode;
                try
                {
                    unscheduleJob(jobDescription);
                    scheduleJob(jobDescription);
                }
                catch (SchedulerException e)
                {
                    logger.error("Couldn't unschedule existing job - job was not rescheduled", e);
                }
            }
        }
    }

    private void init()
    {
        typeService.getType(PersistentJobDescription.class.getName());
        configure();
    }

    private void scheduleJob(PersistentJobDescription jobDescription)
    {
        try
        {
            scheduler.scheduleJob(jobDescription.getJobDetail(), jobDescription.getTrigger());
        }
        catch (Exception e)
        {
            logger.warn("Couldn't schedule Job " + jobDescription.getLabel(), e);
        }
    }

    private void unscheduleJob(PersistentJobDescription jobDescription) throws SchedulerException
    {
        scheduler.deleteJob(jobDescription.getId(), PersistentJobDescription.JOB_GROUP_NAME);
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

}
