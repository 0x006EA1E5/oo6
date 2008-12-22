package org.otherobjects.cms.util;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.iptc.IptcDirectory;

public class ImageUtilsTest extends TestCase
{

    public void testGetImageDimensions() throws FileNotFoundException
    {
        File dog = new File("src/test/java/org/otherobjects/cms/util/dog.jpg");
        Dimension imageDimensions = ImageUtils.getImageDimensions(new FileInputStream(dog));
        assertEquals(800, (int) imageDimensions.getWidth());
        assertEquals(600, (int) imageDimensions.getHeight());
    }

    public void testGetImageMetadata() throws FileNotFoundException
    {
        File frog = new File("src/test/java/org/otherobjects/cms/util/frog.jpg");
        Metadata imageMetadata = ImageUtils.getImageMetadata(new FileInputStream(frog));
        Directory iptcDirectory = imageMetadata.getDirectory(IptcDirectory.class);
        assertEquals("Frog", iptcDirectory.getString(IptcDirectory.TAG_OBJECT_NAME));
        assertEquals("Frog in grass", iptcDirectory.getString(IptcDirectory.TAG_CAPTION));
        assertEquals("iPhoto Original", iptcDirectory.getString(IptcDirectory.TAG_KEYWORDS));
        assertEquals("Rich Aston", iptcDirectory.getString(IptcDirectory.TAG_COPYRIGHT_NOTICE));
        Directory exifDirectory = imageMetadata.getDirectory(ExifDirectory.class);
        assertEquals("Panasonic", exifDirectory.getString(ExifDirectory.TAG_MAKE));
    }

}
