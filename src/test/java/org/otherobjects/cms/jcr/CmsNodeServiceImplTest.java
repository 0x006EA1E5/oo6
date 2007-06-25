package org.otherobjects.cms.jcr;

import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.test.BaseJcrTestCase;

public class CmsNodeServiceImplTest extends BaseJcrTestCase
{
    public void testGetNode()
    {
        CmsNodeService cmsNodeService = (CmsNodeService) getApplicationContext().getBean("cmsNodeService");
        CmsNode node = cmsNodeService.getNode("/site/about/about-us.html");
        assertNotNull(node);
        assertEquals("About us", node.get("title"));
    }

}
