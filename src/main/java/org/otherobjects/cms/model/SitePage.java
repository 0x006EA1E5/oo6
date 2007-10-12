package org.otherobjects.cms.model;

import org.otherobjects.cms.util.StringUtils;

/**
 * FIXME Name should reflect abstract status
 * @author rich
 *
 */
public abstract class SitePage extends BaseNode
{
    // FIXME Move to PublishingOptions object?
    private Template template;
    
    public String getCode()
    {
        return StringUtils.generateUrlCode(getLabel()) + ".html";
    }

    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
    }
    
}
