package org.otherobjects.cms;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.context.GlobalInfoBean;
import org.otherobjects.cms.context.RequestContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import flexjson.JSON;

/**
 * Useful representation of Urls in a webapp. Allows you to change individual properties of a url. Its toString() method will try to return a string representation 
 * suitable for use in the href attribute of an A tag.  
 * @author joerg
 *
 */
public class Url
{

    protected final static Logger logger = LoggerFactory.getLogger(Url.class);

    public static final int STANDARD_HTTP_PORT = 80;
    public static final int STANDARD_HTTPS_PORT = 443;

    public static final String STANDARD_HTTP_SCHEME = "http";
    public static final String STANDARD_HTTPS_SCHEME = "https";

    public static final String STANDARD_URL_CHARACTER_ENCODING = "UTF-8";

    private static final String ILLEGAL_MODIFIATION_MESSAGE = "This URL was created as immuteable. None of its properties can be changed";

    //private static Pattern absolutePattern = Pattern.compile("^([a-zA-Z]*)://");
    //private static Pattern urlPattern = Pattern.compile("([a-zA-Z]*)://([^/]*)?(:\\d+)?/(.*)(#(.*))?");

    private String link;
    private boolean modifieable = true;

    private String scheme;
    private String serverName;
    private int port = -1;
    private String path;
    private String query;
    private String ref;
    private boolean ssl;
    private boolean absolute;

    public Url(String link)
    {
        this(link, true);
    }

    public Url(String link, boolean modifieable)
    {
        this.link = link;
        parseUrl();
        Assert.isTrue(isAbsolute() || link.startsWith("/"), "Url links can only be absolute http links or site internal links starting with a slash");
        this.modifieable = modifieable;
    }

    private void parseUrl()
    {
        //Matcher matcher = urlPattern.matcher(url.getLink());
        try
        {
            URI parsedUrl = new URI(getLink());
            scheme = parsedUrl.getScheme();
            serverName = parsedUrl.getHost();
            port = parsedUrl.getPort();
            path = parsedUrl.getPath();
            query = parsedUrl.getQuery();
            ref = parsedUrl.getFragment();
            ssl = (scheme != null) ? scheme.toLowerCase().equals("https") : false;
            absolute = parsedUrl.isAbsolute();
        }
        catch (URISyntaxException e)
        {
            logger.warn("Couldn't parse " + this.getLink());
        }
    }

    @JSON(include = false)
    public String getAbsoluteLink()
    {
        //TODO wouldn't it be better to create the the link in any case
        //TODO shouldn't this throw an exception if this is not modifieable and not absolute?
        if (absolute) // link is already absolute
            return getLink();
        else
        // non-absolute link
        {
            StringBuffer newUrl = new StringBuffer();
            String scheme = calcScheme();
            newUrl.append(scheme);
            newUrl.append("://");
            newUrl.append(RequestContextUtils.getServerName());
            int serverPort = calcPort();

            if (!((scheme.equals(STANDARD_HTTP_SCHEME) && serverPort == STANDARD_HTTP_PORT) || (scheme.equals(STANDARD_HTTPS_SCHEME) && serverPort == STANDARD_HTTPS_PORT)))
                newUrl.append(":" + serverPort);
            try
            {
                newUrl.append(RequestContextUtils.getContextPath());
                newUrl.append(getLink());
                if (getQuery() != null)
                    newUrl.append("?" + URLEncoder.encode(getQuery(), STANDARD_URL_CHARACTER_ENCODING));
                if (getRef() != null)
                    newUrl.append("#" + URLEncoder.encode(getRef(), STANDARD_URL_CHARACTER_ENCODING));
            }
            catch (UnsupportedEncodingException e)
            {
                logger.warn("Error when trying to encode Url parts", e);
            }
            return newUrl.toString();
        }
    }

    /**
     * determine the scheme using this pattern:
     * <ol>
     *  <li>If this.ssl is true then this will be https</li>
     *  <li>if this.scheme != null then this.scheme</li>
     *  <li>if there is a current request use the scheme of that</li>
     *  <li>default to http</li>
     * </ol>
     * @return
     */
    private String calcScheme()
    {
        if (isSsl())
            return STANDARD_HTTPS_SCHEME;

        if (StringUtils.isNotBlank(getScheme()))
            return getScheme();

        HttpServletRequest req = getOngoingRequest();
        if (req != null)
            return req.getScheme();

        return STANDARD_HTTP_SCHEME;
    }

    /**
     * determine port using this pattern:
     * <ol>
     *  <li>if this.ssl is true and there is an ongoing secure request take that port</li>
     *  <li>if this.ssl is false and there is an ongoing non-secure request take that port</li>
     *  <li>take globalInfo port depending on this.ssl</li>
     * </ol>
     * @return
     */
    private int calcPort()
    {
        HttpServletRequest req = getOngoingRequest();
        if (req != null)
        {
            if (req.isSecure() == isSsl())
                return req.getServerPort();
        }

        if (isSsl())
            return Integer.parseInt(GlobalInfoBean.getInstance().getProperty(GlobalInfoBean.DEFAULT_SECURE_PORT_KEY));
        else
            return Integer.parseInt(GlobalInfoBean.getInstance().getProperty(GlobalInfoBean.DEFAULT_PORT_KEY));
    }

    private HttpServletRequest getOngoingRequest()
    {
        return RequestContextUtils.getHttpServletRequest();
    }

    /**
     * Returns a URL that is suitable for use in an href attribute of the A tag. If this was set to be immutable the link given at creation time will be returned no matter what.
     * If this was not set to be immutable the absolute link will only be returned, if the link was created as absolute or if it is a secure link but the current request isn't secure.
     */
    @Override
    public String toString()
    {
        if (!modifieable)
            return getLink();

        if (isSsl() && !RequestContextUtils.isSecureRequest())
            return getAbsoluteLink();

        if (absolute)
            return link;
        else
        {
            return RequestContextUtils.getContextPath() + getLink();
        }
    }

    public int getDepth()
    {
        int count = 0;
        int startIndex = 0;

        while ((startIndex = getPath().indexOf('/', startIndex)) > -1)
        {
            startIndex++;
            count++;
        }
        return count - 1;
    }

    public boolean isModifieable()
    {
        return modifieable;
    }

    public String getLink()
    {
        return link;
    }

    public String getPath()
    {
        return path;
    }

    public void setPath(String path)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.path = path;
    }

    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.ref = ref;
    }

    public String getQuery()
    {
        return query;
    }

    public void setQuery(String query)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.query = query;
    }

    public boolean isAbsolute()
    {
        return absolute;
    }

    public String getScheme()
    {
        return scheme;
    }

    public void setScheme(String scheme)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.scheme = scheme;
    }

    public String getServerName()
    {
        return serverName;
    }

    public void setServerName(String serverName)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.serverName = serverName;
    }

    public int getPort()
    {
        return port;
    }

    public void setPort(int port)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.port = port;
    }

    public boolean isSsl()
    {
        return ssl;
    }

    public void setSsl(boolean ssl)
    {
        if (!modifieable)
            throw new OtherObjectsException(ILLEGAL_MODIFIATION_MESSAGE);
        this.ssl = ssl;
    }

}
