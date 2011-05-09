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

package org.otherobjects.cms.controllers.site;

import java.io.IOException;
import java.util.ArrayList;
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
import org.otherobjects.cms.site.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Generates a sitemap.xml file for use by search engines using the {@link org.otherobjects.cms.seo.SiteMapGenerator}.
 * 
 * <p>All site pages are included unless they are explicity flagged as 'hidden' in their PublishingOptions.
 * 
 * <p>FIXME Add in modifiction dates
 * 
 * @author rich
 */
@Controller
public class SiteMapController
{
    private final Logger logger = LoggerFactory.getLogger(SiteMapController.class);

    @Resource
    private UniversalJcrDao universalJcrDao;

    private static SiteMapGenerator siteMapGenerator = new SiteMapGenerator();

    /**
     * Generates the sitemap.
     */
    @RequestMapping
    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        this.logger.info("Generating sitemap.");
        Document siteMap = siteMapGenerator.generateSiteMap(getNodes()); // TODO Stream this out -- don't keep in memory

        // Send output
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        outformat.setEncoding("UTF-8");
        XMLWriter writer = new XMLWriter(response.getOutputStream(), outformat);
        writer.write(siteMap);
        writer.flush();
        return null;
    }

    /**
     * Returns a list of TreeNodes representing all items in the site.
     * 
     * @return
     */
    private List<TreeNode> getNodes()
    {

        List<BaseNode> siteNodes = universalJcrDao
                .getAllByJcrExpression("/jcr:root/site//element(*,oo:node) [not(jcr:like(@ooType,'%Folder'))]");

        List<TreeNode> flat = new ArrayList<TreeNode>();
        for (BaseNode b : siteNodes)
        {
            if (b.hasProperty("data.publishingOptions.hidden"))
            {
                Boolean hidden = (Boolean) b.getPropertyValue("data.publishingOptions.hidden");
                if(hidden)
                    continue;
            }
            TreeNode treeNode = new TreeNode(b.getOoUrlPath(), b.getId(), b.getOoUrlPath(), 0);
            treeNode.setModificationTimestamp(b.getModificationTimestamp());
            flat.add(treeNode);
        }

        appendAdditionalNodes(flat);

        return flat;

    }

    /**
     * Override this method to add additional non-JCR nodes to the site map.
     * 
     * @param flat
     */
    public void appendAdditionalNodes(List<TreeNode> flat)
    {
    }
}
