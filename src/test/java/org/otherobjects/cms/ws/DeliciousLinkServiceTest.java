package org.otherobjects.cms.ws;

import java.util.List;

import junit.framework.TestCase;
import del.icio.us.beans.Post;

public class DeliciousLinkServiceTest extends TestCase
{
    public void testGetLinks()
    {
        DeliciousLinkService dls = new DeliciousLinkService();
        List<Post> links = dls.getLinks();
        assertNotNull(links);
        assertEquals(2, links.size());
    }
}
