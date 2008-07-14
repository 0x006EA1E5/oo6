package org.otherobjects.cms.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.validation.ValidatorService;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * Useful functions for actions.
 * 
 * @author rich
 *
 */
public class ActionUtils
{
    private HttpServletRequest request;
    private HttpServletResponse response;
    private BindService bindService;
    private ValidatorService validatorService;

    public ActionUtils(HttpServletRequest request, HttpServletResponse response, BindService bindService, ValidatorService validatorService)
    {
        this.request = request;
        this.response = response;
        this.bindService = bindService;
        this.validatorService = validatorService;
    }

    /**
     * FIXME BaseNode dependency
     * 
     * @param typeName
     * @return
     * @throws Exception
     */
    public Object bindAndValidate(String typeName) throws Exception
    {
        request.getSession().removeAttribute("errors");
        BaseNode target = (BaseNode) Class.forName(typeName).newInstance();
        Errors errors = bindService.bind(target, target.getTypeDef(), request);
        Validator validator = validatorService.getValidator(target);
        if (validator != null)
            validator.validate(target, errors);
        return errors;
    }

    /**
     * TODO Allow url overriding
     * 
     * @param message
     * @param errors
     * @throws IOException
     */
    public void error(String message, Errors errors) throws IOException
    {
        flashError(message);
        request.getSession().setAttribute("errors", errors);
        redirectToReferrer();
    }
    
    /**
     * TODO Allow url overriding
     * 
     * @param message
     * @throws IOException
     */
    public void success(String message) throws IOException
    {
        if(message!=null)
            flashInfo(message);
        redirectToReferrer();
    }

    @SuppressWarnings("unchecked")
    private void flashMessage(String type, String message)
    {
        List<FlashMessage> flashMessages = (List<FlashMessage>) request.getSession(true).getAttribute(FlashMessage.OO_FLASH_MESSAGES_KEY);
        if (flashMessages == null)
        {
            flashMessages = new ArrayList<FlashMessage>();
            request.getSession(true).setAttribute(FlashMessage.OO_FLASH_MESSAGES_KEY, flashMessages);
        }
        flashMessages.add(new FlashMessage(type, message));
    }

    public void flashInfo(String message)
    {
        flashMessage(FlashMessage.INFO, message);
    }

    public void flashWarning(String message)
    {
        flashMessage(FlashMessage.WARNING, message);
    }

    public void flashError(String message)
    {
        flashMessage(FlashMessage.ERROR, message);
    }

    public void redirectTo(String url) throws IOException
    {
        response.sendRedirect(url);
    }

    public void redirectToReferrer() throws IOException
    {
        response.sendRedirect(request.getHeader("referer"));
    }
}
