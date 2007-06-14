package org.otherobjects.cms.jcr;

import junit.framework.TestCase;

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
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

}
