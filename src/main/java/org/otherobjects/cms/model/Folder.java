package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.TypeDef;

@SuppressWarnings("unchecked")
public interface Folder
{
    public String getLabel();

    public void setLabel(String label);

    public List getAllowedTypes();

    public void setAllowedTypes(List<String> allowedTypes);

    public List<TypeDef> getAllAllowedTypes();

    public String getCssClass();

    public void setCssClass(String cssClass);
}
