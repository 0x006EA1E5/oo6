package org.otherobjects.cms.io;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class OoResourceLoaderTest extends AbstractDependencyInjectionSpringContextTests
{
    OoResourceLoader ooResourceLoader;

    DummyResourceBean bean;

    public void setBean(DummyResourceBean bean)
    {
        this.bean = bean;
    }

    public void setOoResourceLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

    @Override
    protected String[] getConfigLocations()
    {
        return new String[]{"classpath:org/otherobjects/cms/io/OoResourceLoaderTest-context.xml"};
    }

    public void testBeanSet()
    {
        assertTrue(bean.getTestfile() != null);
        assertTrue(bean.getTestfile().getMetaData() != null);
    }

    public void testResultingResources() throws IOException
    {
        OoResource test1 = ooResourceLoader.getResource("/core/config/otherobjects.properties");

        assertTrue(test1.exists());
        assertTrue(test1.toString().contains("otherobjects.resources/config/otherobjects.properties"));

        try
        {
            test1.getOutputStream();
            fail();
        }
        catch (IOException e)
        {

        }

        OoResource test2 = ooResourceLoader.getResource("/site/config/site.properties");

        assertTrue(test2.toString().contains("site.resources/config/site.properties"));

        try
        {
            test2.getOutputStream();
            fail();
        }
        catch (IOException e)
        {

        }

        OoResource test3 = ooResourceLoader.getResource("/static/test");

        assertTrue(test3.toString().contains("site.resources/static/test"));

        try
        {
            test3.getOutputStream();
            fail();
        }
        catch (IOException e)
        {

        }

        OoResource test4 = ooResourceLoader.getResource("/data/iotest.properties");
        //        System.out.println(new File(".").getAbsolutePath());
        //        System.out.println(test4.toString());
        //System.out.println(test4.getURI());
        assertTrue(test4.toString().contains("src/test/java/org/otherobjects/cms/io/iotest.properties"));

        OutputStream os = null;
        try
        {
            os = test4.getOutputStream();
            IOUtils.write("site.data.dir.path=./src/test/java/org/otherobjects/cms/io", os);

        }
        catch (IOException e)
        {
            fail();
        }
        finally
        {
            IOUtils.closeQuietly(os);
        }

    }

    public void testMetaDataSet() throws IOException
    {
        OoResource test1 = ooResourceLoader.getResource("classpath:org/otherobjects/cms/io/test.js");

        assertNotNull(test1.getMetaData());

        assertEquals(new Long(5), test1.getMetaData().getUserdId());
    }
}
