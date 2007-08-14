package org.otherobjects.cms.model;

import org.otherobjects.cms.util.StringUtils;


public class SitePage extends DynaNode
{
    public String getCode()
    {
        return StringUtils.generateUrlCode(getLabel()) + ".html";
    }
}
