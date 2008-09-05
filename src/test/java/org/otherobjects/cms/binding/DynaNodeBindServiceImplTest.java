package org.otherobjects.cms.binding;

import junit.framework.TestCase;

import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.PropertyDefImpl;
import org.otherobjects.cms.types.TypeDefImpl;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.types.TypeServiceImpl;
import org.springframework.mock.web.MockHttpServletRequest;

public class DynaNodeBindServiceImplTest extends TestCase
{
    private TypeService typeService = new TypeServiceImpl();

    @Override
    protected void setUp() throws Exception
    {
        //setup type service for Template, TemplateRegion TemplateBlock
        super.setUp();
        SingletonBeanLocator.registerTestBean("typeService", typeService);

        // Add DynaNode type
        TypeDefImpl td = new TypeDefImpl("TestDynaNode");
        td.addProperty(new PropertyDefImpl("testString", "string", null, null, false, true));
        td.addProperty(new PropertyDefImpl("testText", "text", null, null, false, true));
        td.addProperty(new PropertyDefImpl("testDate", "date", null, null, false, true));
        typeService.registerType(td);
                
        ((TypeServiceImpl) typeService).reset();

    }

    public void testEmptyStringBinding() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        
        req.addParameter("data[testString]", "");
        req.addParameter("data[testText]", "  no trailing white space\n");
        req.addParameter("data[testDate]", "");
        
        DynaNode t = new DynaNode("TestDynaNode");

        BindServiceImpl binder = new BindServiceImpl();
        
        binder.bind(t, t.getTypeDef(), req);

        assertEquals(null, t.getData().get("testString"));
        assertEquals("no trailing white space",  t.getData().get("testText"));
        assertEquals(null, t.getData().get("testDate"));
    }
    
    /*
    
    public void testFairlyComplexOOTemplateBinding() throws Exception
    {
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addParameter("layout", "db6b724b-f696-419f-837c-f64796625efe");
        req.addParameter("label", "Client");
        req.addParameter("regions[1].code", "column-2");
        req.addParameter("regions[1].label", "");
        req.addParameter("regions[0].blocks[0]", "9bf23b21-2def-4039-8d6d-2182ca84aeb0");
        req.addParameter("regions[0].label", "");
        req.addParameter("regions[1].blocks[0]", "e90e78b7-fe89-43cf-bf6a-0ce4367206ca");
        req.addParameter("id", "c0a03a8a-d25c-435a-9d3c-9e181f7e9016");
        req.addParameter("regions[0].code", "column-1");

        //setup objects
        Template rootItem = new Template();
        rootItem.setId("c0a03a8a-d25c-435a-9d3c-9e181f7e9016");

        TemplateRegion column2 = new TemplateRegion();
        column2.setCode("column-2");

        TemplateLayout layout = new TemplateLayout();
        layout.setId("db6b724b-f696-419f-837c-f64796625efe");
        layout.setDescription("TEST lAYOUT");

        TemplateBlock block1 = new TemplateBlock();
        block1.setId("9bf23b21-2def-4039-8d6d-2182ca84aeb0");
        block1.setDescription("block1");

        TemplateBlock block2 = new TemplateBlock();
        block2.setId("e90e78b7-fe89-43cf-bf6a-0ce4367206ca");
        block2.setDescription("block2");

        List<TemplateRegion> regions = new ArrayList<TemplateRegion>();
        regions.add(null);
        regions.add(column2);
        rootItem.setRegions(regions);

        // setup mock dao
        Map<String, Object> objects = new HashMap<String, Object>();
        objects.put(layout.getId(), layout);
        objects.put(block1.getId(), block1);
        objects.put(block2.getId(), block2);
        MockGenericDao dao = new MockGenericDao(objects);

        BindServiceImpl bs = new BindServiceImpl();
        bs.setDaoService(new MockDaoService(dao));

        TypeDef templateTypeDef = typeService.getType(Template.class);

        bs.bind(rootItem, templateTypeDef, req);

        assertEquals("Client", rootItem.getLabel());
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[0]"));
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[1]"));
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[0].blocks[0]"));
        assertEquals("column-1", PropertyUtils.getNestedProperty(rootItem, "regions[0].code"));
        assertEquals("column-2", PropertyUtils.getNestedProperty(rootItem, "regions[1].code"));
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[1].blocks[0]"));

        // assertEquals("block1", PropertyUtils.getNestedProperty(rootItem, "regions[0].blocks[0].description"));
        //assertEquals("block2", PropertyUtils.getNestedProperty(rootItem, "regions[1].blocks[0].description"));

    }

    public void testFairlyComplexOOTemplateBindingMultipart() throws Exception
    {
        MockMultipartHttpServletRequest req = new MockMultipartHttpServletRequest();
        req.addFile(new MockMultipartFile("testFile", "This is the file content".getBytes("UTF-8")));

        req.addParameter("layout", "db6b724b-f696-419f-837c-f64796625efe");
        req.addParameter("label", "Client");
        req.addParameter("regions[1].code", "column-2");
        req.addParameter("regions[1].label", "");
        req.addParameter("regions[0].blocks[0]", "9bf23b21-2def-4039-8d6d-2182ca84aeb0");
        req.addParameter("regions[0].label", "");
        req.addParameter("regions[1].blocks[0]", "e90e78b7-fe89-43cf-bf6a-0ce4367206ca");
        req.addParameter("id", "c0a03a8a-d25c-435a-9d3c-9e181f7e9016");
        req.addParameter("regions[0].code", "column-1");

        //setup objects
        Template rootItem = new Template();
        rootItem.setId("c0a03a8a-d25c-435a-9d3c-9e181f7e9016");

        TemplateRegion column2 = new TemplateRegion();
        column2.setCode("column-2");

        TemplateLayout layout = new TemplateLayout();
        layout.setId("db6b724b-f696-419f-837c-f64796625efe");
        layout.setDescription("TEST lAYOUT");

        TemplateBlock block1 = new TemplateBlock();
        block1.setId("9bf23b21-2def-4039-8d6d-2182ca84aeb0");
        block1.setDescription("block1");

        TemplateBlock block2 = new TemplateBlock();
        block2.setId("e90e78b7-fe89-43cf-bf6a-0ce4367206ca");
        block2.setDescription("block2");

        List<TemplateRegion> regions = new ArrayList<TemplateRegion>();
        regions.add(null);
        regions.add(column2);
        rootItem.setRegions(regions);

        // setup mock dao
        Map<String, Object> objects = new HashMap<String, Object>();
        objects.put(layout.getId(), layout);
        objects.put(block1.getId(), block1);
        objects.put(block2.getId(), block2);
        MockGenericDao dao = new MockGenericDao(objects);

        BindServiceImpl bs = new BindServiceImpl();
        bs.setDaoService(new MockDaoService(dao));

        TypeDef templateTypeDef = typeService.getType(Template.class);

        bs.bind(rootItem, templateTypeDef, req);

        assertEquals("Client", rootItem.getLabel());
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[0]"));
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[1]"));
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[0].blocks[0]"));
        assertEquals("column-1", PropertyUtils.getNestedProperty(rootItem, "regions[0].code"));
        assertEquals("column-2", PropertyUtils.getNestedProperty(rootItem, "regions[1].code"));
        assertNotNull(PropertyUtils.getNestedProperty(rootItem, "regions[1].blocks[0]"));

        //assertEquals("block1", PropertyUtils.getNestedProperty(rootItem, "regions[0].blocks[0].description"));
        //assertEquals("block2", PropertyUtils.getNestedProperty(rootItem, "regions[1].blocks[0].description"));

    }*/
}
