package org.otherobjects.cms.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.annotation.Resource;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageSize;
import org.otherobjects.cms.util.ImageMagickResizer;
import org.otherobjects.cms.util.ImageResizer;
import org.otherobjects.cms.util.ImageUtils;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Tool to fetch and, if necessary, resize images.
 * 
 * <p>FIXME Synchronise so same image size can't be created at the same time
 * <p>FIXME Delete sizes on image change
 * <p>FIXME Don't create backgrounds if they won't be seen
 * 
 * <p>All images are stored in the data directory in a folder named "images".
 * Within "images" there are subdirectories for each size/background combination.
 * 
 * <p>There is one special case folder "images/originals" which contains the 
 * original untouched uploaded files. This is handy for backups.
 * 
 * @author rich
 */
@Component
@Tool
public class CmsImageTool
{
    private static final String THUMBNAIL_BACKGROUND_COLOR = "#FFFFFF";
    private static final int THUMBNAIL_SIZE = 100;

    // FIXME Injectable image resizer. Or list with fallbacks.
    private ImageResizer imageResizer = new ImageMagickResizer();

    @Resource
    private OoResourceLoader ooResourceLoader;

    /**
     * Return CmsImageSize representing orignally uploaded image.
     * @param image
     * @return
     * @throws IOException 
     */
    public CmsImageSize getOriginal(CmsImage image) throws IOException
    {
        Assert.notNull(image, "Image must be provided.");
        CmsImageSize original = new CmsImageSize();
        original.setImage(ooResourceLoader.getResource(image.getOriginalResourcePath()));
        original.setWidth(image.getOriginalWidth().intValue());
        original.setHeight(image.getOriginalHeight().intValue());
        original.setDescription(image.getDescription());
        return original;
    }

    public CmsImageSize getThumbnail(CmsImage image)
    {
        Assert.notNull(image, "Image must be provided.");
        return getSize(image, THUMBNAIL_SIZE, THUMBNAIL_SIZE, THUMBNAIL_BACKGROUND_COLOR);
    }

    public CmsImageSize getSize(CmsImage image, Integer width)
    {
        Assert.notNull(image, "Image must be provided.");
        return getSize(image, width, calculateHeight(width, image.getAspectRatio()), null);
    }

    public CmsImageSize getSize(CmsImage image, Integer width, Integer height)
    {
        // Allow for fixed height images
        Assert.notNull(image, "Image must be provided.");
        if (width == null)
        {
            width = calculateWidth(height, image.getAspectRatio());
        }
        if (height == null)
        {
            height = calculateHeight(width, image.getAspectRatio());
        }
        return getSize(image, width, height, null);
    }

    private Integer calculateHeight(Integer width, double aspectRatio)
    {
        return (int) (width / aspectRatio);
    }

    private Integer calculateWidth(Integer height, double aspectRatio)
    {
        double h = height * aspectRatio;
        long round = Math.round(h);
        return new Long(round).intValue();
    }

    public CmsImageSize getSize(CmsImage image, Integer width, Integer height, String backgroundColor)
    {
        try
        {
            Assert.notNull(width, "Image width must be provided.");
            Assert.notNull(height, "Image height must be provided.");

            if (width.equals(image.getOriginalWidth()) && height.equals(image.getOriginalHeight()))
                return getOriginal(image);

            CmsImageSize size = new CmsImageSize();
            size.setWidth(width);
            size.setHeight(height);
            size.setBackgroundColor(backgroundColor);

            OoResource resource = determineResource(image, size);

            // Does file already exist?
            if (!resource.exists())
            {
                createResizedDataFile(image, size, resource);
                // Check that file resized correctly
                Dimension imageDimensions = ImageUtils.getImageDimensions(resource.getInputStream());
                Assert.isTrue(size.getWidth() == imageDimensions.getWidth(), "Resize failed. Width is " + imageDimensions.getWidth() + " but should be " + size.getWidth() + ".");
                Assert.isTrue(size.getHeight() == imageDimensions.getHeight(), "Resize failed. Height is " + imageDimensions.getWidth() + " but should be " + size.getWidth() + ".");
            }

            size.setImage(resource);
            return size;
        }
        catch (IOException e)
        {
            throw new OtherObjectsException("Error resizing image.", e);
        }
    }

    private OoResource determineResource(CmsImage image, CmsImageSize size) throws IOException
    {
        StringBuffer path = new StringBuffer(CmsImage.DEFAULT_FOLDER);
        path.append(size.getWidth());
        path.append("x");
        path.append(size.getHeight());
        if (size.getBackgroundColor() != null)
        {
            path.append("-"+size.getBackgroundColor().substring(1));
        }
        path.append("/");
        path.append(image.getCode());
        return this.ooResourceLoader.getResource(path.toString());
    }

    protected void createResizedDataFile(CmsImage image, CmsImageSize size, OoResource resized) throws IOException
    {
        File originalFile = ooResourceLoader.getResource(image.getOriginalResourcePath()).getFile();
        File destinationFile = resized.getFile();
        Color color = size.getBackgroundColor() != null ? Color.decode(size.getBackgroundColor()) : null;
        this.imageResizer.resize(originalFile, destinationFile, size.getWidth(), size.getHeight(), color, null);
    }

    public void setImageResizer(ImageResizer imageResizer)
    {
        this.imageResizer = imageResizer;
    }

    public void setOoResourceLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

}
