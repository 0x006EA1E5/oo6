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
 * Thrown when a request can not be resolved to a resource.
 * 
 * @author rich
 */
public class NotFoundException extends RuntimeException
{
    private static final long serialVersionUID = 9080494924674908205L;

    public NotFoundException(String message)
    {
        super(message);
    }
}
