package org.otherobjects.cms.seo;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.dom4j.Dom4jXPath;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.otherobjects.framework.SingletonBeanLocator;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.otherobjects.cms.site.TreeNode;

public class SiteMapGeneratorTest extends TestCase
{

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        OtherObjectsConfigurator ooConfig = new OtherObjectsConfigurator();
        ooConfig.setProperty("site.server.name", "localhost");
        ooConfig.setProperty("site.server.context.path", "");
        ooConfig.setProperty("site.server.default.port", "80");
        SingletonBeanLocator.registerTestBean("otherObjectsConfigurator", ooConfig);
    }
    
    public void testFormatW3CDateTime()
    {
        DateTime dt = new DateTime(2007, 9, 22, 12, 23, 34, 0, DateTimeZone.forID("UTC"));
        SiteMapGenerator smg = new SiteMapGenerator();
        String s = smg.formatW3CDateTime(dt.toDate());
        assertEquals("2007-09-22T12:23:34+00:00", s);
    }

    @SuppressWarnings("unchecked")
    public void testGenerateSiteMap() throws Exception
    {
        //        DateTime dt1 = new DateTime(2007, 9, 22, 12, 23, 34, 0, DateTimeZone.forID("+0200"));
        //        DateTime dt2 = new DateTime(2007, 8, 22, 12, 23, 34, 0, DateTimeZone.forID("+0200"));
        //        DateTime dt3 = new DateTime(2007, 10, 22, 12, 23, 34, 0, DateTimeZone.forID("+0200"));
        List<TreeNode> items = new ArrayList<TreeNode>();
        items.add(new TreeNode("/articles/article-1.html", null, "Article 1", 1));
        items.add(new TreeNode("/articles/article-2.html", null, "Article 2", 2));
        items.add(new TreeNode("/articles/article-3.html", null, "Article 3", 3));

        SiteMapGenerator smg = new SiteMapGenerator();
        Document siteMap = smg.generateSiteMap(items);

        
        HashMap map = new HashMap();
        map.put( "sm", "http://www.sitemaps.org/schemas/sitemap/0.9");
        
        Dom4jXPath xpath = new Dom4jXPath( "/sm:urlset/sm:url");
        xpath.setNamespaceContext( new SimpleNamespaceContext( map));
        
        List nodes = xpath.selectNodes( siteMap);
        
        
        // Output XML for info
        StringWriter sw = new StringWriter();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(sw, outformat);
        writer.write(siteMap);
        writer.flush();
        System.out.print(sw.toString());
        assertEquals(3, nodes.size());
        assertEquals("http://localhost/articles/article-3.html", ((Element)nodes.get(2)).element("loc").getText());
    }
}
