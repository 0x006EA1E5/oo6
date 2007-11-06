package org.otherobjects.cms.workbench;

import java.util.List;

import org.apache.jackrabbit.ocm.manager.objectconverter.impl.SimpleFieldsHelper;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.test.BaseJcrTestCase;
import org.otherobjects.cms.types.AbstractTypeService;
import org.otherobjects.cms.util.LoggerUtils;

import ch.qos.logback.classic.Level;

public class NavigatorServiceImplTest extends BaseJcrTestCase
{
    private NavigatorServiceImpl navigatorService;

    @Override
    protected void onSetUp() throws Exception
    {
        LoggerUtils.setLoggerLevel(SimpleFieldsHelper.class, Level.ERROR);
        LoggerUtils.setLoggerLevel(AbstractTypeService.class, Level.ERROR);
        super.onSetUp();
        this.navigatorService = new NavigatorServiceImpl();
        this.navigatorService.setUniversalJcrDao(this.universalJcrDao);
    }

    public void testGetRootItem()
    {
        BaseNode baseNode = this.universalJcrDao.getByPath("/site");
        assertNotNull(baseNode);
    }

    public void testGetSubContainers()
    {
        BaseNode root = this.universalJcrDao.getByPath("/site");
        List<WorkbenchItem> items = this.navigatorService.getSubContainers(root.getId());
        assertTrue(items.size() > 0);
    }

    public void testAddItem()
    {
        adminLogin();
        BaseNode root = this.universalJcrDao.getByPath("/site");
        WorkbenchItem item = this.navigatorService.addItem(root.getId(), "");
        System.out.println(item.getId() + " " + item.getLinkPath());
        assertTrue(item instanceof BaseNode);
        BaseNode baseNode = (BaseNode) item;
        assertTrue(baseNode.getPath().startsWith("/site"));
        logout();
    }

    public void testGetItemContents()
    {
        BaseNode about = this.universalJcrDao.getByPath("/site/about");
        assertTrue(about instanceof BaseNode);

        List<WorkbenchItem> items = this.navigatorService.getItemContents(about.getId());

        assertTrue(items.size() > 0);

        BaseNode article = null;
        for (WorkbenchItem wi : items)
        {
            assertTrue(wi instanceof BaseNode);
            BaseNode wiNode = (BaseNode) wi;
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
        BaseNode root = this.universalJcrDao.getByPath("/site");
        WorkbenchItem item = this.navigatorService.addItem(root.getId(), "");

        BaseNode newNode = (BaseNode) item;
        String newName = newNode.getLabel() + " Renamed";
        //FIXME for some reason GenericJcrDaoJackrabbit can't see this node in its saveSimple else branch. Maybe to do with transaction not committed in test?
        //WorkbenchItem renamedItem = this.navigatorService.renameItem(newNode.getId(), newName);

        //assertTrue(renamedItem.getOoLabel().equals(newName));
        logout();
    }

    public void testMoveItem()
    {
        adminLogin();
        BaseNode root = this.universalJcrDao.getByPath("/site");

        WorkbenchItem item = this.navigatorService.addItem(root.getId(), "");

        List<WorkbenchItem> wis = this.navigatorService.getSubContainers(root.getId());

        WorkbenchItem lastItem = wis.get(wis.size() - 1);

        assertEquals(lastItem.getOoLabel(), item.getOoLabel());

        WorkbenchItem firstItem = wis.get(0);

        this.navigatorService.moveItem(lastItem.getId(), firstItem.getId(), "before");

        //    	List<WorkbenchItem> wisReordered = navigatorService.getSubContainers(root.getId());
        //    	
        //    	assertEquals(item.getLabel(), wisReordered.get(0).getLabel());
        logout();
    }
}
