package org.otherobjects.cms.types;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.otherobjects.cms.model.CmsNode;
import org.springframework.util.Assert;

/**
 * FIXME Split into interface and Jcr impl
 * 
 * @author rich
 */
public class TypeDef implements CmsNode
{
    private static final String JCR_ROOT_PATH = "/types";

    /** JCR GUID. */
    private String id;

    /** The unique name for this type. */
    private String name;

    /** The class name of the backing class (if not using CmsNode). */
    private String className;

    /** Collection of properties for this type. */
    private Map<String, PropertyDef> properties = new LinkedHashMap<String, PropertyDef>();
    //    private List<PropertyDef> properties = new ArrayList<PropertyDef>();

    /** Human friendly name for type. Can be inferred from name */
    private String label;

    /** Description for this type. */
    private String description;

    /** Help text to assist choosing value for this type. */
    private String help;

    public TypeDef()
    {
    }

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

    public void setProperties(Collection<PropertyDef> properties)
    {
        this.properties = new LinkedHashMap<String, PropertyDef>();
        if (properties == null)
            return;
        for (PropertyDef pd : properties)
            addProperty(pd);
    }

    public Collection<PropertyDef> getProperties()
    {
        return (Collection<PropertyDef>) properties.values();
    }

    public void addProperty(PropertyDef pd)
    {
        // Check for duplicates 
        Assert.isNull(getProperty(pd.getName()));
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

    public String getId()
    {
        return id;
    }

    public String getJcrPath()
    {
        return JCR_ROOT_PATH + "/" + getName();
    }

    /** FIXME Copied from dynaNode */
    public void setJcrPath(String jcrPath)
    {
        if (jcrPath == null)
        {
            setName(null);
            return;
        }

        Assert.isTrue(jcrPath.lastIndexOf("/") >= 0, "jcrPath must contain at least one forward slash");
        Assert.isTrue(!jcrPath.endsWith("/"), "jcrPath must not end with a forward slash");

        int slashPos = jcrPath.lastIndexOf("/");
        setName(jcrPath.substring(slashPos + 1));
    }

    public void setId(String id)
    {
        this.id = id;
    }

}
