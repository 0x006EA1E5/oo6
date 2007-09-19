package org.otherobjects.cms.workbench;

import org.otherobjects.cms.beans.BaseDynaNodeTest;
import org.otherobjects.cms.model.DynaNode;

public class ContentServiceImplTest extends BaseDynaNodeTest
{
    // Injected by Spring
    private ContentServiceImpl contentService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.contentService = new ContentServiceImpl();
        this.contentService.setDynaNodeDao(this.dynaNodeDao);
    }

    public void testTestDependencies()
    {
        // Create item in site root
        assertNotNull(this.dynaNodeDao);
        assertNotNull(this.contentService);
    }

    public void testCreateItem()
    {
        adminLogin();
        DynaNode siteRoot = this.dynaNodeDao.getByPath("/site/");
        DynaNode new1 = this.contentService.createItem(siteRoot.getId(), "Article");
        assertEquals("Untitled 1", new1.getLabel());
        DynaNode new2 = this.contentService.createItem(siteRoot.getId(), "Article");
        assertEquals("Untitled 2", new2.getLabel());
        logout();
    }
}
