package org.otherobjects.cms.tools;

import java.io.StringWriter;
import java.text.NumberFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.Resource;


import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.otherobjects.cms.util.StringUtils;
import org.otherobjects.cms.views.Tool;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.ocpsoft.pretty.time.PrettyTime;

/**
 * Tool to be used from templates to aid in generating formatted text.
 * 
 * FIXME 0.6 Use StringUtils methods were possible and align method names.
 * 
 * @author joerg
 *
 */
@Component
@Tool
public class FormatTool
{
    private static final int DEFAULT_SUMMARY_LENGTH = 250;

    @Resource
    private MessageSource messageSource;

    @Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;

    @Resource
    private InlineFormatter inlineFormatter;

    private static PrettyTime prettyTimeFormatter = new PrettyTime();

    public FormatTool()
    {
    }

    public String getProperty(String name)
    {
        return otherObjectsConfigurator.getProperty(name);
    }

    /**
     * Formats textile string into HTML. HTML special chars in the textileSource will get escaped (notably the less than and greater than signs)
     * @param textileSource
     * @return
     */
    public String formatTextile(String textileSource)
    {

        WikiFormatter wf= new WikiFormatter() ; 
        textileSource=wf.format(textileSource);
        
        // Remove double line breaks
        String text = textileSource;//textileSource.replaceAll("/\n\n/", "\n");

        // Recode headings
        text = text.replaceAll("\\\\n", "\n");
        text = text.replaceAll("(?m)^h3", "h4");
        text = text.replaceAll("(?m)^h2", "h3");
        text = text.replaceAll("(?m)^h1", "h2");

        // Recode code blocks
        //        text = text.replaceAll("\\[code\\]", "<pre>\n<code>\n");
        //        text = text.replaceAll("\\[/code\\]", "\n</code>\n</pre>");

        // Recode links
        //        text = text.replaceAll("\\[LINK:([^|]*)\\|CAPTION:([^\\]]*)\\]", "\"$2\":$1");
        //        text = text.replaceAll("(?m)\\[LINK:([^]]*)\\]", "\"$1\":$1");

        // TODO This needs to be optimesd
        // TODO Add support for additional markups
        MarkupParser parser = new MarkupParser(new TextileLanguage());
        StringWriter out = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
        builder.setEmitAsDocument(false);
        parser.setBuilder(builder);
        parser.parse(text);
        parser.setBuilder(null);
        String html = out.toString();

        if (inlineFormatter != null)
            return inlineFormatter.format(html);
        else
            return text;
    }

    public String formatRelativeTimestamp(Date timestamp)
    {
        return prettyTimeFormatter.format(timestamp);
    }

    public String summarize(String content)
    {
        return summarize(content, DEFAULT_SUMMARY_LENGTH);
    }

    public String summarize(String content, int maxLength)
    {
        content = content.replaceAll("\\[[^\\]]*\\]", " ");
        WikiFormatter wf= new WikiFormatter() ; 
        content=wf.format(content);
        content = formatTextile(content);
        content = content.replaceAll("<[a-zA-Z\\/][^>]*>", " ");

        // FIXME Test and improve this
        if (content.length() > maxLength)
        {
            content = content.substring(0, maxLength);
            content = content.substring(0, content.lastIndexOf(' '));
            if (!content.endsWith("."))
                content += "...";
        }

        return content;
    }

    public String trim(String content, int maxLength)
    {
        return StringUtils.trim(content, maxLength);
    }

    /**
     * Formats file size into huma readable form.
     * 
     * @param size in bytes
     * @return formatted string
     */
    public static String formatFileSize(Long size)
    {
        NumberFormat f = NumberFormat.getInstance();
        f.setMaximumFractionDigits(1);
        f.setMinimumFractionDigits(1);

        double s = (double) size / (double) 1024;
        if (s < 1028 * 0.8)
        {
            return f.format(s) + " KB";
        }

        s /= 1024;
        if (s < 1024 * 0.8)
        {
            return f.format(s) + " MB";
        }

        s /= 1024;
        return f.format(s) + " GB";
    }
    
    public static long getCurrentDate()
    {       
        Date d= new Date() ;
        return d.getTime() ;
    }

    public static long getCurrentTime()
    {       
        long currentTimeInSeconds = System.currentTimeMillis()/1000;
        return currentTimeInSeconds ;
    }
    
    /**
     * Parses a string and looks up messages if appropriate. Useful for form labels that may or may note use
     * message codes. If the string appears to be a message code then this is looked up otherwise the string
     * is returned unaltered.
     * 
     * TODO Change format.
     * 
     * @param textileSource
     * @return
     */
    public String getMessage(String message)
    {
        if (message.startsWith("$"))
        {
            // Supports messages in ${code} format
            // FIXME Get locale from somewhere better
            String msg = messageSource.getMessage(message.substring(2, message.length() - 1), null, Locale.ENGLISH);
            return msg != null ? msg : "?" + message + "?";
        }
        if (message.contains(".") && !message.endsWith(".") && message.matches("[a-z0-9\\.]*"))
        {
            // Message
            // FIXME proper regexp here
            String msg = messageSource.getMessage(message, null, Locale.ENGLISH);
            return msg != null ? msg : "?" + message + "?";

        }
        else
            return message;
    }

    protected void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    protected void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    protected void setInlineFormatter(InlineFormatter inlineFormatter)
    {
        this.inlineFormatter = inlineFormatter;
    }
    
    public static String generateName(String name)
    {
        Assert.notNull(name, "Can not generate name from null string.");
        // Strip off package
        name = StringUtils.substringAfterLast(name, ".");
        // Make first letter lowercase
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
}
