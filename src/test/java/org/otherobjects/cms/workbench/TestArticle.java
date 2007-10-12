package org.otherobjects.cms.workbench;

import org.otherobjects.cms.model.SitePage;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type(labelProperty="title")
public class TestArticle extends SitePage
{
    private String title;
    private String content;
    private Template template;

    @Property
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    @Property(type=PropertyType.TEXT)
    public String getContent()
    {
        return content;
    }

    public void setContent(String content)
    {
        this.content = content;
    }


    @Property
    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
    }
}
