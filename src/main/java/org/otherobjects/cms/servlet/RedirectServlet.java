package org.otherobjects.cms.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Temporary servlet to redirect to /go/ from the site root.
 * 
 * @author rich
 */
public class RedirectServlet extends HttpServlet
{
    private final Logger logger = LoggerFactory.getLogger(RedirectServlet.class);
    private static final long serialVersionUID = -6717721957043008039L;
    private static final String REDIRECT_PATH = "/go/";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        logger.info("Redirecting to: {}", REDIRECT_PATH);
        resp.sendRedirect(REDIRECT_PATH);
    }
}
