package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeService;

import flexjson.JSON;

@SuppressWarnings("unchecked")
public class SiteFolder extends DynaNode implements Folder
{
    private String label;
    private String cssClass;
    //FIXME This List should be TypeDefs but they are really DynaNodes...
    private List allowedTypes;

    @Override
    public boolean isFolder()
    {
        return true;
    }
    
    public List getAllAllowedTypes()
    {
        if (getAllowedTypes() != null && getAllowedTypes().size() > 0)
            return getAllowedTypes();
        else
            return (List) ((TypeService)SingletonBeanLocator.getBean("typeService")).getTypesBySuperClass(DynaNode.class);
    }
    
    public String getCssClass()
    {
        return cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
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
        // FIXME Label should be fetched via dedicated method
        return (String) (label != null ? label : (get(getLabelProperty()) != null ? get(getLabelProperty()) : getCode()));
    }

    public void setLabel(String label)
    {
        this.label = label;
    }
}
