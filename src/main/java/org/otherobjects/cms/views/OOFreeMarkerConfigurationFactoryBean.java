package org.otherobjects.cms.views;

import java.io.IOException;

import org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 * Custom FreeMarker Configuration Bean.
 * @author rich
 *
 */
public class OOFreeMarkerConfigurationFactoryBean extends FreeMarkerConfigurationFactoryBean
{
    @Override
    protected void postProcessConfiguration(Configuration config) throws IOException, TemplateException
    {
        config.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
        config.setSetting("template_update_delay", "5"); // Default
//        config.setSetting("template_update_delay", "0"); // Always check for template updates
//        config.setSetting("template_update_delay", "120"); // Always check for template updates
    }
}
