package org.otherobjects.cms.site;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.controllers.handlers.TestPage;
import org.otherobjects.cms.model.SiteFolder;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class SiteTrailImplTest extends BaseJcrTestCase
{
    private SiteTrail siteTrail;

    @Override
    protected void onSetUp() throws Exception
    {
        // TODO Auto-generated method stub
        super.onSetUp();
        registerType(TestPage.class);
        registerType(SiteFolder.class);

        List<SiteItem> siteItems = new ArrayList<SiteItem>();

        siteItems.add(new TestPage());
        siteItems.add(new SiteFolder());
        siteItems.add(new SiteFolder());

        ((TestPage) siteItems.get(0)).setLabel("Test Page");
        ((TestPage) siteItems.get(0)).setExtraNavigationLabel("Test Page Nav label");
        ((TestPage) siteItems.get(0)).setJcrPath("/site/about/about.html");
        ((TestPage) siteItems.get(0)).setFolder(false);
        ((TestPage) siteItems.get(0)).setName("Test");

        ((SiteFolder) siteItems.get(1)).setLabel("About");
        ((SiteFolder) siteItems.get(1)).setExtraNavigationLabel("About Nav");
        ((SiteFolder) siteItems.get(1)).setJcrPath("/site/about");

        ((SiteFolder) siteItems.get(2)).setLabel("Site");
        ((SiteFolder) siteItems.get(2)).setExtraNavigationLabel("Site Nav");
        ((SiteFolder) siteItems.get(2)).setJcrPath("/site");

        siteTrail = new SiteTrailImpl(siteItems);
    }

    public void testFullTitle()
    {
        System.out.println(siteTrail.getFullTitle(","));
        assertEquals("Site Nav,About Nav,Test Page Nav label", siteTrail.getFullTitle(","));
    }
}
