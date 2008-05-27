package org.otherobjects.cms.io;

import java.util.regex.Pattern;

/**
 * enum of path prefixes used to specify {@link OoResource}s in a generic kind of way
 * @author joerg
 *
 */
public enum OoResourcePathPrefix 
{

    CORE("core"), STATIC("static"), SITE("site"), DATA("data");

    private final String pathPrefix;
    private final Pattern pattern;

    OoResourcePathPrefix(String pathPrefix)
    {
        this.pathPrefix = pathPrefix;
        this.pattern = Pattern.compile("^/?" + pathPrefix + "/??");
    }

    public String pathPrefix()
    {
        return this.pathPrefix;
    }

    public Pattern pattern()
    {
        return this.pattern;
    }
}
