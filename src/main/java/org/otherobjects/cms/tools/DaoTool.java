package org.otherobjects.cms.tools;

import javax.annotation.Resource;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.GenericDao;
import org.otherobjects.cms.views.Tool;
import org.springframework.stereotype.Component;

@Component
@Tool
@SuppressWarnings("unchecked")
public class DaoTool
{
    @Resource
    private DaoService daoService;

    public GenericDao get(String name)
    {
        return getDao(name);
    }

    public GenericDao getDao(String name)
    {
        return daoService.getDao(name);
    }
}
