package org.otherobjects.cms.dao;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.otherobjects.cms.hibernate.GenericDaoHibernate;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;

@SuppressWarnings("unchecked")
public class DaoServiceImplTest extends TestCase
{
    private DaoServiceImpl daoService;

    @Override
    protected void setUp() throws Exception
    {
        this.daoService = new DaoServiceImpl();

        Map daoMap = new HashMap();
        daoMap.put("org.otherobjects.cms.model.User", new UserDaoHibernate());
        this.daoService.setDaoMap(daoMap);
        
        GenericApplicationContext ac = new GenericApplicationContext();
        ac.registerBeanDefinition("universalJcrDao", new RootBeanDefinition(UniversalJcrDaoJackrabbit.class));
        this.daoService.setBeanFactory(ac);

        super.setUp();
    }

    public void testGetDao()
    {
        GenericDao dao = this.daoService.getDao("org.otherobjects.cms.model.User");
        assertNotNull(dao);
        assertTrue(dao instanceof GenericDaoHibernate);

        // FIXME Add in test object
//        dao = this.daoService.getDao("org.otherobjects.cms.model.Article");
//        assertNotNull(dao);
//        assertTrue(dao instanceof UniversalJcrDaoJackrabbit);
    }

    public void testDetermineDaoBeanName()
    {
        String t1 = "org.otherobjects.cms.model.Role";
        String t1r = "roleDao";
        String t2 = "org.otherobjects.cms.types.TypeDef";
        String t2r = "typeDefDao";
        String t3 = "Article";
        String t3r = "articleDao";

        assertEquals(t1r, this.daoService.determineDaoBeanName(t1));
        assertEquals(t2r, this.daoService.determineDaoBeanName(t2));
        assertEquals(t3r, this.daoService.determineDaoBeanName(t3));

    }

}
