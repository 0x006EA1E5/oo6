package org.otherobjects.cms.workbench;

import java.util.ArrayList;
import java.util.List;

import org.otherobjects.cms.jcr.UniversalJcrDao;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.Folder;
import org.otherobjects.cms.model.SiteFolder;
import org.springframework.util.Assert;

public class NavigatorServiceImpl implements NavigatorService
{
    private UniversalJcrDao universalJcrDao;

    public final WorkbenchItem getRootItem()
    {
        Object o = universalJcrDao.getByPath("/site");
        return (WorkbenchItem) o;
    }

    public final List<WorkbenchItem> getSubContainers(final String uuid)
    {
        BaseNode parent = universalJcrDao.get(uuid);
        List<BaseNode> all = universalJcrDao.getAllByPath(parent.getJcrPath());

        List<WorkbenchItem> children = new ArrayList<WorkbenchItem>();
        for (BaseNode n : all)
        {
            if (n instanceof Folder)
            {
                children.add(n);
            }
        }
        return children;
    }

    public final WorkbenchItem addItem(final String uuid, final String name)
    {
        BaseNode parent = universalJcrDao.get(uuid);
        Assert.notNull(parent, "Parent node not found: " + uuid);

        int c = 0;
        do
        {
            String newPath = parent.getJcrPath() + "/untitled-" + ++c;
            boolean alreadyExists = (universalJcrDao.existsAtPath(newPath));
            if (!alreadyExists)
            {
                break;
            }

        }
        while (true);

        BaseNode newFolder = new SiteFolder();
        newFolder.setPath(parent.getJcrPath());
        newFolder.setLabel("Untitled " + c);
        return universalJcrDao.save(newFolder);
    }

    public final WorkbenchItem getItem(final String path)
    {
        return null;
    }

    public final List<WorkbenchItem> getItemContents(final String uuid)
    {
        BaseNode node = universalJcrDao.get(uuid);

        List<BaseNode> all = universalJcrDao.getAllByPath(node.getPath());

        List<WorkbenchItem> children = new ArrayList<WorkbenchItem>();
        for (BaseNode n : all)
        {
            if (!(n instanceof Folder))
            {
                children.add(n);
            }
        }
        return children;
    }

    public final WorkbenchItem renameItem(final String uuid, final String newName)
    {
        BaseNode node = universalJcrDao.get(uuid);
        node.setOoLabel(newName);
        BaseNode renamed = universalJcrDao.save(node);
        return renamed;
    }

    public final void moveItem(final String itemId, final String targetId, final String point)
    {
        BaseNode item = universalJcrDao.get(itemId);
        BaseNode target = universalJcrDao.get(targetId);
        universalJcrDao.reorder(item, target, point);
    }

    public void removeItem(final String path)
    {
    }

    public final void setUniversalJcrDao(final UniversalJcrDao universalJcrDao)
    {
        this.universalJcrDao = universalJcrDao;
    }

}
