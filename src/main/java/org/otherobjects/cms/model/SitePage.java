package org.otherobjects.cms.model;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.util.StringUtils;

import flexjson.JSON;

/**
 * FIXME Name should reflect abstract status
 * @author rich
 *
 */
public abstract class SitePage extends BaseNode //implements SiteItem
{
    private Url url;
    private boolean inMenu = false;
    private String extraNavigationLabel;

    @Property(order = 35, type = PropertyType.BOOLEAN)
    public boolean isInMenu()
    {
        return inMenu;
    }

    public void setInMenu(boolean inMenu)
    {
        this.inMenu = inMenu;
    }

    public int getDepth()
    {
        return getHref().getDepth();
    }

    @Property(order = 25)
    public String getExtraNavigationLabel()
    {
        return extraNavigationLabel;
    }

    public void setExtraNavigationLabel(String extraNavigationLabel)
    {
        this.extraNavigationLabel = extraNavigationLabel;
    }

    public String getNavigationLabel()
    {
        return (org.apache.commons.lang.StringUtils.isNotBlank(getExtraNavigationLabel())) ? getExtraNavigationLabel() : getLabel();
    }

    @Override
    public String getCode()
    {
        return StringUtils.generateUrlCode(getLabel()) + ".html";
    }

    @JSON(include = false)
    public Url getHref()
    {
        if (url == null)
            url = new Url(getOoUrlPath());
        return url;
    }

}
