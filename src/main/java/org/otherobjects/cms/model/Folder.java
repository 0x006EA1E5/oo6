package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.TypeDef;

@SuppressWarnings("unchecked")
public interface Folder
{
    String getLabel();

    void setLabel(String label);

    List getAllowedTypes();

    void setAllowedTypes(List<String> allowedTypes);

    List<TypeDef> getAllAllowedTypes();

    String getCssClass();

    void setCssClass(String cssClass);
}
