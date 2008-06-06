package org.otherobjects.cms.binding;

import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.model.CmsNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

public class BindServiceImplNG implements BindService
{
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private DaoService daoService;

    public final static String DYNA_NODE_MAP_NAME = "data";
    private static final Pattern LIST_PATTERN = Pattern.compile("^([\\S&&[^\\.]]*)\\[(\\d+)\\]"); // howevermany non-whitespace characters (apart from the dot) followed by at least one digit in square braces

    private static final Pattern DYNA_NODE_PATTERN = Pattern.compile("^(?:([\\S&&[^\\.]]*)\\.)?" + DYNA_NODE_MAP_NAME + "\\[\"?(.*?)\"?+\\]"); //

    private Map<String, PathPart> pathCache = new HashMap<String, PathPart>();
    private Set<String> clearCache = new HashSet<String>();

    private ServletRequestDataBinder binder = null;

    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request)
    {
        this.binder = new ServletRequestDataBinder(item);

        // Create sub-objects where required
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String parameterName = parameterNames.nextElement();
            prepareObject(item, typeDef, new ParameterPath(parameterName));

        }

        binder.bind(request);
        return binder.getBindingResult();
    }

    /**
     * needs to recursively parse each passed in parameter path and
     * - create not yet existing child objects so that we can bind successfully later
     * - register path specific custom property editors taking into account {@link PropertyDef}s
     * - cache already processed part paths so that we don't do too much work  
     * 
     * knows how to deal with nested paths separated by dots like
     * 
     * person.address.street1
     * 
     * and lists with index notation like
     * 
     * person.address[1].street1
     * 
     * and {@link DynaNode}s looking like
     * 
     * article.data[paragraph1] or article.data["paragraph2"] (referring to the paragraph1 and paragraph2 property of the article DynaNode respectively)
     * 
     */
    protected void prepareObject(Object item, TypeDef typeDef, ParameterPath parameterPath)
    {
        PathPart part = parameterPath.poll();
        if (part == null)
            return; //nothing to do as no path part is left over

        if (pathCache.containsKey(part.getFullPath()))
            return; // this particular path has already been dealt with

        // first deal with dynaNodes
        boolean isDynaNode = false;
        if (typeDef.getProperty(part.getPathPart()).getClassName().equals("DynaNode"))
        {
            isDynaNode = true;
            if (!clearCache.contains(part.getFullPath()))
            {
                try
                {
                    PropertyUtils.setProperty(item, part.getPathPart(), new DynaNode());
                }
                catch (Exception e)
                {
                    logger.warn("Problems setting dynaNode at " + part.getFullPath());
                }
                clearCache.add(part.getFullPath());
            }

        }

        // ok simple things first
        if (!part.isList() && !part.isDynaNode() && !isDynaNode) // simple property
        {
            PropertyDef propertyDef = typeDef.getProperty(part.getPathPart());
            PropertyEditor propertyEditor = propertyDef.getPropertyEditor();
            if (propertyEditor != null)
                try
                {
                    binder.registerCustomEditor(Class.forName(propertyDef.getClassName()), part.getFullPath(), propertyEditor);
                }
                catch (ClassNotFoundException e)
                {
                    logger.warn("Class " + propertyDef.getClassName() + " for property [" + part.getFullPath() + "] couldn't be loaded", e);
                }
        }

        if (part.isList())
        {
            try
            {
                ListProps props = part.getListProps();
                String listPath = part.getFullParentPath() + ((StringUtils.isBlank(part.getFullParentPath())) ? "" : ".") + props.getName();

                int index = props.getIndex();

                PropertyDef propertyDef = typeDef.getProperty(props.getName());
                String collectionElementType = propertyDef.getCollectionElementType();
                Assert.notNull("If this property is a collection the collectionElementType needs to have been set: " + propertyDef.getName());

                List<Object> list = null;
                if (!clearCache.contains(listPath)) // if that particular list is not yet in our cache set, replace it with a fresh one (so that data not in the currently bound request effectively get deleted)
                {
                    list = new ArrayList<Object>();
                    PropertyUtils.setProperty(item, props.getName(), list);
                    clearCache.add(listPath);

                    // register suitable PropertyEditors (only once per list) 
                    if (collectionElementType.equals(PropertyDef.REFERENCE))
                    {
                        String relatedType = propertyDef.getRelatedType();
                        Class relatedPropertyClass = Class.forName(relatedType);
                        if (!BaseNode.class.isAssignableFrom(relatedPropertyClass)) // only register the (long) id based entity reference editor if it is not a BaseNode subtype
                        {
                            binder.registerCustomEditor(relatedPropertyClass, listPath, new EntityReferenceEditor(daoService, relatedPropertyClass));
                        }
                        else
                        {
                            // Add reference editors to all the reference properties                    
                            binder.registerCustomEditor(CmsNode.class, listPath, new CmsNodeReferenceEditor(daoService, relatedType));
                        }
                    }
                }
                else
                // else get it from the object
                {
                    list = (List<Object>) PropertyUtils.getSimpleProperty(item, props.getName());
                }

                fillList(list, index + 1); // make sure the list has a sufficient size

                if (collectionElementType.equals(PropertyDef.COMPONENT)) // set a new instance of component property
                {
                    TypeDef targetTypeDef = propertyDef.getRelatedTypeDef();
                    list.set(index, getRelatedTypeInstance(targetTypeDef));
                }
            }
            catch (Exception e)
            {
                logger.warn("Problem dealing with list property" + part.getFullPath(), e);
            }

        }

        if (part.isDynaNode())
        {
            try
            {
                Assert.isTrue(item instanceof DynaNode, "parent item of a dynaNode datamap needs to be a DynaNode");
                DynaNodeProps props = part.getDynaNodeProps();
                String key = props.getKey();

                PropertyDef propertyDef = typeDef.getProperty(key);

            }
            catch (Exception e)
            {
                logger.warn("Problem dealing with dynaNode property" + part.getFullPath(), e);
            }
        }

        // we need to instantiate container objects (list, component, dynaNode) and clear existing ones once per binding process

        // then register custom property editors for the given path 

        // once fully processed - cache it
        pathCache.put(part.getFullPath(), part);

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

    class ParameterPath
    {
        private String path;
        private Queue<PathPart> pathParts;
        private char pathDelim = '.';

        public ParameterPath(String path)
        {
            this.path = path;
            pathParts = parse(path);
        }

        private Queue<PathPart> parse(String path)
        {
            String parsePath = path + pathDelim; // append delimiter to end of path to allow for easier recursive parsing
            Queue<PathPart> result = new LinkedList<PathPart>();
            StringBuffer buf = new StringBuffer("");
            int i = 0;
            int j = 0;
            while (i < parsePath.lastIndexOf(pathDelim))
            {
                i = parsePath.indexOf(pathDelim, j); // find pathDelim

                result.offer(new PathPart(buf.toString(), parsePath.substring(j, i))); // create entry

                // update parent path buffer
                if (buf.length() > 0)
                    buf.append(pathDelim);

                buf.append(parsePath.substring(j, i));

                // update indices
                j = ++i;
            }

            return result;
        }

        public PathPart poll()
        {
            return pathParts.poll();
        }
    }

    class PathPart
    {
        private String fullParentPath;
        private String pathPart;
        //        private Object pathObject;
        private ListProps listProps;
        private DynaNodeProps dynaNodeProps;

        public PathPart(String fullParentPath, String pathPart)
        {
            this.fullParentPath = fullParentPath;
            this.pathPart = pathPart;

            this.listProps = parseListProps(pathPart);
            if (listProps == null)
                this.dynaNodeProps = parseDynaNodeProps(pathPart);
        }

        private DynaNodeProps parseDynaNodeProps(String pathPart)
        {
            Matcher matcher = DYNA_NODE_PATTERN.matcher(pathPart);
            if (matcher.matches())
            {
                DynaNodeProps props = new DynaNodeProps();
                props.setKey(matcher.group(2));
                return props;
            }
            return null;
        }

        private ListProps parseListProps(String pathPart)
        {
            Matcher matcher = LIST_PATTERN.matcher(pathPart);
            if (matcher.matches())
            {
                ListProps props = new ListProps();
                props.setName(matcher.group(1));
                props.setIndex(Integer.parseInt(matcher.group(2)));
                return props;
            }
            return null;
        }

        public boolean isList()
        {
            return listProps != null;
        }

        public boolean isDynaNode()
        {
            return dynaNodeProps != null;
        }

        public String getFullPath()
        {
            return fullParentPath + ((StringUtils.isBlank(fullParentPath)) ? "" : ".") + pathPart;
        }

        public String getFullParentPath()
        {
            return fullParentPath;
        }

        public String getPathPart()
        {
            return pathPart;
        }

        //        public Object getPathObject()
        //        {
        //            return pathObject;
        //        }
        //
        //        public void setPathObject(Object pathObject)
        //        {
        //            this.pathObject = pathObject;
        //        }

        public ListProps getListProps()
        {
            return listProps;
        }

        public DynaNodeProps getDynaNodeProps()
        {
            return dynaNodeProps;
        }

        //        @Override
        //        public boolean equals(Object obj)
        //        {
        //            if (obj == null)
        //            {
        //                return false;
        //            }
        //            if (obj == this)
        //            {
        //                return true;
        //            }
        //            if (obj.getClass() != getClass())
        //            {
        //                return false;
        //            }
        //            PathPart rhs = (PathPart) obj;
        //            return new EqualsBuilder().append(fullParentPath, rhs.getFullParentPath()).append(pathPart, rhs.getPathPart()).isEquals();
        //        }
        //
        //        @Override
        //        public int hashCode()
        //        {
        //            return new HashCodeBuilder(11, 47).append(fullParentPath).append(pathPart).toHashCode();
        //        }

        @Override
        public String toString()
        {
            return getFullPath();
        }

    }

    class ListProps
    {
        private int index;
        private String name;

        public int getIndex()
        {
            return index;
        }

        public void setIndex(int index)
        {
            this.index = index;
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

    class DynaNodeProps
    {
        private String key;

        public String getKey()
        {
            return key;
        }

        public void setKey(String key)
        {
            this.key = key;
        }
    }

}
