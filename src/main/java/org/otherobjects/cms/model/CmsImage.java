package org.otherobjects.cms.model;

import java.io.File;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Represents a content managed image object.
 * 
 * <p>Does not contain the image data at any size but has a reference
 * <code>originalFileId</code> to the original image in the data store.
 * 
 * @author rich
 */
@Type(labelProperty = "label")
public class CmsImage extends DynaNode
{
    public static final String DATA_FILE_COLLECTION_NAME = "images";
    public static final String ORIGINALS_PATH = "/originals/";

    // Meta data
    private String label;
    private String description;
    private String keywords;
    private String copyright;

    // Source information
    private String originalFileId;
    private Long originalWidth;
    private Long originalHeight;
    private String originalProvider;
    private String originalId;

    // Temporary holder for new and relacement files
    private File newFile;
    private String thumbnailPath;

    /**
     * FIXME Temp hack to allow thumbnails in Workbench interface.
     * 
     * @return
     */
    public String getThumbnailPath()
    {
        if (this.thumbnailPath != null)
            return this.thumbnailPath;

        if (getOriginalFileId() != null)
            return "/data" + getOriginalFileId().replaceAll("originals", "100x100%23FFFFFF");
        else
            return null;

    }

    /**
     * Needed for external image services.
     * 
     * @return
     */
    public void setThumbnailPath(String url)
    {
        this.thumbnailPath = url;
    }

    public double getAspectRatio()
    {
        return ((double) getOriginalWidth() / (double) getOriginalHeight());
    }

    @Override
    @Property(order = 10)
    public String getLabel()
    {
        return this.label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 20, type = PropertyType.TEXT)
    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 30, type = PropertyType.TEXT)
    public String getKeywords()
    {
        return this.keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    @Property(order = 40, type = PropertyType.TEXT)
    public String getCopyright()
    {
        return this.copyright;
    }

    public void setCopyright(String copyright)
    {
        this.copyright = copyright;
    }

    @Property(order = 70)
    public Long getOriginalWidth()
    {
        return this.originalWidth;
    }

    public void setOriginalWidth(Long originalWidth)
    {
        this.originalWidth = originalWidth;
    }

    @Property(order = 80)
    public Long getOriginalHeight()
    {
        return this.originalHeight;
    }

    public void setOriginalHeight(Long originalHeight)
    {
        this.originalHeight = originalHeight;
    }

    @Property(order = 100)
    public String getOriginalProvider()
    {
        return this.originalProvider;
    }

    public void setOriginalProvider(String originalProvider)
    {
        this.originalProvider = originalProvider;
    }

    @Property(order = 110)
    public String getOriginalId()
    {
        return this.originalId;
    }

    public void setOriginalId(String originalId)
    {
        this.originalId = originalId;
    }

    public void setNewFile(File newFile)
    {
        this.newFile = newFile;
    }

    @Property(order = 90)
    public String getOriginalFileId()
    {
        return this.originalFileId;
    }

    public void setOriginalFileId(String originalFileId)
    {
        this.originalFileId = originalFileId;
    }

    public File getNewFile()
    {
        return this.newFile;
    }

    public String getFileName()
    {
        // FIXME Do this propertly once we have support for codeProperty
        return getCode();
    }

}
