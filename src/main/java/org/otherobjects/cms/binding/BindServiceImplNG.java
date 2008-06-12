package org.otherobjects.cms.binding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.util.WebUtils;

/**
 *
 * TODO Improve simple property support: Date
 */
@Scope("prototype")
public class BindServiceImplNG implements BindService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String dateFormat;

    @Resource
    private DaoService daoService;

    private ServletRequestDataBinder binder = null;
    private HttpServletRequest request;

    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request)
    {
        this.binder = new ServletRequestDataBinder(item);
        this.request = request;

        try
        {
            prepareObject(item, typeDef, "");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        binder.bind(request);
        return binder.getBindingResult();
    }

    private void prepareObject(Object item, TypeDef typeDef, String prefix) throws Exception
    {
        // iterate all props
        for (PropertyDef propertyDef : typeDef.getProperties())
        {
            // Determine path
            String path = prefix + propertyDef.getName();

            // Check if we have matching parameters
            Map<String, String> matchingParams = WebUtils.getParametersStartingWith(request, path);

            boolean correspondingParamPresent = matchingParams.size() > 0;

            if (correspondingParamPresent)
            {
                // DynaNodes
                if (item instanceof DynaNode)
                {
                    // no lists as props of DynaNode yet

                }
                else
                {
                    // deal with lists
                    if (propertyDef.getType().equals("list")) //TODO the type should clearly be a constant or enum of sorts
                    {
                        // instantiate list? ensure capacity?
                        List list = (List) PropertyUtils.getNestedProperty(item, path);
                        if (list != null)
                            list.clear();
                        else
                            list = new ArrayList();

                        ListProps listProps = calcListProps(path, matchingParams);
                        sizeList(list, listProps.getRequiredSize());

                        if (propertyDef.getRelatedType().equals("reference"))
                        {
                            // register suitable PropertyEditor
                            String relatedType = propertyDef.getRelatedType();
                            Class relatedPropertyClass = Class.forName(relatedType);

                            binder.registerCustomEditor(CmsNode.class, path, new CmsNodeReferenceEditor(daoService, relatedType));
                        }
                        else if (propertyDef.getRelatedType().equals("component"))
                        {
                            // populate each used list index with a component instance if none there
                            for (Integer ind : listProps.getUsedIndices())
                            {
                                String listItemPath = path + "[" + ind + "]";
                                prepareComponent(item, propertyDef, listItemPath);
                            }
                        }
                        else
                        {
                            binder.registerCustomEditor(Class.forName(propertyDef.getClassName()), path, propertyDef.getPropertyEditor());
                        }
                    }
                    else if (propertyDef.getType().equals("reference")) // deal with references
                    {
                        // register suitable PropertyEditor
                        String relatedType = propertyDef.getRelatedType();
                        Class relatedPropertyClass = Class.forName(relatedType);

                        binder.registerCustomEditor(CmsNode.class, path, new CmsNodeReferenceEditor(daoService, relatedType));
                    }
                    else if (propertyDef.getType().equals("component"))// deal with components
                    {
                        prepareComponent(item, propertyDef, path);
                    }
                    else
                    // simple props
                    {
                        binder.registerCustomEditor(Class.forName(propertyDef.getClassName()), path, propertyDef.getPropertyEditor());
                    }
                }
            }

        }

    }

    private void prepareComponent(Object parent, PropertyDef propertyDef, String path) throws Exception
    {
        BaseNode component = (BaseNode) PropertyUtils.getNestedProperty(parent, path);

        if (component == null)
        {
            // Create object if null 
            component = createObject(propertyDef);
            PropertyUtils.setNestedProperty(parent, path, component);
        }

        // Recurse into the component
        prepareObject(parent, component.getTypeDef(), path + ".");
    }

    public ListProps calcListProps(String path, Map<String, String> listParams)
    {
        ListProps listProps = new ListProps();
        Pattern pattern = Pattern.compile("^" + path.replaceAll("\\.", "\\\\.").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") + "\\[(\\d+)\\]"); // replace .[] which have special meaning in a regex with escaped constructs

        for (String paramName : listParams.keySet())
        {
            Matcher matcher = pattern.matcher(paramName);
            if (matcher.lookingAt())
            {
                listProps.addIndex(Integer.parseInt(matcher.group(1)));
            }
        }
        return listProps;
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

    private void sizeList(List<?> list, int size)
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

    class ListProps
    {
        private Set<Integer> usedIndices = new HashSet<Integer>();

        private int currentHighestIndex = -1;

        public void addIndex(int index)
        {
            usedIndices.add(new Integer(index));
            if (index > currentHighestIndex)
                currentHighestIndex = index;
        }

        public Set<Integer> getUsedIndices()
        {
            return usedIndices;
        }

        public int getRequiredSize()
        {
            return currentHighestIndex + 1;
        }

    }

}
