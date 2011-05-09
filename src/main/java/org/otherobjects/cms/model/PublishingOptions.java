package org.otherobjects.cms.model;

import java.util.Date;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Publishing options for site pages.
 * 
 * @author rich
 */
@Type(codeProperty = "")
public class PublishingOptions extends BaseComponent
{
    private Date embargoUntil;
    private Date expireOn;
    private Boolean showInNavigation;
    private Boolean hidden;
    private String navigationLabel;
    private String accessbilityHotKey;
    private Template template;
    private Long sortOrder = 0L; // FIXME This is only temporary

    public String toString()
    {
        return "Publishing Options";
    }

    @Override
    public String getOoLabel()
    {
        return toString();
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
    public Boolean getShowInNavigation()
    {
        return showInNavigation;
    }

    public void setShowInNavigation(Boolean showInNavigation)
    {
        this.showInNavigation = showInNavigation;
    }

    @Property(order = 40, help = "Hide this item from site search and index.")
    public Boolean getHidden()
    {
        return hidden;
    }

    public void setHidden(Boolean hidden)
    {
        this.hidden = hidden;
    }

    @Property(order = 35, help = "Overrides default label when this item is shown in navigation.")
    public String getNavigationLabel()
    {
        return navigationLabel;
    }

    public void setNavigationLabel(String navigationLabel)
    {
        this.navigationLabel = navigationLabel;
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

    @Property(order = 100)
    public Long getSortOrder()
    {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder)
    {
        this.sortOrder = sortOrder;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accessbilityHotKey == null) ? 0 : accessbilityHotKey.hashCode());
        result = prime * result + ((embargoUntil == null) ? 0 : embargoUntil.hashCode());
        result = prime * result + ((expireOn == null) ? 0 : expireOn.hashCode());
        result = prime * result + ((hidden == null) ? 0 : hidden.hashCode());
        result = prime * result + ((navigationLabel == null) ? 0 : navigationLabel.hashCode());
        result = prime * result + ((showInNavigation == null) ? 0 : showInNavigation.hashCode());
        result = prime * result + ((sortOrder == null) ? 0 : sortOrder.hashCode());
        result = prime * result + ((template == null) ? 0 : template.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        PublishingOptions other = (PublishingOptions) obj;
        if (accessbilityHotKey == null)
        {
            if (other.accessbilityHotKey != null)
                return false;
        }
        else if (!accessbilityHotKey.equals(other.accessbilityHotKey))
            return false;
        if (embargoUntil == null)
        {
            if (other.embargoUntil != null)
                return false;
        }
        else if (!embargoUntil.equals(other.embargoUntil))
            return false;
        if (expireOn == null)
        {
            if (other.expireOn != null)
                return false;
        }
        else if (!expireOn.equals(other.expireOn))
            return false;
        if (hidden == null)
        {
            if (other.hidden != null)
                return false;
        }
        else if (!hidden.equals(other.hidden))
            return false;
        if (navigationLabel == null)
        {
            if (other.navigationLabel != null)
                return false;
        }
        else if (!navigationLabel.equals(other.navigationLabel))
            return false;
        if (showInNavigation == null)
        {
            if (other.showInNavigation != null)
                return false;
        }
        else if (!showInNavigation.equals(other.showInNavigation))
            return false;
        if (sortOrder == null)
        {
            if (other.sortOrder != null)
                return false;
        }
        else if (!sortOrder.equals(other.sortOrder))
            return false;
        if (template == null)
        {
            if (other.template != null)
                return false;
        }
        else if (!template.equals(other.template))
            return false;
        return true;
    }

}
