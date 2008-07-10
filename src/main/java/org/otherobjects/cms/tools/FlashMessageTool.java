package org.otherobjects.cms.tools;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.FlashMessage;
import org.springframework.util.Assert;

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

    
    public BaseNode create(String typeName)
    {
        Assert.notNull(typeName, "typeName must not be null.");
        TypeDef type = ((TypeService)SingletonBeanLocator.getBean("typeService")).getType(typeName);
        Assert.notNull(type, "No type found: " + typeName);
        Assert.notNull(type.getClassName(), "Type does not have backing class specified: " + typeName);
        try
        {
            BaseNode n = (BaseNode) Class.forName(type.getClassName()).newInstance();
            n.setTypeDef(type);
            return n;
        }
        catch (Exception e)
        {
            //TODO Better exception?
            throw new OtherObjectsException("Could not create new instance of type: " + typeName, e);
        }
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
