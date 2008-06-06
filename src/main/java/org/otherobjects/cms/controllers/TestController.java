package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

@Controller
public class TestController extends MultiActionController
{
    @Resource
    private DaoService daoService;

    @Resource
    private TypeService typeService;

    @Resource
    private OoResourceLoader ooResourceLoader;

    @SuppressWarnings("unchecked")
    public ModelAndView test(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {

        // Load data file
        //        logger.debug("Running setup scripts.");
        //        Binding binding = new Binding();
        //        binding.setProperty("daoService", daoService);
        //        binding.setProperty("typeService", typeService);
        //        GroovyShell shell = new GroovyShell(binding);
        //        String script = IOUtils.toString(new FileInputStream("data-transfer/import.groovy"));
        //        shell.evaluate(script);

        // Process blocks
        List<OoResource> resources = ooResourceLoader.getResources("/site/templates/blocks/");
        String path = "/designer/blocks/";
        for (OoResource r : resources)
        {
            UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
            String code = StringUtils.substringBefore(r.getFilename(),".");
            TemplateBlock block = (TemplateBlock) dao.getByPath(path + code);

            if (block == null)
            {
                block = new TemplateBlock();
                block.setPath(path);
            }
            block.setCode(code);
            block.setLabel(code);
            if (r.getMetaData() != null)
            {
                if(r.getMetaData().getTitle()!=null)
                    block.setLabel(r.getMetaData().getTitle());
                else
                block.setDescription(r.getMetaData().getDescription());
            }
            dao.save(block);
        }
        
        // Process layouts
        resources = ooResourceLoader.getResources("/site/templates/layouts/");
        path = "/designer/layouts/";
        for (OoResource r : resources)
        {
            UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
            String code = StringUtils.substringBefore(r.getFilename(),".");
            TemplateLayout block = (TemplateLayout) dao.getByPath(path + code);

            if (block == null)
            {
                block = new TemplateLayout();
                block.setPath(path);
            }
            block.setCode(code);
            block.setLabel(code);
            if (r.getMetaData() != null)
            {
                if(r.getMetaData().getTitle()!=null)
                    block.setLabel(r.getMetaData().getTitle());
                else
                block.setDescription(r.getMetaData().getDescription());
            }
            dao.save(block);
        }
        
        
        return null;
    }

}
