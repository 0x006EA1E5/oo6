package org.otherobjects.cms.jcr;

import java.util.List;

import org.otherobjects.cms.dao.GenericJcrDao;
import org.otherobjects.cms.model.BaseNode;

public interface UniversalJcrDao extends GenericJcrDao<BaseNode>
{
    <X extends BaseNode> List<X> getAllByType(String type);
    <X extends BaseNode> List<X> getAllByType(Class<X> type);

    <X extends BaseNode> List<X> getByPathAndType(String path, String type);
    
    BaseNode create(String type);
    BaseNode copy(String id, String newPath);
}
