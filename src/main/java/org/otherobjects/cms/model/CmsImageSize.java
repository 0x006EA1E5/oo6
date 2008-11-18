package org.otherobjects.cms.model;

import org.otherobjects.cms.io.OoResource;
import org.springframework.util.Assert;

/**
 * Represents an instance of an image at a particular size.
 * 
 * @author rich
 */
public class CmsImageSize
{
    private String description;
    private int width;
    private int height;
    private String backgroundColor;
    private OoResource image;

    @Override
    public String toString()
    {
        return "[CmsImageSize: " + (image.getFilename() !=null ? image.getFilename() : "-") +  "]";
    }

    public OoResource getImage()
    {
        return image;
    }

    public void setImage(OoResource image)
    {
        this.image = image;
    }

    /**
     * Returns description of image contents. Used for ALT text on image tags.
     * @return
     */
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public int getWidth()
    {
        return this.width;
    }

    public void setWidth(int width)
    {
        this.width = width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public void setHeight(int height)
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
