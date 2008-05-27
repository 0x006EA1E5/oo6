package org.otherobjects.cms.jcr;

import java.util.List;

import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.model.BaseNode;

public interface UniversalJcrDao extends GenericJcrDao<BaseNode>
{
    List<BaseNode> getAllByType(Class<?> type);

    List<BaseNode> getByPathAndType(String path, String type);
}
