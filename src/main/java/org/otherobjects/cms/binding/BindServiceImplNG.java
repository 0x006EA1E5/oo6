package org.otherobjects.cms.binding;

import java.lang.reflect.Proxy;
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
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

/**
 * 
 * 
 */
@Scope("prototype")
public class BindServiceImplNG implements BindService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String DYNA_NODE_DATAMAP_NAME = "data";

    @Resource
    private DaoService daoService;

    private ServletRequestDataBinder binder = null;
    private HttpServletRequestModifier wrappedRequest;

    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request)
    {
        this.binder = new ServletRequestDataBinder(item);
        this.wrappedRequest = wrapRequest(request);

        try
        {
            prepareObject(item, typeDef, "");
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        binder.bind(wrappedRequest);
        return wrapBindingResult(binder.getBindingResult());
    }

    private void prepareObject(Object item, TypeDef typeDef, String rootPathPrefix) throws Exception
    {
        // iterate all props
        for (PropertyDef propertyDef : typeDef.getProperties())
        {
            // Determine path
            String path = calcPropertyPath(item, propertyDef, rootPathPrefix);
            String rootPath = rootPathPrefix + path;

            // Check if we have matching parameters
            Map<String, String> matchingParams = WebUtils.getParametersStartingWith(wrappedRequest, rootPath);

            boolean correspondingParamPresent = matchingParams.size() > 0;

            if (correspondingParamPresent)
            {
                // deal with lists
                if (propertyDef.getType().equals("list")) //TODO the type should clearly be a constant or enum of sorts
                {
                    // instantiate list? ensure capacity?
                    List list = (List) PropertyUtils.getNestedProperty(item, path);
                    if (list != null)
                        list.clear();
                    else
                    {
                        list = new ArrayList();
                        PropertyUtils.setNestedProperty(item, path, list);
                    }

                    ListProps listProps = calcListProps(rootPath, matchingParams);
                    sizeList(list, listProps.getRequiredSize());

                    if (propertyDef.getCollectionElementType().equals("reference"))
                    {
                        // register suitable PropertyEditor
                        String relatedType = propertyDef.getRelatedType();
                        Class relatedPropertyClass = Class.forName(relatedType);

                        binder.registerCustomEditor(CmsNode.class, rootPath, new CmsNodeReferenceEditor(daoService, relatedType));
                    }
                    else if (propertyDef.getCollectionElementType().equals("component"))
                    {
                        // populate each used list index with a component instance if none there
                        for (Integer ind : listProps.getUsedIndices())
                        {
                            prepareComponent(item, propertyDef, ind, rootPathPrefix);
                        }
                    }
                    else
                    {
                        binder.registerCustomEditor(Class.forName(propertyDef.getClassName()), rootPath, propertyDef.getPropertyEditor());
                    }
                }
                else if (propertyDef.getType().equals("reference")) // deal with references
                {
                    // register suitable PropertyEditor
                    String relatedType = propertyDef.getRelatedType();
                    Class relatedPropertyClass = Class.forName(relatedType);

                    binder.registerCustomEditor(CmsNode.class, rootPath, new CmsNodeReferenceEditor(daoService, relatedType));
                }
                else if (propertyDef.getType().equals("component"))// deal with components
                {
                    prepareComponent(item, propertyDef, rootPathPrefix);
                }
                else
                // simple props
                {
                    binder.registerCustomEditor(Class.forName(propertyDef.getClassName()), rootPath, propertyDef.getPropertyEditor());
                }
            }
        }
    }

    private String calcPropertyPath(Object item, PropertyDef propertyDef, String rootPathPrefix)
    {
        if (item instanceof DynaNode)
        {
            String propertyPath = DYNA_NODE_DATAMAP_NAME + "[" + propertyDef.getName() + "]";
            wrappedRequest.rewriteParameter(rootPathPrefix + propertyDef.getName(), rootPathPrefix + propertyPath);
            return propertyPath;
        }
        else
            return propertyDef.getName();
    }

    private void prepareComponent(Object item, PropertyDef propertyDef, String rootPathPrefix) throws Exception
    {
        prepareComponent(item, propertyDef, null, rootPathPrefix);

    }

    private void prepareComponent(Object parent, PropertyDef propertyDef, Integer index, String rootPathPrefix) throws Exception
    {
        String propertyPath = calcPropertyPath(parent, propertyDef, rootPathPrefix);

        if (index != null)
            propertyPath += "[" + index + "]";

        BaseNode component = (BaseNode) PropertyUtils.getNestedProperty(parent, propertyPath);

        if (component == null)
        {
            // Create object if null 
            component = createObject(propertyDef);
            PropertyUtils.setNestedProperty(parent, propertyPath, component);
        }

        // Recurse into the component
        prepareObject(component, component.getTypeDef(), rootPathPrefix + propertyPath + ".");
    }

    public ListProps calcListProps(String path, Map<String, String> listParams)
    {
        ListProps listProps = new ListProps();
        Pattern pattern = Pattern.compile("^" + path.replaceAll("\\.", "\\\\.").replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]") + "\\[(\\d+)\\]"); // replace .[] which have special meaning in a regex with escaped constructs

        for (String paramName : listParams.keySet())
        {
            Matcher matcher = pattern.matcher(path + paramName);
            if (matcher.lookingAt())
            {
                listProps.addIndex(Integer.parseInt(matcher.group(1)));
            }
        }
        return listProps;
    }

    /**
     * FIXME Merge this with FormController/TypeService
     * FIXME this is very hacky atm
     */
    private BaseNode createObject(PropertyDef propertyDef)
    {
        // FIXME Allow DynaNode creation here
        // TODO Are types arways class names?

        try
        {
            BaseNode object = (BaseNode) Class.forName(propertyDef.getRelatedType()).newInstance();
            return object;
        }
        catch (Exception e)
        {
            return new DynaNode(propertyDef.getRelatedType());
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

    private BindingResult wrapBindingResult(BindingResult bindingResult)
    {
        return (BindingResult) Proxy.newProxyInstance(bindingResult.getClass().getClassLoader(), new Class[]{BindingResult.class}, new BindingResultWrapper(bindingResult, wrappedRequest
                .getRewrittenPaths()));
    }

    private HttpServletRequestModifier wrapRequest(HttpServletRequest request)
    {
        if (request instanceof MultipartHttpServletRequest)
            return (HttpServletRequestModifier) Proxy.newProxyInstance(request.getClass().getClassLoader(), new Class[]{HttpServletRequestModifier.class, MultipartHttpServletRequest.class},
                    new BindingRequestWrapper(request));
        else
            return (HttpServletRequestModifier) Proxy.newProxyInstance(request.getClass().getClassLoader(), new Class[]{HttpServletRequestModifier.class}, new BindingRequestWrapper(request));
    }

}
