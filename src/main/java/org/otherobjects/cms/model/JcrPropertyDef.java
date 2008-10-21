package org.otherobjects.cms.model;

import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Persistent representation of a PropertyDef for storage in JCR.
 * 
 * FIXME Add validation
 * FIXME If this is a component does it need to be a DynaNode?
 * 
 * @author rich
 */
@Type(label = "Property Def", labelProperty="name")
public class JcrPropertyDef extends BaseNode
{
    private String name;
    private String type;
    private String collectionElementType;
    private String relatedType;
    private String label;
    private String format;
    private String description;
    private Boolean required;
    private Long size;
    private String valang;
    private String help;

    public JcrPropertyDef()
    {
    }

    public PropertyDef toPropertyDef()
    {
        PropertyDefImpl pd = new PropertyDefImpl();
        pd.setName(getName());
        pd.setType(getType());
        pd.setCollectionElementType(getCollectionElementType());
        pd.setRelatedType(getRelatedType());
        pd.setLabel(getLabel());
        pd.setFormat(getFormat());
        pd.setDescription(getDescription());
        if (getRequired() != null)
            pd.setRequired(getRequired());
        if (getSize() != null)
            pd.setSize(getSize().intValue());
        pd.setValang(getValang());
        pd.setHelp(getHelp());
        return pd;
    }
    
    @Override
    public String getOoLabel()
    {
        return getName();
    }

    @Property(order = 10)
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Property(order = 20)
    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    @Property(order = 30)
    public String getCollectionElementType()
    {
        return collectionElementType;
    }

    public void setCollectionElementType(String collectionElementType)
    {
        this.collectionElementType = collectionElementType;
    }

    @Property(order = 40)
    public String getRelatedType()
    {
        return relatedType;
    }

    public void setRelatedType(String relatedType)
    {
        this.relatedType = relatedType;
    }

    @Property(order = 50)
    public String getFormat()
    {
        return format;
    }

    public void setFormat(String format)
    {
        this.format = format;
    }

    @Property(order = 60)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 70, type = PropertyType.TEXT)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 80)
    public Boolean getRequired()
    {
        return required;
    }

    public void setRequired(Boolean required)
    {
        this.required = required;
    }

    @Property(order = 90)
    public Long getSize()
    {
        return size;
    }

    public void setSize(Long size)
    {
        this.size = size;
    }

    @Property(order = 100, type = PropertyType.TEXT)
    public String getValang()
    {
        return valang;
    }

    public void setValang(String valang)
    {
        this.valang = valang;
    }

    @Property(order = 110)
    public String getHelp()
    {
        return help;
    }

    public void setHelp(String help)
    {
        this.help = help;
    }

}
