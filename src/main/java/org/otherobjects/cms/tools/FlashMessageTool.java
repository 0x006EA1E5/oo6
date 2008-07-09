package org.otherobjects.cms.tools;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.util.FlashMessage;

/**
 * Accessor for flash messages. 
 * 
 * @author rich
 */
public class FlashMessageTool
{
    private HttpServletRequest request;

    public FlashMessageTool(HttpServletRequest request)
    {
        this.request = request;
    }

    /**
     * Fetches the current flash messages. This will effectively mark them as read
     * so a subsequent call of this method in the same request will return null.
     * 
     * @return a list of <code>FlashMessage</code>s
     */
    @SuppressWarnings("unchecked")
    public List<FlashMessage> getMessages()
    {
        // Don't create a session if one does not exist
        HttpSession session = request.getSession(false);
        if (session != null)
        {
            List<FlashMessage> messages = (List<FlashMessage>) session.getAttribute(FlashMessage.OO_FLASH_MESSAGES_KEY);
            if (messages != null)
            {
                // Clear messages so they are only shown once
                session.removeAttribute(FlashMessage.OO_FLASH_MESSAGES_KEY);
                return messages;
            }
        }
        return null;
    }
}
