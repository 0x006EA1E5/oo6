package org.otherobjects.cms.workbench;

import java.util.Date;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.StringUtils;

/**
 * Default implementaion of content service.
 * 
 * @author rich
 */
public class ContentServiceImpl implements ContentService
{
    private TypeService typeService;
    private DynaNodeDao dynaNodeDao;

    public void createItem(String container, String typeName)
    {
        //TODO Check location and type are valid
        DynaNode parent = dynaNodeDao.get(container);
        String label = "Untitled " + new Date().getTime();
        DynaNode item = new DynaNode(typeName);
        item.setPath(parent.getJcrPath() + "/");
        item.setCode(StringUtils.generateUrlCode(label) + ".html");
        item.setLabel(label);
        dynaNodeDao.save(item);
    }

    public TypeService getTypeService()
    {
        return typeService;
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public DynaNodeDao getDynaNodeDao()
    {
        return dynaNodeDao;
    }

    public void setDynaNodeDao(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

}
