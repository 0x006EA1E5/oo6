package org.otherobjects.cms.util;

import javax.servlet.http.Cookie;

import junit.framework.TestCase;

public class CookieManagerTest extends TestCase
{

    public void testGetCookie()
    {

        CookieManager cookieManager = new CookieManager();
        assertNull(cookieManager.getCookie("c1"));

        Cookie[] cookies = new Cookie[]{new Cookie("c1", "one"), new Cookie("c2", "two")};
        cookieManager.setCookies(cookies);
        assertEquals(cookies[0], cookieManager.getCookie("c1"));
        assertEquals(cookies[1], cookieManager.getCookie("c2"));
    }

}
