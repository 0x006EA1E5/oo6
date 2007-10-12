package org.otherobjects.cms.dao;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class PagedListImplTest extends TestCase {
	
	private List<String> testList;
	private List<SampleBean> testList2;
	

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		testList = new ArrayList<String>();
		testList.add("Item1");
		testList.add("Item2");
		testList.add("Item3");
		testList.add("Item4");
		testList.add("Item5");
		
		
		testList2 = new ArrayList<SampleBean>();
		testList2.add(new SampleBean("Item2"));
		testList2.add(new SampleBean("Item6"));
		testList2.add(new SampleBean("Item4"));
		testList2.add(new SampleBean("Item1"));
		testList2.add(new SampleBean("Item5"));
		testList2.add(new SampleBean("Item3"));
		
	}
	
	public void testWithComparator()
	{
		PagedListImpl<SampleBean> pr = new PagedListImpl<SampleBean>(3, 2, testList2, new BeanPropertyComparator("testProp"));
		assertEquals("Item4", pr.next().getTestProp());
		assertEquals("Item5", pr.next().getTestProp());
		assertEquals("Item6", pr.next().getTestProp());
		
	}
	
	public void testPageCount()
	{
		PagedListImpl<String> pr = new PagedListImpl<String>(2,1,testList);
		assertTrue(pr.getPageCount() == 3);
		
		PagedListImpl<String> pr2 = new PagedListImpl<String>(4,1,testList);
		assertTrue(pr2.getPageCount() == 2);
		
		PagedListImpl<String> pr3 = new PagedListImpl<String>(5,1,testList);
		assertTrue(pr3.getPageCount() == 1);
		
		PagedListImpl<String> pr4 = new PagedListImpl<String>(10,1,testList);
		assertTrue(pr4.getPageCount() == 1);
		
		PagedListImpl<String> pr5 = new PagedListImpl<String>(10,1,new ArrayList<String>());
		assertTrue(pr5.getPageCount() == 0);
		
	}
	
	public void testSlicing()
	{
		PagedListImpl<String> pr = new PagedListImpl<String>(4,2,testList);
		assertEquals("Item5", pr.next());
		assertEquals(false, pr.hasNext());
	}
	
	public void testNoSlice()
	{
		PagedListImpl<String> pr = new PagedListImpl<String>(5,10,2,testList,false);
		assertEquals("Item1", pr.next());
		assertEquals(2, pr.getPageCount());
	}
	
	public void testPageCalcs()
	{
		PagedListImpl<String> pr = new PagedListImpl<String>(4,2,testList);
		assertEquals(2, pr.getPageCount());
		assertEquals(false, pr.isFirstPage());
		assertEquals(false, pr.hasNextPage());
		assertEquals(true, pr.isLastPage());
		
		PagedListImpl<String> pr2= new PagedListImpl<String>(4,1,testList);
		assertEquals(2, pr2.getPageCount());
		assertEquals(true, pr2.isFirstPage());
		assertEquals(true, pr2.hasNextPage());
		assertEquals(false, pr2.isLastPage());
		
		PagedListImpl<String> pr3 = new PagedListImpl<String>(10,1,new ArrayList<String>());
		assertEquals(0, pr3.getPageCount());
		assertEquals(true, pr3.isFirstPage());
		assertEquals(false, pr3.hasNextPage());
		assertEquals(true, pr3.isLastPage());
		
		PagedListImpl<String> pr4 = new PagedListImpl<String>(10,1,null);
		assertEquals(0, pr4.getPageCount());
		assertEquals(true, pr4.isFirstPage());
		assertEquals(false, pr4.hasNextPage());
		assertEquals(true, pr4.isLastPage());
	}
	
	public void testNoIndexOutOfBounds()
	{
		PagedListImpl<String> pr = new PagedListImpl<String>(4,1,testList);
		System.out.println("Page1:");
		for(String s: pr)
		{
			System.out.println(s);
		}
		
		System.out.println("Page2:");
		PagedListImpl<String> pr2 = new PagedListImpl<String>(4,2,testList);
		for(String s: pr2)
		{
			System.out.println(s);
		}
	}
	
	
}
