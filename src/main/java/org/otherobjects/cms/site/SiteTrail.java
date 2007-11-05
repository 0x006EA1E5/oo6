package org.otherobjects.cms.site;

import java.util.Iterator;

public interface SiteTrail
{
    /**
     * An iterator of all SiteItems in the specified SiteItem's trail starting at the topmost parent.
     *  
     *  @return
     */
    public Iterator<SiteItem> getCrumbs();

    /**
     * An Iterator of all SiteItems in the specified SiteItem's trail starting at the specified siteItem moving up until the topmost parent.
     * 
     * @return
     */
    public Iterator<SiteItem> getTrailParts();

    /**
     * a string containing all the navigationLabels of all SiteItems making up the specified siteItem separated by the specified separator. The order of labels goes from topmost parent -> siteItem
     * 
     * @param separator
     * @return
     */
    public String getFullTitle(String separator);
}
