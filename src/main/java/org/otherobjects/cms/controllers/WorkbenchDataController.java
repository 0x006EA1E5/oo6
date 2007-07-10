package org.otherobjects.cms.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller providing a REST style interface to site data. Currently supports navigator structure and
 * container contents. Data is served in JSON format.
 * 
 * <p>/navigator?node= (sends sub-contaniners of specified container)
 * <br>/listing?node= (sends items in specified container)
 * <br/>/type/typeName (sends typeDef info)
 * 
 * @author rich
 */
public class WorkbenchDataController implements Controller
{
    private Logger logger = LoggerFactory.getLogger(WorkbenchDataController.class);
    
    private DynaNodeDao dynaNodeDao;
    private TypeService typeService;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String path = request.getPathInfo();
        path = path.substring(6);

        if (path.contains("/types/"))
            return generateTypeData(request);
        else if (path.contains("/item/"))
            return generateItemData(request);
        else if (path.endsWith("/listing"))
            return generateListingData(request);
        else if (path.endsWith("/navigator"))
            return generateNavigatorData(request);
        else
            return null;
    }

    private ModelAndView generateItemData(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        String id = path.substring(path.lastIndexOf("/")+1);
        
        logger.info("Sending item data: {} ", id);
        
        DynaNode dynaNode = dynaNodeDao.get(id);
        
        Assert.notNull(dynaNode, "No item found: " + dynaNode);
        
        ModelAndView view = new ModelAndView("jsonView");
        view.addObject("json", dynaNode);
        view.addObject("jsonIncludes", "properties");
        return view;
    }
    private ModelAndView generateTypeData(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        String typeName = path.substring(path.lastIndexOf("/")+1);
        
        logger.info("Sending type definition: {} ", typeName);
        
        TypeDef type = typeService.getType(typeName);
        Assert.notNull(type, "No type found: " + typeName);

        ModelAndView view = new ModelAndView("jsonView");
        view.addObject("json", type);
        view.addObject("jsonIncludes", "properties");
        return view;
    }

    private ModelAndView generateNavigatorData(HttpServletRequest request)
    {

        ModelAndView view = new ModelAndView("jsonView");

        String nodeId = request.getParameter("node");
        String jcrPath = "/site";
        if (nodeId != null && nodeId.length() > 10)
        {
            DynaNode node = dynaNodeDao.get(nodeId);
            jcrPath = node.getJcrPath();
        }

        // FIXME Use NavigatorService
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();
        List<DynaNode> contents = dynaNodeDao.getAllByPath(jcrPath);

        for (DynaNode dynaNode : contents)
        {
            if (dynaNode.getOoType().equals("Folder"))
            {
                Map<String, Object> n1 = new HashMap<String, Object>();
                n1.put("id", dynaNode.getId());
                n1.put("text", dynaNode.getLabel());
                n1.put("leaf", false);
                nodes.add(n1);
            }
        }
        view.addObject("data", nodes);
        return view;
    }

    private ModelAndView generateListingData(HttpServletRequest request)
    {
        String jcrPath = "/site";

        ModelAndView view = new ModelAndView("jsonView");
        List<Map<String, Object>> nodes = new ArrayList<Map<String, Object>>();

        String nodeId = request.getParameter("node");
        if (nodeId != null && nodeId.length() > 10)
        {
            DynaNode node = dynaNodeDao.get(nodeId);
            jcrPath = node.getJcrPath();
        }
        List<DynaNode> contents = dynaNodeDao.getAllByPath(jcrPath);

        for (DynaNode dynaNode : contents)
        {

            if (dynaNode.getOoType().equals("Article"))
            {
                Map<String, Object> n1 = new HashMap<String, Object>();
                n1.put("id", dynaNode.getId());
                n1.put("label", dynaNode.getLabel());
                n1.put("linkPath", dynaNode.getLinkPath());
                nodes.add(n1);
            }

        }
        view.addObject("data", nodes);
        return view;
    }

    public DynaNodeDao getDynaNodeDao()
    {
        return dynaNodeDao;
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }
}
