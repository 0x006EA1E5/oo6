package org.otherobjects.cms.controllers.handlers;

import org.otherobjects.cms.model.SitePage;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

@Type(labelProperty="name")
public class TestPage extends SitePage
{
    private String name;
    private Template template;

    @Property
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
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
