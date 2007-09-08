package org.otherobjects.cms.util;

/**
 * Various string manipulation functions. 
 * 
 * <p>TODO Can we replace entirely with commons-lang?
 * 
 * @author rich
 */
public class StringUtils
{
    
    public static String generateLabel(String original)
    {
        String converted = new String();
        for (int i = 0; i < original.length(); i++)
        {
            char c = original.charAt(i);

            if (i == 0)
            {
                converted +=  String.valueOf(c).toUpperCase();
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
        if(name==null) return null;
        return name.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-\\s]", "");
    }
    
}
