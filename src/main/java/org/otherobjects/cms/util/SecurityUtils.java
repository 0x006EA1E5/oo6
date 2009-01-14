package org.otherobjects.cms.util;

/**
 * FIXME Confused with SecurityUtil
 * @author rich
 *
 */
public class SecurityUtils
{
    public static String md5Hash(String text)
    {
        return null;
    }

    /**
     * A simple random password generator, user can specify the length.
     * 
     * Copyright 1999 - 2003 Roseanne Zhang, All Rights Reserved
     * http://bobcat.webappcabaret.net/javachina/jc/share/PwGen.htm
     */
    public static String generatePassword(int length)
    {
        char[] pw = new char[length];
        int c = 'A';
        int r1 = 0;
        for (int i = 0; i < length; i++)
        {
            r1 = (int) (Math.random() * 3);
            switch (r1)
            {
                case 0 :
                    c = '0' + (int) (Math.random() * 10);
                    break;
                case 1 :
                    c = 'a' + (int) (Math.random() * 26);
                    break;
                case 2 :
                    c = 'a' + (int) (Math.random() * 26);
                    break;
            }
            pw[i] = (char) c;
        }
        return new String(pw);
    }
}
