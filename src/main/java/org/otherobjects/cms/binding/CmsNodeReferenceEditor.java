package org.otherobjects.cms.binding;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.BaseNode;
import org.springframework.util.StringUtils;

public class CmsNodeReferenceEditor extends java.beans.PropertyEditorSupport
{
    private String type;
    private DaoService daoService;
    
    public CmsNodeReferenceEditor(DaoService daoService, String type)
    {
        this.daoService = daoService;
        this.type = type;
    }

    /**
     * Lookup a DynaNode from the UUID String.
     */
    @SuppressWarnings("unchecked")
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
            setValue(daoService.getDao(type).get(text));
        }
    }

    /**
     * Format the DynaNode as String, using its UUID.
     */
    public String getAsText()
    {
        BaseNode value = (BaseNode) getValue();
        return value != null ? value.getId() : null;
    }
}
