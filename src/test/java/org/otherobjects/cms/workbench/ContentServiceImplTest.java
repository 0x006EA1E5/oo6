package org.otherobjects.cms.workbench;

import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class ContentServiceImplTest extends BaseJcrTestCase
{
    private ContentServiceImpl contentService;

    @Override
    protected void onSetUp() throws Exception
    {
        super.onSetUp();
        this.contentService = new ContentServiceImpl();
        this.contentService.setDaoService(daoService);
        this.contentService.setUniversalJcrDao(universalJcrDao);
        registerType(TestArticle.class);
    }

    public void testCreateItem()
    {
        adminLogin();
        SiteFolder siteRoot = (SiteFolder) universalJcrDao.getByPath("/site/");
        TestArticle new1 = (TestArticle) contentService.createItem(siteRoot.getId(), TestArticle.class.getName());
        assertEquals("Untitled 1", new1.getLabel());
        TestArticle new2 = (TestArticle) contentService.createItem(siteRoot.getId(), TestArticle.class.getName());
        assertEquals("Untitled 2", new2.getLabel());
        logout();
    }
}
