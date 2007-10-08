package org.otherobjects.cms.types;

import java.util.Collection;

/**
 * FIXME Split into interface and Jcr impl
 * 
 * @author rich
 */
public interface TypeDef
{
    public PropertyDef getProperty(String name);

    public String getName();

    public Collection<PropertyDef> getProperties();

    public String getLabel();

    public String getDescription();

    public String getHelp();

    public String getClassName();

    public boolean hasClass();

    public String getSuperClassName();
    
    public String getLabelProperty();

    public void setTypeService(TypeService typeService);

    public void setClassName(String createCustomDynaNodeClass);

    public TypeService getTypeService();

}
