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
    private String query;
    private String mapping;
    private String feedFormat;
    private long defaultImageWidth = 300;

    @Override
    public String getCode()
    {
        return org.otherobjects.cms.util.StringUtils.generateUrlCode(getLabel()) + ".xml";
    }

    public Url getFeedUrl()
    {
        return new Url(getLinkPath());
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
        return label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(type = PropertyType.TEXT, order = 20)
    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    @Property(type = PropertyType.TEXT, order = 30)
    public String getMapping()
    {
        return mapping;
    }

    public void setMapping(String mapping)
    {
        this.mapping = mapping;
    }

    @Property(order = 40)
    public String getFeedFormat()
    {
        return feedFormat;
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
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(type = PropertyType.NUMBER, order = 50)
    public long getDefaultImageWidth()
    {
        return defaultImageWidth;
    }

    public void setDefaultImageWidth(long defaultImageWidth)
    {
        this.defaultImageWidth = defaultImageWidth;
    }

}
