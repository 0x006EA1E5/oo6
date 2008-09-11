package org.otherobjects.cms.model;

import java.util.List;

import org.otherobjects.cms.jcr.GenericJcrDaoJackrabbit;

public class FolderDaoImpl extends GenericJcrDaoJackrabbit<SiteFolder> implements FolderDao
{
    public FolderDaoImpl()
    {
        super(SiteFolder.class);
    }

    public List<SiteFolder> getFolders()
    {
        return getAllByJcrExpression("/jcr:root//element(*) [@ooType='org.otherobjects.cms.model.SiteFolder']");
    }
}
