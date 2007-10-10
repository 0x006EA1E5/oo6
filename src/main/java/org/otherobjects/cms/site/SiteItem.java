package org.otherobjects.cms.site;

public interface SiteItem {
	/**
	 * The string to put in an anchor tags href attribute to link to this SiteItem. Might be relative or absolute.
	 * 
	 * @return - href string
	 */
	public String getHref();
	
	/**
	 * Title of this SiteItem. Used in a pages html -> head -> title text, in a breadcrumb or as the caption of a navigational link
	 * @return
	 */
	public String getTitle();
	
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
