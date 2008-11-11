package org.otherobjects.cms.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

import org.otherobjects.cms.views.Tool;

/**
 * Generates URL to Gravatar images based on user email address.
 * 
 * <p>For more info on Gravatars see <a href="http://en.gravatar.com/">http://en.gravatar.com/</a>. Details of
 * the algorithm are available at <a href="http://en.gravatar.com/site/implement/url">http://en.gravatar.com/site/implement/url</a>.
 * 
 * TODO Add hasGravatar method
 * TODO Create Gravatar cache service
 * 
 * @author rich
 */
@Tool("gravatar")
public class GravatarUtils
{
    private static final String GRAVATAR_SERVER = "http://www.gravatar.com/avatar/";

    public String getUrl(String email)
    {
        return getUrl(email, 0, null);
    }

    public String getUrl(String email, int size)
    {
        return getUrl(email, size, null);
    }

    /**
     * Generated url of Gravatar image. 
     * 
     * @param email email address of user
     * @param size size in pixels (defaults to 80 if less than 1)
     * @param placeholder the full url of an image to use if the user has no Gravatar
     * 
     * @return
     */
    public String getUrl(String email, int size, String placeholder)
    {
        StringBuffer url = new StringBuffer(GRAVATAR_SERVER);

        // Add image name
        url.append(md5Hex(email));
        url.append(".jpg");

        // Add options
        StringBuffer options = new StringBuffer();
        if (size > 0)
            options.append("s=" + size);
        try
        {
            if (placeholder != null)
            {
                if (options.length() > 0)
                    options.append("&");
                options.append("d=" + URLEncoder.encode(placeholder, "UTF-8"));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            // Ignore -- UTF-8 must be supported
        }

        if (options.length() > 0)
            url.append("?" + options.toString());

        return url.toString();
    }

    private static String hex(byte[] array)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i)
        {
            sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
        }
        return sb.toString();
    }

    private static String md5Hex(String message)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            return hex(md.digest(message.getBytes("CP1252")));
        }
        catch (Exception e)
        {
            // Ignore
        }
        return null;
    }
}
