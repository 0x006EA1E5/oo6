package org.otherobjects.cms.io;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import flexjson.JSONSerializer;

/**
 * Simple bean to hold metaData for {@link OoResource}s. It's toString() method produces a JSON string
 * 
 * FIXME Finalise resource metaData content
 * 
 * @author joerg
 */
public class OoResourceMetaData
{
    private String label;
    private String description;
    private String author;
    private Long userdId;
    private Date modificationTimestamp;
    private Date creationDate;
    private HashMap<String, String> properties = new HashMap<String, String>();

    @Override
    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append("{\"metaData\":");
        buf.append(new JSONSerializer().exclude("class").serialize(this));
        buf.append("}");
        return buf.toString();
    }

    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
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

    public HashMap<String, String> getProperties()
    {
        return properties;
    }

    public void setProperties(HashMap<String, String> properties)
    {
        this.properties = properties;
    }
}
