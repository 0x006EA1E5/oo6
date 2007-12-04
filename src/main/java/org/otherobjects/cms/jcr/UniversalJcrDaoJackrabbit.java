package org.otherobjects.cms.jcr;

import java.util.List;

import org.apache.jackrabbit.ocm.query.Filter;
import org.apache.jackrabbit.ocm.query.Query;
import org.apache.jackrabbit.ocm.query.QueryManager;
import org.otherobjects.cms.model.BaseNode;

public class UniversalJcrDaoJackrabbit extends GenericJcrDaoJackrabbit<BaseNode> implements UniversalJcrDao
{
    public UniversalJcrDaoJackrabbit()
    {
    }

    @Override
    public List<BaseNode> getAll()
    {
        throw new UnsupportedOperationException("Not available in the universal dao. Use a specific dao or the generic dao instead.");
    }

    @SuppressWarnings("unchecked")
    public List<BaseNode> getAllByType(Class type)
    {
        QueryManager queryManager = getJcrMappingTemplate().createQueryManager();
        Filter filter = queryManager.createFilter(type);
        Query query = queryManager.createQuery(filter);
        return (List<BaseNode>) getJcrMappingTemplate().getObjects(query);
    }

    @SuppressWarnings("unchecked")
    public List<BaseNode> getByPathAndType(String path, String type)
    {
        // TODO Check path ends with slash
        String xpath = "/jcr:root" + path + "/element(*, oo:node) [@ooType = '" + type + "']";
        return getAllByJcrExpression(xpath);
    }
}
