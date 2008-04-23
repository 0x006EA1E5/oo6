package org.otherobjects.cms.model;

import org.otherobjects.cms.model.CmsImage;
import org.otherobjects.cms.model.MetaData;
import org.otherobjects.cms.model.SitePage;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type(labelProperty = "title")
public class Article extends SitePage
{
    private String title;
    private String content;
    private CmsImage image;
    private MetaData metaData;

    @Property(order = 30)
    public CmsImage getImage()
    {
        return image;
    }

    public void setImage(CmsImage image)
    {
        this.image = image;
    }

    @Property(order = 50, type = PropertyType.COMPONENT)
    public MetaData getMetaData()
    {
        return metaData;
    }

    public void setMetaData(MetaData metaData)
    {
        this.metaData = metaData;
    }

    @Property(order = 10)
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Property(order = 20, type = PropertyType.TEXT)
    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }

}
