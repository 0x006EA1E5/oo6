package org.otherobjects.cms.controllers.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.tools.CmsImageTool;
import org.otherobjects.cms.tools.FlashMessageTool;
import org.otherobjects.cms.tools.FormatTool;
import org.otherobjects.cms.tools.SecurityTool;
import org.otherobjects.cms.tools.UrlTool;
import org.otherobjects.cms.util.ObjectInspector;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ModelModifierInterceptor extends HandlerInterceptorAdapter
{
    private DaoService daoService;
    private OoResourceLoader ooResourceLoader;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception
    {
        if (modelAndView != null)
        {
            //add user id to model if there is a user
            //            Long currentUserId = SecurityTool.getUserId();
            //            if (currentUserId != null)
            //                modelAndView.addObject("userId", currentUserId);

            // session stuff
            HttpSession session = request.getSession(false);
            if (session != null)
            {
                Integer counter = (Integer) session.getAttribute("counter");
                if (counter == null)
                    counter = 0;
                session.setAttribute("counter", ++counter);

                modelAndView.addObject("counter", counter);
                modelAndView.addObject("sessionId", session.getId());
            }
            // tools
            modelAndView.addObject("cmsImageTool", new CmsImageTool());
            modelAndView.addObject("urlTool", new UrlTool(ooResourceLoader));
            modelAndView.addObject("objectInspector", new ObjectInspector());
            modelAndView.addObject("formatTool", new FormatTool());
            modelAndView.addObject("security", new SecurityTool());
            modelAndView.addObject("daoService", daoService);
            modelAndView.addObject("dao", daoService);

            modelAndView.addObject("flash", new FlashMessageTool(request));
            modelAndView.addObject("jcr", daoService.getDao("BaseNode"));
        }

    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public void setOoResourceLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

}
