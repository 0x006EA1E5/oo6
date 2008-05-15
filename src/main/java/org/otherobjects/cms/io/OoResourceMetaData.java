package org.otherobjects.cms.io;

import java.util.Date;

import flexjson.JSONSerializer;

public class OoResourceMetaData
{
    private String title;
    private String description;
    private String author;
    private Long userdId;
    private Date modificationTimestamp;
    private Date creationDate;

    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("{\"metaData\":");
        buf.append(new JSONSerializer().exclude("class").serialize(this));
        buf.append("}");
        return buf.toString();
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getAuthor()
    {
        return author;
    }

    public void setAuthor(String author)
    {
        this.author = author;
    }

    public Long getUserdId()
    {
        return userdId;
    }

    public void setUserdId(Long userdId)
    {
        this.userdId = userdId;
    }

    public Date getModificationTimestamp()
    {
        return modificationTimestamp;
    }

    public void setModificationTimestamp(Date modificationTimestamp)
    {
        this.modificationTimestamp = modificationTimestamp;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

}
