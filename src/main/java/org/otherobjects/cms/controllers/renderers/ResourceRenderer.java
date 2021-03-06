package org.otherobjects.cms.controllers.renderers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.model.CmsNode;
import org.springframework.web.servlet.ModelAndView;

public interface ResourceRenderer
{
    ModelAndView handleRequest(CmsNode resourceObject, HttpServletRequest request, HttpServletResponse response) throws Exception;
}
