package org.otherobjects.cms.model;

import org.otherobjects.cms.Url;

public interface Linkable
{
    /**
     * The string to put in an anchor tags href attribute to link to this SiteItem. Might be relative or absolute.
     * 
     * @return - href Url
     */
    Url getHref();
}
