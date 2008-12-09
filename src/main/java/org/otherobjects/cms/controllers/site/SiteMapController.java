package org.otherobjects.cms.controllers.site;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.otherobjects.cms.seo.SiteMapGenerator;
import org.otherobjects.cms.site.NavigationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generates a sitemap.xml file for use by search engines using the {@link org.otherobjects.cms.seo.SiteMapGenerator}.
 * 
 * @author rich
 */
@Controller
public class SiteMapController
{
    private final Logger logger = LoggerFactory.getLogger(SiteMapController.class);


    @Resource
    private NavigationService navigationService;

    @RequestMapping
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.logger.info("Generating sitemap.");
        SiteMapGenerator siteMapGenerator = new SiteMapGenerator();
        Document siteMap = siteMapGenerator.generateSiteMap(navigationService.getAllNodes());

        // Send output
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(response.getOutputStream(), outformat);
        writer.write(siteMap);
        writer.flush();
        return null;
    }
}
