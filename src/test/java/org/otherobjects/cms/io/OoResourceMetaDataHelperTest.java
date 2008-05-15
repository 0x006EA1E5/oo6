package org.otherobjects.cms.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class OoResourceMetaDataHelperTest extends TestCase
{
    public void testReadMetaData() throws Exception
    {
        Resource testResource = new ClassPathResource("org/otherobjects/cms/io/test.ftl");

        OoResource resource = new DefaultOoResource(testResource, "classpath:org/otherobjects/cms/io/test.ftl", false);

        assertEquals(OoResourceMetaDataTest.TEST_JSON_METADATA, new OoResourceMetaDataHelper().readMetaDataString(resource));

        testResource = new ClassPathResource("org/otherobjects/cms/io/test.js");
        resource = new DefaultOoResource(testResource, "classpath:org/otherobjects/cms/io/test.js", false);

        assertEquals(OoResourceMetaDataTest.TEST_JSON_METADATA, new OoResourceMetaDataHelper().readMetaDataString(resource));

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

            OoResource resource = new DefaultOoResource(new FileSystemResource(target), "./src/test/java/org/otherobjects/cms/io/write-test.js", true);

            new OoResourceMetaDataHelper().writeMetaDataString(resource, OoResourceMetaDataTest.TEST_JSON_METADATA);

            LineIterator li = FileUtils.lineIterator(target);

            String line1 = li.nextLine();
            assertTrue(line1.contains(OoResourceMetaDataTest.TEST_JSON_METADATA));
            String line2 = li.nextLine();
            assertTrue(line2.contains(originalData));
        }
        finally
        {
            FileUtils.deleteQuietly(target);
        }

    }
}
