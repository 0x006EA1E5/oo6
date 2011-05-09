package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class RedirectResource extends BaseNode
{
    private static final String ICON_PATH = "/otherobjects/static/icons/redirect.png";

    private String label;
    private String url;
    private Boolean temporary;
    private PublishingOptions publishingOptions;
    
    @Override
    public String getCode()
    {
        return org.otherobjects.cms.util.StringUtils.generateUrlCode(getLabel()) + ".html";
    }
    
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

    @Property(order = 20)
    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    @Property(order = 30)
    public Boolean getTemporary()
    {
        return temporary;
    }

    public void setTemporary(Boolean temporary)
    {
        this.temporary = temporary;
    }
    
    @Property(order = 500, type=PropertyType.COMPONENT)
    public PublishingOptions getPublishingOptions()
    {
        return publishingOptions;
    }

    public void setPublishingOptions(PublishingOptions publishingOptions)
    {
        this.publishingOptions = publishingOptions;
    }
}
