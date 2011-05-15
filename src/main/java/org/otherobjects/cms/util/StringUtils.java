package org.otherobjects.cms.util;

/**
 * Various string manipulation functions. 
 * 
 * <p>TODO Can we replace entirely with commons-lang?
 * 
 * @author rich
 */
public class StringUtils extends org.apache.commons.lang.StringUtils
{
    
    public static String trim(String content, int maxLength)
    {
        if(content == null)
            return null;
        
        // FIXME Test and improve this
        // FIXME Respect max length and all puctation
        if (content.length() > maxLength)
        {
            content = content.substring(0, maxLength);
            content = content.substring(0, content.lastIndexOf(' '));
            if (!content.endsWith("."))
                content += "...";
        }
        return content;
    }


    public static String generateLabel(String original)
    {
        String converted = new String();
        for (int i = 0; i < original.length(); i++)
        {
            char c = original.charAt(i);

            if (i == 0)
            {
                converted += String.valueOf(c).toUpperCase();
            }
            else if (c >= 'A' && c <= 'Z')
            {
                converted += " " + c;
            }
            else
            {
                converted += c;
            }
        }
        return (converted);
    }

    public static String generateUrlCode(String name)
    {
        if (name == null)
            return null;
        return name.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-\\s]", "");
    }

    public static String codeToClassName(String code)
    {
        StringBuffer name = new StringBuffer();
        boolean uppercase = true;
        for (int i = 0; i < code.length(); i++)
        {
            String nextChar = code.substring(i, i + 1);
            if (nextChar.equals("-"))
            {
                uppercase = true;
                continue;
            }
            if (uppercase)
            {
                name.append(nextChar.toUpperCase());
            }
            else
                name.append(nextChar);
            uppercase = false;
        }
        return name.toString();
    }

    public static String getExtension(String string) {
    	return substringAfterLast(string, ".");
    }
}
