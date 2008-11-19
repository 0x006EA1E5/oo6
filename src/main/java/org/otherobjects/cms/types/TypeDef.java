package org.otherobjects.cms.types;

import java.util.List;

/**
 * @author rich
 */
public interface TypeDef
{
    // TODO These need to be pluggable
    String JACKRABBIT = "jackrabbit";
    String HIBERNATE = "hibernate";
    String FILESYSTEM = "FILESYSTEM";
    
    PropertyDef getProperty(String name);

    String getName();

    String getStore();

    List<PropertyDef> getProperties();

    String getLabel();

    String getDescription();

    String getHelp();

    String getClassName();
    
    String getAdminControllerUrl();

    boolean hasClass();

    String getSuperClassName();
    
    String getLabelProperty();

    void setTypeService(TypeService typeService);

    void setClassName(String createCustomDynaNodeClass);

    TypeService getTypeService();

}
