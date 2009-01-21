package org.otherobjects.cms.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class TemplateBlock extends BaseNode
{
    private String code;
    private String label;
    private String description;
    private String keywords;
    private Boolean global;

    @Property(order = 10)
    public String getCode()
    {
        return code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @Property(order = 20)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 35, type = PropertyType.TEXT)
    public String getKeywords()
    {
        return keywords;
    }
    
    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }
    
    @Property(order = 30, type = PropertyType.TEXT)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }
    
    @Property(order = 40)
    public Boolean getGlobal()
    {
        return global;
    }

    public void setGlobal(Boolean global)
    {
        this.global = global;
    }
    
    public boolean isGlobalBlock()
    {
        return (global != null && global.booleanValue());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        return EqualsBuilder.reflectionEquals(this, obj);
    }
    
}
