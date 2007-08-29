package org.otherobjects.cms.dao;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.otherobjects.cms.types.TypeDefDaoJackrabbit;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

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

        GenericApplicationContext ac = new GenericApplicationContext();
        // FIXME Test with beans in the factory
        daoService.setBeanFactory(ac);
        
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

    public void testDetermineDaoBeanName()
    {
        String t1 = "org.otherobjects.cms.model.Role";
        String t1r = "roleDao";
        String t2 = "org.otherobjects.cms.types.TypeDef";
        String t2r = "typeDefDao";
        String t3 = "Article";
        String t3r = "articleDao";
        
        assertEquals(t1r, daoService.determineDaoBeanName(t1));
        assertEquals(t2r, daoService.determineDaoBeanName(t2));
        assertEquals(t3r, daoService.determineDaoBeanName(t3));
        
    }

}
