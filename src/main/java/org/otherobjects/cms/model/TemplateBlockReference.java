package org.otherobjects.cms.model;

import java.util.Date;

import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Links a block type instace and it's associated data.
 * 
 * @author rich
 */
@Type
public class TemplateBlockReference extends BaseNode
{
    private String code;
    private TemplateBlock block;
    private DynaNode blockData;

    public TemplateBlockReference()
    {
        this.code = String.valueOf(new Date().getTime());
    }

    /**
     * Determines if this block reference and it's data are both published.
     *  
     * @return
     */
    public boolean isFullyPublished()
    {
        if (!isPublished())
            return false;
        if (blockData != null && !blockData.isPublished())
            return false;
        return true;
    }

    public String getCode()
    {
        return this.code;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    @Property(order = 10, type = PropertyType.REFERENCE)
    public TemplateBlock getBlock()
    {
        return block;
    }

    public void setBlock(TemplateBlock block)
    {
        this.block = block;
    }

    @Property(order = 20, type = PropertyType.COMPONENT, fieldType = "none")
    public DynaNode getBlockData()
    {
        return blockData;
    }

    public void setBlockData(DynaNode blockData)
    {
        this.blockData = blockData;
    }

}
