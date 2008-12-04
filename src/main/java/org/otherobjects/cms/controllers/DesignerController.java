package org.otherobjects.cms.controllers;

import java.util.ArrayList;
import java.util.HashMap;
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
import org.otherobjects.cms.model.TemplateBlockReference;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.model.TemplateRegion;
import org.otherobjects.cms.util.RequestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that manages template design.
 * 
 * @author rich
 */
@Controller
public class DesignerController
{
    private final Logger logger = LoggerFactory.getLogger(DesignerController.class);

    @Resource
    private DaoService daoService;

    /**
     * Saves arrangmement of blocks on a template.
     */
    @RequestMapping("/designer/saveArrangement/**")
    public ModelAndView saveArrangement(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        String templateId = request.getParameter("templateId");

        String arrangement = request.getParameter("arrangement");
        logger.info("Arrangement: " + arrangement);
        JSONArray regions = new JSONArray(arrangement);

        // Load existing template
        Template template = (Template) dao.get(templateId);
        Map<String, TemplateBlockReference> blockRefs = new HashMap<String, TemplateBlockReference>();

        // Gather all existing blockRefs
        for (TemplateRegion tr : template.getRegions())
        {
            for (TemplateBlockReference trb : tr.getBlocks())
            {
                blockRefs.put(trb.getId(), trb);
            }
            tr.setBlocks(new ArrayList<TemplateBlockReference>());
        }

        // Re-insert blockRefs according to arrangement
        for (int i = 0; i < regions.length(); i++)
        {
            JSONObject region = (JSONObject) regions.get(i);
            TemplateRegion tr = template.getRegion((String) region.get("name"));

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

    /**
     * Creates a new template for specified type.
     */
    @RequestMapping("/designer/createTemplate/**")
    public ModelAndView createTemplate(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        UniversalJcrDao dao = (UniversalJcrDao) daoService.getDao("baseNode");
        String resourceObjectId = RequestUtils.getId(request);
        BaseNode resourceObject = dao.get(resourceObjectId);

        String templateCode = request.getParameter("code");
        String layoutId = request.getParameter("layout");

        Template template = new Template();
        template.setPath("/designer/templates/");
        template.setCode(templateCode);
        template.setLabel(resourceObject.getTypeDef().getLabel() + " Template");
        TemplateLayout layout = (TemplateLayout) dao.get(layoutId);
        template.setLayout(layout);
        dao.save(template, false);
        // FIXME Need to wrap linkPath
        response.sendRedirect(resourceObject.getOoUrlPath());
        return null;
    }

    /**
     * Changes layout for specified template.
     */
    @RequestMapping("/designer/changeLayout/**")
    public ModelAndView changeLayout(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        return null;
    }

}
