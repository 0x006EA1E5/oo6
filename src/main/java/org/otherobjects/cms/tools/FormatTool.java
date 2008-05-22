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
        // TODO This needs to be optimesd
        // TODO Add support for additional markups
        MarkupParser parser = new MarkupParser(new TextileDialect());
        StringWriter out = new StringWriter();
        HtmlDocumentBuilder builder = new HtmlDocumentBuilder(out);
        builder.setEmitAsDocument(false);
        parser.setBuilder(builder);
        parser.parse(textileSource);
        parser.setBuilder(null);
        return out.toString();
    }
}
