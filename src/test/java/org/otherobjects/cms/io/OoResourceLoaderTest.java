package org.otherobjects.cms.io;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit38.AbstractJUnit38SpringContextTests;

@ContextConfiguration(locations = {"file:./src/test/java/org/otherobjects/cms/io/resource-context.xml"})
public class OoResourceLoaderTest extends AbstractJUnit38SpringContextTests
{
    @Autowired
    private OoResourceLoader ooResourceLoader;

    @Autowired
    private DummyResourceBean bean;

    public void setBean(DummyResourceBean bean)
    {
        this.bean = bean;
    }

    public void testBeanSet()
    {
        assertTrue(bean.getTestfile() != null);
        assertTrue(bean.getTestfile().getMetaData() != null);
    }

    public void testGetResources() throws IOException
    {
        OoResource resource = ooResourceLoader.getResource("/site/templates/blocks/");
        List<OoResource> resources = ooResourceLoader.getResources("/site/templates/blocks/");
        System.err.println(resources);
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
            // Expected exception. Pass.
        }

        OoResource test2 = ooResourceLoader.getResource("/site/config/site.properties");

        // FIXME At the moment site resources are split in 2 locations
        //assertTrue(test2.toString().contains("site.resources/config/site.properties"));

        try
        {
            test2.getOutputStream();
            fail();
        }
        catch (IOException e)
        {
            // Expected exception. Pass.
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
            // Expected exception. Pass.
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

        //assertEquals(new Long(5), test1.getMetaData().getUserdId());
    }
}
