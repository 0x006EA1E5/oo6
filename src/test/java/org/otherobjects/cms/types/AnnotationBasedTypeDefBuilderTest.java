package org.otherobjects.cms.types;

import java.io.IOException;
import java.util.Set;

import junit.framework.TestCase;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

public class AnnotationBasedTypeDefBuilderTest extends TestCase
{
    public void testFindAnnotatedClasses() throws IOException, ClassNotFoundException
    {
        AnnotationBasedTypeDefBuilder annotationBasedTypeDefBuilder = new AnnotationBasedTypeDefBuilder();
        Set<Class<?>> found = annotationBasedTypeDefBuilder.findAnnotatedClasses("org.otherobjects.cms.types");
        assertEquals(2, found.size());
        assertTrue(found.contains(TestBean.class));
    }

    public void testGetTypeDef() throws Exception
    {
        TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean.class);

        assertEquals(typeDef.getProperty("id").getType(), PropertyType.NUMBER.value());

        PropertyDef[] dummy = {};
        PropertyDef[] propertiesArray = typeDef.getProperties().toArray(dummy);

        assertEquals(propertiesArray[2].getName(), "dob");

        assertEquals(PropertyType.STRING.value(), typeDef.getProperty("others").getCollectionElementType());

    }

    public void testGetTypeDefNoTypeAnnotation() throws Exception
    {
        try
        {
            TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean2.class);
            fail();
        }
        catch (Exception e)
        {
        }
    }

    public void testGetTypeDefNoBeanStyle() throws Exception
    {
        try
        {
            TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean3.class);
            fail();
        }
        catch (OtherObjectsException e)
        {
            assertTrue(e.getMessage().startsWith("The annotated method"));
        }
    }

    public class TestBean2
    {
        Long id;

        public TestBean2()
        {

        }

        public Long getId()
        {
            return id;
        }

        @Property(order = 1, description = "The id", label = "id", required = true, type = PropertyType.NUMBER)
        public void setId(Long id)
        {
            this.id = id;
        }

    }

    @Type(label = "TestBean", description = "An annotated test bean", labelProperty = "name")
    public class TestBean3
    {
        Long id;
        String name;

        public TestBean3()
        {

        }

        @Property(order = 1, description = "The id", label = "id", required = true, type = PropertyType.NUMBER)
        public Long getId()
        {
            return id;
        }

        public void setId(Long id)
        {
            this.id = id;
        }

        @Property(order = 2, description = "The name", label = "nem", required = true, type = PropertyType.STRING)
        public String obtainName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

    }

}
