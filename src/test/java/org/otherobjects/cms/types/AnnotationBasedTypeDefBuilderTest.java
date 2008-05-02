package org.otherobjects.cms.types;

import junit.framework.TestCase;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.binding.TestComponentObject;
import org.otherobjects.cms.binding.TestObject;
import org.otherobjects.cms.binding.TestReferenceObject;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

public class AnnotationBasedTypeDefBuilderTest extends TestCase
{

    public void testGetTypeDef() throws Exception
    {
        TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean.class);

        assertEquals(typeDef.getProperty("id").getType(), PropertyType.NUMBER.value());

        PropertyDef[] dummy = {};
        PropertyDef[] propertiesArray = typeDef.getProperties().toArray(dummy);

        assertEquals(propertiesArray[2].getName(), "dob");

        assertEquals(PropertyType.STRING.value(), typeDef.getProperty("others").getCollectionElementType());

    }

    public void testInferredAttributes() throws Exception
    {
        TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestObject.class);

        assertEquals(PropertyType.LIST.value(), typeDef.getProperty("testStringsList").getType());
        assertEquals(PropertyType.STRING.value(), typeDef.getProperty("testStringsList").getCollectionElementType());

        assertEquals(PropertyType.LIST.value(), typeDef.getProperty("testReferencesList").getType());
        assertEquals(PropertyType.REFERENCE.value(), typeDef.getProperty("testReferencesList").getCollectionElementType());
        assertEquals(TestReferenceObject.class.getName(), typeDef.getProperty("testReferencesList").getRelatedType());

        assertEquals(PropertyType.LIST.value(), typeDef.getProperty("testComponentsList").getType());
        assertEquals(PropertyType.COMPONENT.value(), typeDef.getProperty("testComponentsList").getCollectionElementType());
        assertEquals(TestComponentObject.class.getName(), typeDef.getProperty("testComponentsList").getRelatedType());

    }

    public void testGetTypeDefNoTypeAnnotation() throws Exception
    {
        try
        {
            new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean2.class);
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
            new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean3.class);
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
