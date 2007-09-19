package org.otherobjects.cms.workbench;

import java.util.List;

import org.otherobjects.cms.beans.BaseDynaNodeTest;
import org.otherobjects.cms.model.DynaNode;

public class NavigatorServiceImplTest extends BaseDynaNodeTest
{
    private NavigatorServiceImpl navigatorService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.navigatorService = new NavigatorServiceImpl();
        this.navigatorService.setDynaNodeDao(this.dynaNodeDao);
    }

    public void testTestDependencies()
    {
        // Create item in site root
        assertNotNull(this.dynaNodeDao);
        assertNotNull(this.navigatorService);
    }

    public void testGetRootItem()
    {
        DynaNode dynaNode = this.dynaNodeDao.getByPath("/site");
        assertNotNull(dynaNode);
    }

    public void testGetSubContainers()
    {
        DynaNode root = this.dynaNodeDao.getByPath("/site");
        List<WorkbenchItem> items = this.navigatorService.getSubContainers(root.getId());
        assertTrue(items.size() > 0);
    }

    public void testAddItem()
    {
        adminLogin();
        DynaNode root = this.dynaNodeDao.getByPath("/site");
        WorkbenchItem item = this.navigatorService.addItem(root.getId(), "");
        System.out.println(item.getId() + " " + item.getLinkPath());
        assertTrue(item instanceof DynaNode);
        DynaNode dynaNode = (DynaNode) item;
        assertTrue(dynaNode.getPath().startsWith("/site"));
        logout();
    }

    public void testGetItemContents()
    {
        DynaNode about = this.dynaNodeDao.getByPath("/site/about");
        assertTrue(about instanceof DynaNode);

        List<WorkbenchItem> items = this.navigatorService.getItemContents(about.getId());

        assertTrue(items.size() > 0);

        DynaNode article = null;
        for (WorkbenchItem wi : items)
        {
            assertTrue(wi instanceof DynaNode);
            DynaNode wiNode = (DynaNode) wi;
            if (wiNode.getOoType().equals("Article"))
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
        DynaNode root = this.dynaNodeDao.getByPath("/site");
        WorkbenchItem item = this.navigatorService.addItem(root.getId(), "");

        DynaNode newNode = (DynaNode) item;
        this.navigatorService.renameItem(newNode.getId(), "asdasdasdasdasd");

        System.out.println(newNode.getLabel());

        //assertTrue(newNode.getLabel().equals("asdasdasdasdasd"));
        logout();

    }

    public void testMoveItem()
    {
        adminLogin();
        DynaNode root = this.dynaNodeDao.getByPath("/site");

        WorkbenchItem item = this.navigatorService.addItem(root.getId(), "");

        List<WorkbenchItem> wis = this.navigatorService.getSubContainers(root.getId());

        WorkbenchItem lastItem = wis.get(wis.size() - 1);

        assertEquals(lastItem.getLabel(), item.getLabel());

        WorkbenchItem firstItem = wis.get(0);

        this.navigatorService.moveItem(lastItem.getId(), firstItem.getId(), "before");

        //    	List<WorkbenchItem> wisReordered = navigatorService.getSubContainers(root.getId());
        //    	
        //    	assertEquals(item.getLabel(), wisReordered.get(0).getLabel());
        logout();
    }
}
