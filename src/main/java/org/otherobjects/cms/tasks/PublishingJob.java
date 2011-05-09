package org.otherobjects.cms.tasks;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.scheduler.AbstractSpringQuartzJob;
import org.otherobjects.cms.scheduler.ScheduledJob;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Cron scheduled job, responsible for publishing unpublishing content where embargo date is before the current date and time or is not set
 * and expiry date is after the current date and time or not set.
 * Also responsible for unpublishing all expired content.
 * Runs as the "admin" user daily at 5am
 * @author rcastro
 *
 */
@ScheduledJob(label = "PublishingJob", cronExpression = "00 00 05 * * ?", username = "admin")
@Component
public class PublishingJob extends AbstractSpringQuartzJob
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private DaoService daoService;

    /**
     * Publishes/Unpublishes content taking into account embargo and expiry dates
     */
    public void executeJob(JobExecutionContext context) throws JobExecutionException
    {
        try
        {
            daoService = (DaoService) getApplicationContext(context).getBean("daoService");
        }
        catch (Exception e)
        {
            if (e instanceof JobExecutionException)
            {
                throw (JobExecutionException) e;
            }
            else
            {
                logger.error("Exception getting the dao bean. " + e.getMessage());
            }
        }

        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        Date date = new Date();
        //publishing query, gets content from the edit workspace
        List<BaseNode> list = getContentToPublish(universalJcrDao);
        for (BaseNode n : list)

        {
            universalJcrDao.publish(n, "published by task at " + date.toString());
        }
        //unpublish, from live default workspace
        list = getContentToUnPublish(universalJcrDao);
        for (BaseNode n : list)
        {
            universalJcrDao.unpublish(n, "unpublished by task at " + date.toString());
        }
        logger.debug("Published/Unpublished. ");

    }

    public List<BaseNode> getContentToPublish(UniversalJcrDao universalJcrDao)
    {
        Date date = new Date();
        return universalJcrDao.getAllByJcrExpression("/jcr:root/site//element(*) [@published='false' and (publishingOptions/@embargoUntil<" + date.getTime()
                + " or not(publishingOptions/@embargoUntil)) and (publishingOptions/@expireOn>" + date.getTime() + " or not(publishingOptions/@embargoUntil))]", true);
    }

    public List<BaseNode> getContentToUnPublish(UniversalJcrDao universalJcrDao)
    {
        Date date = new Date();
        return universalJcrDao.getAllByJcrExpression("/jcr:root/site//element(*) [publishingOptions/@expireOn<=" + date.getTime() + "]");

    }

}
