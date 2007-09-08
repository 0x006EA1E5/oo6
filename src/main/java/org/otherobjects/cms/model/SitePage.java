package org.otherobjects.cms.model;

import org.otherobjects.cms.util.StringUtils;


public class SitePage extends DynaNode
{
    // FIXME Move to PublishingOptions object?
    private DynaNode template;
    
    public String getCode()
    {
        return StringUtils.generateUrlCode(getLabel()) + ".html";
    }

    public DynaNode getTemplate()
    {
        return template;
    }

    public void setTemplate(DynaNode template)
    {
        this.template = template;
    }
    
}
