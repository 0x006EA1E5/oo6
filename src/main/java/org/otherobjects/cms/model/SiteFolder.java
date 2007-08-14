package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeService;

import flexjson.JSON;

public class SiteFolder extends DynaNode
{
    private String label;
    private String cssClass;
    private List allowedTypes;

    public String getCssClass()
    {
        return cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    public List getAllAllowedTypes()
    {
        if (getAllowedTypes() != null && getAllowedTypes().size() > 0)
            return getAllowedTypes();
        else
            return (List) ((TypeService)SingletonBeanLocator.getBean("typeService")).getTypesBySuperClass(SitePage.class);
    }

    @JSON(include = false)
    public List getAllowedTypes()
    {
        return allowedTypes;
    }

    public void setAllowedTypes(List allowedTypes)
    {
        this.allowedTypes = allowedTypes;
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
