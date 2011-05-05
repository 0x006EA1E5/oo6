package org.otherobjects.cms.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author <a href="mailto:chris@othermedia.com">chris</a>
 */
public class WikiFormatter implements Formatter
{
    protected List<String> htmlRegions;
    protected List<String> codeRegions;
    protected List<HashMap<String, String>> subZones;

    /**
     * Formats Wiki text into HTML
     * 
     * @param input
     * @return
     */
    public String format(String input)
    {
  
        // Remove any rogue \r
        input = input.replaceAll("\r", "");

        // Pull out blocks that should not be formatted
        input = removeCodeRegions(input);

        // pull out any sub sones ro be formatted in their own div tags
        input = removeSubZones(input);

        // Format 
        List<String> zones = splitZones(input);
        Iterator<String> iterator = zones.iterator();
        StringBuffer formattedHtml = new StringBuffer();
        while (iterator.hasNext())
        {
            String zone = iterator.next();
            formattedHtml.append(zone);
        }

        // Reinsert blocks that should not be formatted
        input = insertCodeRegions(formattedHtml.toString());
        // format and reinsert sub zones
        input = insertSubZones(input);

        input = wrapLongLines(input);

        return input;
    }

    /**
     * Insert a few word break characters in url strings. To prevent long urls 
     * from exploding layout boxes.
     * 
     * @param input
     * @return
     */
    protected String wrapLongLines(String input)
    {
        return input;
        // FIXME Restors wrapping but only for captions -- not for URL!
        // return input.replaceAll("([^/^>^<])(/)([^/^>^<])", "$1$2<wbr/>$3");
    }

    /**
     * @param input
     * @return
     */
    protected String removeCodeRegions(String input)
    {
        // Pull out [code] and [html] blocks
        this.codeRegions = new ArrayList<String>();
        this.htmlRegions = new ArrayList<String>();
        StringBuffer inputWithoutCodeRegions = new StringBuffer();
        Pattern p = Pattern.compile("\\[(code|html)\\]\n*(.*?)\n*\\[/\\1\\]", Pattern.DOTALL);
        Matcher m = p.matcher(input);

        while (m.find())
        {
            String tag = m.group(1);
            if (tag.equals("html"))
            {
                m.appendReplacement(inputWithoutCodeRegions, "|||Html Region|||");
                htmlRegions.add(m.group(2));
            }
            else if (tag.equals("code"))
            {
                m.appendReplacement(inputWithoutCodeRegions, "|||Code Region|||");
                codeRegions.add(m.group(2));
            }
        }
        m.appendTail(inputWithoutCodeRegions);
        return inputWithoutCodeRegions.toString();
    }

    /**
     * @param input
     * @return
     */
    protected String insertCodeRegions(String input)
    {
        // remove unnecessary paragraph tags introducted in formatting html       
        input = input.replaceAll("<p>\\|\\|\\|", "\\|\\|\\|").replaceAll("\\|\\|\\|</p>", "\\|\\|\\|");
        StringBuffer inputWithCodeRegions = new StringBuffer();

        Pattern p = Pattern.compile("\\|\\|\\|(Html|Code) Region\\|\\|\\|");
        Matcher m = p.matcher(input);

        Iterator<String> codeRegionsIterator = this.codeRegions.iterator();
        Iterator<String> htmlRegionsIterator = this.htmlRegions.iterator();

        while (m.find())
        {
            String tag = m.group(1);
            if (tag.equals("Html"))
            {
                String replacement = htmlRegionsIterator.next();
                replacement = replacement.replaceAll("\\$", "\\\\\\$");
                m.appendReplacement(inputWithCodeRegions, replacement);
            }
            else if (tag.equals("Code"))
            {
                String replacement = codeRegionsIterator.next();
                replacement = replacement.replaceAll("\\$", "\\\\\\$");
                replacement = replacement.replaceAll("\\<", "&lt;");
                replacement = replacement.replaceAll("\\>", "&gt;");
                m.appendReplacement(inputWithCodeRegions, "<pre>" + replacement + "</pre>");
            }
        }
        m.appendTail(inputWithCodeRegions);
        return inputWithCodeRegions.toString();
    }

    /**
     * @param input
     * @return
     */
    protected String removeSubZones(String input)
    {
        // Pull out [quote] blocks
        this.subZones = new ArrayList<HashMap<String, String>>();
        StringBuffer inputWithoutSubZones = new StringBuffer();
        Pattern p = Pattern.compile("\\[([-0-9a-zA-Z]+)( *\\| *([^\\]]+))?\\]\n*(.*?)\n*\\[/\\1\\]", Pattern.DOTALL);
        Matcher m = p.matcher(input);

        while (m.find())
        {
            String tag = m.group(1);
            m.appendReplacement(inputWithoutSubZones, "|||subzone|||");
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("tagName", tag);
            hm.put("tagBody", m.group(3));
            hm.put("contents", m.group(4));
            subZones.add(hm);
        }
        m.appendTail(inputWithoutSubZones);
        return inputWithoutSubZones.toString();
    }

    /**
     * @param input
     * @return
     */
    protected String insertSubZones(String input)
    {
        // remove unnecessary paragraph tags introducted in formatting html       
        input = input.replaceAll("<p>\\|\\|\\|", "\\|\\|\\|").replaceAll("\\|\\|\\|</p>", "\\|\\|\\|");

        StringBuffer inputWithSubZones = new StringBuffer();
        Pattern p = Pattern.compile("\\|\\|\\|subzone\\|\\|\\|");
        Matcher m = p.matcher(input);

        Iterator<HashMap<String, String>> subZonesIterator = this.subZones.iterator();

        while (m.find())
        {
            HashMap<String, String> hm = subZonesIterator.next();
            String tagName = (String) hm.get("tagName");
            String tagBody = (String) hm.get("tagBody");
            String contents = (String) hm.get("contents");

            String replacement = format(contents);

            HashMap<String, String> attributes = parseTagAttributes(tagBody);

            String align = (String) attributes.get("align");
            if (align != null && (align.equals("left") || align.equals("right")))
                tagName += "-" + align;
            replacement = "<div class=\"" + tagName + "\">" + replacement.replaceAll("\\$", "\\\\\\$") + "</div>";
            m.appendReplacement(inputWithSubZones, replacement);
        }
        m.appendTail(inputWithSubZones);
        return inputWithSubZones.toString();
    }

    protected HashMap<String, String> parseTagAttributes(String tagBody)
    {
        HashMap<String, String> attributes = new HashMap<String, String>();
        int pos = 0;

        try
        {
            Pattern pattern = Pattern.compile("([^\\|^\\(=|:)]+)[=|:]?([^\\|]*)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(tagBody);

            while (matcher.find())
            {
                if (matcher.group(1) != null)
                {
                    attributes.put(matcher.group(1).toLowerCase().trim(), matcher.group(2).trim());
                }
            }
        }
        catch (Exception e)
        {
            // do nothing
        }

        return attributes;
    }

    /**
     * Splits source test into table and non-tables areas.
     * 
     * @param input
     * @return
     */
    protected List<String> splitZones(String input)
    {
        // Regex seaches for areas tables zones
        // Rules is: 
        //     (^|\\n\\n) - Search for begining of line or double carrige return
        //     (\\{(.+)\\}\\n)? - Search for an optional css class line e.g. {red}
        //     ={3,} - Search for three or more equal signs
        //     (...)+? - Check for the next group repeating one or more times
        //          Inside the brackets:
        //          \\n[^\\n]*? - means check for zero or more non-carrige return charcters preceded by a carrige return
        //     ={3,} - Then finish with series of equal signs
        //     (?=(\\n\\n|$)) - as long as they are followed by two carrige returns or the end of the line
        Pattern p = Pattern.compile("(^|\\n\\n)(\\{(.+)\\}\\n)?={3,}(\\n[^\\n]*?)+?={3,}(?=(\\n\\n|$))");
        //For Columns: Pattern p2 = Pattern.compile("(^|\\n\\n)(\\{(.+)\\}\\n)?\\|{3,}(\\n[^\\n]*?)+?\\|{3,}(?=(\\n\\n|$))");

        Matcher m = p.matcher(input);

        List<String> zones = new ArrayList<String>();
        int lastZoneEnd = 0;

        while (m.find())
        {
            // Previous non-table zone
            if (lastZoneEnd != m.start())
            {
                String zone = input.substring(lastZoneEnd, m.start());
                zones.add(formatTextZone(zone));
            }

            // Table zone
            String zone = m.group();
            zones.add(formatTableZone(zone));

            lastZoneEnd = m.end();
        }

        // Add last tail zone;
        if (lastZoneEnd != input.length())
        {
            String zone = input.substring(lastZoneEnd);
            zones.add(formatTextZone(zone));
        }

        return zones;
    }

    /**
     * Formats a Wiki table into HTML.
     * 
     * @param input
     * @return
     */
    protected String formatTableZone(String input)
    {

        // Strip off any leading carriage returns
        input = input.replaceAll("\\A\\n{0,2}", "");

        String[] rows = input.split("\\n?\\n?={3,}\\n?\\n?");
        StringBuffer tableHtml = new StringBuffer();
        boolean firstCellRow = true;
        boolean rowOdd = true;
        String tableAttributes = "";

        for (int i = 0; i < rows.length; i++)
        {
            String row = rows[i];
            StringBuffer rowHtml = new StringBuffer();

            if (row.indexOf("---") > 0)
            {
                // Cell row
                if (firstCellRow == true)
                {
                    rowHtml.append("<tbody>");
                    firstCellRow = false;
                }

                // Set odd or even row class
                if (rowOdd)
                {
                    rowHtml.append("<tr class=\"odd\">");
                }
                else
                {
                    rowHtml.append("<tr class=\"even\">");
                }
                rowOdd = !rowOdd;

                String[] cells = row.split("\\n?-{3,}\\n?");
                for (int j = 0; j < cells.length; j++)
                {
                    String cellAttributes = "";
                    String cell = cells[j];
                    // Deal with attributes
                    if (cell.startsWith("{"))
                    {
                        // Take curly bracketed part and parse attributes, leave remainder for formatting
                        cellAttributes = parseAttributes(cell.substring(0, cell.indexOf("}") + 1));
                        cell = cell.substring(cell.indexOf("}") + 1);
                    }
                    rowHtml.append("<td" + cellAttributes + ">" + formatTextZone(cell) + "</td>");
                }
                rowHtml.append("</tr>");
            }
            else if (row.indexOf("+++") > 0)
            {
                // Header row
                rowHtml.append("<thead><tr>");
                String[] cells = row.split("\\n?\\+{3,}\\n?");
                for (int j = 0; j < cells.length; j++)
                {
                    String cellAttributes = "";
                    String cell = cells[j];
                    // Deal with attributes
                    if (cell.startsWith("{"))
                    {
                        // Take curly bracketed part and parse attributes, leave remainder for formatting
                        cellAttributes = parseAttributes(cell.substring(0, cell.indexOf("}") + 1));
                        cell = cell.substring(cell.indexOf("}") + 1);
                    }
                    // Note that Cell Header contents are only inline formatted
                    rowHtml.append("<th" + cellAttributes + ">" + formatInlineText(cell) + "</th>");
                }
                rowHtml.append("</tr></thead>");
            }
            else if (row.startsWith("{"))
            {
                tableAttributes = parseAttributes(row);
            }
            else
            {
                // Caption
                if (row.length() > 0)
                    rowHtml.append("<caption>" + row + "</caption>");
            }
            tableHtml.append(rowHtml);
        }

        String t = "<table" + tableAttributes + ">" + tableHtml.toString() + "</tbody></table>";
        return t;
    }

    protected String parseAttributes(String attribute)
    {
        // Strip off curly brackets
        attribute = attribute.substring(1, attribute.length() - 1);
        // If we have a = then we are extra attributes (eg class="red" colspan="2")
        if (attribute.indexOf("=") >= 0)
        {
            return " " + attribute;
        }
        return " class=\"" + attribute + "\"";
    }

    /**
     * Formats a non-table Wiki text into HTML.
     * 
     * @param input
     * @return
     */
    protected String formatTextZone(String input)
    {
        // Remove leading carriage returns
        input = input.replaceFirst("^\n*", "");

        // Split zone into units separated by two or more carrige returns
        String[] textUnits = input.split("\\n{2,}");
        StringBuffer textZoneHtml = new StringBuffer();

        for (int i = 0; i < textUnits.length; i++)
        {
            // Check first chars of every unit to determine if unit is a list or a paragraph etc.
            String unit = textUnits[i];

            StringBuffer unitHtml = new StringBuffer();

            // Look for styling
            Pattern pattern = Pattern.compile("(\\{.*?\\})(\\n)?", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(unit);
            String unitAttributes = "";
            if (matcher.find())
            {
                StringBuffer removed = new StringBuffer();
                String braceContents = matcher.group(1);
                unitAttributes = parseAttributes(braceContents);
                matcher.appendReplacement(removed, "");
                matcher.appendTail(removed);
                unit = removed.toString();
            }

            // Check for Bulleted list
            if (unit.startsWith("* "))
            {
                unitHtml.append(formatBulletedList(unit, unitAttributes));
            }
            // Check for legacy Bulleted list (no space after *)
            else if (unit.startsWith("*") && !unit.matches("(?s)^\\*([^\\*]*?)[^\\s]\\*(\\s.*|$)"))
            {
                unitHtml.append(formatBulletedList(unit, unitAttributes));
            }
            // Check for Numbered list
            else if (unit.startsWith("#"))
            {
                unitHtml.append(formatNumberedList(unit, unitAttributes));
            }
            // Check for Heading
            else if (unit.startsWith("!"))
            {
                unitHtml.append(formatHeading(unit, unitAttributes));
            }
            // Check for Inline tag as its own block.
            else if (unit.startsWith("[") && (unit.indexOf(']')==unit.lastIndexOf(']')) && unit.toLowerCase().indexOf("block:y")>0)
            {
                unitHtml.append(formatInlineText(unit));
            }
            // Unit is a normal paragraph
            else
            {
                unitHtml.append("<p" + unitAttributes + ">" + formatInlineText(unit) + "</p>");
            }
            textZoneHtml.append(unitHtml);
        }

        return textZoneHtml.toString();

    }

    /**
     * Formats a inline Wiki text into HTML.
     * 
     * @param input
     * @return
     */
    public String formatInlineText(String input)
    {
        //Remove leading and trailing carrige returns
        input = input.replaceAll("(^\\n|\\n$)", "");

        // Replace ampersands first so that HTML entities can be added. 
        input = input.replaceAll("&", "&amp\\;");

        // Replace arrow with HTML equivalents
        input = input.replaceAll("<-{2}>", "&harr\\;");
        input = input.replaceAll("-{2}>", "&rarr\\;");
        input = input.replaceAll("<-{2}", "&larr\\;");

        // Replace double arrow with HTML equivalents
        input = input.replaceAll("<={2}>", "&hArr\\;");
        input = input.replaceAll("={2}>", "&rArr\\;");
        input = input.replaceAll("<={2}", "&lArr\\;");

        input = formatAngledBrackets(input);

        // Replace lines of spaces with a single non-breaking space
        input = input.replaceAll("^\\s+$", "&nbsp\\;");

        // Bold
        input = input.replaceAll("(^|\\W|_)\\*(?!\\s)([^\\*]*?[^\\s])\\*(?=\\W|_|$)", "$1<strong>$2</strong>");

        // Italics
        input = input.replaceAll("(^|\\W)_(?!\\s)([^\\n]*?)_(?=\\W|$)", "$1<em>$2</em>");

        // Superscript
        input = input.replaceAll("\\^([^\\n]*?)\\^", "<sup>$1</sup>");

        // Subscript
        input = input.replaceAll("~([^\\n]*?)~", "<sub>$1</sub>");

        // Replace all carrige returns with BR tags
        input = input.replaceAll("\\n\\s*", "<br />");

        // TODO Add inline text class and attribute support
        return input;
    }

    /**
     * @param input
     * @return
     */
    protected String formatAngledBrackets(String input)
    {
        // Replace angled brackets HTML equivalents
        input = input.replaceAll("<", "&lt\\;");
        input = input.replaceAll(">", "&gt\\;");
        return input;
    }

    /**
     * Formats a Wiki headings into HTML.
     * 
     * @param input
     * @param unitAttributes 
     * @return
     */
    protected String formatHeading(String input, String unitAttributes)
    {
        StringBuffer headingHtml = new StringBuffer();

        // Check to see which heading level is required (1, 2 or 3 adjusted by the firstHeadingLevel offset
        // now put in to try/catch structure primarily for JUnit test conditions when no configuration props available

        int baseHeadingLevel = 2;
        try
        {
            baseHeadingLevel = 2;
        }
        catch (Exception e)
        {
            baseHeadingLevel = 2;
        }

        if (input.startsWith("!!!"))
        {
            // Remove markup
            input = input.replaceFirst("!!!\\s*", "");
            headingHtml.append("<h" + (baseHeadingLevel + 2) + unitAttributes + ">" + formatInlineText(input) + "</h" + (baseHeadingLevel + 2) + ">");
        }
        else if (input.startsWith("!!"))
        {
            // Remove markup
            input = input.replaceFirst("!!\\s*", "");
            headingHtml.append("<h" + (baseHeadingLevel + 1) + unitAttributes + ">" + formatInlineText(input) + "</h" + (baseHeadingLevel + 1) + ">");
        }
        else if (input.startsWith("!"))
        {
            // Remove markup
            input = input.replaceFirst("!\\s*", "");
            headingHtml.append("<h" + baseHeadingLevel + unitAttributes + ">" + formatInlineText(input) + "</h" + baseHeadingLevel + ">");
        }
        return headingHtml.toString();
    }

    protected String formatNumberedList(String input, String unitAttributes)
    {
        StringBuffer numberedListHtml = new StringBuffer();
        numberedListHtml.append("<ol" + unitAttributes + ">");

        // Remove first hash
        input = input.replaceFirst("\\n?#\\s?", "");

        // Now split unit into lines separated by a carrige return and a hash 
        String[] numberedLines = input.split("\\n#\\s?");

        for (int i = 0; i < numberedLines.length; i++)
        {
            String numberedLine = numberedLines[i];
            int startSubList;
            StringBuffer numberedLineHtml = new StringBuffer();

            numberedLineHtml.append("<li>");

            // Check for indented bullet list
            if ((startSubList = numberedLine.indexOf("\n *")) > 0)
            {
                // Add Formatted text to start of list
                numberedLineHtml.append(formatInlineText(numberedLine.substring(0, startSubList)));

                // Remove single space before bullets and hashes from remaining text.
                // This means that the remaining text now looks like a normal list.
                numberedLine = numberedLine.substring(startSubList);
                numberedLine = numberedLine.replaceAll("\\s(?=\\*)", "");
                numberedLine = numberedLine.replaceAll("\\s(?=\\#)", "");

                //Format the sub bulleted list.
                numberedLineHtml.append(formatBulletedList(numberedLine, ""));
            }
            // Check for indent numbered list
            else if ((startSubList = numberedLine.indexOf("\n #")) > 0)
            {
                // Add Formatted text to start of list
                numberedLineHtml.append(formatInlineText(numberedLine.substring(0, startSubList)));

                // Remove single space before bullets and hashes from remaining text.
                // This means that the remaining text now looks like a normal list.
                numberedLine = numberedLine.substring(startSubList);
                numberedLine = numberedLine.replaceAll("\\s(?=\\*)", "");
                numberedLine = numberedLine.replaceAll("\\s(?=\\#)", "");

                //Format the sub bulleted list.
                numberedLineHtml.append(formatNumberedList(numberedLine, ""));
            }
            else
            // No indented lists so insert formatted line
            {
                numberedLineHtml.append(formatInlineText(numberedLine));
            }

            numberedLineHtml.append("</li>");

            numberedListHtml.append(numberedLineHtml);
        }

        numberedListHtml.append("</ol>");
        return numberedListHtml.toString();
    }

    protected String formatBulletedList(String input, String unitAttributes)
    {
        StringBuffer bulletListHtml = new StringBuffer();
        bulletListHtml.append("<ul" + unitAttributes + ">");

        // Remove first star
        input = input.replaceFirst("\\n?\\*\\s?", "");

        // Now split unit into lines separated by a carrige return and a star 
        String[] bulletLines = input.split("\\n\\*\\s?");

        for (int i = 0; i < bulletLines.length; i++)
        {
            String bulletLine = bulletLines[i];
            int startSubList;
            StringBuffer bulletLineHtml = new StringBuffer();

            bulletLineHtml.append("<li>");

            // Check for indented bullet list
            if ((startSubList = bulletLine.indexOf("\n *")) > 0)
            {
                // Add Formatted text to start of list
                bulletLineHtml.append(formatInlineText(bulletLine.substring(0, startSubList)));

                //Remove single space before bullets and hashes from remaining text
                bulletLine = bulletLine.substring(startSubList);
                bulletLine = bulletLine.replaceAll("\\s(?=\\*)", "");
                bulletLine = bulletLine.replaceAll("\\s(?=\\#)", "");

                //Add the formatted sub bulleted list.
                bulletLineHtml.append(formatBulletedList(bulletLine, ""));
            }
            // Check for indent numbered list
            else if ((startSubList = bulletLine.indexOf("\n #")) > 0)
            {
                // Add Formatted text to start of list
                bulletLineHtml.append(formatInlineText(bulletLine.substring(0, startSubList)));

                // Remove single space before bullets and hashes from remaining text
                bulletLine = bulletLine.substring(startSubList);
                bulletLine = bulletLine.replaceAll("\\s(?=\\*)", "");
                bulletLine = bulletLine.replaceAll("\\s(?=\\#)", "");

                //Add the formatted sub bulleted list.
                bulletLineHtml.append(formatNumberedList(bulletLine, ""));
            }
            else
            // No indented lists so insert formatted line
            {
                bulletLineHtml.append(formatInlineText(bulletLine));
            }

            bulletLineHtml.append("</li>");

            bulletListHtml.append(bulletLineHtml);
        }

        bulletListHtml.append("</ul>");
        return bulletListHtml.toString();
    }

}
