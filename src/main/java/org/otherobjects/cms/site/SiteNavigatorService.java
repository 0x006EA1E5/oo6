package org.otherobjects.cms.site;

import java.util.List;

public interface SiteNavigatorService
{
    String JCR_SITE_ROOT_PATH = "/site";

    /**
     * 
     * @param siteItem - a siteItem or null to get items in the root
     * @return - list of siteItems underneath specified siteItem or null if siteItem is a page/leaf-node 
     */
    List<SiteItem> getSiteItems(SiteItem siteItem);

    /**
     * Method to let you specify which subtree of the current branch specified by siteItem to return.
     * Returned items will have parentLevel + 1
     * if siteItem is null and parentLevel is greater than 0 null will be returned.
     * 
     * 
     * @param siteItem - a siteItem or null to get items in the root
     * @param parentLevel - level of parent item for items returned
     * @return - list of siteItems in siteItem's branch above minLevel or null if (minLevel - siteItem's level) > 1 or siteItem's level + 1 is minLevel and siteItem is a page/leaf-node
     */
    List<SiteItem> getSiteItems(SiteItem siteItem, int parentLevel);

    /**
     * Get the parent of the specified SiteItem or null if specified SiteItem is the root or its parent can't be determined
     * 
     * @param siteItem
     * @return
     */
    SiteItem getParentSiteItem(SiteItem siteItem);

    SiteTrail getTrail(SiteItem siteItem);

}
