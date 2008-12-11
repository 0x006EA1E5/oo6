package org.otherobjects.cms.controllers.renderers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.PagedList;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.Linkable;
import org.otherobjects.cms.model.SyndicationFeedResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

/**
 * Renders a SyndicationFeedResource.
 * 
 * TODO Need to test that essential mappings are valid.
 * TODO Warn on unsupported mappings.
 * 
 */
public class SyndicationFeedRenderer implements ResourceRenderer
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private DaoService daoService;

    //private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public ModelAndView handleRequest(CmsNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Assert.notNull(resourceObject, "resourceObject must not be null");
        Assert.isInstanceOf(SyndicationFeedResource.class, resourceObject, "resourceObject must be a SyndicationFeedResource not: " + resourceObject.getClass().getName());

        SyndicationFeedResource feedObject = (SyndicationFeedResource) resourceObject;
        Map<String, String> mappings = feedObject.getMappingsMap();
        Assert.notNull(mappings, "The mappings for a SyndicationFeedResource can't be null");

        // Assert.notNull(redirect.getUrl(), "Redirect does not specify a destination URL.");

        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType(feedObject.getFeedFormat());

        feed.setTitle(feedObject.getLabel());
        feed.setLink(feedObject.getFeedUrl());
        feed.setDescription(feedObject.getDescription());

        List<SyndEntry> entries = new ArrayList<SyndEntry>();

        Object daoObject = this.daoService.getDao(BaseNode.class);

        PagedList<BaseNode> items = ((UniversalJcrDao) daoObject).pageByJcrExpression(feedObject.getSelector().getQuery(), 15, 1);

        for (BaseNode node : items)
        {
            if (node instanceof Linkable)
            {
                SyndEntry entry;

                entry = new SyndEntryImpl();

                String permaLink = ((Linkable) node).getHref().getAbsoluteLink();
                entry.setLink(permaLink);

                if (mappings.containsKey("description"))
                {
                    SyndContent description;
                    description = new SyndContentImpl();
                    try
                    {
                        description.setValue((String) PropertyUtils.getNestedProperty(node, mappings.get("description")));
                        entry.setDescription(description);
                    }
                    catch (Exception e)
                    {
                        this.logger.warn("Couldn't get description value from node " + node.getId(), e);
                    }
                    //mappings.remove("description");
                }

                //                if (mappings.containsKey("image"))
                //                {
                //                    try
                //                    {
                //                        CmsImage image = (CmsImage) PropertyUtils.getNestedProperty(node, mappings.get("image"));
                //                        SyndEnclosure enclosure = new SyndEnclosureImpl();
                //                        CmsImageSize size = cmsImageTool.getSize(image, (int) feedObject.getDefaultImageWidth());
                //                        DataFile dataFile = size.getDataFile();
                //
                //                        enclosure.setUrl(dataFile.getExternalUrl());
                //                        enclosure.setType(dataFile.getMimeType().toString());
                //                        enclosure.setLength(dataFile.getFileSize());
                //                        entry.getEnclosures().add(enclosure);
                //                    }
                //                    catch (Exception e)
                //                    {
                //                        logger.warn("Couldn't get image object ", e);
                //                    }
                //                    mappings.remove("image");
                //                }

                for (Map.Entry<String, String> singleMapping : mappings.entrySet())
                {

                    try
                    {
                        Object sourceValue = PropertyUtils.getNestedProperty(node, singleMapping.getValue());
                        PropertyUtils.setNestedProperty(entry, singleMapping.getKey(), sourceValue);
                    }
                    catch (Exception e)
                    {
                        this.logger.warn("Couldn't get or set property with key " + singleMapping.getKey());
                    }
                }

                //FIXME entry.setAuthor(node.getUserName());
                entry.setUpdatedDate(node.getModificationTimestamp());

                entries.add(entry);
            }
        }

        feed.setEntries(entries);

        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, response.getWriter());
        response.setContentType("text/xml");
        return null;
    }
}
