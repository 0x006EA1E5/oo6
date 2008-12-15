package org.otherobjects.cms.controllers.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.monitoring.PerformanceInfo;
import org.otherobjects.cms.tools.FlashMessageTool;
import org.otherobjects.cms.tools.FormatTool;
import org.otherobjects.cms.tools.UrlTool;
import org.otherobjects.cms.util.CookieManager;
import org.otherobjects.cms.views.FreemarkerToolProvider;
import org.springframework.context.MessageSource;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ModelModifierInterceptor extends HandlerInterceptorAdapter
{
    private DaoService daoService;
    private OoResourceLoader ooResourceLoader;
    private OtherObjectsConfigurator otherObjectsConfigurator;
    private MessageSource messageSource;
    private FreemarkerToolProvider freemarkerToolProvider;
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
            // tools
            modelAndView.addObject("urlTool", new UrlTool(this.ooResourceLoader));
            modelAndView.addObject("formatTool", new FormatTool(this.messageSource, this.otherObjectsConfigurator));
            //modelAndView.addObject("navigationTool", new NavigationTool(navigationService));
            modelAndView.addObject("daoService", this.daoService);
            modelAndView.addObject("dao", this.daoService);
            modelAndView.addObject("flash", new FlashMessageTool(request));
            modelAndView.addObject("jcr", this.daoService.getDao("BaseNode"));
            if (performanceInfo != null)
                modelAndView.addObject("performanceInfo", performanceInfo);
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
            if (this.freemarkerToolProvider != null)
            {
                // FIXME Add scoping and caching to tool generation
                modelAndView.addAllObjects(this.freemarkerToolProvider.getTools());
            }
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

    public void setMessageSource(MessageSource messageSource)
    {
        this.messageSource = messageSource;
    }

    public void setOtherObjectsConfigurator(OtherObjectsConfigurator otherObjectsConfigurator)
    {
        this.otherObjectsConfigurator = otherObjectsConfigurator;
    }

    public void setFreemarkerToolProvider(FreemarkerToolProvider freemarkerToolProvider)
    {
        this.freemarkerToolProvider = freemarkerToolProvider;
    }

    public void setPerformanceInfo(PerformanceInfo performanceInfo)
    {
        this.performanceInfo = performanceInfo;
    }
}
