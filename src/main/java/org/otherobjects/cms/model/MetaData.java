package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * MetaData for site pages. This data augments the publishing and audit information already
 * stored and is primarily for SEO (internal search and external engines).
 */
// FIXME This must not extend BaseNode.
@Type
public class MetaData extends BaseComponent
{
    private String title;
    private String description;
    private String keywords;

    public String getOoLabel()
    {
        return "MD";
    }
    
    public String getCode()
    {
        // FIXME This should not be needed -- should be determined by parent typeDef.
        return "metaData";
    }

    @Property(order = 10)
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Property(order = 20)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 30)
    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }
}
