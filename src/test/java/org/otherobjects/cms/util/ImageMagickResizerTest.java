package org.otherobjects.cms.util;

import java.awt.Color;
import java.io.File;

import junit.framework.TestCase;

public class ImageMagickResizerTest extends TestCase
{
    public void testExecCommand()
    {
        ImageMagickResizer imr = new ImageMagickResizer();

        // Try a simple command that should always work
        int status = imr.execCommand(new String[]{"ls"});
        assertEquals(0, status);

        // A bad command
        try
        {
            imr.execCommand(new String[]{"notacommand"});
            fail();
        }
        catch (Exception e)
        {
        }
    }

    public void testResize()
    {
        File dog = new File("src/test/java/org/otherobjects/cms/util/dog.jpg");
        File dogW = new File("/tmp/dogW.jpg");
        File dogH = new File("/tmp/dogH.jpg");
        File dogWH = new File("/tmp/dogWH.jpg");
        File dogWHC = new File("/tmp/dogWHC.jpg");
        assert (dog.exists());

        ImageMagickResizer imr = new ImageMagickResizer();
        imr.resize(dog, dogW, 200, null, null, null);
        imr.resize(dog, dogH, null, 200, null, null);
        imr.resize(dog, dogWH, 125, 125, null, null);
        imr.resize(dog, dogWHC, 200, 200, new Color(255, 0, 0), null);

        // FIXME Should load images in here and check sizes at least
    }
}
