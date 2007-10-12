package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Represents a content managed external link object.
 * 
 * @author rich
 */
@Type(label = "External Link", labelProperty = "label")
public class CmsExternalLink extends BaseNode
{
    private String url;

    // Meta data
    private String label;
    private String description;
    private String keywords;

    // Source information
    private String originalProvider;
    private String originalId;

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

    @Property(order = 15)
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @Property(order = 20, type = PropertyType.TEXT)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 30, type = PropertyType.TEXT)
    public String getKeywords()
    {
        return this.keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    @Property(order = 100)
    public String getOriginalProvider()
    {
        return this.originalProvider;
    }

    public void setOriginalProvider(String originalProvider)
    {
        this.originalProvider = originalProvider;
    }

    @Property(order = 110)
    public String getOriginalId()
    {
        return this.originalId;
    }

    public void setOriginalId(String originalId)
    {
        this.originalId = originalId;
    }
}
