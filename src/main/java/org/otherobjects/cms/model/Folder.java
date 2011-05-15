package org.otherobjects.cms.model;

import java.io.Serializable;
import java.util.List;

import org.otherobjects.cms.types.TypeDef;

@SuppressWarnings("serial")
public abstract class Folder extends BaseNode implements Serializable
{
    public abstract String getLabel();

    public abstract void setLabel(String label);

    abstract List<?> getAllowedTypes();

    abstract void setAllowedTypes(List<String> allowedTypes);

    abstract List<TypeDef> getAllAllowedTypes();

    abstract String getCssClass();

    abstract void setCssClass(String cssClass);
}
