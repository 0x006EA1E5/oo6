package org.otherobjects.cms.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.annotation.Type;
import org.springframework.util.Assert;

import flexjson.JSON;

/**
 * 
 * @author rich
 */
@Type
public class JcrTypeDef extends DynaNode implements TypeDef
{
    private static final String JCR_ROOT_PATH = "/types";
    private static final String DEFAULT_SUPER_CLASS_NAME = DynaNode.class.getName();

    private String name;
    private String className;
    private String superClassName;
    private Map<String, PropertyDef> properties = new LinkedHashMap<String, PropertyDef>();
    private String label;
    private String description;
    private String help;
    private String labelProperty;
    private TypeService typeService;

    public JcrTypeDef()
    {
    }

    public JcrTypeDef(String name)
    {
        setName(name);
    }

    @Override
    public String toString()
    {
        return "[Type: " + getName() + "]";
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

    public void setProperties(Collection<PropertyDefImpl> properties)
    {
        this.properties = new LinkedHashMap<String, PropertyDef>();
        if (properties == null)
            return;
        for (PropertyDefImpl pd : properties)
            addProperty(pd);
    }

    public Collection<PropertyDef> getProperties()
    {
        ArrayList<PropertyDef> properties = new ArrayList<PropertyDef>();
        properties.addAll((Collection<PropertyDef>) this.properties.values());
        return properties;
    }

    public void addProperty(PropertyDefImpl pd)
    {
        // Check for duplicates 
        Assert.isNull(getProperty(pd.getName()));
        pd.setParentTypeDef(this);
        this.properties.put(pd.getName(), pd);
    }

    public String getLabel()
    {
        return label != null ? label : createLabel(getName());
    }

    protected String createLabel(String name2)
    {
        return StringUtils.substringAfterLast(name, ".");
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
        if (this.className != null)
            return this.className;
        else
            return getName();
    }

    public void setClassName(String className)
    {
        this.className = className;
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

    public String getLabelProperty()
    {
        return labelProperty;
    }

    public void setLabelProperty(String labelProperty)
    {
        this.labelProperty = labelProperty;
    }

    @JSON(include = false)
    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    /**
     * Determines if this TypeDef has a class associated with it. This 
     * only return true if the backing class exists or has been generated.
     * @return
     */
    public boolean hasClass()
    {
        return (this.className != null);
    }

    public String getSuperClassName()
    {
        return superClassName != null ? superClassName : DEFAULT_SUPER_CLASS_NAME;
    }

    public void setSuperClassName(String superClassName)
    {
        this.superClassName = superClassName;
    }

}
