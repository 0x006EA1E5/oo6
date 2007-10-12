package org.otherobjects.cms.seo;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.otherobjects.cms.controllers.handlers.TestPage;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.TypeDef;

public class SiteMapGeneratorTest extends TestCase
{

    public void testFormatW3CDateTime()
    {
        DateTime dt = new DateTime(2007, 9, 22, 12, 23, 34, 0, DateTimeZone.forID("UTC"));
        SiteMapGenerator smg = new SiteMapGenerator();
        String s = smg.formatW3CDateTime(dt.toDate());
        assertEquals("2007-09-22T12:23:34+00:00", s);
    }

    public void testGenerateSiteMap() throws Exception
    {

        DateTime dt1 = new DateTime(2007, 9, 22, 12, 23, 34, 0, DateTimeZone.forID("+0200"));
        DateTime dt2 = new DateTime(2007, 8, 22, 12, 23, 34, 0, DateTimeZone.forID("+0200"));
        DateTime dt3 = new DateTime(2007, 10, 22, 12, 23, 34, 0, DateTimeZone.forID("+0200"));
        List<BaseNode> items = new ArrayList<BaseNode>();
        TestPage page1 = createSamplePage("Article 1", dt1);
        TestPage page2 = createSamplePage("Article 2", dt2);
        TestPage page3 = createSamplePage("Article 3", dt3);
        items.add(page1);
        items.add(page2);
        items.add(page3);

        SiteMapGenerator smg = new SiteMapGenerator();
        Document siteMap = smg.generateSiteMap(items);

        // Output XML for info
        StringWriter sw = new StringWriter();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(sw, outformat);
        writer.write(siteMap);
        writer.flush();
        System.out.print(sw.toString());

        assertEquals(3, siteMap.selectNodes("/urlset/url").size());
        assertEquals("/articles/article-3.html", siteMap.valueOf("/urlset/url[3]/loc"));
    }

    private TestPage createSamplePage(String title, DateTime date) throws Exception
    {
        TestPage testPage = new TestPage();
        TypeDef td = new AnnotationBasedTypeDefBuilder().getTypeDef(TestPage.class);
        testPage.setPath("/articles/");
        testPage.setTypeDef(td);
        testPage.setName(title);
        testPage.setModificationTimestamp(date.toDate());
        return testPage;
        
    }

}
