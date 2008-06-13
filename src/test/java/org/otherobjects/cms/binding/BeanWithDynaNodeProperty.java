package org.otherobjects.cms.binding;

import org.otherobjects.cms.jcr.dynamic.DynaNode;

public class BeanWithDynaNodeProperty
{
    private DynaNode testNode;
    private String name;

    public DynaNode getTestNode()
    {
        return testNode;
    }

    public void setTestNode(DynaNode testNode)
    {
        this.testNode = testNode;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

}
