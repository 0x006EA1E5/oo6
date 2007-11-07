package org.otherobjects.cms;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.context.GlobalInfoBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import junit.framework.TestCase;

public class UrlTest extends TestCase
{
    public static final String SERVER_NAME = "some.test.server";
    public static final String CONTEXT_PATH = "/test";

    private void setUpGlobalInfo(String serverName, String contextPath, String defaultPort, String defaultSecurePort) throws Exception
    {
        SimpleNamingContextBuilder simpleNamingContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        String _serverName = StringUtils.defaultString(serverName, "127.0.0.1");
        String _contextPath = StringUtils.defaultString(contextPath, "/testglobal");
        String _defaultPort = StringUtils.defaultString(defaultPort, "8080");
        String _defaultSecurePort = StringUtils.defaultString(defaultSecurePort, "8443");

        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_SERVER_NAME_PATH, _serverName);
        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_CONTEXT_PATH_PATH, _contextPath);
        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_DEFAULT_PORT_PATH, _defaultPort);
        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_DEFAULT_SECURE_PORT_PATH, _defaultSecurePort);

        GlobalInfoBean gi = new GlobalInfoBean();
        gi.setJndiTemplate(new JndiTemplate());
        Resource[] resource = new Resource[]{new ClassPathResource("org/otherobjects/cms/context/globalInfo.properties")};
        gi.setPropertyResources(resource);

        gi.afterPropertiesSet();
    }

    /**
     * setup context so that RequestContextUtils used in Url have something to work on
     * 
     */
    private void setupMockOngoingRequest(boolean secure, String contextPath, int port)
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.setContextPath(contextPath);
        req.setServerPort(port);
        req.setServerName(SERVER_NAME);
        req.setScheme(secure ? "https" : "http");
        req.setSecure(secure);

        ServletRequestAttributes reqatt = new ServletRequestAttributes(req);
        MockServletContext ctx = new MockServletContext();

        StaticWebApplicationContext appCtx = new StaticWebApplicationContext();
        appCtx.setServletContext(ctx);

        reqatt.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, appCtx, RequestAttributes.SCOPE_GLOBAL_SESSION);

        RequestContextHolder.setRequestAttributes(reqatt);
    }

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

    public void testGetDepth()
    {
        Url url = new Url("http://some.server");
        System.out.println(url.getDepth());
    }

    public void testGetAbsoluteLink() throws Exception
    {
        setupMockOngoingRequest(false, CONTEXT_PATH, 80);

        Url url1 = new Url("/some.html");
        System.out.println(url1.getAbsoluteLink());
        assertEquals("http://" + SERVER_NAME + CONTEXT_PATH + "/some.html", url1.getAbsoluteLink());

        setupMockOngoingRequest(false, CONTEXT_PATH, 8080);

        Url url2 = new Url("/some.html");
        System.out.println(url2.getAbsoluteLink());
        assertEquals("http://" + SERVER_NAME + ":8080" + CONTEXT_PATH + "/some.html", url2.getAbsoluteLink());

        setupMockOngoingRequest(false, CONTEXT_PATH, 8080);
        setUpGlobalInfo(null, null, null, "7531");
        Url url3 = new Url("/some.html");
        url3.setSsl(true);
        System.out.println(url3.getAbsoluteLink());
        assertEquals("https://" + SERVER_NAME + ":7531" + CONTEXT_PATH + "/some.html", url3.getAbsoluteLink());

        RequestContextHolder.setRequestAttributes(null);
        System.out.println(url3.getAbsoluteLink());

        setupMockOngoingRequest(true, CONTEXT_PATH, 443);
        Url url4 = new Url("/some.html");
        url4.setSsl(true);
        System.out.println(url4.toString());
        assertEquals(CONTEXT_PATH + "/some.html", url4.toString());

        setupMockOngoingRequest(false, CONTEXT_PATH, 80);
        System.out.println(url4.toString());
        assertEquals("https://" + SERVER_NAME + ":7531" + CONTEXT_PATH + "/some.html", url4.toString());
    }
}
