package org.otherobjects.cms.jcr;

import java.util.Calendar;

import javax.jcr.Node;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.model.CmsNode;

public class BeanCoderImpl implements BeanCoder
{

    public CmsNode convertFromNode(Node bean)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Node convertToNode(CmsNode bean)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Class<?> getDataClass(String type)
    {
        if (type.equals("date"))
            return Calendar.class;
        else if (type.equals("boolean"))
            return Boolean.class;
        else if (type.equals("text"))
            return String.class;
        else if (type.equals("html"))
            return String.class;
        else if (type.equals("number"))
            return Long.class;
        else if (type.equals("string"))
            return String.class;
        else if (type.equals("number"))
            return Calendar.class;
        else if (type.equals("reference"))
            return Node.class;
        else
            throw new OtherObjectsException("Unknown or unsupporter type: " + type);
    }

}
