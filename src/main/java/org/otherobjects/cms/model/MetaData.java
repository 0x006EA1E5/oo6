package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * FIXME Should this extend BaseNode?
 */
@Type
public class MetaData extends BaseNode
{
    private String title;
    private String description;
    private List<String> keywords;

    @Override
    public String getCode()
    {
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
    public List<String> getKeywords()
    {
        return keywords;
    }

    public void setKeywords(List<String> keywords)
    {
        this.keywords = keywords;
    }

}
