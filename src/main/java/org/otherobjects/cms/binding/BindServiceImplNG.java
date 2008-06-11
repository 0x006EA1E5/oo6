package org.otherobjects.cms.binding;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.util.WebUtils;

/**
 *
 * TODO Improve simple property support: Date
 */
public class BindServiceImplNG implements BindService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String dateFormat;

    @Resource
    private DaoService daoService;

    private ServletRequestDataBinder binder = null;

    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request)
    {
        this.binder = new ServletRequestDataBinder(item);

        try
        {
            prepareObject(item, typeDef, "", request);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        binder.bind(request);
        return binder.getBindingResult();
    }

    private void prepareObject(Object item, TypeDef typeDef, String prefix, HttpServletRequest request) throws Exception
    {
        List<String> propertyPath = new LinkedList<String>();

        // iterate all props
        for (PropertyDef propertyDef : typeDef.getProperties())
        {
            // deal with lists
            if (propertyDef.getType().equals("list")) //TODO the type should clearly be a constant or enum of sorts
            {
                // instantiate list? ensure capacity?
                if (propertyDef.getRelatedType().equals("reference"))
                {
                    // register ReferencePropertyEditor for this list path
                    //recurse
                }
                else if (propertyDef.getRelatedType().equals("component"))
                {
                    // register suitable PropertyEditor
                    // recurse
                }
                else
                {
                    //noop
                }
            }
            else if (propertyDef.getType().equals("reference")) // deal with references
            {
                // Determine path
                String path = prefix + propertyDef.getName();

                // register suitable PropertyEditor
                String relatedType = propertyDef.getRelatedType();
                Class relatedPropertyClass = Class.forName(relatedType);

                binder.registerCustomEditor(CmsNode.class, path, new CmsNodeReferenceEditor(daoService, relatedType));
            }
            else if (propertyDef.getType().equals("component"))// deal with components
            {
                // Determine path
                String path = prefix + propertyDef.getName();

                // Check if we have matching parameters
                Map matchingParams = WebUtils.getParametersStartingWith(request, path);

                if (matchingParams.size() > 0)
                {
                    BaseNode component = (BaseNode) PropertyUtils.getNestedProperty(item, path);

                    if (component == null)
                    {
                        // Create object if null 
                        component = createObject(propertyDef);
                        PropertyUtils.setNestedProperty(item, path, component);
                    }

                    // Recurse into the component
                    prepareObject(item, component.getTypeDef(), propertyDef.getName() + ".", request);

                }
            }
            else
            // simple props
            {
                binder.registerCustomEditor(Class.forName(propertyDef.getClassName()), "", propertyDef.getPropertyEditor());
            }

        }

    }

    /**
     * FIXME Merge this with FormController/TypeService
     */
    private BaseNode createObject(PropertyDef propertyDef)
    {
        try
        {
            // FIXME Allow DynaNode creation here
            // TODO Are types arways class names?
            return (BaseNode) Class.forName(propertyDef.getRelatedType()).newInstance();
        }
        catch (Exception e)
        {
            // Ignore for now
            return null;
        }

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

    public void setDaoService(DaoService daoService)
    {
        this.daoService = daoService;
    }

}
