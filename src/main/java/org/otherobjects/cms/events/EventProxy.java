/*
 * This file is part of the OTHERobjects Content Management System.
 * 
 * Copyright 2007-2009 OTHER works Limited.
 * 
 * OTHERobjects is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * OTHERobjects is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with OTHERobjects. If not, see <http://www.gnu.org/licenses/>.
 */
package org.otherobjects.cms.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Simple event proxy that can be defined in the root application context and
 * then be injected into beans in a child context so that
 * they can then register themselves as applicationListeners with this bean to
 * be able to receive applicationEvents from the parent/root context.
 * 
 * In order not to interfere with the current semantics of beans implementing
 * {@link ApplicationListener} EventProxy uses a differently named
 * albeit identical interface {@link RootEventListener}.
 * 
 * @author joerg
 * 
 */
@Component
public class EventProxy extends SimpleApplicationEventMulticaster implements ApplicationListener<ApplicationEvent>
{
    public EventProxy()
    {
        setTaskExecutor(new SimpleAsyncTaskExecutor());
    }

    public void onApplicationEvent(ApplicationEvent event)
    {
        multicastEvent(event);
    }

    public void addRootEventListener(RootEventListener listener)
    {
        super.addApplicationListener(new ApplicationListenerAdapter(listener));
    }

    class ApplicationListenerAdapter implements ApplicationListener<ApplicationEvent>
    {
        private RootEventListener rootEventListener;

        public ApplicationListenerAdapter(RootEventListener rootEventListener)
        {
            this.rootEventListener = rootEventListener;
        }

        public void onApplicationEvent(ApplicationEvent event)
        {
            rootEventListener.onApplicationEvent(event);
        }
    }
}
