package org.otherobjects.cms.model;

import java.util.List;

import flexjson.JSON;

@SuppressWarnings("unchecked")
public class SmartFolder extends DynaNode implements Folder
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
    public List getAllowedTypes()
    {
        // Smart folders are read only
        return null;
    }

    public void setAllowedTypes(List allowedTypes)
    {
        // Not relevant for Database backed folders
    }

    @Override
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getQuery()
    {
        return this.query;
    }

    public void setQuery(String query)
    {
        this.query = query;
    }

    public String getCssClass()
    {
        return this.cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    public String getSearchTerm()
    {
        return this.searchTerm;
    }

    public void setSearchTerm(String searchTerm)
    {
        this.searchTerm = searchTerm;
    }

}
