package org.otherobjects.cms.beans;

import net.sf.cglib.core.DefaultNamingPolicy;
import net.sf.cglib.core.Predicate;

public class NamingPolicy extends DefaultNamingPolicy
{
    public String getClassName(String prefix, String source, Object key, Predicate names)
    {

        StringBuffer sb = new StringBuffer();
        sb.append((prefix != null) ? (prefix.startsWith("java") ? "$" + prefix : prefix) : "net.sf.cglib.empty.Object");
        sb.append("$$");
        sb.append(source.substring(source.lastIndexOf('.') + 1));
        sb.append("ByCGLIB$$");
        sb.append(Integer.toHexString(key.hashCode()));
        String base = sb.toString();
        String attempt = base;
        int index = 2;
        while (names.evaluate(attempt))
        {
            attempt = base + "_" + index++;
        }

        return attempt;
    }
}
