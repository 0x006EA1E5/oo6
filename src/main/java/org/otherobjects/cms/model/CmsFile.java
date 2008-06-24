package org.otherobjects.cms.model;

import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

@Type(labelProperty = "label")
public class CmsFile extends BaseNode
{
    public static final String DATA_FILE_COLLECTION_NAME = "files";

    // Meta data
    private String label;
    private String description;
    private String keywords;
    private String copyright;
    private String mimeType;

    // Source information
    private String originalFileName;
    //    private File newFile;

    private OoResource file;

    @Override
    @Property(order = 10, required = true)
    public String getLabel()
    {
        return label;
    }

    @Override
    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 20)
    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    @Property(order = 30)
    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    @Property(order = 40)
    public String getCopyright()
    {
        return copyright;
    }

    public void setCopyright(String copyright)
    {
        this.copyright = copyright;
    }

    @Property(order = 50)
    public String getMimeType()
    {
        return mimeType;
    }

    public void setMimeType(String mimeType)
    {
        this.mimeType = mimeType;
    }

    @Property(order = 60)
    public String getOriginalFileName()
    {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName)
    {
        this.originalFileName = originalFileName;
    }

    //    @Property(type = PropertyType.TEXT)
    //    @JSON(include = false)
    //    public File getNewFile()
    //    {
    //        return newFile;
    //    }
    //
    //    public void setNewFile(File newFile)
    //    {
    //        this.newFile = newFile;
    //    }

    @Property(order = 70)
    public OoResource getFile()
    {
        return file;
    }

    public void setFile(OoResource file)
    {
        this.file = file;
    }

}
