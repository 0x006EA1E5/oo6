package org.otherobjects.cms.workbench;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.util.Assert;

/**
 * Default implementaion of content service.
 * 
 * @author rich
 */
public class ContentServiceImpl implements ContentService
{
    private DynaNodeDao dynaNodeDao;

    public DynaNode createItem(String container, String typeName)
    {
        Assert.notNull("container must be specified.",container);
        Assert.notNull("typeName must be specified.",typeName);
        
        //TODO Make sure this throws exception is not exists
        DynaNode parent = dynaNodeDao.get(container);
        dynaNodeDao.create(typeName);
        
        //TODO M2 Merge this code with NavService version
        int c = 0;
        do
        {
            String newPath = parent.getJcrPath() + "/untitled-article-" + ++c +".html";
            boolean alreadyExists = (dynaNodeDao.existsAtPath(newPath));
            if(!alreadyExists) break;
            
        } while(true);
        
        DynaNode newNode = dynaNodeDao.create(typeName);
        newNode.setPath(parent.getJcrPath());
        newNode.setCode("untitled-article-" + c +".html"); //TODO M2 Auto generate
        newNode.setLabel("Untitled Article " + c);
        return dynaNodeDao.save(newNode);
    }
    
    public void publishItem(String uuid)
    {
        Assert.notNull("item must be specified.",uuid);
        
        DynaNode item = dynaNodeDao.get(uuid);
        dynaNodeDao.publish(item);
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

}
