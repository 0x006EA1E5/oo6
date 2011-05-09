package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

import flexjson.JSON;

@SuppressWarnings("unchecked")
@Type
public class SmartFolder extends Folder
{
    private String label;
    private String query;
    private String searchTerm;
    private String cssClass;

    public List getAllAllowedTypes()
    {
        return getAllowedTypes();
    }

    @JSON(include = false)
   // @Property(order = 50, collectionElementType = PropertyType.STRING)
    public List<String> getAllowedTypes()
    {
        // Smart folders are read only
        return null;
    }

    public void setAllowedTypes(List allowedTypes)
    {
        // Not relevant for Database backed folders
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

    @Property(order = 20)
    public String getQuery()
    {
        return this.query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    @Property(order = 40)
    public String getCssClass()
    {
        return this.cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    @Property(order = 30)
    public String getSearchTerm()
    {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm)
    {
        this.searchTerm = searchTerm;
    }

}
