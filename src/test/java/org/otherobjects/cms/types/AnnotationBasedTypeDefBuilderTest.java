package org.otherobjects.cms.types;

import java.util.Date;
import java.util.List;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.types.annotation.PropertyDefAnnotation;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.TypeDefAnnotation;

import junit.framework.TestCase;

public class AnnotationBasedTypeDefBuilderTest extends TestCase {
	
	@TypeDefAnnotation(jcrPath = "/Test/tests", label = "TestBean", description = "An annotated test bean", labelProperty = "name")
	public class TestBean
	{
		Long id;
		String name;
		Date dob;
		List<String> others;
		
		public TestBean()
		{
			
		}
		
		@PropertyDefAnnotation(order = 1, description = "The id", label = "id", required = true, type = PropertyType.NUMBER)
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		@PropertyDefAnnotation(order = 2, description = "The name", label = "nem", required = true, type = PropertyType.STRING)
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		@PropertyDefAnnotation(order = 3, description = "The dob", label = "dob", required = false, type = PropertyType.DATE)
		public Date getDob() {
			return dob;
		}

		public void setDob(Date dob) {
			this.dob = dob;
		}
		
		@PropertyDefAnnotation(order = 4, description = "The others", label = "others", required = false, type = PropertyType.LIST, collectionElementType = PropertyType.STRING)
		public List<String> getOthers() {
			return others;
		}

		public void setOthers(List<String> others) {
			this.others = others;
		}
		
		
	}
	
	public class TestBean2
	{
		Long id;
		
		public TestBean2()
		{
			
		}
		
		@PropertyDefAnnotation(order = 1, description = "The id", label = "id", required = true, type = PropertyType.NUMBER)
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		
		
		
	}
	
	@TypeDefAnnotation(jcrPath = "/Test/tests", label = "TestBean", description = "An annotated test bean", labelProperty = "name")
	public class TestBean3
	{
		Long id;
		String name;
		
		
		public TestBean3()
		{
			
		}
		
		@PropertyDefAnnotation(order = 1, description = "The id", label = "id", required = true, type = PropertyType.NUMBER)
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}
		
		@PropertyDefAnnotation(order = 2, description = "The name", label = "nem", required = true, type = PropertyType.STRING)
		public String obtainName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
		
		
		
		
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
		try{
			TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean2.class);
			fail();
		}
		catch(OtherObjectsException e)
		{
			assertTrue(e.getMessage().startsWith("TypeDef can't be build"));
		}
	}
	
	public void testGetTypeDefNoBeanStyle() throws Exception
	{
		try{
			TypeDef typeDef = new AnnotationBasedTypeDefBuilder().getTypeDef(TestBean3.class);
			fail();
		}
		catch(OtherObjectsException e)
		{
			assertTrue(e.getMessage().startsWith("The annotated method"));
		}
	}
}
