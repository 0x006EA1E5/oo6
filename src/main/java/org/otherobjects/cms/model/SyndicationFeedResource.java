package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

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
    private Boolean rssFormat;

    @Override
    public String getOoIcon()
    {
        return ICON_PATH;
    }

    @Property(order = 10)
    public String getLabel()
    {
        return label;
    }

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
    public Boolean getRssFormat()
    {
        return rssFormat;
    }

    public void setRssFormat(Boolean rssFormat)
    {
        this.rssFormat = rssFormat;
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

}
