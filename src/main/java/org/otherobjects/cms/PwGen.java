package org.otherobjects.cms;

class PwGen
{
    public static String getPassword(int n)
    {
        char[] pw = new char[n];
        int c = 'A';
        int r1 = 0;
        for (int i = 0; i < n; i++)
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

    public static void main(String[] args)
    {
        int len = 8; // default 8 on unix, more is useless

        if (args.length >= 1)
        {
            try
            {
                len = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException nfe)
            {
            }
        }
        System.out.println(getPassword(len));
    }
}
