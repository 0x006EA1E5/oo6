package org.otherobjects.cms.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.IdentifierUtils;
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
        String blockId = request.getParameter("uuid");
        logger.info("Rendering block name: " + blockName);
        logger.info("Rendering block id : " + blockId);

        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");

        ModelAndView view = null;
        if (IdentifierUtils.isUUID(blockName))
        {
            // Page block
            // FIXME Hardcoded page block name
            view = new ModelAndView("blocks/content");
            view.addObject("resourceObject", dao.get(blockName));
        }
        else
        {
            // Global block
            view = new ModelAndView("blocks/" + blockName);
            view.addObject("blockData", dao.getByPath("/blocks/" + blockName));
        }
        // Return page and context

        //view.addObject("template", template);
        view.addObject("daoService", this.daoService);

        return view;
    }

    @RequestMapping("/block/form/**")
    public ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        ModelAndView view = new ModelAndView("blocks/oo-form");

        String blockName = StringUtils.remove(request.getPathInfo(), "/block/form/");
        String blockId = request.getParameter("uuid");
        logger.info("Generating form for block: {} {}", blockName, blockId);

        if (blockId != null)
        {
            // Page block
            BaseNode blockData = dao.get(blockId);
            view.addObject("blockData", blockData);
            view.addObject("blockGlobal", false);
        }
        else
        {
            // Global block
            String typeDefName = StringUtils.codeToClassName(blockName) + "Block";
            BaseNode blockData = dao.getByPath("/blocks/" + blockName);
            view.addObject("blockGlobal", true);
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
        }
        view.addObject("blockName", blockName);
        view.addObject("daoService", this.daoService);

        return view;
    }
}
