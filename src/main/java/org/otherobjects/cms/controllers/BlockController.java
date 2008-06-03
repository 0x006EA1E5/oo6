package org.otherobjects.cms.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that serves up block content. Used for serving up content for AJAX.
 * 
 * FIXME Needs merging with SiteController
 * FIXME Needs to respect block security
 * 
 * @author rich
 */
@Controller
public class BlockController
{
    private final Logger logger = LoggerFactory.getLogger(BlockController.class);

    @Resource
    private DaoService daoService;

    @Resource
    private TypeService typeService;

    @RequestMapping("/block/get/**")
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        String blockName = StringUtils.remove(request.getPathInfo(), "/block/get/");
        logger.info("Rendering block: " + blockName);

        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        BaseNode blockData = dao.getByPath("/blocks/" + blockName);
        
        // Return page and context
        ModelAndView view = new ModelAndView("blocks/"+blockName);

        //view.addObject("template", template);
        view.addObject("blockData", blockData);
        view.addObject("daoService", this.daoService);

        return view;
    }

    @RequestMapping("/block/form/**")
    public ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String blockName = StringUtils.remove(request.getPathInfo(), "/block/form/");
        logger.info("Generating form for block: " + blockName);

        String typeDefName = StringUtils.codeToClassName(blockName) + "Block";

        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");

        ModelAndView view = new ModelAndView("blocks/oo-form");
        BaseNode blockData = dao.getByPath("/blocks/" + blockName);
        if (blockData != null)
        {
            view.addObject("blockData", blockData);
        }
        else
        {
            // New blocks
            view.addObject("location", dao.getByPath("/blocks/").getId());
            view.addObject("typeDef", typeService.getType(typeDefName));
        }
        view.addObject("blockName", blockName);
        view.addObject("daoService", this.daoService);

        return view;
    }
}
