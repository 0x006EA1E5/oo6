package org.otherobjects.cms.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class OoResourceMetaDataHelperTest extends TestCase
{
    public void testReadMetaData() throws Exception
    {
        Resource testResource = new ClassPathResource("org/otherobjects/cms/io/test.ftl");
        InputStream resourceAsStream = getClass().getResourceAsStream("org/otherobjects/cms/io/test.ftl");

        OoResource resource = new DefaultOoResource(testResource, "classpath:org/otherobjects/cms/io/test.ftl", null, false);

        assertEquals(OoResourceMetaDataTest.TEST_JSON_METADATA.trim(), new OoResourceMetaDataHelper().readMetaDataString(resource).trim());

        testResource = new ClassPathResource("org/otherobjects/cms/io/test.js");
        resource = new DefaultOoResource(testResource, "classpath:org/otherobjects/cms/io/test.js", null, false);

        assertEquals(OoResourceMetaDataTest.TEST_JSON_METADATA.trim(), new OoResourceMetaDataHelper().readMetaDataString(resource).trim());

    }

    public void testWriteMetaData() throws Exception
    {
        File target = null;
        try
        {
            String originalData = "original data in line 1";
            target = new File("./src/test/java/org/otherobjects/cms/io/write-test.js");
            target.createNewFile();

            OutputStreamWriter osw = null;
            try
            {
                osw = new OutputStreamWriter(new FileOutputStream(target), "UTF-8");
                IOUtils.write(originalData, osw);
            }
            finally
            {
                IOUtils.closeQuietly(osw);
            }

            OoResource resource = new DefaultOoResource(new FileSystemResource(target), "./src/test/java/org/otherobjects/cms/io/write-test.js", null, true);

            new OoResourceMetaDataHelper().writeMetaDataString(resource, OoResourceMetaDataTest.TEST_JSON_METADATA);
            assertTrue(FileUtils.readFileToString(target).contains(OoResourceMetaDataTest.TEST_JSON_METADATA));
        }
        finally
        {
            FileUtils.deleteQuietly(target);
        }

    }
}
