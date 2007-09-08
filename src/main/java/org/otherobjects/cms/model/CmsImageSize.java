package org.otherobjects.cms.model;

import org.springframework.util.Assert;

/**
 * Represents an instance of an image at a particular size.
 * 
 * FIXME How might we handle crops?
 * FIXME Would be nice if this was simpler than a DynaNode
 * 
 * @author rich
 */
public class CmsImageSize extends DynaNode
{
    private String fileName;
    private Long width;
    private Long height;
    private String backgroundColor;
    private DataFile dataFile;

    @Override
    public String toString()
    {
        return getFileId();
    }

    public String getFileId()
    {
        Assert.notNull(getFileName(), "CmsImageSize fileName can not be null");
        return "/" + CmsImage.DATA_FILE_COLLECTION_NAME + getCollectionPath() + getFileName();
    }

    public String getCollectionPath()
    {
        StringBuffer cp = new StringBuffer();
        if (getWidth() == null && getHeight() == null)
            cp.append(CmsImage.ORIGINALS_PATH);
        else
        {
            cp.append("/");
            cp.append(getWidth());
            cp.append("x");
            cp.append(getHeight());
            if (getBackgroundColor() != null)
                cp.append(getBackgroundColor());
            cp.append("/");
        }
        return cp.toString();
    }

    public DataFile getDataFile()
    {
        return this.dataFile;
    }

    public void setDataFile(DataFile file)
    {
        this.dataFile = file;
    }

    public String getFileName()
    {
        return this.fileName;
    }

    public void setFileName(String fileName)
    {
        this.fileName = fileName;
    }

    public Long getWidth()
    {
        return this.width;
    }

    public void setWidth(Long width)
    {
        this.width = width;
    }

    public Long getHeight()
    {
        return this.height;
    }

    public void setHeight(Long height)
    {
        this.height = height;
    }

    public String getBackgroundColor()
    {
        return this.backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor)
    {
        if (backgroundColor != null)
        {
            Assert.isTrue(backgroundColor.matches("#[0-9A-F]{6}"), "Color must be in uppercase hex format eg #FF003B");
        }
        this.backgroundColor = backgroundColor;
    }

    //    public boolean matches(Integer width, Integer height, String backgroundColor)
    //    {
    //        // Avoid NPEs
    //        if (width == null)
    //            width = 0;
    //        if (height == null)
    //            height = 0;
    //        if (backgroundColor == null)
    //            backgroundColor = "";
    //
    //        if (!width.equals(getWidth()))
    //            return false;
    //        if (!height.equals(getHeight()))
    //            return false;
    //        if (!backgroundColor.equals(getBackgroundColor()))
    //            return false;
    //
    //        return true;
    //    }

}
