package org.otherobjects.cms.model;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class Template extends BaseNode
{
    private String label;
    private TemplateLayout layout;
    private List<TemplateRegion> regions = new ArrayList<TemplateRegion>();

    public TemplateRegion getRegion(String regionCode)
    {
        if (getRegions() == null)
            return null;
        for (TemplateRegion r : getRegions())
        {
            if (r.getCode().equals(regionCode))
                return r;
        }
        return null;
    }

    @Property(order = 10)
    public String getCode()
    {
        return super.getCode();
    }

    public void setCode(String code)
    {
        super.setCode(code);
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
    public TemplateLayout getLayout()
    {
        return layout;
    }

    public void setLayout(TemplateLayout layout)
    {
        this.layout = layout;
    }

    @Property(order = 40, collectionElementType = PropertyType.COMPONENT, fieldType="none")
    public List<TemplateRegion> getRegions()
    {
        return regions;
    }

    public void setRegions(List<TemplateRegion> regions)
    {
        this.regions = regions;
    }
}
