package org.otherobjects.cms.views;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

/**
 * An otherobjects specific subclass of Springs {@link org.springframework.web.servlet.view.velocity.VelocityView VelocityView} that
 * just before rendering stuffs another object (under the name $ctxInfo ) into the context that allows template designers to find 
 * out more about the context at runtime.
 *  
 * @author joerg
 *
 */
public class VelocityView extends org.springframework.web.servlet.view.velocity.VelocityView
{

    private static Set<String> objectMethods = new HashSet<String>();
    {
        objectMethods.add("clone");
        objectMethods.add("equals");
        objectMethods.add("finalize");
        objectMethods.add("hashCode");
        objectMethods.add("getClass");
        objectMethods.add("notify");
        objectMethods.add("notifyAll");
        objectMethods.add("wait");
    }

    private ViewToolsService viewToolsService;

    @Override
    public void afterPropertiesSet() throws Exception
    {
        super.afterPropertiesSet();
        this.viewToolsService = (ViewToolsService) getApplicationContext().getBean("viewToolsService");
    }

    @Override
    protected void doRender(Context context, HttpServletResponse response) throws Exception
    {
        this.viewToolsService.populateContext(context);
        context.put("ctxInfo", new ContextInspector(context));
        super.doRender(context, response);
    }

    public class ContextInspector
    {

        private final Context context;

        private ContextInspector(Context context)
        {
            this.context = context;
        }

        /**
         * 
         * @return - a list of all objects in the current context formatted as an HTML ul 
         */
        public String describeContext()
        {
            StringBuffer buf = new StringBuffer();
            buf.append("The current model context contains these objects: ");
            buf.append("<ul>");
            for (int i = 0; i < this.context.getKeys().length; i++)
            {
                buf.append("<li>");
                buf.append(this.context.getKeys()[i].toString());
                buf.append("</li>");

            }
            buf.append("</ul>");
            return buf.toString();
        }

        /**
         * 
         * @param toolName
         * @return - a list of all public (non java.lang.Object) methods with the parameters they take as an HTML ul
         */
        public String describeTool(String toolName)
        {
            StringBuffer buf = new StringBuffer();

            Object tool = this.context.get(toolName);

            Method[] methods = tool.getClass().getMethods();

            buf.append("<ul>");
            for (int i = 0; i < methods.length; i++)
            {
                if (!objectMethods.contains(methods[i].getName())) // only list methods that are not inherited from Object
                {
                    buf.append("<li>");
                    buf.append(methods[i].getName());
                    Class<?>[] parameters = methods[i].getParameterTypes();
                    if (parameters.length > 0)
                    {
                        buf.append("(");
                        for (Class<?> element : parameters)
                        {
                            buf.append(element.getName());
                            buf.append(" ");
                        }
                        buf.append(")");
                    }
                    buf.append("</li>");
                }
            }
            buf.append("</ul>");
            return buf.toString();
        }

        /**
         * 
         * @return - a list of all tools in the context with all their (non java.lang.Object) methods as a nested ul
         */
        public String describeAll()
        {
            StringBuffer buf = new StringBuffer();
            Object[] keys = this.context.getKeys();
            buf.append("<ul>");
            for (Object element : keys)
            {
                buf.append("<li>");
                buf.append(element.toString());
                buf.append(" has the following methods:");
                buf.append(describeTool(element.toString()));
                buf.append("</li>");
            }
            buf.append("</ul>");
            return buf.toString();
        }

    }

}
