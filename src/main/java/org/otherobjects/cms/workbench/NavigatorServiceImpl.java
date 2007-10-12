package org.otherobjects.cms.workbench;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.Folder;
import org.otherobjects.cms.model.SiteFolder;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
public class NavigatorServiceImpl implements NavigatorService
{
    private UniversalJcrDao universalJcrDao;
    
    public WorkbenchItem getRootItem()
    {
        Object o = universalJcrDao.getByPath("/site");
        return (WorkbenchItem) o;
    }

    public List<WorkbenchItem> getSubContainers(String uuid)
    {
        BaseNode parent = universalJcrDao.get(uuid);
        List<BaseNode> all = universalJcrDao.getAllByPath(parent.getJcrPath());

        List<WorkbenchItem> children = new ArrayList<WorkbenchItem>();
        for (BaseNode n : all)
        {
            if (n instanceof Folder)
                children.add(n);
        }
        return children;
    }

    public WorkbenchItem addItem(String uuid, String name)
    {
        BaseNode parent = universalJcrDao.get(uuid);
        Assert.notNull(parent, "Parent node not found: " + uuid);

        int c = 0;
        do
        {
            String newPath = parent.getJcrPath() + "/untitled-" + ++c;
            boolean alreadyExists = (universalJcrDao.existsAtPath(newPath));
            if(!alreadyExists) break;
            
        } while(true);

        BaseNode newFolder = new SiteFolder();
        newFolder.setPath(parent.getJcrPath());
        newFolder.setLabel("Untitled " + c);
        return universalJcrDao.save(newFolder);
    }

    public WorkbenchItem getItem(String path)
    {
        return null;
    }

    public List<WorkbenchItem> getItemContents(String uuid)
    {
        BaseNode node = universalJcrDao.get(uuid);
        
        List<BaseNode> all = universalJcrDao.getAllByPath(node.getPath());

        List<WorkbenchItem> children = new ArrayList<WorkbenchItem>();
        for (BaseNode n : all)
        {
            if (!(n instanceof Folder))
                children.add(n);
        }
        return children;
    }

    public WorkbenchItem renameItem(String uuid, String newName)
    {
        BaseNode node = universalJcrDao.get(uuid);
        node.setOoLabel(newName);
        BaseNode renamed = universalJcrDao.save(node);
        return renamed;
    }

    public void moveItem(String itemId, String targetId, String point)
    {
        BaseNode item = universalJcrDao.get(itemId);
        BaseNode target = universalJcrDao.get(targetId);
        universalJcrDao.reorder(item, target, point);
    }

    public void removeItem(String path)
    {
    }

    public void setUniversalJcrDao(UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

}
