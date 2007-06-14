package org.otherobjects.cms.types;

public class PropertyDef
{
    private String name;
    private String type;
    private String relatedType;
    
    /** Human friendly name for property. Can be inferred from name */
    private String label;
    
    /** Description for this property. */
    private String description;

    /** Help text to assist choosing value for this property. */
    private String help;

    public PropertyDef(String name, String propertyType, String relatedType)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getRelatedType()
    {
        return relatedType;
    }

    public void setRelatedType(String relatedType)
    {
        this.relatedType = relatedType;
    }

}
