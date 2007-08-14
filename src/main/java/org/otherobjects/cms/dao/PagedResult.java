package org.otherobjects.cms.dao;

import java.util.Iterator;

/**
 * Simple results wrapper top allow for paged views of large lists of items. Implementations need to make sure
 * that by using the iterator methods you'll only get the items you should be able to get for the page settings your working with.
 *  
 * @author joerg
 *
 * @param <T>
 */
public interface PagedResult<T> extends Iterable<T>, Iterator<T> {
	
	/** Max no. of items to display on a page*/
	public int getPageSize();
	
	/** How many pages are needed to show all items provided the given page size */
	public int getPageCount();
	
	/** Page currently shown. Index is 1 based */
	public int getCurrentPage();
	
	/** Is  current page last page? */
	public boolean isLastPage();
	
	/** Is current page first page*/
	public boolean isFirstPage();
	
	/** Is there another page after the current one */
	public boolean hasNextPage();
	
	/** Number of total items in the underlying result list */
	public int getItemTotal();
	
}
