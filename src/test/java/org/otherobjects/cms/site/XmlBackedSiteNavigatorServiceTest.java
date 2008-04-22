//package org.otherobjects.cms.site;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.apache.velocity.app.VelocityEngine;
//import org.springframework.test.AbstractSingleSpringContextTests;
//import org.springframework.ui.velocity.VelocityEngineUtils;
//
//public class XmlBackedSiteNavigatorServiceTest extends AbstractSingleSpringContextTests
//{
//
//    private XmlBackedSiteNavigatorService navService;
//    private VelocityEngine velocityEngine;
//
//    @Override
//    protected String[] getConfigLocations()
//    {
//        return new String[]{"file:src/test/resources/applicationContext-xmlNavigationService.xml", "file:src/test/resources/applicationContext-globalInfo.xml"};
//    }
//
//    @Override
//    protected void onSetUp() throws Exception
//    {
//        super.onSetUp();
//        navService = (XmlBackedSiteNavigatorService) getApplicationContext().getBean("navService");
//        velocityEngine = (VelocityEngine) getApplicationContext().getBean("testVelocityEngine");
//    }
//
//    public void testGetSiteItems()
//    {
//        List<SiteItem> rootItems = navService.getSiteItems(null);
//        assertTrue(rootItems.size() == 3);
//
//        assertFalse(rootItems.get(0).isFolder());
//        assertTrue(rootItems.get(1).isFolder());
//        assertTrue(rootItems.get(2).isFolder());
//
//        assertNull((navService.getSiteItems(rootItems.get(0))));
//
//        List<SiteItem> child3 = navService.getSiteItems(rootItems.get(2));
//        assertTrue(child3.size() == 3);
//
//        List<SiteItem> child33 = navService.getSiteItems(child3.get(2));
//    }
//
//    public void testVelocityooNaviagtionMacro()
//    {
//        Map<String, Object> model = new HashMap<String, Object>();
//
//        SiteItem welcome = navService.getSiteItems(null).get(0);
//        SiteItem products = navService.getSiteItems(null).get(2);
//        SiteItem categories = navService.getSiteItems(products).get(2);
//        SiteItem category1 = navService.getSiteItems(categories).get(0);
//
//        model.put("resourceObject", welcome);
//        model.put("category1", category1);
//        model.put("navigatorService", navService);
//
//        String result = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "MacroCaller.vm", model);
//
//        System.out.println(result);
//
//        //velocityEngine.
//    }
//
//}
