package org.otherobjects.cms.tools;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.views.Tool;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

@Component
@Scope("request")
@Tool
public class FormTool
{
    @Resource
    private HttpServletRequest request;

    @Resource
    private TypeService typeService;

    public FormTool()
    {
    }

    public Object registerFormObject(String attributeName, String typeName) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {
        this.request.setAttribute(attributeName + "TypeDef", typeService.getType(typeName));

        // Look to see if we have a previous binding result
        HttpSession session = request.getSession(false);
        if (session != null)
        {
            BindingResult errors = (BindingResult) session.getAttribute(BindingResult.MODEL_KEY_PREFIX + attributeName);
            if (errors != null)
            {
                this.request.setAttribute(BindingResult.MODEL_KEY_PREFIX + attributeName, errors);
                return null;
            }
        }

        this.request.setAttribute(attributeName, Class.forName(typeName).newInstance());
        return null;
    }

}
