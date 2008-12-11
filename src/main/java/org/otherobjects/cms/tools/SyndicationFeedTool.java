package org.otherobjects.cms.tools;

import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.SyndicationFeedResource;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

/**
 * Tool to generate and cache lists of {@link SyndicationFeedResource}s.
 *  
 * @author rich
 */
@Component
@Tool
@SuppressWarnings("unchecked")
public class SyndicationFeedTool
{
    @Resource
    private UniversalJcrDao universalJcrDao;

    private List feeds = null;

    public List<SyndicationFeedResource> getFeeds()
    {
        // FIXME Sync this
        // FIXME Need to listen to publish events and invalidate as appropriate
        if (this.feeds == null)
        {
            this.feeds = this.universalJcrDao.getAllByJcrExpression("/jcr:root//* [@ooType='org.otherobjects.cms.model.SyndicationFeedResource' and @includeInHeader = 'true']");
        }
        return this.feeds;
    }
}
