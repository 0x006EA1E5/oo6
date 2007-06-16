package org.otherobjects.cms.types;

/**
 * Defines a property of a type.
 * 
 * <p>Currently supported simple property types are:
 * 
 * <ul>
 * <li>string (String.class)
 * <li>text (String.class)
 * <li>date (Date.class)
 * <li>time (Date.class)
 * <li>timestamp (Date.class)
 * <li>boolean (Boolean.class)
 * <li>number (Long.class Integer.class)
 * <li>decimal (BigDecimal.class, Float.class, Double.class) -- specify decimal places in format -- stored as double
 * </ul>
 * 
 * @author rich
 *
 */
public class PropertyDef
{
    private String name;
    private String type;
    private String relatedType;
    
    /** To specify format for decimals. TODO Maybe others? */
    private String format; 
    
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
