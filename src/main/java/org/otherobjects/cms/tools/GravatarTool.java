/*
 * This file is part of the OTHERobjects Content Management System.
 * 
 * Copyright 2007-2009 OTHER works Limited.
 * 
 * OTHERobjects is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OTHERobjects is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OTHERobjects.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.otherobjects.cms.tools;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;

import javax.annotation.Resource;

import org.otherobjects.cms.model.User;
import org.otherobjects.cms.model.UserDao;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

/**
 * Generates URL to Gravatar images based on user email address.
 * 
 * <p>For more info on Gravatars see <a href="http://en.gravatar.com/">http://en.gravatar.com/</a>. Details of
 * the algorithm are available at <a href="http://en.gravatar.com/site/implement/url">http://en.gravatar.com/site/implement/url</a>.
 * 
 * @author rich
 */
@Component
@Tool
public class GravatarTool
{
    private static final String GRAVATAR_SERVER = "http://www.gravatar.com/avatar/";
    private static final String GRAVATAR_SECURE_SERVER = "https://secure.gravatar.com/avatar/";

    @Resource
    protected UserDao userDao;

    /**
     * Generates url of Gravatar image for provided user. Username must be valid.
     *  
     * @param username the username for the required user
     * @param size width in pixels of required image
     * @return the url to the image
     */
    public String getUrl(String username, int size, boolean ssl)
    {
        return getUrl(username, size, null, ssl);
    }
    
    /**
     * Generates url of Gravatar image for provided user. Username must be valid.
     * If the user does not have a Gravatar image then the provided placeholder image url 
     * is returned.
     *  
     * @param username the username for the required user
     * @param size width in pixels of required image
     * @param placeholder the url of image to use when no Gravatar is available
     * @return the url to the image
     */
    public String getUrl(String username, int size, String placeholder, boolean ssl)
    {
        User user = (User) userDao.loadUserByUsername(username);
        return getUrlForEmail(user.getEmail(), size, null, ssl);
    }

    /**
     * Generates url of Gravatar image for provided user. The email address does not 
     * need to be for a user in OTHERobjects.
     * 
     * If the user does not have a Gravatar image then the provided placeholder image url 
     * is returned (provide a null placeholder to use the default Gravatar one). 
     *  
     * @param email the email address for the required user
     * @param size width in pixels of required image
     * @param placeholder the url of image to use when no Gravatar is available
     * @param ssl whether or not to get an image from a secure Gravatar url 
     * @return the url to the image
     */
    public String getUrlForEmail(String email, int size, String placeholder, boolean ssl)
    {
        StringBuffer url = new StringBuffer(ssl ? GRAVATAR_SECURE_SERVER : GRAVATAR_SERVER);
        
        // Add image name
        url.append(md5Hex(email));
        url.append(".jpg");

        // Add options
        StringBuffer options = new StringBuffer();
        if (size > 0)
        {
            options.append("s=" + size);
        }
        try
        {
            if (placeholder != null)
            {
                if (options.length() > 0)
                {
                    options.append("&");
                }
                options.append("d=" + URLEncoder.encode(placeholder, "UTF-8"));
            }
        }
        catch (UnsupportedEncodingException e)
        {
            // Ignore -- UTF-8 must be supported
        }

        if (options.length() > 0)
        {
            url.append("?" + options.toString());
        }

        return url.toString();
    }
    
    public String getUrlForEmail(String email, int size, String placeholder)
    {
        return getUrlForEmail(email, size, placeholder, false);
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
