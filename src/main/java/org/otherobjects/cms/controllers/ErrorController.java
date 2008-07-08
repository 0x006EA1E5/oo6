package org.otherobjects.cms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Error controller handles errors within the View layer. If an error occurs the user 
 * is redirected to this controller which will print the correct error page.
 * 
 * @author rich
 */
public class ErrorController extends AbstractController
{

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ModelAndView mv = new ModelAndView();
        mv.setViewName("/site/templates/error-500");
        String message = request.getParameter("msg");
        mv.addObject("exceptionMessage", message != null ? message : "An unknown error has occurred.");
        return mv;
    }
}
