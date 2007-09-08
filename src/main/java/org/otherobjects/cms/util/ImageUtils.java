package org.otherobjects.cms.util;

import java.awt.Dimension;
import java.awt.Image;
import java.io.File;

import javax.swing.ImageIcon;

import org.otherobjects.cms.OtherObjectsException;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;

public class ImageUtils
{
    public static Dimension getImageDimensions(File imageFile)
    {
        Image image = new ImageIcon(imageFile.getAbsolutePath()).getImage();
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        return new Dimension(width, height);
    }

    public static Metadata getImageMetadata(File imageFile)
    {
        try
        {
            return JpegMetadataReader.readMetadata(imageFile);
        }
        catch (JpegProcessingException e)
        {
            throw new OtherObjectsException("Could not read image metadata: s" + imageFile, e);
        }
    }
}
