package org.otherobjects.cms.views;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContextException;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;

import freemarker.template.Template;
import freemarker.template.TemplateException;

public class OOFreeMarkerView extends FreeMarkerView
{
    @Override
    protected void processTemplate(Template template, Map model, HttpServletResponse response) throws IOException, TemplateException
    {
        // TODO Auto-generated\ method stub
        try
        {
            super.processTemplate(template, model, response);
        }
        catch (RuntimeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void checkTemplate() throws ApplicationContextException
    {
        try
        {
            super.checkTemplate();
        }
        catch (RuntimeException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void doRender(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        try
        {
            super.doRender(model, request, response);
        }
        catch (Exception e)
        {
            response.reset();
            setUrl("/site/templates/error-500.ftl");
            model.put("exception", e);
            super.doRender(model, request, response);
        }
    }
}
