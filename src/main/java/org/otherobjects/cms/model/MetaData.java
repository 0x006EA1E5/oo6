package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

/**
 * FIXME Should this extend DynaNode?
 */
@Type
public class MetaData extends DynaNode
{
    private String title;
    private String description;
    private List<String> keywords;

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

    @Property(order = 30, type = PropertyType.LIST, collectionElementType = PropertyType.STRING)
    public List<String> getKeywords()
    {
        return keywords;
    }

    public void setKeywords(List<String> keywords)
    {
        this.keywords = keywords;
    }

}
