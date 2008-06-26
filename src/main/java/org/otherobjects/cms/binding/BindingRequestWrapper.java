package org.otherobjects.cms.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.OtherObjectsException;

public class BindingRequestWrapper implements InvocationHandler
{
    private HttpServletRequest request;
    private Map<String, String> rewritePaths = new HashMap<String, String>();
    private Map<String, String[]> mutableParams = new HashMap<String, String[]>();

    private static Map<Method, SpecialMethods> overiddenMethods = new HashMap<Method, SpecialMethods>();

    {
        try
        {
            overiddenMethods.put(HttpServletRequest.class.getMethod("getParameter", new Class[]{String.class}), SpecialMethods.GETPARAMETER);
            overiddenMethods.put(HttpServletRequest.class.getMethod("getParameterMap", new Class[]{}), SpecialMethods.GETPARAMETERMAP);
            overiddenMethods.put(HttpServletRequest.class.getMethod("getParameterNames", new Class[]{}), SpecialMethods.GETPARAMETERNAMES);
            overiddenMethods.put(HttpServletRequest.class.getMethod("getParameterValues", new Class[]{String.class}), SpecialMethods.GETPARAMETERVALUES);
            overiddenMethods.put(MutableHttpServletRequest.class.getMethod("rewriteParameter", new Class[]{String.class, String.class}), SpecialMethods.REWRITEPARAMETER);
            overiddenMethods.put(MutableHttpServletRequest.class.getMethod("getRewrittenPaths", new Class[]{}), SpecialMethods.GETREWRITTENPATHS);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public BindingRequestWrapper(HttpServletRequest request)
    {
        this.request = request;
        this.mutableParams.putAll(request.getParameterMap());
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if (overiddenMethods.containsKey(method))
        {
            switch (overiddenMethods.get(method))
            {
                case GETPARAMETER :
                    return getParameter((String) args[0]);
                case GETPARAMETERMAP :
                    return getParameterMap();
                case GETPARAMETERNAMES :
                    return getParameterNames();
                case GETPARAMETERVALUES :
                    return getParameterValues((String) args[0]);
                case REWRITEPARAMETER :
                    rewriteParameter((String) args[0], (String) args[1]);
                    return null;
                case GETREWRITTENPATHS :
                    return this.rewritePaths;
                default :
                    throw new OtherObjectsException("Unforseen method was called");
            }
        }
        else
            return method.invoke(request, args);
    }

    public void rewriteParameter(String originalParameterName, String newParameterName)
    {
        if (mutableParams.containsKey(originalParameterName) && !mutableParams.containsKey(newParameterName))
        {
            mutableParams.put(newParameterName, mutableParams.get(originalParameterName));
            mutableParams.remove(originalParameterName);
            rewritePaths.put(originalParameterName, newParameterName);
        }
    }

    public String getParameter(String name)
    {
        String[] values = getParameterValues(name);
        if ((values == null) || (values.length < 1))
            return null;
        return values[0];
    }

    public Map getParameterMap()
    {
        return Collections.unmodifiableMap(mutableParams);
    }

    public Enumeration getParameterNames()
    {
        return Collections.enumeration(mutableParams.keySet());
    }

    public String[] getParameterValues(String name)
    {
        return mutableParams.get(name);
    }

    enum SpecialMethods {
        GETPARAMETER("getParameter"), GETPARAMETERMAP("getParameterMap"), GETPARAMETERNAMES("getParameterNames"), GETPARAMETERVALUES("getParameterValues"), REWRITEPARAMETER("rewriteParameter"), GETREWRITTENPATHS(
                "getRewrittenPaths");

        private final String methodName;

        SpecialMethods(String methodName)
        {
            this.methodName = methodName;
        }

        public String methodName()
        {
            return this.methodName;
        }
    }

}
