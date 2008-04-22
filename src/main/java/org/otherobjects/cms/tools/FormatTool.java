package org.otherobjects.cms.tools;


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
        return formatTextile(textileSource, true);
    }

    public static String formatTextile(String textileSource, boolean alwaysEscapeHtmlSpecialChars)
    {
        return textileSource;
//        TextParser textileParser = new TextParser();
//        return textileParser.parseTextile(textileSource, !alwaysEscapeHtmlSpecialChars);
    }
}
