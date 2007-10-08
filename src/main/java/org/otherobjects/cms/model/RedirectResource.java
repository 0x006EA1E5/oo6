package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class RedirectResource extends DynaNode
{
    private static final String ICON_PATH = "otherobjects.resources/static/icons/page-white-go.png";

    private String label;
    private String url;
    private Boolean temporary;

    @Override
    public String getOoIcon()
    {
        return ICON_PATH;
    }

    @Property(order = 10)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 10)
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @Property(order = 10)
    public Boolean getTemporary()
    {
        return temporary;
    }

    public void setTemporary(Boolean temporary)
    {
        this.temporary = temporary;
    }

}
