package org.otherobjects.cms.workbench;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class ContentServiceImplTest extends BaseJcrTestCase
{
    // Injected by Spring
    private DynaNodeDao dynaNodeDao;
    private ContentServiceImpl contentService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        contentService = new ContentServiceImpl();
        contentService.setDynaNodeDao(dynaNodeDao);
    }

    public void testTestDependencies()
    {
        // Create item in site root
        assertNotNull(dynaNodeDao);
        assertNotNull(contentService);
    }

    public DynaNodeDao getDynaNodeDao()
    {
        return dynaNodeDao;
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    public void testCreateItem()
    {
    	adminLogin();
        DynaNode siteRoot = dynaNodeDao.getByPath("/site/");
        DynaNode new1 = contentService.createItem(siteRoot.getId(), "Article");
        assertEquals("Untitled 1", new1.getLabel()); 
        DynaNode new2 = contentService.createItem(siteRoot.getId(), "Article");
        assertEquals("Untitled 2", new2.getLabel());
        logout();
    }
}
