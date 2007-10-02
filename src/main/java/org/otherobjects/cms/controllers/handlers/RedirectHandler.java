package org.otherobjects.cms.controllers.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.model.RedirectResource;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;

/**
 * Handler to redirect a moved page to its new location.
 * 
 * @author rich
 */
public class RedirectHandler implements ResourceHandler
{
    public ModelAndView handleRequest(DynaNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        Assert.notNull(resourceObject, "resourceObject must not be null");
        Assert.isInstanceOf(RedirectResource.class, resourceObject, "resourceObject must be a RedirectResource not: " + resourceObject.getClass().getName());

        RedirectResource redirect = (RedirectResource) resourceObject;
        Assert.notNull(redirect.getUrl(), "Redirect does not specify a destination URL.");

        if (redirect.getTemporary())
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY); //302
        else
            response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY); //301

        response.setHeader("Location", redirect.getUrl());

        // No view needed -- just redirect headers
        return null;
    }
}
