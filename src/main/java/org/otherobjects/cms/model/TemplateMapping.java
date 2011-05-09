package org.otherobjects.cms.model;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.annotation.Type;

/**
 * Publishing options for site pages.
 * 
 * @author rich
 */
@Type(codeProperty = "")
public class TemplateMapping extends BaseComponent
{
    private Template template;
    private TypeDef type;

    //    public String toString()
    //    {
    //        return "Publishing Options";
    //    }
    //
    //    @Override
    //    public String getOoLabel()
    //    {
    //        return toString();
    //    }

    public TypeDef getType()
    {
        return type;
    }

    public void setType(TypeDef type)
    {
        this.type = type;
    }

    public Template getTemplate()
    {
        return template;
    }

    public void setTemplate(Template template)
    {
        this.template = template;
    }

    public TemplateMapping(Template template, String type)
    {
        super();
        this.setTemplate(template);
        TypeService typeService = ((TypeService) SingletonBeanLocator.getBean("typeService"));
        this.setType(typeService.getType(type));
    }
}
