package org.otherobjects.cms.workbench;

import java.util.List;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class NavigatorServiceImplTest extends BaseJcrTestCase
{
	private DynaNodeDao dynaNodeDao;
    private NavigatorServiceImpl navigatorService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        navigatorService = new NavigatorServiceImpl();
        navigatorService.setDynaNodeDao(dynaNodeDao);
    }
    
    public void testTestDependencies()
    {
        // Create item in site root
        assertNotNull(dynaNodeDao);
        assertNotNull(navigatorService);
    }
    
    public void testGetRootItem()
    {
    	DynaNode dynaNode = dynaNodeDao.getByPath("/site");
    	assertNotNull(dynaNode);
    }
    
    public void testGetSubContainers()
    {
    	DynaNode root = dynaNodeDao.getByPath("/site");
    	List<WorkbenchItem> items= navigatorService.getSubContainers(root.getId());
    	assertTrue(items.size() > 0);
    }
    
    public void testAddItem()
    {
    	adminLogin();
    	DynaNode root = dynaNodeDao.getByPath("/site");
    	WorkbenchItem item = navigatorService.addItem(root.getId(), "");
    	System.out.println(item.getId() + " " + item.getLinkPath());
    	assertTrue(item instanceof DynaNode);
    	DynaNode dynaNode = (DynaNode) item;
    	assertTrue(dynaNode.getPath().startsWith("/site"));
    	logout();
    }
    
    public void testGetItemContents()
    {
    	DynaNode about = dynaNodeDao.getByPath("/site/about");
    	assertTrue(about instanceof DynaNode);
    	
    	List<WorkbenchItem> items = navigatorService.getItemContents(about.getId());
    	
    	assertTrue(items.size() > 0);
    	
    	DynaNode article = null;
    	for(WorkbenchItem wi: items)
    	{
    		assertTrue(wi instanceof DynaNode);
    		DynaNode wiNode = (DynaNode) wi;
    		if(wiNode.getOoType().equals("Article"))
    		{
    			article = wiNode;
    			break;
    		}
    	}
    	
    	assertNotNull(article);
    }
    
    public void testRenameItem()
    {
    	adminLogin();
    	DynaNode root = dynaNodeDao.getByPath("/site");
    	WorkbenchItem item = navigatorService.addItem(root.getId(), "");
    	
    	DynaNode newNode = (DynaNode) item;
    	navigatorService.renameItem(newNode.getId(), "asdasdasdasdasd");
    	
    	System.out.println(newNode.getLabel());
    	
    	//assertTrue(newNode.getLabel().equals("asdasdasdasdasd"));
    	logout();
    	
    }
    
    public void testMoveItem()
    {
    	adminLogin();
    	DynaNode root = dynaNodeDao.getByPath("/site");
    	
    	WorkbenchItem item = navigatorService.addItem(root.getId(), "");
    	
    	List<WorkbenchItem> wis = navigatorService.getSubContainers(root.getId());
    	
    	WorkbenchItem lastItem = wis.get(wis.size() -1);
    	
    	assertEquals(lastItem.getLabel(), item.getLabel());
    	
    	WorkbenchItem firstItem = wis.get(0);
    	
    	navigatorService.moveItem(lastItem.getId(), firstItem.getId(), "before");
    	
//    	List<WorkbenchItem> wisReordered = navigatorService.getSubContainers(root.getId());
//    	
//    	assertEquals(item.getLabel(), wisReordered.get(0).getLabel());
    	logout();
    }

	public void setDynaNodeDao(DynaNodeDao dynaNodeDao) {
		this.dynaNodeDao = dynaNodeDao;
	}
    
    
}
