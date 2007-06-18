package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class TypeDef
{
    /** The unique name for this type. */
    private String name;
    
    /** The class name of the backing class (if not using CmsNode). */
    private String className;
    
    /** Collection of properties for this type. */
    private Map<String, PropertyDef> properties = new LinkedHashMap<String, PropertyDef>();

    /** Human friendly name for type. Can be inferred from name */
    private String label;

    /** Description for this type. */
    private String description;

    /** Help text to assist choosing value for this type. */
    private String help;

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
    
    public void addProperty(PropertyDef pd)
    {
        this.properties.put(pd.getName(), pd);
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getHelp()
    {
        return help;
    }

    public void setHelp(String help)
    {
        this.help = help;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

}
