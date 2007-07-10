package org.otherobjects.cms.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller providing JSON data to the workbench. Currently supports navigator structure and
 * container contents.
 * 
 * @author rich
 */
public class WorkbenchDataController implements Controller
{
    private DynaNodeDao dynaNodeDao;

    public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        String path = request.getPathInfo();
        path = path.substring(6);

        if (path.endsWith("listing"))
            return generateListingData(request);
        else
            return generateNavigatorData(request);

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
}
