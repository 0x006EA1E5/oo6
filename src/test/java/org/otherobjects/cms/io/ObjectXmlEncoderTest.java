package org.otherobjects.cms.io;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.binding.TestComponentObject;
import org.otherobjects.cms.binding.TestObject;
import org.otherobjects.cms.binding.TestReferenceObject;
import org.otherobjects.cms.config.OtherObjectsConfigurator;
import org.otherobjects.cms.dao.MockDaoService;
import org.otherobjects.cms.dao.MockGenericJcrDao;
import org.otherobjects.cms.dao.UniversalJcrDaoJackrabbit;
import org.otherobjects.cms.datastore.JackrabbitDataStore;
import org.otherobjects.cms.model.Role;
import org.otherobjects.cms.model.Template;
import org.otherobjects.cms.model.TemplateBlock;
import org.otherobjects.cms.model.TemplateBlockReference;
import org.otherobjects.cms.model.TemplateLayout;
import org.otherobjects.cms.model.TemplateRegion;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.AnnotationBasedTypeDefBuilder;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;

import edu.emory.mathcs.backport.java.util.Arrays;

public class ObjectXmlEncoderTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();

    @Override
    protected void setUp() throws Exception
    {
        //setup type service for Template, TemplateRegion TemplateBlock
        super.setUp();
        SingletonBeanLocator.registerTestBean("typeService", typeService);
        AnnotationBasedTypeDefBuilder typeDefBuilder = new AnnotationBasedTypeDefBuilder();
        OtherObjectsConfigurator otherObjectsConfigurator = new OtherObjectsConfigurator();
        otherObjectsConfigurator.setProperty("otherobjects.default.date.format", "yyyy-MM-dd");
        otherObjectsConfigurator.setProperty("otherobjects.default.time.format", "HH:mm:ss");
        otherObjectsConfigurator.setProperty("otherobjects.default.timestamp.format", "yyyy-MM-dd HH:mm:ss");
        typeDefBuilder.setOtherObjectsConfigurator(otherObjectsConfigurator);
        typeDefBuilder.afterPropertiesSet();

        typeService.registerType(typeDefBuilder.getTypeDef(TestObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TestReferenceObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TestComponentObject.class));
        typeService.registerType(typeDefBuilder.getTypeDef(Template.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TemplateLayout.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TemplateRegion.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TemplateBlock.class));
        typeService.registerType(typeDefBuilder.getTypeDef(TemplateBlockReference.class));
        ((TypeServiceImpl) typeService).reset();

        // Hibernate stored types
        typeService.registerType(typeDefBuilder.getTypeDef(User.class));
        typeService.registerType(typeDefBuilder.getTypeDef(Role.class));
        
        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("ArticlePage");
        td.setLabelProperty("title");
        td.addProperty(new PropertyDefImpl("title", "string", null, null, true));
        typeService.registerType(td);

    }
    
    @SuppressWarnings("unchecked")
    public void testEncode() 
    {
        TestObject t1 = new TestObject();
        t1.setPath("/path/");
        t1.setId("test-id-1");
        t1.setTestString("test-name");
        t1.setTestDate(new Date());
        t1.setTestTime(new Date());
        t1.setTestTimestamp(new Date());
        t1.setTestNumber(456L);
        t1.setTestDecimal(new BigDecimal("12.23"));
        t1.setTestBoolean(true);
        
        TestReferenceObject tr1 = new TestReferenceObject();
        tr1.setJcrPath("/test/tr1");
        tr1.setId("4135bb8e-61e3-4cbc-a20a-fdc7d143ddb9");
        t1.setTestReference(tr1);

        TestComponentObject tc1 = new TestComponentObject();
        tc1.setName("component-name");
        t1.setTestComponent(tc1);
        TestComponentObject tc2 = new TestComponentObject();
        tc1.setName("component-name-2");
        t1.setTestComponent(tc1);
        
        t1.setTestStringsList(Arrays.asList(new String[]{"one", "two", "three"}));
        t1.setTestComponentsList(Arrays.asList(new TestComponentObject[]{tc1, tc2}));
        
        ObjectXmlEncoder encoder = new ObjectXmlEncoder();
        TypeDef type = typeService.getType(TestObject.class);
        Document doc = encoder.encode(t1, type);
        
        MockGenericJcrDao dao = new MockGenericJcrDao(tr1);
        MockDaoService daoService = new MockDaoService(dao);
        
        ObjectXmlDecoder decoder = new ObjectXmlDecoder();
        decoder.setTypeService(typeService);
        JackrabbitDataStore jackrabbitDataStore = new JackrabbitDataStore();
        UniversalJcrDaoJackrabbit universalJcrDao = new UniversalJcrDaoJackrabbit();
        universalJcrDao.setTypeService(typeService);
        jackrabbitDataStore.setUniversalJcrDao(universalJcrDao);
        decoder.setDaoService(daoService);
        decoder.setJackrabbitDataStore(jackrabbitDataStore);

        List<Object> decodedObjects = decoder.decode(doc);
        TestObject t1r = (TestObject) decodedObjects.get(0);
        assertEquals(1, decodedObjects.size());
        assertEquals(t1.getId(), t1r.getId());
        assertEquals(t1.getJcrPath(), t1r.getJcrPath());
        assertEquals(t1.getTestString(), t1r.getTestString());
        assertEquals(t1.getTestDecimal(), t1r.getTestDecimal());
        assertEquals(t1.getTestBoolean(), t1r.getTestBoolean());
        assertEquals(t1.getTestNumber(), t1r.getTestNumber());
        //assertEquals(t1.getTestTimestamp(), t1r.getTestTimestamp()); TODO Need to round date when setting
        assertEquals(t1.getTestReference().getId(), t1r.getTestReference().getId());
        assertEquals(t1.getTestComponent().getName(), t1r.getTestComponent().getName());
        assertEquals(t1.getTestStringsList().get(0), t1r.getTestStringsList().get(0));
        assertEquals(t1.getTestComponentsList().get(0).getName(), t1r.getTestComponentsList().get(0).getName());
        
    }
}
