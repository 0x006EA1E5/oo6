package org.otherobjects.cms.tools;

import java.io.StringWriter;
import java.util.Locale;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

import org.springframework.context.MessageSource;

/**
 * Tool to be used from templates to aid in generating formatted text.
 * @author joerg
 *
 */
public class FormatTool
{
    private MessageSource messageSource;
    
    public FormatTool(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    /**
     * Formats textile string into HTML. HTML special chars in the textileSource will get escaped (notably the less than and greater than signs)
     * @param textileSource
     * @return
     */
    public static String formatTextile(String textileSource)
    {

        // Remove double line breaks
        String text = textileSource.replaceAll("/\n\n/", "\n");

        // Recode headings
        text = text.replaceAll("(?m)^\\!!!", "h3. ");
        text = text.replaceAll("(?m)^\\!!", "h2. ");
        text = text.replaceAll("(?m)^\\!", "h1. ");

        // Recode code blocks
        text = text.replaceAll("\\[code\\]", "<pre>\n<code>\n");
        text = text.replaceAll("\\[/code\\]", "\n</code>\n</pre>");

        // Recode links
        text = text.replaceAll("\\[LINK:([^|]*)\\|CAPTION:([^\\]]*)\\]", "\"$2\":$1");
        text = text.replaceAll("(?m)\\[LINK:([^]]*)\\]", "\"$1\":$1");

        // TODO This needs to be optimesd
        // TODO Add support for additional markups
        MarkupParser parser = new MarkupParser(new TextileDialect());
        StringWriter out = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
        builder.setEmitAsDocument(false);
        parser.setBuilder(builder);
        parser.parse(text);
        parser.setBuilder(null);
        return out.toString();
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
        if(message.startsWith("$"))
        {
            // Message
            // FIXME Get locale from somewhere better
            return messageSource.getMessage(message.substring(2, message.length()-1), null, Locale.ENGLISH);
        }
        if(message.contains("."))
        {
            // Message
            // FIXME proper regexp here
            return messageSource.getMessage(message, null, Locale.ENGLISH);
        }
        else
            return message;
    }

    protected void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }
}
