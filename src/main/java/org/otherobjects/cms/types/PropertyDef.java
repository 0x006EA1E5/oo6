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
public interface PropertyDef
{
    public static final Object LIST = "list";
    public static final Object REFERENCE = "reference";
    public static final Object COMPONENT = "component";

    /**
     * Returns the name of the Class required to store this property. This is looked up from the TypeService.
     */
    public String getClassName();

    public String getName();

    public String getType();

    public String getRelatedType();

    public String getFormat();

    public String getLabel();

    public String getCollectionElementType();

    public String getDescription();

    public String getHelp();

    public boolean isRequired();

    public int getSize();

    public String getValang();

    public int getOrder();

    public TypeDef getRelatedTypeDef();

    public TypeService getTypeService();

    public TypeDef getParentTypeDef();

}
