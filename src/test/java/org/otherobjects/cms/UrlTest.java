package org.otherobjects.cms;

import junit.framework.TestCase;

public class UrlTest extends TestCase
{

    public void testWrongLink()
    {
        try
        {
            Url url = new Url("some/relative/lin/without/leading/slash");
            fail();
        }
        catch (Exception e)
        {

        }
    }

    public void testParseUrl()
    {
        Url url = new Url("http://server/path");
        assertEquals(true, url.isAbsolute());
        //Url.parseUrl(url);

        assertEquals("http", url.getScheme());
        assertEquals(false, url.isSsl());
        assertEquals("server", url.getServerName());
        assertEquals("/path", url.getPath());
        System.out.println(url.getPort());

        Url url1 = new Url("http://server.de:456/path");
        //Url.parseUrl(url1);
        assertEquals(456, url1.getPort());

        Url url2 = new Url("https://server.de:443/path");
        //Url.parseUrl(url2);
        assertEquals(443, url2.getPort());
        assertEquals(true, url2.isSsl());

    }

    public void testNonModifieable()
    {
        Url url = new Url("/test", false);

        try
        {
            url.setPath("/modified");
            fail();
        }
        catch (OtherObjectsException e)
        {

        }

        assertTrue(url.toString().endsWith("/test"));
    }
}
