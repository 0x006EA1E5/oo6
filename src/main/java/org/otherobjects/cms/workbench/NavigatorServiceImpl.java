package org.otherobjects.cms.workbench;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;

public class NavigatorServiceImpl implements NavigatorService
{
    private DynaNodeDao dynaNodeDao;

    public WorkbenchItem getRootItem()
    {
        Object o = dynaNodeDao.getByPath("/site");
        return (WorkbenchItem) o;
    }

    public List<WorkbenchItem> getSubContainers(String uuid)
    {
        DynaNode parent = dynaNodeDao.get(uuid);
        List<DynaNode> all = dynaNodeDao.getAllByPath(parent.getJcrPath());

        List<WorkbenchItem> children = new ArrayList<WorkbenchItem>();
        for (DynaNode n : all)
        {
            if (n.getOoType().equals("Folder"))
                children.add(n);
        }
        return children;
    }

    public void addItem(String uuid, String name)
    {
        DynaNode parent = dynaNodeDao.get(uuid);

        DynaNode newFolder = new DynaNode("Folder");
        newFolder.setPath(parent.getJcrPath() + "/");
        newFolder.setCode("untitled");
        newFolder.set("label", "Untitled");
        dynaNodeDao.save(newFolder);
    }

    public WorkbenchItem getItem(String path)
    {
        return null;
    }

    public List<WorkbenchItem> getItemContents(String path)
    {
        return null;
    }

    public WorkbenchItem moveItem(String uuid, String newPath)
    {
        DynaNode node = dynaNodeDao.get(uuid);
//        node.setCode("untitled2");
        node.set("label", "Untitled2");
        dynaNodeDao.save(node);
        dynaNodeDao.move(node,node.getPath() + "untitled2");
        return null;
    }

    public void removeItem(String path)
    {
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
