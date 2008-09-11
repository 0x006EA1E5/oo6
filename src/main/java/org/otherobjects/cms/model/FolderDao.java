package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.dao.GenericJcrDao;

public interface FolderDao extends GenericJcrDao<SiteFolder>
{
    List<SiteFolder> getFolders();
}
