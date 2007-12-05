package org.otherobjects.cms.scheduler;

import java.util.List;

import org.otherobjects.cms.events.PublishEvent;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.PersistentJobDescription;
import org.otherobjects.cms.scripting.ScriptResourceResolver;
import org.otherobjects.cms.util.StringUtils;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;

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
    private UniversalJcrDao universalJcrDao;
    private ScriptResourceResolver scriptResourceResolver;

    /**
     * Listens to contextRefreshed and publish events to initialize / refresh jobs. 
     */
    public void onApplicationEvent(ApplicationEvent event)
    {
        //initialise when ctx is ready
        if (event instanceof ContextRefreshedEvent && ((ContextRefreshedEvent) event).getApplicationContext().getParent() == null) // only do for root context refresh
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
     * Schedules all valid jobs from repository's <code>/scheduler</code> path.
     */
    private void init()
    {
        // FIXME How to handle non-jobs in the folder? Don't want an exception.
        List<BaseNode> jobs = universalJcrDao.getAllByPath("/scheduler");
        for (BaseNode job : jobs)
        {
            if (job instanceof PersistentJobDescription)
            {
                PersistentJobDescription jobDescription = (PersistentJobDescription) job;
                if (jobDescription.isValid())
                    scheduleJob(jobDescription);
            }
            else
            {
                logger.warn("Non JobDescription found in scheduler folder: " + job.getJcrPath());
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
            if (scheduler.deleteJob(jobDescription.getId(), PersistentJobDescription.JOB_GROUP_NAME))
            {
                logger.info("Job " + jobDescription.getLabel() + " [" + jobDescription.getId() + "] already existed and was therefore deleted before scheduling it again");
            }
            setScriptResource(jobDescription);
            scheduler.scheduleJob(jobDescription.getJobDetail(), jobDescription.getTrigger());
        }
        catch (Exception e)
        {
            logger.warn("Couldn't schedule Job: " + jobDescription.getLabel() + " [" + jobDescription.getId() + "]", e);
        }
    }

    private void setScriptResource(PersistentJobDescription jobDescription) throws Exception
    {
        if (StringUtils.isNotBlank(jobDescription.getGroovyScriptName()))
        {
            Resource scriptResource = scriptResourceResolver.resolveScriptName(jobDescription.getGroovyScriptName());
            jobDescription.getJobDetail().getJobDataMap().put(QuartzGroovyJobExecutor.GROOVY_SCRIPT_RESOURCE_KEY, scriptResource);
        }

    }

    public void setScheduler(Scheduler scheduler)
    {
        this.scheduler = scheduler;
    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

    public void setScriptResourceResolver(ScriptResourceResolver scriptResourceResolver)
    {
        this.scriptResourceResolver = scriptResourceResolver;
    }
}
