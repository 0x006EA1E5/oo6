package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.seo.SiteMapGenerator;
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
    private UniversalJcrDao universalJcrDao;

    @RequestMapping("/map.xml")
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.logger.info("Generating sitemap.");
        // TODO This list could be huge -- we need to be careful of memory
        // FIXME Better way of exculding components needed
        List<BaseNode> items = universalJcrDao.getAllByJcrExpression("/jcr:root/site//element(*, oo:node) [@ooType != 'org.otherobjects.cms.model.MetaData']");
        SiteMapGenerator siteMapGenerator = new SiteMapGenerator();
        Document siteMap = siteMapGenerator.generateSiteMap(items);

        // Send output
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(response.getOutputStream(), outformat);
        writer.write(siteMap);
        writer.flush();
        return null;
    }
}
