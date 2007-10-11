package org.otherobjects.cms;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.context.RequestContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * 
 * @author joerg
 *
 */
public class Url {
	
	protected final static Logger logger = LoggerFactory.getLogger(Url.class);
	
	public static final int STANDARD_HTTP_PORT = 80;
	public static final int STANDARD_HTTPS_PORT = 443;
	
	public static final String STANDARD_URL_CHARACTER_ENCODING = "UTF-8";
	
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
		try {
			URI parsedUrl = new URI(getLink());
			scheme = parsedUrl.getScheme();
			serverName = parsedUrl.getHost();
			port = parsedUrl.getPort();
			path = parsedUrl.getPath();
			query = parsedUrl.getQuery();
			ref = parsedUrl.getFragment();
			ssl = parsedUrl.getScheme().toLowerCase().equals("https");
			absolute = parsedUrl.isAbsolute();
		} catch (URISyntaxException e) {
			logger.warn("Couldn't parse " + this.getLink());
		}
	}
	
	
	
	public String getAbsoluteLink()
	{
		if(absolute) // link is already absolute
			return getLink();
		else // non-absolute link
		{	
			HttpServletRequest req = RequestContextUtils.getHttpServletRequest();
			StringBuffer newUrl = new StringBuffer();
			newUrl.append(req.getScheme());
			newUrl.append("://");
			newUrl.append(req.getServerName());
			int serverPort = req.getServerPort();
			if(serverPort != STANDARD_HTTP_PORT && serverPort != STANDARD_HTTPS_PORT)
				newUrl.append(":" + serverPort);
			try {
				newUrl.append(URLEncoder.encode(RequestContextUtils.getContextPath(), STANDARD_URL_CHARACTER_ENCODING));
				newUrl.append(URLEncoder.encode(getLink(), STANDARD_URL_CHARACTER_ENCODING));
				if(getQuery() != null)
					newUrl.append("?" + URLEncoder.encode(getQuery(), STANDARD_URL_CHARACTER_ENCODING));
				if(getRef() != null)
					newUrl.append("#" + URLEncoder.encode(getRef(), STANDARD_URL_CHARACTER_ENCODING));
			} catch (UnsupportedEncodingException e) {
				logger.warn("Error when trying to encode Url parts", e);
			}
			return newUrl.toString();
		}
	}
	
	public String toString()
	{
		if(absolute)
			return link;
		else
			return RequestContextUtils.getContextPath() + getLink();
	}
	
	public boolean isModifieable() {
		return modifieable;
	}

	public String getLink() {
		return link;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
	public boolean isAbsolute() {
		return absolute;
	}

	public String getScheme() {
		return scheme;
	}

	public void setScheme(String scheme) {
		this.scheme = scheme;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isSsl() {
		return ssl;
	}

	public void setSsl(boolean ssl) {
		this.ssl = ssl;
	}
	
}
