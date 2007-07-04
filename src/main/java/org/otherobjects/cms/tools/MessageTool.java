package org.otherobjects.cms.tools;

import java.util.Locale;

import org.springframework.context.MessageSource;

/**
 * Tool to fetch localisation messages.
 * 
 * @author rich
 */
public class MessageTool
{
    private MessageSource messageSource;
    private Locale locale = Locale.UK;

    public String get(String code)
    {
        return messageSource.getMessage(code, null, locale);
    }

    public MessageSource getMessageSource()
    {
        return messageSource;
    }

    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    public Locale getLocale()
    {
        return locale;
    }

    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }
}
