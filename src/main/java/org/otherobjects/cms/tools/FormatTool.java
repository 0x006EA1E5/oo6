package org.otherobjects.cms.tools;

import java.io.StringWriter;

import net.java.textilej.parser.MarkupParser;
import net.java.textilej.parser.builder.HtmlDocumentBuilder;
import net.java.textilej.parser.markup.textile.TextileDialect;

/**
 * Tool to be used from templates to aid in generating formatted text.
 * @author joerg
 *
 */
public class FormatTool
{
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
}
