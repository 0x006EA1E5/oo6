package org.otherobjects.cms.model;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

import flexjson.JSON;

/**
 * Mappings example:
 * 
 * title:headline
 * description:content
 * publishedDate:publicationDate
 * link:linkPath
 * 
 * @author rich
 */
@Type(label = "Syndication Feed")
public class SyndicationFeedResource extends BaseNode
{
    private static final String ICON_PATH = "otherobjects.resources/static/icons/feed.png";

    private String label;
    private String description;
    private Selector selector;
    private String mapping = "title:ooLabel\ndescription:teaser";
    private String feedFormat = "atom_1.0";
    private long defaultImageWidth = 300;
    private String customUrl;
    private Boolean includeInHeader;

    @Override
    public String getCode()
    {
        return org.otherobjects.cms.util.StringUtils.generateUrlCode(getLabel()) + ".xml";
    }

    public String getFeedUrl()
    {
        if (StringUtils.isNotEmpty(getCustomUrl()))
            return getCustomUrl();
        else
            return new Url(getOoUrlPath()).getAbsoluteLink();
    }

    public String getFeedMimeType()
    {
        // FIXME There must be a better way
        if (this.feedFormat.startsWith("atom"))
            return "application/atom+xml";
        else if (this.feedFormat.startsWith("rss"))
            return "application/rss+xml";
        else
            return "application/xml";
    }

    @JSON(include = false)
    public Map<String, String> getMappingsMap()
    {
        if (StringUtils.isNotBlank(getMapping()))
        {
            Map<String, String> mappings = new HashMap<String, String>();
            for (String mapping : getMapping().split("\\s"))
            {
                if (mapping.indexOf(':') > -1)
                {
                    String[] keyvalue = mapping.split(":");
                    mappings.put(keyvalue[0], keyvalue[1]);
                }
            }
            return mappings;
        }
        return null;
    }

    @Override
    public String getOoIcon()
    {
        return ICON_PATH;
    }

    @Override
    @Property(order = 10)
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(type = PropertyType.COMPONENT, order = 20)
    public Selector getSelector()
    {
        return this.selector;
    }

    public void setSelector(Selector selector)
    {
        this.selector = selector;
    }

    @Property(type = PropertyType.TEXT, order = 30)
    public String getMapping()
    {
        return this.mapping;
    }

    public void setMapping(String mapping)
    {
        this.mapping = mapping;
    }

    @Property(order = 40)
    public String getFeedFormat()
    {
        return this.feedFormat;
    }

    /**
     * Sets the format of the feed. The format of the 'type' property must be [FEEDNAME]_[FEEDVERSION] with the FEEDNAME in lower case, for example: rss_0.9, rss_0.93, atom_1.0.
     * @param feedFormat
     */
    public void setFeedFormat(String feedFormat)
    {
        this.feedFormat = feedFormat;
    }

    @Property(type = PropertyType.TEXT, order = 15)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(type = PropertyType.NUMBER, order = 50)
    public long getDefaultImageWidth()
    {
        return this.defaultImageWidth;
    }

    public void setDefaultImageWidth(long defaultImageWidth)
    {
        this.defaultImageWidth = defaultImageWidth;
    }

    @Property(order = 60, size = 250)
    public String getCustomUrl()
    {
        return this.customUrl;
    }

    public void setCustomUrl(String customUrl)
    {
        this.customUrl = customUrl;
    }

    @Property(order = 70)
    public Boolean getIncludeInHeader()
    {
        return this.includeInHeader;
    }

    public void setIncludeInHeader(Boolean includeInHeader)
    {
        this.includeInHeader = includeInHeader;
    }

}
