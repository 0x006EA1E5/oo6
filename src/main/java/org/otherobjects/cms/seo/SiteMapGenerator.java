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

package org.otherobjects.cms.seo;

import java.util.Date;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.site.TreeNode;
import org.springframework.util.Assert;

/**
 * Generates SiteMap protocol XML from a lit of nodes.
 * 
 * <p>See <a href="http://www.sitemaps.org/protocol.php">http://www.sitemaps.org/protocol.php</a>
 * for protocol details
 * 
 * @author rich
 */
public class SiteMapGenerator
{
    private static final DateTimeFormatter W3C_DATE_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZZ");

    /**
     * Generates the XML. 
     * 
     * @param items
     * @return
     */
    public Document generateSiteMap(List<TreeNode> items)
    {
        // Add urlset with correct namespace
        QName rootName = DocumentFactory.getInstance().createQName("urlset", "", "http://www.sitemaps.org/schemas/sitemap/0.9");
        Element urlset = DocumentFactory.getInstance().createElement(rootName);
        Document doc = DocumentFactory.getInstance().createDocument(urlset);

        // Must be in UTF-8 according to spec
        doc.setXMLEncoding("UTF-8");
        
        urlset.addAttribute("xmlns:xsi","http://www.w3.org/2001/XMLSchema-instance");
        urlset.addAttribute("xsi:schemaLocation","http://www.sitemaps.org/schemas/sitemap/0.9 http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd");
    
        for (TreeNode item : items)
        {
            // FIXME We should store URLs in the TreeNode?
            String linkPath = new Url(item.getUrl()).getAbsoluteLink();
            Date modificationTimestamp = item.getModificationTimestamp();

            Assert.hasText(linkPath, "URL can not be null for item: " + item.getPath());
            Assert.notNull(modificationTimestamp, "modificationTimestamp can not be null for item: " + item.getPath());

            // Required elements
            Element entry = urlset.addElement("url");
            entry.addElement("loc").addText(linkPath);
            
            // Optional elements
            entry.addElement("lastmod").addText(formatW3CDateTime(modificationTimestamp));
            // recipient.addElement("changefreq").addText();
            // recipient.addElement("qriority").addText();
        }
        return doc;
    }

    /**
     * Formats a date into W3C DateTime format.
     * 
     * <p>See <a href="http://www.w3.org/TR/NOTE-datetime">http://www.w3.org/TR/NOTE-datetime</a> for the specification.
     * 
     * <p>Note that this will always return the time in UTC.
     * 
     * @param date
     * @return
     */
    public String formatW3CDateTime(Date date)
    {
        return W3C_DATE_FORMATTER.withZone(DateTimeZone.forID("UTC")).print(date.getTime());
    }
}
