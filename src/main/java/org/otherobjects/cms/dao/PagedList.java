package org.otherobjects.cms.dao;

import java.util.List;

/**
 * Simple results wrapper to allow for paged views of large lists of items. Implementations need to make sure
 * that by using the iterator methods you'll only get the items you should be able to get for the page settings your working with.
 *  
 * @author joerg
 *
 * @param <T>
 */
public interface PagedList<T> extends Iterable<T>
{
    /** Max no. of items to display on a page. */
    int getPageSize();

    /** How many pages are needed to show all items provided the given page size. */
    int getPageCount();

    /** Page currently shown. Index is 1 based. */
    int getCurrentPage();

    /** Is  current page last page? */
    boolean isLastPage();

    /** Is current page first page? */
    boolean isFirstPage();

    /** Is there another page after the current one? */
    boolean hasNextPage();

    /** Number of total items in the underlying result list. */
    int getItemTotal();

    /** List backing this PagedList. */
    List<T> getItems();

}
