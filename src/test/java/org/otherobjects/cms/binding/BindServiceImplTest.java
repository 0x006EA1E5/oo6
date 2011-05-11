package org.otherobjects.cms.binding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.otherobjects.cms.dao.MockDaoService;
import org.otherobjects.cms.dao.MockGenericDao;
import org.otherobjects.cms.dao.UniversalJcrDaoJackrabbit;
import org.otherobjects.cms.datastore.JackrabbitDataStore;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.otherobjects.framework.SingletonBeanLocator;
import org.otherobjects.framework.config.OtherObjectsConfigurator;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.validation.BindingResult;

public class BindServiceImplTest extends TestCase
{
    private final TypeService typeService = new TypeServiceImpl();

    @Override
    protected void setUp() throws Exception
    {
        // FIXME Rewrite using TestObject
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

        this.typeService.registerType(typeDefBuilder.getTypeDef(TestObject.class));
        this.typeService.registerType(typeDefBuilder.getTypeDef(TestReferenceObject.class));
        this.typeService.registerType(typeDefBuilder.getTypeDef(TestComponentObject.class));
        ((TypeServiceImpl) this.typeService).reset();

        // Hibernate stored types
        this.typeService.registerType(typeDefBuilder.getTypeDef(User.class));
        this.typeService.registerType(typeDefBuilder.getTypeDef(Role.class));

        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("ArticlePage");
        td.setLabelProperty("title");
        td.addProperty(new PropertyDefImpl("title", "string", null, null, true));
        this.typeService.registerType(td);

    }

    public void testEmptyStringBinding() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("name", "");
        req.addParameter("testString", "  no trailing white space\n");
        req.addParameter("testDate", "");
        req.addParameter("testReference", "");

        TestObject t = new TestObject();

        BindServiceImpl binder = new BindServiceImpl();
        TypeDef typeDef = this.typeService.getType(TestObject.class);

        binder.bind(t, typeDef, req);

        assertEquals(null, t.getName());
        assertEquals("no trailing white space", t.getTestString());
        assertEquals(null, t.getTestReference());
        assertEquals(null, t.getTestDate());
    }

    public void testFairlyComplexOOTemplateBinding() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("id", "c0a03a8a-d25c-435a-9d3c-9e181f7e9016");
        req.addParameter("name", "Name");
        req.addParameter("testReference", "db6b724b-f696-419f-837c-f64796625efe");
        req.addParameter("testComponentsList[0].name", "TC1");
        req.addParameter("testComponentsList[1].name", "TC2");

        // Setup objects and DAOs

        TestReferenceObject tr1 = new TestReferenceObject();
        tr1.setName("TR1");
        tr1.setId("db6b724b-f696-419f-837c-f64796625efe");

        Map<String, Object> objects = new HashMap<String, Object>();
        objects.put(tr1.getId(), tr1);
        MockGenericDao dao = new MockGenericDao(objects);

        BindServiceImpl bs = new BindServiceImpl();
        JackrabbitDataStore jackrabbitDataStore = new JackrabbitDataStore();
        UniversalJcrDaoJackrabbit universalJcrDao = new UniversalJcrDaoJackrabbit();
        universalJcrDao.setTypeService(this.typeService);
        jackrabbitDataStore.setUniversalJcrDao(universalJcrDao);
//        bs.setJackrabbitDataStore(jackrabbitDataStore);
        bs.setDaoService(new MockDaoService(dao));

        TestObject item = new TestObject();
        TypeDef typeDef = this.typeService.getType(TestObject.class);

        bs.bind(item, typeDef, req);

        assertEquals("Name", item.getName());
        assertEquals("TR1", item.getTestReference().getName());
        assertEquals("TC1", item.getTestComponentsList().get(0).getName());
        assertEquals("TC2", item.getTestComponentsList().get(1).getName());
    }

    public void testFairlyComplexOOTemplateBindingMultipart() throws Exception
    {
        MockMultipartHttpServletRequest req = new MockMultipartHttpServletRequest();
        req.addFile(new MockMultipartFile("testFile", "This is the file content".getBytes("UTF-8")));

        req.addParameter("name", "Name");

        BindServiceImpl bs = new BindServiceImpl();
        JackrabbitDataStore jackrabbitDataStore = new JackrabbitDataStore();
        UniversalJcrDaoJackrabbit universalJcrDao = new UniversalJcrDaoJackrabbit();
        universalJcrDao.setTypeService(this.typeService);
        jackrabbitDataStore.setUniversalJcrDao(universalJcrDao);
//        bs.setJackrabbitDataStore(jackrabbitDataStore);

        TestObject item = new TestObject();
        TypeDef typeDef = this.typeService.getType(TestObject.class);

        bs.bind(item, typeDef, req);

        assertEquals("Name", item.getName());
        assertEquals("testFile", item.getTestFile().getName());
        assertEquals(24, item.getTestFile().getBytes().length);
    }

    public void testSetValue()
    {
        TestObject to = new TestObject();
        BindServiceImpl bs = new BindServiceImpl();
        bs.setValue(to, "name", "Test");
    }

    public void testHibernateBinding() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("email", "user@email.com");
        req.addParameter("username", "test");
        req.addParameter("roles[0]", "org.otherobjects.cms.model.Role-1");

        //setup objects
        Role role = new Role();
        role.setId(1L);
        role.setName("Test Role");

        Map<Serializable, Object> objects = new HashMap<Serializable, Object>();
        objects.put(1L, role);
        MockGenericDao roleDao = new MockGenericDao(objects);

        BindServiceImpl bs = new BindServiceImpl();
        bs.setDaoService(new MockDaoService(roleDao));

        TypeDef userTypeDef = this.typeService.getType(User.class);

        User u = new User();

        BindingResult result = bs.bind(u, userTypeDef, req);

        assertEquals(0, result.getErrorCount());

    }

}
