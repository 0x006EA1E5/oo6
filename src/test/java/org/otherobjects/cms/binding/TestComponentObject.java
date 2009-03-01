package org.otherobjects.cms.binding;

import java.util.List;

import org.otherobjects.cms.model.BaseComponent;
import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type(labelProperty = "name", codeProperty="name")
public class TestComponentObject extends BaseComponent
{
    private String name;
    private String requiredString;
    private TestReferenceObject reference;
    private TestComponentObject component;

    private List<String> stringsList;
    private List<TestComponentObject> componentsList;

    public TestComponentObject()
    {
    }
    
    public TestComponentObject(String name)
    {
        setName(name);
    }

    
    @Property
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    @Property(required = true)
    public String getRequiredString()
    {
        return requiredString;
    }

    public void setRequiredString(String requiredString)
    {
        this.requiredString = requiredString;
    }

    @Property(type = PropertyType.COMPONENT)
    public TestComponentObject getComponent()
    {
        return component;
    }

    public void setComponent(TestComponentObject component)
    {
        this.component = component;
    }

    @Property
    public List<String> getStringsList()
    {
        return stringsList;
    }

    public void setStringsList(List<String> stringsList)
    {
        this.stringsList = stringsList;
    }

    @Property(collectionElementType = PropertyType.COMPONENT)
    public List<TestComponentObject> getComponentsList()
    {
        return componentsList;
    }

    public void setComponentsList(List<TestComponentObject> componentsList)
    {
        this.componentsList = componentsList;
    }

    @Property
    public TestReferenceObject getReference()
    {
        return reference;
    }

    public void setReference(TestReferenceObject reference)
    {
        this.reference = reference;
    }

}
