package org.otherobjects.cms.context;

import junit.framework.TestCase;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jndi.JndiTemplate;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;

public class GlobalInfoTest extends TestCase
{

    public void testInitilisation() throws Exception
    {
        GlobalInfoBean gi = new GlobalInfoBean();
        gi.setJndiTemplate(new JndiTemplate());
        Resource[] resource = new Resource[]{new ClassPathResource("org/otherobjects/cms/context/globalInfo.properties")};
        gi.setPropertyResources(resource);

        gi.afterPropertiesSet();
        System.out.println(gi.listProperties());
    }

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        SimpleNamingContextBuilder simpleNamingContextBuilder = SimpleNamingContextBuilder.emptyActivatedContextBuilder();
        String serverName = "127.0.0.1";
        String contextPath = "test";
        String defaultPort = "80";
        String defaultSecurePort = "443";

        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_SERVER_NAME_PATH, serverName);
        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_CONTEXT_PATH_PATH, contextPath);
        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_DEFAULT_PORT_PATH, defaultPort);
        simpleNamingContextBuilder.bind("java:comp/env/" + GlobalInfoBean.JNDI_DEFAULT_SECURE_PORT_PATH, defaultSecurePort);
    }

}
