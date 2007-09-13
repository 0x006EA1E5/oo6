package org.otherobjects.cms.model;

import org.otherobjects.cms.dao.GenericDao;

public interface CmsImageDao extends GenericDao<DynaNode, String>
{
    public CmsImage createCmsImage();

    public void publish(DynaNode dynaNode);
}
