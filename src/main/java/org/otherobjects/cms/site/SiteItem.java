package org.otherobjects.cms.site;

import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.Linkable;

public interface SiteItem extends Linkable, CmsNode
{
    /**
     * Label of this SiteItem. Used in a pages html -> head -> title text, in a breadcrumb or as the caption of a navigational link
     * @return
     */
    String getLabel();

    /**
     * Label to use in dynamically generated navigation. Should default to label but maybe overridden (i.e. to enable more concise navigation trees)
     * @return
     */
    String getNavigationLabel();

    /**
     * Indicates whether this SiteItem is a Folder (true - container, non-leaf node) or a page (false - leaf-node) 
     * @return
     */
    boolean isFolder();

    /**
     * Indicates whether Item should be shown in any dynamically generated navigation or not
     * @return
     */
    boolean isInMenu();

    /**
     * Indicates depth of this item in the site structure. Items directly underneath the root are 1
     * @return
     */
    int getDepth();

}
