package org.otherobjects.cms.binding;

import java.beans.PropertyEditorSupport;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DaoService;
import org.springframework.util.StringUtils;

public class EntityReferenceEditor extends PropertyEditorSupport
{
    private Class<?> type;
    private DaoService daoService;

    public EntityReferenceEditor(DaoService daoService, Class<?> type)
    {
        this.daoService = daoService;
        this.type = type;
    }

    private static Pattern pattern = Pattern.compile("^\\d+$");

    /**
     * Lookup an Entity from an id string.
     */
    @SuppressWarnings("unchecked")
    public void setAsText(String id) throws IllegalArgumentException
    {
        if (!StringUtils.hasText(id))
        {
            // Treat empty String as null value.
            setValue(null);
        }
        else if (!pattern.matcher(id).matches())
        {
            throw new IllegalArgumentException("Not a valid ID: " + id);
        }
        else
        {
            setValue(daoService.getDao(type).get(new Long(Long.parseLong(id))));
        }
    }

    /**
     * Format the Entity as a String, using its id value.
     * //TODO we might need a marker interface for Entities 
     */
    public String getAsText()
    {
        boolean hasId = false;
        try
        {
            hasId = getValue().getClass().getMethod("getId") != null;
        }
        catch (Exception e)
        {
            // do nothing
        }

        if (hasId)
        {
            Long id = null;
            try
            {
                id = (Long) PropertyUtils.getSimpleProperty(getValue(), "id");
            }
            catch (Exception e)
            {
                // do nothing
            }

            return (id != null) ? id.toString() : null;
        }
        else
            return null;
    }
}
