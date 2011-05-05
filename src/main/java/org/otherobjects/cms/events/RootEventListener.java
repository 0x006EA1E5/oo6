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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OTHERobjects.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.otherobjects.cms.events;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * Sister interface to {@link ApplicationListener} to use when you don't want to be autosubscribed to your own context's events, 
 * but only subscribe to the events being proxied by {@link EventProxy}
 *  
 * @author joerg
 *
 */
public interface RootEventListener
{
    void onApplicationEvent(ApplicationEvent event);
}
