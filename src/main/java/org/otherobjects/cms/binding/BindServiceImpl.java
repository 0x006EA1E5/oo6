package org.otherobjects.cms.binding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

/**
 * Very thin wrapper around springs ServletRequestDataBinder with our default CustomEditor(s) configured.
 * Call the bind method after you have beanified your DynaNode using {@link JcrBeanService} (This might change)
 * 
 * @author joerg
 *
 */
public class BindServiceImpl implements BindService
{
    private String dateFormat;
    private DaoService daoService;

    static Pattern listPattern = Pattern.compile("^([\\S&&[^\\.]]*)\\[(\\d+)\\]"); // howevermany non-whitespace characters (apart from the dot) followed by at least one digit in square braces

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

    @SuppressWarnings("unchecked")
    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request)
    {
        ServletRequestDataBinder binder = new ServletRequestDataBinder(item);

        // Create sub-objects where required
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String propertyName = parameterNames.nextElement();
            // FIXME Add caching here so easy level is processed only once 
            prepareObject(item, typeDef, propertyName, propertyName, binder);

        }

        // Add date editor
        binder.registerCustomEditor(java.util.Date.class, new CustomDateEditor(new SimpleDateFormat(dateFormat), true));

        // Bind
        binder.bind(request);
        return binder.getBindingResult();
    }

    /**
     * walks the propertyPath and creates components and lists of components along the way.
     * Stops once all of the propertyPath is consumed or if it comes across a simple property or 
     * a list that contains simple properties.
     * If the properties it finds along the way are already there it leaves them alone.
     * @param parent
     * @param typeDef
     * @param propertyPath
     * @param binder 
     */
    @SuppressWarnings("unchecked")
    protected void prepareObject(Object parent, TypeDef typeDef, String propertyPath, String fullPath, ServletRequestDataBinder binder)
    {
        if (propertyPath.equals("id") || propertyPath.startsWith("_") || propertyPath.startsWith("editableId")) // ignore id field and _ fields that are to help the binding process to discover unchecked checkboxes
            return;

        Matcher matcher = listPattern.matcher(propertyPath);
        String propertyName;
        String leftOverPath;
        String listWithIndex = null;
        int listIndex = -1;
        boolean isList = false;
        if (matcher.lookingAt()) // we have a list
        {
            isList = true;
            propertyName = matcher.group(1);
            listWithIndex = matcher.group();
            listIndex = Integer.parseInt(matcher.group(2));
            int thisPartPathLength = listWithIndex.length();
            if (thisPartPathLength < propertyPath.length())
                leftOverPath = propertyPath.substring(thisPartPathLength + 1);
            else
                leftOverPath = null;
        }
        else
        // subObject other than list
        {
            int dotIndex = propertyPath.indexOf(".");
            if (dotIndex > -1)
            {
                propertyName = propertyPath.substring(0, dotIndex);
                //Assert.isTrue(dotIndex + 1 < propertyName.length(), "a valid propertyPath can't end in a dot");
                leftOverPath = propertyPath.substring(dotIndex + 1);
            }
            else
            {
                propertyName = propertyPath; // apparently we reached the end leaf
                leftOverPath = null;
            }
        }

        // find out type of object
        Object newParent = null;
        PropertyDef propertyDef = typeDef.getProperty(propertyName);
        // TODO Show complete parameter name at this point
        Assert.notNull(propertyDef, "No property found for parameter path: " + propertyName);
        TypeDef newTypeDef = null;
        if (isList)
        {
            List listProperty;
            // this is a list so it needs to have a collectionElementType in propertyDef
            String collectionElementType = propertyDef.getCollectionElementType();
            Assert.notNull("If this property is a collection the collectionElementType needs to have been set: " + propertyDef.getName());

            // create list
            try
            {
                listProperty = (List) PropertyUtils.getSimpleProperty(parent, propertyName);
                if (listProperty == null)
                {
                    listProperty = new ArrayList();
                    PropertyUtils.setSimpleProperty(parent, propertyName, listProperty);
                }

                fillList(listProperty, listIndex + 1);

                if (collectionElementType.equals(PropertyDef.COMPONENT)) // FIXME Explain: we only create list elements that are supposed to be components
                {
                    if (listProperty.get(listIndex) == null)
                    {
                        newTypeDef = propertyDef.getRelatedTypeDef();
                        newParent = getRelatedTypeInstance(newTypeDef);
                        listProperty.set(listIndex, newParent);
                    }
                    else
                    {
                        newTypeDef = propertyDef.getRelatedTypeDef();
                        newParent = listProperty.get(listIndex);
                    }
                }
                else if (collectionElementType.equals(PropertyDef.REFERENCE))
                {
                    String relatedType = propertyDef.getRelatedType();
                    Class relatedPropertyClass = Class.forName(relatedType);
                    if (!BaseNode.class.isAssignableFrom(relatedPropertyClass)) // only register the (long) id based entity reference editor if it is not a BaseNode subtype
                    {
                        binder.registerCustomEditor(relatedPropertyClass, fullPath, new EntityReferenceEditor(daoService, relatedPropertyClass));
                    }
                    else
                    {
                        // Add reference editors to all the reference properties                    
                        binder.registerCustomEditor(CmsNode.class, fullPath, new CmsNodeReferenceEditor(daoService, relatedType));
                    }
                }
            }
            catch (Exception e)
            {
                throw new OtherObjectsException("Error getting or instantiating list property: " + propertyName, e);
            }
        }
        else if (propertyDef.getType().equals(PropertyDef.COMPONENT))
        {
            try
            {
                newTypeDef = propertyDef.getRelatedTypeDef();
                newParent = PropertyUtils.getSimpleProperty(parent, propertyName);
                if (newParent == null)
                {
                    newParent = getRelatedTypeInstance(newTypeDef);
                    PropertyUtils.setSimpleProperty(parent, propertyName, newParent);
                }
            }
            catch (Exception e)
            {
                throw new OtherObjectsException("Error getting or instantiating component property: " + propertyName, e);
            }
        }
        else if (propertyDef.getType().equals(PropertyDef.REFERENCE))
        {
            // Add reference editors to all the reference propreties                    
            binder.registerCustomEditor(CmsNode.class, fullPath, new CmsNodeReferenceEditor(daoService, propertyDef.getRelatedType()));
        }
        else
        {
            // neither a list nor a component - stop here
            return;
        }

        if (leftOverPath != null && newParent != null && newTypeDef != null)
            prepareObject(newParent, newTypeDef, leftOverPath, fullPath, binder);
    }

    private Object getRelatedTypeInstance(TypeDef typeDef) throws Exception
    {

        Object relatedTypeInstance = Class.forName(typeDef.getClassName()).newInstance();
        PropertyUtils.setSimpleProperty(relatedTypeInstance, "ooType", typeDef.getName());
        return relatedTypeInstance;
    }

    private void fillList(List<?> list, int size)
    {
        int initialSize = list.size();
        if (initialSize >= size)
            return;

        for (int i = 0; i < (size - initialSize); i++)
        {
            list.add(null);
        }
    }

    public void setDateFormat(String dateFormat)
    {
        this.dateFormat = dateFormat;
    }

}
