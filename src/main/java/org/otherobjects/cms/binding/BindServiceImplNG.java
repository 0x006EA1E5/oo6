package org.otherobjects.cms.binding;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.ServletRequestDataBinder;

public class BindServiceImplNG implements BindService
{
    public final static String DYNA_NODE_MAP_NAME = "data";
    private static final Pattern LIST_PATTERN = Pattern.compile("^([\\S&&[^\\.]]*)\\[(\\d+)\\]"); // howevermany non-whitespace characters (apart from the dot) followed by at least one digit in square braces

    private static final Pattern DYNA_NODE_PATTERN = Pattern.compile("^(?:([\\S&&[^\\.]]*)\\.)?" + DYNA_NODE_MAP_NAME + "\\[\"?(.*?)\"?+\\]"); //

    private Map<String, PathPart> pathCache = new HashMap<String, PathPart>();

    public BindingResult bind(Object item, TypeDef typeDef, HttpServletRequest request)
    {
        ServletRequestDataBinder binder = new ServletRequestDataBinder(item);

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
     * @param binder 
     * @param typeDef 
     * 
     */
    protected void prepareObject(Object item, TypeDef typeDef, ParameterPath parameterPath)
    {
        PathPart part = parameterPath.poll();
        if (part == null)
            return; //nothing to do as no path part is left over

        if (pathCache.containsKey(part.getFullPath()))
            return; // this particular path has already been dealt with

        // we need to instantiate container objects (list, component, dynaNode) and clear existing ones once per binding process

        // then register custom property editors for the given path 

        // once fully processed - cache it
        pathCache.put(part.getFullPath(), part);

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
        private Object pathObject;

        private Boolean isList;
        private Boolean isDynaNode;

        private String listPropertyName;
        private String dynaNodePropertyName;

        public PathPart(String fullParentPath, String pathPart)
        {
            this.fullParentPath = fullParentPath;
            this.pathPart = pathPart;
        }

        public String getFullPath()
        {
            return fullParentPath + ((StringUtils.isBlank(fullParentPath)) ? "" : ".") + pathPart;
        }

        public boolean isList()
        {
            if (isList == null)
                parseList();

            return isList.booleanValue();
        }

        private void parseList()
        {
            Matcher matcher = LIST_PATTERN.matcher(pathPart);
            if (matcher.matches())
            {
                isList = Boolean.TRUE;
                listPropertyName = matcher.group(1);
            }
            else
                isList = Boolean.FALSE;
        }

        public boolean isDynaNode()
        {
            if (isDynaNode == null)
            {
                parseDynaNode();
            }

            return isDynaNode.booleanValue();
        }

        private void parseDynaNode()
        {
            Matcher matcher = DYNA_NODE_PATTERN.matcher(pathPart);
            if (matcher.matches())
            {
                isDynaNode = Boolean.TRUE;
                dynaNodePropertyName = matcher.group(2);
            }
            else
                isDynaNode = Boolean.FALSE;
        }

        public String getListPropertyName()
        {
            if (listPropertyName == null)
                parseList();

            return listPropertyName;
        }

        public String getDynaNodePropertyName()
        {
            if (dynaNodePropertyName == null)
                parseDynaNode();

            return dynaNodePropertyName;
        }

        public String getFullParentPath()
        {
            return fullParentPath;
        }

        public String getPathPart()
        {
            return pathPart;
        }

        public Object getPathObject()
        {
            return pathObject;
        }

        public void setPathObject(Object pathObject)
        {
            this.pathObject = pathObject;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (obj == this)
            {
                return true;
            }
            if (obj.getClass() != getClass())
            {
                return false;
            }
            PathPart rhs = (PathPart) obj;
            return new EqualsBuilder().append(fullParentPath, rhs.getFullParentPath()).append(pathPart, rhs.getPathPart()).isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(11, 47).append(fullParentPath).append(pathPart).toHashCode();
        }

        @Override
        public String toString()
        {
            return getFullPath();
        }

    }

}
