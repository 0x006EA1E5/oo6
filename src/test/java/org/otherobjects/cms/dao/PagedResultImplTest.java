package org.otherobjects.cms.dao;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class PagedResultImplTest extends TestCase {
	
	private List<String> testList;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testList = new ArrayList<String>();
		testList.add("Item1");
		testList.add("Item2");
		testList.add("Item3");
		testList.add("Item4");
		testList.add("Item5");
	}
	
	public void testPageCount()
	{
		PagedResultImpl<String> pr = new PagedResultImpl<String>(2,1,testList);
		assertTrue(pr.getPageCount() == 3);
		
		PagedResultImpl<String> pr2 = new PagedResultImpl<String>(4,1,testList);
		assertTrue(pr2.getPageCount() == 2);
		
		PagedResultImpl<String> pr3 = new PagedResultImpl<String>(5,1,testList);
		assertTrue(pr3.getPageCount() == 1);
		
		PagedResultImpl<String> pr4 = new PagedResultImpl<String>(10,1,testList);
		assertTrue(pr4.getPageCount() == 1);
		
		PagedResultImpl<String> pr5 = new PagedResultImpl<String>(10,1,new ArrayList<String>());
		assertTrue(pr5.getPageCount() == 0);
		
	}
	
	public void testSlicing()
	{
		PagedResultImpl<String> pr = new PagedResultImpl<String>(4,2,testList);
		assertEquals("Item5", pr.next());
		assertEquals(false, pr.hasNext());
	}
	
	public void testNoSlice()
	{
		PagedResultImpl<String> pr = new PagedResultImpl<String>(5,10,2,testList,false);
		assertEquals("Item1", pr.next());
		assertEquals(2, pr.getPageCount());
	}
	
	public void testPageCalcs()
	{
		PagedResultImpl<String> pr = new PagedResultImpl<String>(4,2,testList);
		assertEquals(2, pr.getPageCount());
		assertEquals(false, pr.isFirstPage());
		assertEquals(false, pr.hasNextPage());
		assertEquals(true, pr.isLastPage());
		
		PagedResultImpl<String> pr2= new PagedResultImpl<String>(4,1,testList);
		assertEquals(2, pr2.getPageCount());
		assertEquals(true, pr2.isFirstPage());
		assertEquals(true, pr2.hasNextPage());
		assertEquals(false, pr2.isLastPage());
		
		PagedResultImpl<String> pr3 = new PagedResultImpl<String>(10,1,new ArrayList<String>());
		assertEquals(0, pr3.getPageCount());
		assertEquals(true, pr3.isFirstPage());
		assertEquals(false, pr3.hasNextPage());
		assertEquals(true, pr3.isLastPage());
		
		PagedResultImpl<String> pr4 = new PagedResultImpl<String>(10,1,null);
		assertEquals(0, pr4.getPageCount());
		assertEquals(true, pr4.isFirstPage());
		assertEquals(false, pr4.hasNextPage());
		assertEquals(true, pr4.isLastPage());
	}
	
	public void testNoIndexOutOfBounds()
	{
		PagedResultImpl<String> pr = new PagedResultImpl<String>(4,1,testList);
		System.out.println("Page1:");
		for(String s: pr)
		{
			System.out.println(s);
		}
		
		System.out.println("Page2:");
		PagedResultImpl<String> pr2 = new PagedResultImpl<String>(4,2,testList);
		for(String s: pr2)
		{
			System.out.println(s);
		}
	}
	
}
