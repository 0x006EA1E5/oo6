package org.otherobjects.cms.io;

import java.util.regex.Pattern;

/**
 * enum of path prefixes used to specify {@link OoResource}s in a generic kind of way
 * 
 *  /public-data/
 *  /private-data/
 *  /private-data/uploads/
 *  
 *  /site/ = file:webapp/WEB-INF/
 *  /site/static = file:webapp/static
 *  
 *  /otherobjects = classpath:otherobjects.resources/
 *  /otherobjects/static = classpath:otherobjects.resources/static
 *  
 *  /tmp
 *  
 *  /widgets...
 *  /plugins...
 *  
 *  /other*
 * 
 * @author joerg
 *
 */
public enum OoResourcePathPrefix {

    CORE("core", "otherobjects.resources"), STATIC("static", "site.resources/static"), SITE("site", "WEB-INF"), OTHEROBJECTS("otherobjects", "otherobjects.resources"), DATA("data", null), UPLOAD("upload", null);

    private final String pathPrefix;
    private final String replacementFilePathPrefix;
    private final Pattern pattern;

    OoResourcePathPrefix(String pathPrefix, String replacementFilePathPrefix)
    {
        this.pathPrefix = pathPrefix;
        this.replacementFilePathPrefix = replacementFilePathPrefix;
        this.pattern = Pattern.compile("^/?" + pathPrefix + "/??"); // the second slash is matched reluctantly so that if you use the pattern for replacement the slash after pathPrefix will not actually be replaced
    }

    public String pathPrefix()
    {
        return this.pathPrefix;
    }

    public String replacementFilePathPrefix()
    {
        return this.replacementFilePathPrefix;
    }

    public Pattern pattern()
    {
        return this.pattern;
    }
}
