package org.otherobjects.cms.dao;

import java.util.HashMap;
import java.util.Map;

import org.otherobjects.cms.types.TypeDefDaoJackrabbit;

import junit.framework.TestCase;

@SuppressWarnings("unchecked")
public class DaoServiceImplTest extends TestCase
{
    private DaoServiceImpl daoService;

    @Override
    protected void setUp() throws Exception
    {
        daoService = new DaoServiceImpl();

        Map daoMap = new HashMap();
        daoMap.put("org.otherobjects.cms.types.TypeDef", new TypeDefDaoJackrabbit());
        daoMap.put("org.otherobjects.cms.model.DynaNode", new DynaNodeDaoJackrabbit());
        
        daoService.setDaoMap(daoMap);
        super.setUp();
    }

    public void testGetDao()
    {
        GenericDao dao = daoService.getDao("org.otherobjects.cms.types.TypeDef");
        assertNotNull(dao);
        assertTrue(dao instanceof TypeDefDaoJackrabbit);
        
        dao = daoService.getDao("Article");
        assertNotNull(dao);
        assertTrue(dao instanceof DynaNodeDaoJackrabbit);
    }

}
