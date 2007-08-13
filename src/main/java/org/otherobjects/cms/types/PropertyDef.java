package org.otherobjects.cms.types;

import java.util.ArrayList;

import org.otherobjects.cms.util.StringUtils;
import org.springframework.util.Assert;

import flexjson.JSON;

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
 * <li>decimal (BigDecimal.class, Float.class, Double.class) -- specify decimal places in format -- stored as a BigDecimal
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
    public static final Object LIST = "list";
    public static final Object REFERENCE = "reference";
    public static final Object COMPONENT = "component";

    /** Property name. */
    private String name;

    /** Defines type of this property. */
    private String type;

    /** Type of items in collection if this property is a collection. */
    private String collectionElementType;

    /** Type of reference or component.  */
    private String relatedType;

    /** To specify format for decimals. TODO M2 Implement this for BigDecimal and maybe others? */
    private String format;

    /** Human friendly name for property. Can be inferred from name */
    private String label;

    /** Description for this property. */
    private String description;

    /** To flag whether this property can be left empty or not */
    private boolean required = false;

    /** To indicate how long this property can be (only makes for string type properties). Defaults to -1 which means no limit */
    private int size = -1;

    /** 
     * Holds valang rules to build a validator for the type that is made of this PropertyDef. Beware: the name of the property is not included in the rule unlike with standard valang rules 
     * so the syntax is this:
     * 
     * { ? : <predicate_expression> : <message> [: <error_code> [: <args> ]] }
     * 
     * (above can be repeated multiple times separated by spaces)
     * 
     * Notice the ? placeholder which gets substituted for the property name. This has to appear exactly as shown in the valang string.
     * Beware: Don't use ? in your message or error_code unless you want it to be replaced by the property name.
     */
    private String valang;

    private String help;

    /** Reference to parent TypeDef. */
    private TypeDef parentTypeDef;

    public PropertyDef()
    {
    }
    
    /**
     * 
     * @param name
     * @param propertyType - oo type: simple props, component, reference or list
     * @param relatedType - for components and references or lists thereof, type of component/reference
     * @param collectionElementType - for lists only: type of elements of the list: simple, component, reference
     * @param required
     */
    public PropertyDef(String name, String propertyType, String relatedType, String collectionElementType, boolean required)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
        setCollectionElementType(collectionElementType);
        setRequired(required);
    }
    
    /**
     * 
     * @param name
     * @param propertyType - oo type: simple props, component, reference or list
     * @param relatedType - for components and references or lists thereof, type of component/reference
     * @param collectionElementType - for lists only: type of elements of the list: simple, component, reference
     */
    public PropertyDef(String name, String propertyType, String relatedType, String collectionElementType)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
        setCollectionElementType(collectionElementType);
    }

    /**
     * Returns the name of the Class required to store this property. This is looked up from the TypeService.
     */
    @JSON(include=false)
    public String getClassName()
    {
        String className;
        if (getType().equals(LIST))
            className = ArrayList.class.getName();
        else if (getType().equals(REFERENCE) || getType().equals(COMPONENT))
            className = getRelatedTypeDef().getClassName();
        else
            className = ((JcrTypeServiceImpl) getTypeService()).getClassNameForType(this.type);
        Assert.notNull(className, "No class found for property: " + getName() + " of type: " + getType() + ".");
        return className;
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
        if (label == null)
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

    public boolean isRequired()
    {
        return required;
    }

    public void setRequired(boolean required)
    {
        this.required = required;
    }

    public int getSize()
    {
        return size;
    }

    public void setSize(int size)
    {
        this.size = size;
    }

    public String getValang()
    {
        return valang;
    }

    public void setValang(String valang)
    {
        this.valang = valang;
    }

    public TypeDef getRelatedTypeDef()
    {
        if (getRelatedType() == null)
            return null;
        return getTypeService().getType(getRelatedType());
    }

    @JSON(include = false)
    public TypeService getTypeService()
    {
        // FIXME Remove singleton access
        TypeService typeService = getParentTypeDef().getTypeService();
        Assert.notNull(typeService, "No TypeService registered with this proprerty's typeDef.");
        return typeService;
    }

    @JSON(include = false)
    public TypeDef getParentTypeDef()
    {
        return parentTypeDef;
    }

    public void setParentTypeDef(TypeDef typeDef)
    {
        this.parentTypeDef = typeDef;
    }

	public String getCollectionElementType() {
		return collectionElementType;
	}

	public void setCollectionElementType(String collectionElementType) {
		this.collectionElementType = collectionElementType;
	}
    
    

}
