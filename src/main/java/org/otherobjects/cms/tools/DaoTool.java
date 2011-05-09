package org.otherobjects.cms.tools;

import java.io.Serializable;

import javax.annotation.Resource;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

@Component
@Tool
public class DaoTool
{
    @Resource
    private DaoService daoService;

    public GenericDao<Serializable, Serializable> get(String name)
    {
        return getDao(name);
    }

    public GenericDao<Serializable, Serializable> getDao(String name)
    {
        return daoService.getDao(name);
    }
}
