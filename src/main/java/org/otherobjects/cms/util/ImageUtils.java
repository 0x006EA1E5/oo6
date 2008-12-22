package org.otherobjects.cms.util;

import java.awt.Dimension;
import java.awt.Image;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.otherobjects.cms.OtherObjectsException;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.metadata.Metadata;

public class ImageUtils
{
    public static Dimension getImageDimensions(InputStream inputStream)
    {
        try
        {
            Image image = ImageIO.read(inputStream);
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            return new Dimension(width, height);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not read image dimensions.", e);
        }
    }

    public static Metadata getImageMetadata(InputStream inputStream)
    {
        try
        {
            return JpegMetadataReader.readMetadata(inputStream);
        }
        catch (Exception e)
        {
            throw new OtherObjectsException("Could not read image metadata.", e);
        }
    }
}
