package org.otherobjects.cms.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

    public ActionUtils(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
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
}
