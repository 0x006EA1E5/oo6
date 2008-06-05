package org.otherobjects.cms.tools;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class FormatDirective implements TemplateDirectiveModel
{
    @SuppressWarnings("unchecked")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
    {
        String text = ((SimpleScalar) params.get("text")).getAsString();
        String html = FormatTool.formatTextile(text);
        env.getOut().write(html);
    }
}
