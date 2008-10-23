package org.otherobjects.cms.controllers.site;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SiteSearchController {

    private DaoService daoService;
    
    private OoResourceLoader ooResourceLoader;

    @RequestMapping
    public ModelAndView search(HttpServletRequest request, HttpServletResponse response)
    {
        UniversalJcrDao jcr = (UniversalJcrDao) daoService.getDao("BaseNode");
        Object ooTemplate = jcr.getByPath("/designer/templates/searchresults");
        
        // Choose layout
        ModelAndView mv  = new ModelAndView("/site/templates/layouts/two-column");
        mv.addObject("ooTemplate", ooTemplate);
        mv.addObject("test", "Hello Rich");
        return mv;
    }
    
    public DaoService getDaoService() {
        return daoService;
    }


    public void setDaoService(DaoService daoService) {
        this.daoService = daoService;
    }


    public OoResourceLoader getOoResourceLoader() {
        return ooResourceLoader;
    }


    public void setOoResourceLoader(OoResourceLoader ooResourceLoader) {
        this.ooResourceLoader = ooResourceLoader;
    }
}
