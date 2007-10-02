package org.otherobjects.cms.controllers.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.model.DynaNode;
import org.springframework.web.servlet.ModelAndView;

public interface ResourceHandler
{
    public ModelAndView handleRequest(DynaNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
