package org.otherobjects.cms.types;

import java.util.Collection;

/**
 * FIXME Split into interface and Jcr impl
 * 
 * @author rich
 */
public interface TypeDef
{
    PropertyDef getProperty(String name);

    String getName();

    Collection<PropertyDef> getProperties();

    String getLabel();

    String getDescription();

    String getHelp();

    String getClassName();

    boolean hasClass();

    String getSuperClassName();
    
    String getLabelProperty();

    void setTypeService(TypeService typeService);

    void setClassName(String createCustomDynaNodeClass);

    TypeService getTypeService();

}
