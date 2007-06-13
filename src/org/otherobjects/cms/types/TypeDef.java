package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;


public class TypeDef
{
    private boolean internal = false;
    private String name;
    private String validators;
    private String javaClass;
    private Map<String, PropertyDef> properties = new LinkedHashMap<String, PropertyDef>();

    public TypeDef(String name)
    {
        setName(name);
    }

    public PropertyDef getProperty(String name)
    {
        return properties.get(name);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Collection<PropertyDef> getProperties()
    {
        return (Collection<PropertyDef>) properties.values();
    }

    public String getJavaClass()
    {
        return javaClass;
    }

    public void setJavaClass(String javaClass)
    {
        this.javaClass = javaClass;
    }

    public String getValidators()
    {
        return validators;
    }

    public void setValidators(String validators)
    {
        this.validators = validators;
    }

    public boolean isInternal()
    {
        return internal;
    }

    public void setInternal(boolean internal)
    {
        this.internal = internal;
    }

}
