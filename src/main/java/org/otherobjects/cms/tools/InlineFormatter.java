package org.otherobjects.cms.tools;

import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.otherobjects.cms.views.FreeMarkerToolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.template.Configuration;
import freemarker.template.Template;

@Component
public class InlineFormatter
{
    private final Logger logger = LoggerFactory.getLogger(InlineFormatter.class);

    @Resource
    private FreeMarkerConfigurer freeMarkerConfigurer;

    @Resource 
    private FreeMarkerToolProvider freeMarkerToolProvider;
    
    public InlineFormatter()
    {

    }

    public String format(String input)
    {
        StringBuffer output = new StringBuffer();
        try
        {
            if (input != null && input.length() > 0)
            {
                Pattern pattern = Pattern.compile("\\[([A-Z]+)[=|:]([^\\]]+)\\]", Pattern.MULTILINE);
                Matcher matcher = pattern.matcher(input);

                while (matcher.find())
                {
                    String tag = matcher.group(0);
                    String tagName = matcher.group(1);
                    String tagBody = escapeDollarSigns(matcher.group(2));

                    logger.info("Found inline " + tagName + ": " + tagBody);

                    // If the tag returns null then there has been an error.
                    // Leave tag in place.
                    String tagOutput = processTag(tagName, tagBody);
                    if (tagOutput == null)
                    {
                        tagOutput = tag;
                    }
                    matcher.appendReplacement(output, tagOutput);
                }
                matcher.appendTail(output);
            }
        }
        catch (Exception e)
        {
            logger.error("error in parseTags(), with: " + input, e);
            return "{error<!-- " + e.getMessage() + " -->}";
        }
        return output.toString();
    }

    protected String escapeDollarSigns(String text)
    {
        // get rid of $ signs in the replacement string
        // as they are parsed as group references so we need to escape them
        // text = text.replaceAll("\\+$", "$");
        text = text.replaceAll("\\$", "\\\\\\$");
        return text;
    }

    public Map<String, String> parseAttributes(String tagBody)
    {
        Map<String, String> attributes = new HashMap<String, String>();
        String id;
        int pos = 0;

        try
        {
            pos = tagBody.indexOf("|");
            if (pos > 0)
            {
                id = tagBody.substring(0, pos).trim();
                attributes.put("id", id);
                tagBody = tagBody.substring(pos + 1);

                Pattern pattern = Pattern.compile("([^\\|^\\(=|:)]+)[=|:]?([^\\|]*)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(tagBody);

                while (matcher.find())
                {
                    logger.debug("parseAttributes found: '" + matcher.group(1) + "', '" + matcher.group(2) + "'");
                    if (matcher.group(1) != null)
                    {
                        attributes.put(matcher.group(1).toLowerCase().trim(), matcher.group(2).trim());
                    }
                }
            }
            else
            {
                attributes.put("id", tagBody);
            }
        }
        catch (Exception e)
        {
            logger.error("Problem parsing attributes", e);
        }

        return attributes;
    }

    public String processTag(String tagName, String tagBody)
    {
        try
        {
            Map<String, String> attributes = parseAttributes(tagBody);

            Configuration configuration = freeMarkerConfigurer.getConfiguration();

            /* Get or create a template */
            String templateName = "/otherobjects/templates/inline/" + tagName + ".ftl";
            Template temp;
            try
            {
                temp = configuration.getTemplate(templateName);
            }
            catch (FileNotFoundException e)
            {
                // If we don't find an OTHERobjects inline template try for a site one
                templateName = "/site/templates/inline/" + tagName + ".ftl";
                temp = configuration.getTemplate(templateName);
            }

            /* Create a data-model */
            Map<String, Object> context = new HashMap<String, Object>();
            context.put("tag", new InlineTag(tagName, attributes));
            context.put("tagName", tagName);
            context.put("tagBody", tagBody);
            context.putAll(this.freeMarkerToolProvider.getTools());

            /* Merge data-model with template */
            Writer out = new StringWriter();
            temp.process(context, out);
            out.flush();
            return out.toString();

        }
        catch (Exception e)
        {
            return "{error<!-- " + e.getMessage() + " -->}";
        }
    }

    public class InlineTag
    {
        private Map<String, String> attributes;

        public InlineTag(String tagName, Map<String, String> attributes)
        {
            setAttributes(attributes);
            getAttributes().put("name", tagName);
        }

        public Map<String, String> getAttributes()
        {
            return attributes;
        }

        public void setAttributes(Map<String, String> attributes)
        {
            this.attributes = attributes;
        }

        public String get(String name)
        {
            return getAttributes().get(name);
        }

        public String get(String name, String defaultValue)
        {
            String value = get(name);
            return value != null ? value : defaultValue;
        }

        public Integer getInteger(String name)
        {
            if (get(name) != null)
                return new Integer(get(name));
            else
                return null;
        }

        public Integer getInteger(String name, Integer defaultValue)
        {
            Integer value = getInteger(name);
            return value != null ? value : defaultValue;
        }

        public Boolean getBoolean(String name)
        {
            boolean v = false;
            String value = get(name);
            if (value != null)
            {
                value = value.toLowerCase();
                v = value.startsWith("y") || value.startsWith("t");
                return new Boolean(v);
            }
            else
            {
                return null;
            }
        }

        public Boolean getBoolean(String name, Boolean defaultValue)
        {
            Boolean value = getBoolean(name);
            return value != null ? value : defaultValue;
        }
    }
}
