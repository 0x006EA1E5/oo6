package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.CmsExternalLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class BookmarkletController implements Controller
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private DaoService daoService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String url = request.getParameter("url");
        String title = request.getParameter("title");

        UniversalJcrDao dao = (UniversalJcrDao) this.daoService.getDao("dynaNode");
        CmsExternalLink link = new CmsExternalLink();
        link.setPath("/libraries/links/");
        link.setCode("" + new Date().getTime());
        link.set("label", title);
        link.set("url", url);
        dao.save(link);
        dao.publish(link, null);

        String path = "otherobjects.resources/templates/bookmarklet.html";
        ModelAndView view = new ModelAndView(path);
        view.addObject("request", request);
        view.addObject("newUrl", url);
        view.addObject("newTitle", title);
        return view;
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

}
