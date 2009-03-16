package org.otherobjects.cms.types;

import java.beans.PropertyEditor;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.beans.propertyeditors.CustomBooleanEditor;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.Ordered;
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
 * <li>decimal (BigDecimal.class, Float.class, Double.class) -- TODO specify decimal places in format -- stored as a BigDecimal
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
public class PropertyDefImpl implements PropertyDef, Ordered
{
    // TODO Do we want these default here?
    private static String dateFormat = "yyyy-MM-dd";
    private static String timeFormat = "HH:mm";
    private static String timestampFormat = "yyyy-MM-dd HH:mm";

    /** Property name. */
    private String name;

    /** Defines type of this property. */
    private String type;

    /** Optional field type. */
    private String fieldType;

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

    /** Defines the max length for this property (only relevant for string type properties). Defaults to -1 which indicates no size limit */
    private int size = -1;

    /** Indicates that this property is dynamic. */
    private boolean dynamic;

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

    /** A propertyEditor  instance used when binding this property. Can be left null in which case default propertyEditors will be used. 
     * But if you have specific data binding requirements this is a good way of specifying them */
    private PropertyEditor propertyEditor;

    private int order = Ordered.LOWEST_PRECEDENCE;

    public PropertyDefImpl()
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
    public PropertyDefImpl(String name, String propertyType, String relatedType, String collectionElementType, boolean required)
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
     * @param required
     */
    public PropertyDefImpl(String name, String propertyType, String relatedType, String collectionElementType, boolean required, boolean dynamic)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
        setCollectionElementType(collectionElementType);
        setRequired(required);
        setDynamic(dynamic);
    }

    /**
     * 
     * @param name
     * @param propertyType - oo type: simple props, component, reference or list
     * @param relatedType - for components and references or lists thereof, type of component/reference
     * @param collectionElementType - for lists only: type of elements of the list: simple, component, reference
     */
    public PropertyDefImpl(String name, String propertyType, String relatedType, String collectionElementType)
    {
        setName(name);
        setType(propertyType);
        setRelatedType(relatedType);
        setCollectionElementType(collectionElementType);
    }

    /**
     * Returns the name of the Class required to store this property. This is looked up from the TypeService.
     */
    @JSON(include = false)
    public String getClassName()
    {
        String className;
        if (getType().equals(LIST))
            className = ArrayList.class.getName();
        else if (getType().equals(REFERENCE) || getType().equals(COMPONENT))
            className = getRelatedTypeDef().getClassName();
        else
            className = ((TypeServiceImpl) getTypeService()).getClassNameForType(this.type);
        Assert.notNull(className, "No class found for property: " + getName() + " of type: " + getType() + ".");
        return className;
    }

    /**
     * return a suitable {@link PropertyEditor} for this property or null if one can't be determined
     */
    @JSON(include = false)
    public PropertyEditor getPropertyEditor()
    {
        if (this.propertyEditor == null)
        {
            TypeServiceImpl typeService = (TypeServiceImpl) getTypeService();
            String type2 = (this.type == PropertyDefImpl.LIST) ? this.collectionElementType : this.type;
            Class<?> propertyEditorTargetClass = typeService.getClassForType(type2);
            Assert.notNull(propertyEditorTargetClass, "The target class must not be null. Cases in which getClassForType would return null should've been handled upstream");
            if (typeService.getClassForType(type2).equals(String.class))
                propertyEditor = new StringTrimmerEditor(true);
            else if (typeService.getClassForType(type2).equals(Boolean.class))
                propertyEditor = new CustomBooleanEditor(true);
            else if (typeService.getClassForType(type2).equals(Long.class))
                propertyEditor = new CustomNumberEditor(Long.class, true);
            else if (typeService.getClassForType(type2).equals(BigDecimal.class))
                propertyEditor = new CustomNumberEditor(BigDecimal.class, true);
            else if (typeService.getClassForType(type2).equals(Date.class))
            {
                if (type2.equals("date"))
                {
                    Assert.notNull(dateFormat, "No dateFormat set in PropertyTypeDefImpl");
                    propertyEditor = new CustomDateEditor(new SimpleDateFormat(dateFormat), true); //FIXME These need to be set globally somewhere
                }
                else if (type2.equals("time"))
                {
                    Assert.notNull(timeFormat, "No timeFormat set in PropertyTypeDefImpl");
                    propertyEditor = new CustomDateEditor(new SimpleDateFormat(timeFormat), true);
                }
                else if (type2.equals("timestamp"))
                {
                    Assert.notNull(timestampFormat, "No timestampFormat set in PropertyTypeDefImpl");
                    propertyEditor = new CustomDateEditor(new SimpleDateFormat(timestampFormat), true);
                }

            }
        }
        return propertyEditor;
    }

    /**
     * Returns true if this is a dynamic property.
     *
     * @return
     */
    public boolean isDynamic()
    {
        return dynamic;
    }

    /**
     * Sets this property as dynamic.
     *
     * @return
     */
    public void setDynamic(boolean dynamic)
    {
        this.dynamic = dynamic;
    }

    /**
     * Returns the correct field name for this property by using data[name] notation
     * for dynamic properties.
     * 
     * <p>TODO Would this be better as a subclass? Yes.
     * 
     * @return
     */
    public String getFieldName()
    {
        if (isDynamic())
            return DynaNode.DYNA_NODE_DATAMAP_NAME + "[" + name + "]";
        else
            return name;
    }
    
    
    /**
     * Returns the correct field type for this property. If fieldType is set then this
     * is returned, otherwise the default type is used.
     * 
     * @return
     */
    public String getDefaultFieldType()
    {
        return getFieldType() != null ? getFieldType() : getType();
    }

    /**
     * Returns the correct field name for this property by using data[name] notation
     * for dynamic properties.
     * 
     * <p>TODO Would this be better as a subclass? Yes.
     * 
     * @return
     */
    public String getPropertyPath()
    {
        if (isDynamic())
            return "data." + name + "";
        else
            return name;
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
        if (StringUtils.isEmpty(label))
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
        Assert.notNull(typeService, "No TypeService registered with this property's typeDef.");
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

    public String getCollectionElementType()
    {
        return collectionElementType;
    }

    public void setCollectionElementType(String collectionElementType)
    {
        this.collectionElementType = collectionElementType;
    }

    public int getOrder()
    {
        return order;
    }

    public void setOrder(int order)
    {
        this.order = order;
    }

    public static void setDateFormat(String dateFormat)
    {
        PropertyDefImpl.dateFormat = dateFormat;
    }

    public static void setTimeFormat(String timeFormat)
    {
        PropertyDefImpl.timeFormat = timeFormat;
    }

    public static void setTimestampFormat(String timestampFormat)
    {
        PropertyDefImpl.timestampFormat = timestampFormat;
    }

    public String getFieldType()
    {
        return fieldType;
    }

    /**
     * Options: 
     * 
     * <li> null (will use default)
     * <li> none (field will not be included in forms) 
     * 
     * @param fieldType
     */
    public void setFieldType(String fieldType)
    {
        this.fieldType = fieldType;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
}
