package org.otherobjects.cms.dao;

public class MockDaoService implements DaoService
{

    private GenericDao dao;

    public MockDaoService(GenericDao dao)
    {
        this.dao = dao;
    }

    public GenericDao getDao(Class clazz)
    {
        return dao;
    }

    public GenericDao getDao(String type)
    {
        return dao;
    }

    public boolean hasDao(String type)
    {
        return true;
    }

}
