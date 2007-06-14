package org.otherobjects.cms;

public class OtherObjectsException extends RuntimeException
{
    private static final long serialVersionUID = 3748806921413325385L;

    public OtherObjectsException(String message)
    {
        super(message);
    }
    public OtherObjectsException(String message, Exception e)
    {
        super(message, e);
    }
}
