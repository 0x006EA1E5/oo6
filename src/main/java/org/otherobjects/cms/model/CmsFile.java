package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@Type(labelProperty = "label", adminControllerUrl="/otherobjects/file")
public class CmsFile extends BaseNode
{
    public static final String DATA_FILE_COLLECTION_NAME = "files";

    // Meta data
    private String label;
    private String description;
    private String keywords;
    private String copyright;
    private String mimeType;
    private Long fileSize;

    // Source information
    private String originalFileName;
    //    private File newFile;

    private CommonsMultipartFile newFile;

    /**
     * Generates default code based on image label and file extension.
     */
    @Override
    public String getCode()
    {
        String fileStem = StringUtils.substringBeforeLast(getOriginalFileName(),".");
        return this.code != null ? this.code : StringUtils.generateUrlCode(fileStem) + "." + getExtension();
    }
    
    @Override
    @Property(order = 10, required = false)
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
    
    
    @Property(order = 70)
    public Long getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(Long fileSize)
    {
        this.fileSize = fileSize;
    }
    

    @Property(order = 5, type=PropertyType.TRANSIENT)
    public CommonsMultipartFile getNewFile()
    {
        return newFile;
    }

    public void setNewFile(CommonsMultipartFile newFile)
    {
        this.newFile = newFile;
    }

    /**
     * Returns extension of original file name. 
     * @return
     */
    public String getExtension()
    {
        if(getOriginalFileName() == null)
            return null;
        
        String extension = getOriginalFileName().substring(getOriginalFileName().lastIndexOf(".") + 1);
        return extension;
    }
    
}
