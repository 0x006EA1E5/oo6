package org.otherobjects.cms.binding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.otherobjects.framework.SingletonBeanLocator;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.otherobjects.cms.dao.MockDaoService;
import org.otherobjects.cms.dao.MockGenericDao;
import org.otherobjects.cms.dao.UniversalJcrDaoJackrabbit;
import org.otherobjects.cms.datastore.HibernateDataStore;
import org.otherobjects.cms.datastore.JackrabbitDataStore;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.springframework.mock.web.MockHttpServletRequest;

public class HibernateBindServiceImplTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();

    @Override
    protected void setUp() throws Exception
    {
        //setup type service for Template, TemplateRegion TemplateBlock
        super.setUp();
        SingletonBeanLocator.registerTestBean("typeService", this.typeService);
        AnnotationBasedTypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        OtherObjectsConfigurator otherObjectsConfigurator = new OtherObjectsConfigurator();
        otherObjectsConfigurator.setProperty("otherobjects.default.date.format", "yyy-MM-dd");
        otherObjectsConfigurator.setProperty("otherobjects.default.time.format", "yyy-MM-dd");
        otherObjectsConfigurator.setProperty("otherobjects.default.timestamp.format", "yyy-MM-dd");
        typeDefBuilder.setOtherObjectsConfigurator(otherObjectsConfigurator);
        typeDefBuilder.afterPropertiesSet();

        // Hibernate stored types
        this.typeService.registerType(typeDefBuilder.getTypeDef(TestDbObject.class));
        this.typeService.registerType(typeDefBuilder.getTypeDef(User.class));
        this.typeService.registerType(typeDefBuilder.getTypeDef(Role.class));
    }

    public void testReferenceBinding() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("role", "org.otherobjects.cms.model.Role-1");

        // setup mock dao
        Map<Serializable, Object> objects = new HashMap<Serializable, Object>();
        objects.put(1L, new Role("TEST","Test Role"));
        MockGenericDao dao = new MockGenericDao(objects);

        MockDaoService daoService = new MockDaoService(dao);
        
        BindServiceImpl bs = new BindServiceImpl();
        JackrabbitDataStore jackrabbitDataStore = new JackrabbitDataStore();
        UniversalJcrDaoJackrabbit universalJcrDao = new UniversalJcrDaoJackrabbit();
        universalJcrDao.setTypeService(this.typeService);
        jackrabbitDataStore.setUniversalJcrDao(universalJcrDao);
        HibernateDataStore hibernateDataStore = new HibernateDataStore();
        hibernateDataStore.setDaoService(daoService);
//        bs.setHibernateDataStore(hibernateDataStore);

        bs.setDaoService(new MockDaoService(dao));

        TypeDef templateTypeDef = this.typeService.getType(TestDbObject.class);

        TestDbObject rootItem = new TestDbObject();
        bs.bind(rootItem, templateTypeDef, req);

        assertNotNull(rootItem.getRole());
        assertEquals("TEST", rootItem.getRole().getAuthority());

    }
}
