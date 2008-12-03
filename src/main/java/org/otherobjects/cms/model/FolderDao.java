package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.site.Tree;

public interface FolderDao extends GenericJcrDao<SiteFolder>
{
    List<SiteFolder> getFolders();
    Tree getFolderTree();
}
