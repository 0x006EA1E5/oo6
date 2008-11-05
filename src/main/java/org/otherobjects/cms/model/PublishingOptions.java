package org.otherobjects.cms.model;

import java.util.Date;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Publishing options for site pages.
 * 
 * @author rich
 */
@Type
public class PublishingOptions extends BaseNode
{
    private Date embargoUntil;
    private Date expireOn;
    private Boolean showInMenu;
    private Boolean hidden;
    private String accessbilityHotKey;
    private Template template;

    
    @Override
    public String getOoLabel()
    {
        return "Publishing Options";
    }
    
    @Override
    public String getCode()
    {
        // FIXME This should not be needed -- should be determined by parent typeDef.
        return "publishingOptions";
    }
    
    @Property(order = 10)
    public Date getEmbargoUntil()
    {
        return embargoUntil;
    }

    public void setEmbargoUntil(Date embargoUntil)
    {
        this.embargoUntil = embargoUntil;
    }

    @Property(order = 20)
    public Date getExpireOn()
    {
        return expireOn;
    }

    public void setExpireOn(Date expireOn)
    {
        this.expireOn = expireOn;
    }

    @Property(order = 30)
    public Boolean getShowInMenu()
    {
        return showInMenu;
    }

    public void setShowInMenu(Boolean showInMenu)
    {
        this.showInMenu = showInMenu;
    }

    @Property(order = 40)
    public Boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    @Property(order = 50)
    public String getAccessbilityHotKey()
    {
        return accessbilityHotKey;
    }

    public void setAccessbilityHotKey(String accessbilityHotKey)
    {
        this.accessbilityHotKey = accessbilityHotKey;
    }

    @Property(order = 6)
    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
    }
}
