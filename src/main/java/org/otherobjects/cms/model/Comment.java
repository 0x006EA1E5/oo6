package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type(labelProperty = "username")
public class Comment extends BaseNode
{
    private String itemId;
    private String comment;
    private String username;
    
    @Override
    public String getOoLabel()
    {
        return getComment();
    }

    @Property(order = 10)
    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    @Property(order = 20, required=true, type=PropertyType.TEXT)
    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }

    @Property(order = 30, type=PropertyType.STRING, required=true)
    public String getItemId()
    {
        return itemId;
    }

    public void setItemId(String itemId)
    {
        this.itemId = itemId;
    }
}
