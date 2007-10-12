package org.otherobjects.cms.controllers.handlers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.model.SyndicationFeedResource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.io.SyndFeedOutput;

public class SyndicationFeedHandler implements ResourceHandler
{
    private DaoService daoService;
    private static final DateFormat DATE_PARSER = new SimpleDateFormat("yyyy-MM-dd");

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public ModelAndView handleRequest(CmsNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Assert.notNull(resourceObject, "resourceObject must not be null");
        Assert.isInstanceOf(SyndicationFeedResource.class, resourceObject, "resourceObject must be a SyndicationFeedResource not: " + resourceObject.getClass().getName());

        SyndicationFeedResource feedObject = (SyndicationFeedResource) resourceObject;
        
//        Assert.notNull(redirect.getUrl(), "Redirect does not specify a destination URL.");
        
        SyndFeed feed = new SyndFeedImpl();
        feed.setFeedType("atom_1.0");

        feed.setTitle(feedObject.getLabel());
        feed.setLink("http://rome.dev.java.net");
        feed.setDescription(feedObject.getDescription());

        List<SyndEntry> entries = new ArrayList<SyndEntry>();
        SyndEntry entry;
        SyndContent description;

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v1.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
        entry.setPublishedDate(DATE_PARSER.parse("2004-06-08"));
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Initial release of ROME");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v2.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome02");
        entry.setPublishedDate(DATE_PARSER.parse("2004-06-16"));
        description = new SyndContentImpl();
        description.setType("text/plain");
        description.setValue("Bug fixes, minor API changes and some new features");
        entry.setDescription(description);
        entries.add(entry);

        entry = new SyndEntryImpl();
        entry.setTitle("ROME v3.0");
        entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome03");
        entry.setPublishedDate(DATE_PARSER.parse("2004-07-27"));
        description = new SyndContentImpl();
        description.setType("text/html");
        description.setValue("<p>More Bug fixes, mor API changes, some new features and some Unit testing</p>"
                + "<p>For details check the <a href=\"http://wiki.java.net/bin/view/Javawsxml/RomeChangesLog#RomeV03\">Changes Log</a></p>");
        entry.setDescription(description);
        entries.add(entry);

        feed.setEntries(entries);

        SyndFeedOutput output = new SyndFeedOutput();
        output.output(feed, response.getWriter());
        response.setContentType("text/xml");
        return null;
    }
}
