package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class TemplateRegion extends DynaNode
{
    private String code;
    private String label;
    private List<TemplateBlock> blocks;

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

    @Property(order = 30)
    public List<TemplateBlock> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(List<TemplateBlock> blocks)
    {
        this.blocks = blocks;
    }

}
