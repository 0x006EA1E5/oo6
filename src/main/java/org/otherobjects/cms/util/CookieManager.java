package org.otherobjects.cms.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieManager
{
    private Cookie[] cookies;

    protected CookieManager()
    {
    }

    public CookieManager(HttpServletRequest request)
    {
        this.cookies = request.getCookies();
    }

    public Cookie getCookie(String name)
    {
        if (getCookies() == null)
            return null;

        for (Cookie c : getCookies())
        {
            if (c.getName().equals(name))
                return c;
        }
        return null;
    }

    protected Cookie[] getCookies()
    {
        return this.cookies;
    }

    protected void setCookies(Cookie[] cookies)
    {
        this.cookies = cookies;
    }
}
