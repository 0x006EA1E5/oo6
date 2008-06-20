package org.otherobjects.cms.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateBlockReference;
import org.otherobjects.cms.model.TemplateRegion;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.RequestUtils;
import org.otherobjects.cms.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
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
     * Directly renders the requested template. No block processing is performed.
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

    /**
     * Creates a new block for the template and returns a rendering of it.
     * 
     */
    @RequestMapping("/block/create/**")
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        
        String blockName = RequestUtils.getId(request);
        String resourceObjectId = request.getParameter("resourceObjectId");
        String templateId = request.getParameter("templateId");
        String regionCode = request.getParameter("regionCode");
        
        Template template = (Template) dao.get(templateId);
        TemplateBlock templateBlock = (TemplateBlock) dao.getByPath("/designer/blocks/"+blockName);

        TemplateBlockReference blockRef = new TemplateBlockReference();
        blockRef.setBlock(templateBlock);
        
        TemplateRegion region = template.getRegion(regionCode);
        if(region==null)
        {
            region = new TemplateRegion();
            region.setCode(regionCode);
            region.setLabel(regionCode);
            template.getRegions().add(region);
        }
        Assert.notNull(region, "No region found for code: " + regionCode);
        
        if(region.getBlocks()==null)
            region.setBlocks(new ArrayList<TemplateBlockReference>());
        
        region.getBlocks().add(blockRef);
        dao.save(template,false);
        
        logger.info("Rendering block: {}", blockRef.getCode());
        
        // FIXME This woudl not be needed if OCM always set UUIDs after save
        template = (Template) dao.get(templateId);
        List<TemplateBlockReference> blocks = template.getRegion(regionCode).getBlocks();
        blockRef= blocks.get(blocks.size()-1);
        
        ModelAndView view = new ModelAndView("blocks/oo-block-render");
        view.addObject("blockReference", blockRef);
        if (!blockRef.getBlock().isGlobalBlock())
        {
            // Page block
            BaseNode resourceObject = dao.get(resourceObjectId);
            view.addObject("resourceObject", resourceObject);
        }
        else
        {
            // Global block
            view.addObject("blockData", blockRef.getBlockData());
        }
        return view;
    }
    /**
     * Renders block and associated data.
     * 
     */
    @RequestMapping("/block/get/**")
    public ModelAndView get(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        String blockRefId = RequestUtils.getId(request);
        String resourceObjectId = request.getParameter("resourceObjectId");
        TemplateBlockReference blockRef = (TemplateBlockReference) dao.get(blockRefId);

        logger.info("Rendering block: {}", blockRef.getCode());
        
        ModelAndView view = new ModelAndView("blocks/oo-block-render");
        view.addObject("blockReference", blockRef);
        if (!blockRef.getBlock().isGlobalBlock())
        {
            // Page block
            BaseNode resourceObject = dao.get(resourceObjectId);
            view.addObject("resourceObject", resourceObject);
            
        }
        else
        {
            // Global block
            view.addObject("blockData", blockRef.getBlockData());
            view.addObject("bn", null);
            view.addObject("bf", false);
            view.addObject("bt", true);
        }
        return view;
    }

    /**
     * Renders form for requested block.
     * 
     */
    @RequestMapping("/block/form/**")
    public ModelAndView form(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        ModelAndView view = new ModelAndView("blocks/oo-block-form");

        String blockRefId = RequestUtils.getId(request);
        String resourceObjectId = request.getParameter("resourceObjectId");
        TemplateBlockReference blockRef = (TemplateBlockReference) dao.get(blockRefId);

        logger.info("Generating form for block: {}", blockRef.getCode());
        view.addObject("blockReference", blockRef);

        if (!blockRef.getBlock().isGlobalBlock())
        {
            // Page block
            BaseNode blockData = dao.get(resourceObjectId);
            view.addObject("blockData", blockData);
            view.addObject("blockGlobal", false);
        }
        else
        {
            // Global block
            String blockCode = blockRef.getBlock().getCode();
            String typeDefName = StringUtils.codeToClassName(blockCode) + "Block";
            view.addObject("blockGlobal", true);
            BaseNode blockData = blockRef.getBlockData();
            if (blockData  != null)
            {
                view.addObject("blockData", blockData);
            }
            else
            {
                // New blocks
                view.addObject("location", blockRef.getId());
                view.addObject("typeDef", typeService.getType(typeDefName));
            }
        }
        view.addObject("daoService", this.daoService);

        return view;
    }

    /**
     * Saves arrangmement of blocks on a template.
     * 
     */
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
        Map<String,TemplateBlockReference> blockRefs = new HashMap<String, TemplateBlockReference>();

        // Gather all existing blockRefs
        for(TemplateRegion tr : template.getRegions())
        {
            for(TemplateBlockReference trb : tr.getBlocks())
            {
                blockRefs.put(trb.getId(), trb);
            }
            tr.setBlocks(new ArrayList<TemplateBlockReference>());
        }
        
        // Re-insert blockRefs according to arrangement
        for (int i = 0; i < regions.length(); i++)
        {
            JSONObject region = (JSONObject) regions.get(i);
            TemplateRegion tr = template.getRegions().get(i);

            JSONArray blockIds = (JSONArray) region.get("blockIds");
            for (int j = 0; j < blockIds.length(); j++)
            {
                String blockId = (String) blockIds.get(j);
                tr.getBlocks().add(blockRefs.get(blockId));
            }
        }
        dao.save(template, false);
        return null;
    }

}
