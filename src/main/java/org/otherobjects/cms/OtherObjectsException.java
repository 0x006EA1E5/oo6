/*
 * This file is part of the OTHERobjects Content Management System.
 * Copyright 2008 OTHER works Limited.
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
 * 
 */
package org.otherobjects.cms;

/**
 * Generic OTHERobjects exception.
 * 
 * @author rich
 */
public class OtherObjectsException extends RuntimeException
{
    private static final long serialVersionUID = 3748806921413325385L;

    public OtherObjectsException(String message)
    {
        super(message);
    }
    public OtherObjectsException(String message, Throwable e)
    {
        super(message, e);
    }
}
