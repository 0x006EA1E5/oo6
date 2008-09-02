package org.otherobjects.cms.controllers;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class WorkbenchController
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DaoService daoService;

    //    @Resource
    //    private LocaleResolver localeResolver;

    @RequestMapping({"", "/", "/workbench/*"})
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        //        String newLocale = ServletRequestUtils.getStringParameter(request, "locale");
        //        if (newLocale != null)
        //        {
        //            this.localeResolver.setLocale(request, response, StringUtils.parseLocaleString(newLocale));
        //            this.logger.info("Locale set to: " + this.localeResolver.resolveLocale(request));
        //        }

        String path = request.getPathInfo();

        if (path.length() < 10)
            path = "/otherobjects/workbench/workbench";
        else
        {
            path = path.substring(10);
            if (path.equals("/"))
                path = "/otherobjects/workbench/workbench";
            else
                path = "/otherobjects/workbench/" + path;
        }
        path = path.replaceAll(".html", "");
        this.logger.info("WorkbenchController: " + path);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        ModelAndView view = new ModelAndView(path);
        view.addObject("user", principal);
        view.addObject("request", request);
        view.addObject("cmsImageTool", new CmsImageTool());
        return view;
    }

    @RequestMapping({"/workbench/view/*"})
    public ModelAndView view(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String id = RequestUtils.getId(request);
        ModelAndView mav = new ModelAndView("/otherobjects/templates/pages/view");
        mav.addObject("id", id);
        return mav;
    }

    @RequestMapping({"/workbench/edit/*"})
    public ModelAndView edit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String id = RequestUtils.getId(request);
        UniversalJcrDao universalJcrDao = (UniversalJcrDao) this.daoService.getDao(BaseNode.class);
        ModelAndView mav = new ModelAndView("/otherobjects/templates/pages/edit");
        BaseNode item = universalJcrDao.get(id);
        mav.addObject("id", id);
        mav.addObject("object", item);
        mav.addObject("typeDef", item.getTypeDef());
        return mav;
    }

}
