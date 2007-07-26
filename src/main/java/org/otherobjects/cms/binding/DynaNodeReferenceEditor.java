package org.otherobjects.cms.binding;

import org.otherobjects.cms.dao.DynaNodeDao;
import org.otherobjects.cms.model.DynaNode;
import org.springframework.util.StringUtils;

public class DynaNodeReferenceEditor extends java.beans.PropertyEditorSupport
{
    private DynaNodeDao dynaNodeDao;
    
    public DynaNodeReferenceEditor(DynaNodeDao dynaNodeDao)
    {
        this.dynaNodeDao = dynaNodeDao;
    }

    /**
     * Lookup a DynaNode from the UUID String.
     */
    public void setAsText(String text) throws IllegalArgumentException
    {
        if (!StringUtils.hasText(text))
        {
            // Treat empty String as null value.
            setValue(null);
        }
        else if (text != null && text.length() != 36)
        {
            //TODO better uuid regexp
            throw new IllegalArgumentException("Not a valid UUID: " + text);
        }
        else
        {
            setValue(dynaNodeDao.get(text));
        }
    }

    /**
     * Format the DynaNode as String, using its UUID.
     */
    public String getAsText()
    {
        DynaNode value = (DynaNode) getValue();
        return value != null ? value.getId() : null;
    }
}
