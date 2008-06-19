package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class TemplateRegion extends BaseNode
{
    private String code;
    private String label;
    private List<TemplateBlockReference> blocks;

    @Override
    @Property(order = 10)
    public String getCode()
    {
        return code;
    }

    @Override
    public void setCode(String code)
    {
        this.code = code;
    }

    @Override
    @Property(order = 20)
    public String getLabel()
    {
        return label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 30, collectionElementType = PropertyType.COMPONENT)
    public List<TemplateBlockReference> getBlocks()
    {
        return blocks;
    }

    public void setBlocks(List<TemplateBlockReference> blocks)
    {
        this.blocks = blocks;
    }

}
