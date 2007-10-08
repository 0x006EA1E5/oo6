package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

import flexjson.JSON;

@Type
public class SiteFolder extends DynaNode implements Folder
{
    private String label;
    private String cssClass;
    private List<String> allowedTypes;

    @Override
    public boolean isFolder()
    {
        return true;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllAllowedTypes()
    {
        if (getAllowedTypes() != null && getAllowedTypes().size() > 0)
            return getAllowedTypes();
        else
            return (List) ((TypeService) SingletonBeanLocator.getBean("typeService")).getTypesBySuperClass(DynaNode.class);
    }

    @Property(order = 40)
    public String getCssClass()
    {
        return cssClass;
    }

    public void setCssClass(String cssClass)
    {
        this.cssClass = cssClass;
    }

    @JSON(include = false)
    @Property(order = 50, collectionElementType = PropertyType.STRING)
    public List<String> getAllowedTypes()
    {
        return allowedTypes;
    }

    public void setAllowedTypes(List<String> allowedTypes)
    {
        this.allowedTypes = allowedTypes;
    }

    @Property(order = 20)
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
