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

package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

/**
 * CmsLinks used to reference elements of the site, these are specific to the node that contains them 
 */
@Type(codeProperty = "title")
public class CmsLink extends BaseComponent
{
    private String title;
    private String url;

    public String getOoLabel()
    {
        return "CmsLink to "+this.title+")";
    }

    /**
     * Returns the custom page title.
     * 
     * @return
     */
    @Property(order = 10, size = 150)
    public String getTitle()
    {
        return title;
    }

    /**
     * Sets the custom page title. Use this to override the page title that will by default be based on the objects label.
     * 
     * @param title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }

    /**
     * Returns the meta description.
     * 
     * @return
     */
    @Property(order = 20, type = PropertyType.TEXT, size = 150)
    public String getUrl()
    {
        return url;
    }

    /**
     * Sets the meta description.
     * 
     * @param description
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

 

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((url == null) ? 0 : url.hashCode());
        result = prime * result + ((title == null) ? 0 : title.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        CmsLink other = (CmsLink) obj;
        if (url == null)
        {
            if (other.url != null)
                return false;
        }
        else if (!url.equals(other.url))
            return false;
        if (title == null)
        {
            if (other.title != null)
                return false;
        }
        else if (!title.equals(other.title))
            return false;
        return true;
    }

}
