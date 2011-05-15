package org.otherobjects.cms.controllers.interceptors;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.monitoring.PerformanceInfo;
import org.otherobjects.cms.tools.FlashMessageTool;
import org.otherobjects.cms.util.CookieManager;
import org.otherobjects.cms.views.FreeMarkerToolProvider;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ModelModifierInterceptor extends HandlerInterceptorAdapter {
    
    @Resource
    private DaoService daoService;
    @Resource
    private OtherObjectsConfigurator otherObjectsConfigurator;
    @Resource
    private FreeMarkerToolProvider freeMarkerToolProvider;
    @Resource
    private PerformanceInfo performanceInfo;

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
                {
                    counter = 0;
                }
                session.setAttribute("counter", ++counter);

                modelAndView.addObject("counter", counter);
                modelAndView.addObject("sessionId", session.getId());
            }
            
            // Add boolean to indicate secure connections
            modelAndView.addObject("ssl", request.isSecure());
            
            // tools
            //modelAndView.addObject("navigationTool", new NavigationTool(navigationService));
            modelAndView.addObject("flash", new FlashMessageTool(request));
            modelAndView.addObject("jcr", this.daoService.getDao("BaseNode"));
            modelAndView.addObject("ooDeveloperMode", true);
            modelAndView.addObject("ooNewUi", true);
//            modelAndView.addObject("ooNewUi", false);
            if (this.performanceInfo != null)
            {
                modelAndView.addObject("performanceInfo", this.performanceInfo);
            }
            modelAndView.addObject("ooEnvironment", this.otherObjectsConfigurator.getProperty("otherobjects.environment"));

            // Add cookies
            modelAndView.addObject("Cookies", new CookieManager(request));

            // Add url to context if not already defined
            // FIXME Is this the right name?
            if (!modelAndView.getModelMap().containsKey("urlPath"))
            {
                modelAndView.addObject("urlPath", request.getPathInfo());
            }
            modelAndView.addObject("url", request.getPathInfo());

            // Add auto-detected tools
            if (this.freeMarkerToolProvider != null)
            {
                // FIXME Add scoping and caching to tool generation
                modelAndView.addAllObjects(this.freeMarkerToolProvider.getTools());
            }
        }
    }

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    public void setFreeMarkerToolProvider(FreeMarkerToolProvider freeMarkerToolProvider)
    {
        this.freeMarkerToolProvider = freeMarkerToolProvider;
    }

    public void setPerformanceInfo(PerformanceInfo performanceInfo)
    {
        this.performanceInfo = performanceInfo;
    }
}
