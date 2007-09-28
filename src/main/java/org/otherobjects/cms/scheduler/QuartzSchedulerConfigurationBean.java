package org.otherobjects.cms.scheduler;

import java.util.List;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.events.PublishEvent;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.TypeService;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

/**
 * Simple bean that configures the built-in quartz scheduler by querying for all {@link PersistentJobDescription} objects under a certain
 * repository path. Configuration happens at application context startup. 
 * Scheduled jobs are refreshed when their repository based {@link PersistentJobDescription} is published.
 * 
 * 
 * @author joerg
 *
 */
public class QuartzSchedulerConfigurationBean implements ApplicationListener
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private Scheduler scheduler;
    private DynaNodeDao dynaNodeDao;
    private TypeService typeService;

    /**
     * listen to contextRefreshed and publish events to initialize / refresh jobs 
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        //initialise when ctx is ready
        if (event instanceof ContextRefreshedEvent && ((ContextRefreshedEvent)event).getApplicationContext().getParent() == null) // only do for root context refresh
        {
            init();
        }
        //refresh or add jobs when they are published
        else if (event instanceof PublishEvent)
        {
            CmsNode cmsNode = ((PublishEvent) event).getCmsNode();
            if (cmsNode instanceof PersistentJobDescription)
            {
                PersistentJobDescription jobDescription = (PersistentJobDescription) cmsNode;
                scheduleJob(jobDescription);
            }
        }
    }
    
    /**
     * schedule all valid jobs from repository's /scheduler path
     */
    private void init()
    {
        typeService.getType(PersistentJobDescription.class.getName());
        
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
    
    /**
     * Schedule given job, deleting any existing job with the same name beforehand.
     * 
     * @param jobDescription
     */
    private void scheduleJob(PersistentJobDescription jobDescription)
    {
        try
        {
        	if(scheduler.deleteJob(jobDescription.getId(), PersistentJobDescription.JOB_GROUP_NAME))
        	{
        		logger.info("Job " + jobDescription.getLabel() + " [" + jobDescription.getId() + "] already existed and was therefore deleted before scheduling it again");
        	}
            scheduler.scheduleJob(jobDescription.getJobDetail(), jobDescription.getTrigger());
        }
        catch (Exception e)
        {
            logger.warn("Couldn't schedule Job " + jobDescription.getLabel() + " [" + jobDescription.getId() + "]", e);
        }
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
