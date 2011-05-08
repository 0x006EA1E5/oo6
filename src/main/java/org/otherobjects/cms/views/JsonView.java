package org.otherobjects.cms.views;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.View;

import flexjson.JSONSerializer;

public class JsonView implements View
{
    public static final String JSON_DATA_KEY = "jsonData";
    public static final String JSON_INCLUDES_KEY = "jsonIncludes";
    public static final String JSON_DEEP_SERIALIZE = "jsonDeep";
    public static final String JSON_MIME_OVERRIDE = "mimeOverride";

    private static final String MIME_TYPE = "application/json";
    private String mimeOverride = null;
    private MessageSource messageSource;
    //TODO Make this configurable?
    private static final String ENCODING = "UTF-8";

    @SuppressWarnings("unchecked")
    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        PrintWriter output = response.getWriter();
        StringWriter writer = new StringWriter();
        JSONSerializer serializer = new JSONSerializer();

        if (model.containsKey(JSON_MIME_OVERRIDE))
        {
            this.mimeOverride = (String) model.get(JSON_MIME_OVERRIDE);
        }

        boolean deep = false;
        if (model.containsKey(JSON_DEEP_SERIALIZE))
            deep = (Boolean) model.get(JSON_DEEP_SERIALIZE);

        if (model.containsKey(JSON_DATA_KEY))
        {
            if (model.containsKey(JSON_INCLUDES_KEY))
                serializer = serializer.include((String[]) model.get(JSON_INCLUDES_KEY));

            if (deep)
                writer.write(serializer.deepSerialize(model.get(JSON_DATA_KEY)));
            else
                writer.write(serializer.serialize(model.get(JSON_DATA_KEY)));
        }
        else
        {
            if (deep)
                writer.write(serializer.deepSerialize(model));
            else
                writer.write(serializer.serialize(model));
        }
        String json = writer.toString();

        Locale locale = LocaleContextHolder.getLocale();
        MessageSource messageSource = getMessageSource();

        // FIXME Is this a good idea or even a good place?
        json = localiseString(json, messageSource, locale);

        // FIXME Is there a more performant way of doing this
        //logger.debug(json);
        response.setCharacterEncoding(ENCODING);
        output.write(json);
    }

    protected String localiseString(String text, MessageSource messageSource, Locale locale)
    {
        Pattern p = Pattern.compile("\\$\\{([\\w\\.]*)\\}");
        Matcher m = p.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (m.find())
        {
            String msg = messageSource.getMessage(m.group(1), null, locale);
            m.appendReplacement(sb, msg);
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public MessageSource getMessageSource()
    {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    public String getContentType()
    {
        return (mimeOverride == null) ? MIME_TYPE : mimeOverride;
    }

}
