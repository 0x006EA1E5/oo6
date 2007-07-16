package org.otherobjects.cms.types;

import org.otherobjects.cms.util.StringUtils;

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
 * <p>Currently supported bean property types are:
 * 
 * <ul>
 * <li>component
 * <li>reference
 * </ul>
 * 
 * <p>Components persist independently and can't be saved or deleted independently of the CmsNode they belong to. 
 * Can be multiple levels deep.
 * 
 * <p>Currently supported collection property types (which can be used with any of the above) are:
 * 
 * <ul>
 * <li>list (must be ArrayList?)
 * </ul>
 * 
 * <p>Note that collection elements must always be of the same property type.
 * 
 * @author rich
 *
 */
public class PropertyDef
{
    /** Property name. */
    private String name;

    /** Defines type of this property. */
    private String type;

    /** Type of collection if this property can have multiple values. */
    private String collectionType;

    /** Type of reference or component.  */
    private String relatedType;

    /** To specify format for decimals. TODO Maybe others? */
    private String format;

    /** Human friendly name for property. Can be inferred from name */
    private String label;

    /** Description for this property. */
    private String description;
    
    /** To flag whether this property can be left empty or not */
    private boolean required = false;
    
    /** To indicate how long this property can be (only makes for string type properties). Defaults to -1 which means no limit */
    private int size = -1;
    
    /** Holds valang rules to build a validator for the type that is made of this PropertyDef. Beware: the name of the property is not included in the rule unlike with standard valang rules 
     * so the syntax is this:
     * { $THIS : <predicate_expression> : <message> [: <error_code> [: <args> ]] }
     * (above can be repeated multiple times)
     * 
     * Notice the $THIS placeholder which gets substituted for the property name. This has to appear exactly as shown in the valang string.
     * 
     * */
    private String valang;
    
    

    private String help;

    public PropertyDef()
    {
    }
    
    public PropertyDef(String name, String propertyType, String relatedType, String collectionType)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
        setCollectionType(collectionType);
    }

    @Override
    public String toString()
    {
        return getName();
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

    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    public String getLabel()
    {   
        if(label == null)
            return StringUtils.generateLabel(getName());
        else
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

    public String getCollectionType()
    {
        return collectionType;
    }

    public void setCollectionType(String collectionType)
    {
        this.collectionType = collectionType;
    }

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getValang() {
		return valang;
	}

	public void setValang(String valang) {
		this.valang = valang;
	}
	
}
