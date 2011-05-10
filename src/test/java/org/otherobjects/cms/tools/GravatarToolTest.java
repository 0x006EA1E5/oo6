package org.otherobjects.cms.tools;

import junit.framework.TestCase;

public class GravatarToolTest extends TestCase
{
    public void testGetUrl()
    {
        GravatarTool gu = new GravatarTool();
        
        String r2r = gu.getUrlForEmail("rich.aston@othermedia.com",50,null);
        String r2a = "http://www.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg?s=50";
        assertEquals(r2a, r2r);
        
        String r3r = gu.getUrlForEmail("rich.aston@othermedia.com",30,"http://test.com/default.jpg");
        String r3a = "http://www.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg?s=30&d=http%3A%2F%2Ftest.com%2Fdefault.jpg";
        assertEquals(r3a, r3r);
        
        String r4r = gu.getUrlForEmail("rich.aston@othermedia.com", 50, null, true);
        String r4a = "https://secure.gravatar.com/avatar/f3393615ce87349a39acb05efb134314.jpg?s=50";
        assertEquals(r4r, r4a);
    }
}
