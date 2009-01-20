package org.otherobjects.cms.tools;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * Tool for processing template blocks. Also manages caching.
 */
@Component
@Tool
public class BlockTool implements TemplateDirectiveModel
{
    @Resource
    private CacheManager cacheManager;

    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;

    private Cache cache;

    @PostConstruct
    private void init()
    {
        this.cache = cacheManager.getCache("org.otherobjects.cms.BLOCK_CACHE");
    }

    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body) throws TemplateException, IOException
    {
        if (params.get("code") != null)
        {
            Writer out = env.getOut();
            String code = ((SimpleScalar) params.get("code")).toString();
            code = "/site/templates/blocks/" + code + ".ftl";

            Element element = cache.get(code);

            if (element == null)
            {
                Template templateForInclusion = env.getTemplateForInclusion(code, "UTF-8", true);
                StringWriter htmlWriter = new StringWriter();
                env.setOut(htmlWriter);
                env.include(templateForInclusion);
                env.setOut(out);
                element = new Element(code, htmlWriter.toString());
                cache.put(element);
            }

            out.write((String) element.getObjectValue());

        }
    }

}
