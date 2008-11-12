package org.otherobjects.cms.model;

import junit.framework.TestCase;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;

public class SiteFolderTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();

    public void testGetAllAllowedTypes()
    {
        //SiteFolder f = new SiteFolder();
        // FIXME f.getAllAllowedTypes();
    }

    public void testGetCode() throws Exception
    {
        SingletonBeanLocator.registerTestBean("typeService", typeService);
        AnnotationBasedTypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        typeService.registerType(typeDefBuilder.getTypeDef(SiteFolder.class));

        SiteFolder folder = new SiteFolder();
        folder.setLabel("Test");
        assertEquals("Test", folder.getOoLabel());
        assertEquals("test", folder.getCode());
    }
}
