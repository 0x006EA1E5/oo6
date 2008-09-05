package org.otherobjects.cms.util;

public class FlashMessage
{
    public static final String OO_FLASH_MESSAGES_KEY = "ooFlashMessages";
    public static final String INFO = "info";
    public static final String WARNING = "warning";
    public static final String ERROR = "error";
    
    private String type;
    private String message;

    public FlashMessage(String type, String message)
    {
        this.type = type;
        this.message = message;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getType()
    {
        return type;
    }

    public String getMessage()
    {
        return message;
    }
}
