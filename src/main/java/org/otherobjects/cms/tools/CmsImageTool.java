package org.otherobjects.cms.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.io.File;

import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.CmsImageSize;
import org.otherobjects.cms.model.DataFile;
import org.otherobjects.cms.model.DataFileDao;
import org.otherobjects.cms.model.DataFileDaoFileSystem;
import org.otherobjects.cms.util.ImageMagickResizer;
import org.otherobjects.cms.util.ImageResizer;
import org.otherobjects.cms.util.ImageUtils;
import org.springframework.util.Assert;

/**
 * Tool to fetch and, if necessary, resize images.
 * 
 * <p>FIXME Synchronise so same image size can't be created at the same time
 * <p>FIXME Delete sizes on image change
 * <p>FIXME Don't create backgrounds if they won't be seen
 * 
 * @author rich
 */
public class CmsImageTool
{
    private static final String THUMBNAIL_BACKGROUND_COLOR = "#FFFFFF";
    private static final int THUMBNAIL_SIZE = 100;

    private final DataFileDao dataFileDao = new DataFileDaoFileSystem();
    private final ImageResizer imageResizer = new ImageMagickResizer();

    public CmsImageSize getOriginal(CmsImage image)
    {
        Assert.notNull(image, "Image must be provided.");
        CmsImageSize original = new CmsImageSize();
        original.setFileName(image.getFileName());
        // If no width/height set then we will assume that we want the original
        original.setDataFile(this.dataFileDao.get(original.getFileId()));
        Assert.notNull(original.getDataFile(), "Could not find original data file: " + original.getFileId());
        original.setWidth(image.getOriginalWidth());
        original.setHeight(image.getOriginalHeight());
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
            width = calculateWidth(height, image.getAspectRatio());
        if (height == null)
            height = calculateHeight(width, image.getAspectRatio());
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
        Assert.notNull(width, "Image width must be provided.");
        Assert.notNull(height, "Image height must be provided.");

        if (width.equals(longToInteger(image.getOriginalWidth())) && height.equals(longToInteger(image.getOriginalHeight())))
            return getOriginal(image);

        CmsImageSize size = new CmsImageSize();
        size.setFileName(image.getFileName());
        size.setWidth(integerToLong(width));
        size.setHeight(integerToLong(height));
        size.setBackgroundColor(backgroundColor);

        DataFile dataFile = null;

        // Does file already exist?
        if (this.dataFileDao.exists(size.getFileId()))
            dataFile = this.dataFileDao.get(size.getFileId());
        else
        {
            dataFile = createResizedDataFile(image, size);
            // Check that file resized correctly
            Dimension imageDimensions = ImageUtils.getImageDimensions(dataFile.getFile());
            if (size.getWidth() != null)
                Assert.isTrue(size.getWidth().equals(new Long((long) imageDimensions.getWidth())), "Resize failed. Dimension is " + imageDimensions.getWidth() + " but should be " + size.getWidth()
                        + ".");
            if (size.getHeight() != null)
                Assert.isTrue(size.getHeight().equals(new Long((long) imageDimensions.getHeight())));
            size.setWidth(new Long((long) imageDimensions.getWidth()));
            size.setHeight(new Long((long) imageDimensions.getHeight()));
        }

        size.setDataFile(dataFile);
        return size;
    }

    private Long integerToLong(Integer integer)
    {
        if (integer == null)
            return null;
        else
            return Long.valueOf(integer);
    }

    private Integer longToInteger(Long lng)
    {
        if (lng == null)
            return null;
        else
            return lng.intValue();
    }

    private DataFile createResizedDataFile(CmsImage image, CmsImageSize size)
    {
        File originalFile = getOriginal(image).getDataFile().getFile();
        // FIXME better naming for temp file needed
        File destinationFile = new File("/tmp/" + image.getFileName());
        Color color = size.getBackgroundColor() != null ? Color.decode(size.getBackgroundColor()) : null;
        this.imageResizer.resize(originalFile, destinationFile, longToInteger(size.getWidth()), longToInteger(size.getHeight()), color, null);

        // Save data file
        DataFile dataFile = new DataFile(destinationFile);
        dataFile.setId(size.getFileId());
        this.dataFileDao.save(dataFile);
        destinationFile.delete();
        return dataFile;
    }
}
