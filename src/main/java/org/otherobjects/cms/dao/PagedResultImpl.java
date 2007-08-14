package org.otherobjects.cms.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.springframework.util.Assert;

/**
 * 
 * @author joerg
 *
 * @param <T>
 */
public class PagedResultImpl<T> implements PagedResult<T> {

	protected int pageSize;
	protected int itemTotal;
	protected int currentPage;
	protected int pageCount;
	protected List<T> items;
	protected Iterator<T> iterator;
	protected boolean sliceList;
	
	/**
	 * 
	 * @param pageSize - number of items per page
	 * @param itemTotal - total number of items in result (not only this page)
	 * @param currentPage - page to display (with 1 based index)
	 * @param items - list of items
	 * @param sliceList - set to true if this list contains the whole result rather than just the items for the current page
	 * @param comparator - if not null this is used to sort items before setting the iterator
	 */
	public PagedResultImpl(int pageSize, int itemTotal, int currentPage, List<T> items, boolean sliceList, Comparator comparator)
	{
		this.pageSize = pageSize;
		this.itemTotal = itemTotal;
		this.currentPage = currentPage;
		
		this.sliceList = sliceList;
		
		this.pageCount = calcPageCount();
		
		if(items == null || items.size() == 0) // some null result safety
		{
			this.items = (items == null) ? new ArrayList<T>() : items;
			this.iterator = this.items.iterator();
			this.pageCount = 0;
			this.currentPage = 0;
		}
		else if(!sliceList)
		{
			Assert.isTrue(items.size() <= pageSize, "The list that was passed in must have a size <= pageSize if sliceList is false");
			this.items = items;
			if(comparator != null)
				Collections.sort(this.items, comparator);
			this.iterator = items.iterator();
		}
		else
		{
			int startIndex = pageSize * (currentPage - 1); // 0 for currentPage = 1, 10 for currentPage=1 and pageSize=10
			int endIndex = startIndex + pageSize;
			endIndex = (endIndex >= items.size()) ? items.size() : endIndex; // if there are not enough items left, set it to last index + 1
			
			if(comparator != null)
			{
				Collections.sort(items, comparator);
			}
			this.items = Collections.unmodifiableList(items.subList(startIndex, endIndex)); // subList is exclusive of endIndex hence no IndexOutOfBounds
			
			this.iterator = this.items.iterator();
		}
	}
	
	/**
	 * 
	 * @param pageSize - number of items per page
	 * @param itemTotal - total number of items in result (not only this page)
	 * @param currentPage - page to display (with 1 based index)
	 * @param items - list of items
	 * @param sliceList - set to true if this list contains the whole result rather than just the items for the current page
	 */
	public PagedResultImpl(int pageSize, int itemTotal, int currentPage, List<T> items, boolean sliceList)
	{
		this(pageSize, itemTotal, currentPage, items, sliceList, null);
	}
	
	/**
	 * This constructor calculates the total number of items from using items.size and sets sliceList to true
	 * @param pageSize - number of items per page
	 * @param currentPage - page to display (with 1 based index)
	 * @param items - list of items
	 */
	public PagedResultImpl(int pageSize, int currentPage, List<T> items)
	{
		this(pageSize, (items == null) ? 0 : items.size(), currentPage, items, true);
	}
	
	/**
	 * This constructor calculates the total number of items from using items.size and sets sliceList to true
	 * @param pageSize - number of items per page
	 * @param currentPage - page to display (with 1 based index)
	 * @param items - list of items
	 * @param comparator - if not null this is used to sort items before setting the iterator
	 */
	public PagedResultImpl(int pageSize, int currentPage, List<T> items, Comparator comparator)
	{
		this(pageSize, (items == null) ? 0 : items.size(), currentPage, items, true, comparator);
	}
	
	private int calcPageCount() {
		int result = itemTotal / pageSize;
		boolean remainder = itemTotal % pageSize != 0;
		return remainder ? ++result : result;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getItemTotal() {
		return itemTotal;
		
	}

	public int getPageCount() {
		return pageCount;
	}

	public int getPageSize() {
		return pageSize;
	}

	public boolean hasNextPage() {
		return currentPage < pageCount;
	}

	public boolean isFirstPage() {
		return currentPage == 1 || currentPage == 0; // the latter is the case if the underlying list is null or empty
	}

	public boolean isLastPage() {
		return currentPage == pageCount;
	}

	public Iterator<T> iterator() {

		return iterator;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public T next() {
		return iterator.next();
	}

	public void remove() {
		throw new UnsupportedOperationException("PageResult iterators are not supposed to be modified");
	}

}
