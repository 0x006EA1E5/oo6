package org.otherobjects.cms.model;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeService;

import flexjson.JSON;

@SuppressWarnings("unchecked")
public class DbFolder extends DynaNode implements Folder
{

    private String label;
    private String mainType;
    private String mainTypeQuery;
    private String cssClass;

    public List getAllAllowedTypes()
    {
        return getAllowedTypes();
    }

    @JSON(include = false)
    public List getAllowedTypes()
    {
        // Only allowed type is the main type...    
        List types = new ArrayList();
        types.add(((TypeService) SingletonBeanLocator.getBean("typeService")).getType(this.mainType));
        return types;
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

    public String getMainType()
    {
        return this.mainType;
    }

    public void setMainType(String mainType)
    {
        this.mainType = mainType;
    }

    public String getMainTypeQuery()
    {
        return this.mainTypeQuery;
    }

    public void setMainTypeQuery(String mainTypeQuery)
    {
        this.mainTypeQuery = mainTypeQuery;
    }

    public String getCssClass()
    {
        return this.cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

}
