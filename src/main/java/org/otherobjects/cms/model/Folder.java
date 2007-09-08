package org.otherobjects.cms.model;

import java.util.List;

@SuppressWarnings("unchecked")
public interface Folder
{
    public String getLabel();

    public void setLabel(String label);

    public List getAllowedTypes();

    public void setAllowedTypes(List allowedTypes);

    public List getAllAllowedTypes();

    public String getCssClass();

    public void setCssClass(String cssClass);
}
