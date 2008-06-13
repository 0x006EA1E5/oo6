package org.otherobjects.cms.binding;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.validation.BindingResult;

public class BindingResultWrapper implements InvocationHandler
{
    private BindingResult bindingResult;
    private Map<String, String> rewrittenPaths;

    private static Set<Method> proxiedMethods = new HashSet<Method>();

    {
        try
        {
            proxiedMethods.add(BindingResult.class.getMethod("getFieldError", new Class[]{String.class}));
            proxiedMethods.add(BindingResult.class.getMethod("getFieldErrorCount", new Class[]{String.class}));
            proxiedMethods.add(BindingResult.class.getMethod("getFieldErrors", new Class[]{String.class}));
            proxiedMethods.add(BindingResult.class.getMethod("getFieldType", new Class[]{String.class}));
            proxiedMethods.add(BindingResult.class.getMethod("getFieldValue", new Class[]{String.class}));
            proxiedMethods.add(BindingResult.class.getMethod("hasFieldErrors", new Class[]{String.class}));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    public BindingResultWrapper(BindingResult bindingResult, Map<String, String> rewrittenPaths)
    {
        this.bindingResult = bindingResult;
        this.rewrittenPaths = rewrittenPaths;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
        if (proxiedMethods.contains(method))
        {
            String path = (String) args[0];
            if (rewrittenPaths.containsKey(path))
                return method.invoke(bindingResult, new Object[]{rewrittenPaths.get(path)});
            else
                return method.invoke(bindingResult, args);

        }
        return method.invoke(bindingResult, args);
    }

}
