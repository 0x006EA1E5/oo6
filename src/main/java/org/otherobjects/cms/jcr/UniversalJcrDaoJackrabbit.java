package org.otherobjects.cms.jcr;

import java.util.List;

import org.otherobjects.cms.model.BaseNode;

public class UniversalJcrDaoJackrabbit extends GenericJcrDaoJackrabbit<BaseNode> implements UniversalJcrDao
{
    public UniversalJcrDaoJackrabbit()
    {
    }
      
    @Override
    public List<BaseNode> getAll()
    {
        throw new UnsupportedOperationException("Not available in the niversal dao. Use a specific dao or the generic dao instead.");
    }
}
