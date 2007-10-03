package org.otherobjects.cms.model;

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
public class SyndicationFeedResource extends DynaNode
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
     
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getMapping()
    {
        return mapping;
    }

    public void setMapping(String mapping)
    {
        this.mapping = mapping;
    }

    public Boolean getRssFormat()
    {
        return rssFormat;
    }

    public void setRssFormat(Boolean rssFormat)
    {
        this.rssFormat = rssFormat;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

}
