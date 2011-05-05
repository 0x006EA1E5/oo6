package org.otherobjects.cms.events;

import org.springframework.context.ApplicationEvent;

public class ModificationEvent extends ApplicationEvent
{

    private static final long serialVersionUID = -6396327287329531404L;

    private Object item;

    public ModificationEvent(Object source, Object item)
    {
        super(source);
        this.item = item;
    }

    public Object getItem()
    {
        return item;
    }
}
