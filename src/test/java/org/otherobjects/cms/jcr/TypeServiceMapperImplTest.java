package org.otherobjects.cms.jcr;

import junit.framework.TestCase;

import org.apache.jackrabbit.ocm.mapper.model.ClassDescriptor;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;

public class TypeServiceMapperImplTest extends TestCase
{
    private TypeServiceMapperImpl typeMapper;
    private TypeService types;

    @Override
    public void setUp()
    {
        types = new TypeService();
        typeMapper = new TypeServiceMapperImpl();

        TypeDef td = new TypeDef("site_NewsStory");
        td.addProperty(new PropertyDef("data.title", "string", null));
        td.addProperty(new PropertyDef("data.content", "string", null));
        types.registerType(td);
        
        typeMapper = new TypeServiceMapperImpl(types);
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testCreateClassDescriptor()
    {
        TypeDef typeDef = types.getType("site_NewsStory");
        ClassDescriptor cd = typeMapper.createClassDescriptor(typeDef);

        assertEquals("nt:unstructured", cd.getJcrNodeType());
        assertEquals(typeDef.getProperties().size() + 4, cd.getFieldDescriptors().size());

    }

    public void testGetClassDescriptorByClass()
    {
        ClassDescriptor cd = typeMapper.getClassDescriptorByClass(CmsNode.class);
        assertNotNull(cd);
    }

}
