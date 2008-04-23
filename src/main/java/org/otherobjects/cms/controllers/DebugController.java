package org.otherobjects.cms.controllers;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.Resource;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;
import org.springmodules.jcr.JcrCallback;
import org.springmodules.jcr.JcrTemplate;

/**
 * Debug controller which provides information about the running application. Also provides simple query facility on 
 * the datastores.
 * 
 * TODO Add groovy scripting suport
 * TODO Add query histories
 * 
 * @author rich
 */
@Controller
public class DebugController extends MultiActionController
{
    @Resource
    private JcrTemplate jcrTemplate;

    @Resource
    private JdbcTemplate jdbcTemplate;

    public ModelAndView debug(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // Determine imageMagick status
        String imageMagickError = null;
        String imageMagickVersion = null;
        try
        {
            //TODO Make bin path a config property
            String binPath = "/opt/local/bin/";

            String command = binPath + "convert --version";

            Process exec = Runtime.getRuntime().exec(command);
            exec.waitFor();
            imageMagickVersion = IOUtils.toString(exec.getInputStream());
        }
        catch (Exception e)
        {
            imageMagickError = "Could not find ImageMagick binary: " + e.getMessage();
        }

        ModelAndView mav = new ModelAndView("/debug/debug.ftl");
        mav.addObject("imageMagickError", imageMagickError);
        mav.addObject("imageMagickVersion", imageMagickVersion);
        mav.addObject("userDetails", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return mav;
    }

    /**
     * Provides a view onto the data in the database.
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView database(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        final String sql = request.getParameter("sql");
        String rowsHtml = (String) jdbcTemplate.execute(new StatementCallback()
        {
            public Object doInStatement(Statement stmt) throws SQLException, DataAccessException
            {
                StringBuffer html = new StringBuffer();

                if (sql != null)
                {
                    html.append("<table>");
                    ResultSet resultSet = stmt.executeQuery(sql);

                    // Add in header rows
                    html.append("\n<tr>");
                    for (int col = 1; col <= resultSet.getMetaData().getColumnCount(); col++)
                        html.append("<th>" + resultSet.getMetaData().getColumnName(col) + "</th>");
                    html.append("</tr>");

                    while (resultSet.next())
                    {
                        html.append("\n<tr>");
                        for (int col = 1; col <= resultSet.getMetaData().getColumnCount(); col++)
                            html.append("<td>" + resultSet.getString(col) + "</td>");

                        html.append("</tr>");
                    }
                    html.append("</table>");
                }
                else
                    html.append("<p>No query.</p>");
                return html.toString();
            }
        });

        ModelAndView mav = new ModelAndView("/debug/database.ftl");
        mav.addObject("rowsHtml", rowsHtml);
        mav.addObject("sql", sql);
        return mav;
    }

    /**
     * Provides a view onto the data in JCR.
     * 
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ModelAndView jcr(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        final String xpath = request.getParameter("xpath");
        String nodesHtml = (String) jcrTemplate.execute(new JcrCallback()
        {
            public Object doInJcr(Session session) throws RepositoryException
            {
                StringBuffer html = new StringBuffer();

                if (xpath != null)
                {
                    QueryManager queryManager = session.getWorkspace().getQueryManager();
                    Query query = queryManager.createQuery(xpath, javax.jcr.query.Query.XPATH);
                    QueryResult queryResult = query.execute();
                    NodeIterator nodeIterator = queryResult.getNodes();
                    while (nodeIterator.hasNext())
                        renderNodeInfo(html, nodeIterator.nextNode());
                }
                else
                    renderNodeInfo(html, session.getRootNode());
                return html.toString();
            }
        });

        ModelAndView mav = new ModelAndView("/debug/jcr.ftl");
        mav.addObject("nodesHtml", nodesHtml);
        mav.addObject("xpath", xpath);
        return mav;
    }

    protected void renderNodeInfo(StringBuffer html, Node node) throws RepositoryException
    {
        if (node.getPath().equals("/jcr:system"))
            return;

        html.append("\n<ul>");
        html.append("<li><strong>" + node.getPath() + "</strong><span class=\"properties-area\"");
        html.append("<br/>");

        PropertyIterator properties = node.getProperties();
        while (properties.hasNext())
        {
            Property prop = properties.nextProperty();
            if (prop.getDefinition().isMultiple())
                html.append(" " + prop.getName() + "=" + prop.getValues() + "<br/>");
            else
                html.append(" " + prop.getName() + "=" + prop.getValue().getString() + "<br/>");
        }
        html.append("</span>");
        html.append("</span>");

        NodeIterator nodes = node.getNodes();
        while (nodes.hasNext())
        {
            Node nextNode = nodes.nextNode();
            renderNodeInfo(html, nextNode);
        }
        html.append("</li>");
        html.append("</ul>");
    }
}
