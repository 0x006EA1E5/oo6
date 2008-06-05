package org.otherobjects.cms.controllers;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONObject;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateRegion;
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

    /**
     * Simply renders the requested block. No special processing is performed.
     */
    @RequestMapping("/block/render/**")
    public ModelAndView render(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        String blockName = StringUtils.remove(request.getPathInfo(), "/block/render/");
        logger.info("Rendering block name: " + blockName);

        ModelAndView view = new ModelAndView("blocks/" + blockName);
        view.addObject("blockName", blockName);
        view.addObject("daoService", this.daoService);

        return view;
    }

    @RequestMapping("/block/get/**")
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
    {

        String blockName = StringUtils.remove(request.getPathInfo(), "/block/get/");
        String blockId = request.getParameter("uuid");
        logger.info("Rendering block name: " + blockName);
        logger.info("Rendering block id : " + blockId);

        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        BaseNode resourceObject = dao.get(blockId);
        TemplateBlock block = (TemplateBlock) dao.getByPath("/designer/blocks/" + blockName);

        ModelAndView view = new ModelAndView("blocks/oo-render-block");
        if (block.getGlobal() == null || !block.getGlobal().booleanValue())
        {
            // Page block
            view.addObject("resourceObject", resourceObject);
            view.addObject("blockGlobal", false);
        }
        else
        {
            // Global block
            view.addObject("blockData", dao.getByPath("/blocks/" + blockName));
        }

        // Return page and context
        view.addObject("blockGlobal", block.getGlobal());
        view.addObject("blockName", blockName);
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

    //FIXME This does not really belong here
    @RequestMapping("/block/saveArrangement/**")
    public ModelAndView saveArrangement(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        String templateId = request.getParameter("templateId");
        
        String arrangement = request.getParameter("arrangement");
        logger.info("Arrangement: " + arrangement);
        JSONArray regions = new JSONArray(arrangement);

        // Load existing template
        Template template = (Template) dao.get(templateId);
        ArrayList<TemplateRegion> arrayList = new ArrayList<TemplateRegion>();

        for (int i = 0; i < regions.length(); i++)
        {
            JSONObject region = (JSONObject) regions.get(i);
            String regionName = (String) region.get("name");
            TemplateRegion tr = new TemplateRegion();
            tr.setBlocks(new ArrayList<TemplateBlock>());
            tr.setCode(regionName);

            //region.setProperty("type", "TemplateRegion");
            //region.setProperty("title", regionName);

            JSONArray blockIds = (JSONArray) region.get("blockIds");
            for (int j = 0; j < blockIds.length(); j++)
            {
                String blockId = (String) blockIds.get(j);
                TemplateBlock b = (TemplateBlock) dao.getByPath("/designer/blocks/" + blockId);
                tr.getBlocks().add(b);
            }
            arrayList.add(tr);
        }
        template.setRegions(arrayList);
        dao.save(template);
        return null;
    }

}
