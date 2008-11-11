package org.otherobjects.cms.tools;

import junit.framework.TestCase;

public class GravatarUtilsTest extends TestCase
{
    public void testGetUrl()
    {
        GravatarUtils gu = new GravatarUtils();
        String r1r = gu.getUrl("rich.aston@othermedia.com");
        String r1a = "http://www.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg";
        assertEquals(r1a, r1r);
        
        String r2r = gu.getUrl("rich.aston@othermedia.com",50);
        String r2a = "http://www.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg?s=50";
        assertEquals(r2a, r2r);
        
        String r3r = gu.getUrl("rich.aston@othermedia.com",30,"http://test.com/default.jpg");
        String r3a = "http://www.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg?s=30&d=http%3A%2F%2Ftest.com%2Fdefault.jpg";
        assertEquals(r3a, r3r);
    }

}
