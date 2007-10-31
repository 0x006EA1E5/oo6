package org.otherobjects.cms.site;

import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.Linkable;

public interface SiteItem extends Linkable, CmsNode
{
    /**
     * Label of this SiteItem. Used in a pages html -> head -> title text, in a breadcrumb or as the caption of a navigational link
     * @return
     */
    public String getLabel();

    /**
     * Indicates whether this SiteItem is a Folder (true - container, non-leaf node) or a page (false - leaf-node) 
     * @return
     */
    public boolean isFolder();

    /**
     * Indicates depth of this item in the site structure. Items directly underneath the root are 1
     * @return
     */
    public int getDepth();

}
