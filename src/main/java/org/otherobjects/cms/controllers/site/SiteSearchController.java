package org.otherobjects.cms.controllers.site;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.Template;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
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
        Template ooTemplate = (Template) jcr.getByPath("/designer/templates/searchresults");
        Assert.notNull(ooTemplate, "No search results template found: /designer/templates/searchresults");
        
        // Choose layout
        // FIXME Merge this nasty code with PageRenderer
        ModelAndView mv  = new ModelAndView("/site/templates/layouts/" + ooTemplate.getLayout().getCode().replaceAll("\\.html", "") + "");
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
