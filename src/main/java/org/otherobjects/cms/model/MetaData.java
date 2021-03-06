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
import org.otherobjects.cms.util.StringUtils;

/**
 * MetaData for site pages. This data augments the publishing and audit information already
 * stored and is primarily for SEO (internal search and external engines).
 */
@Type(codeProperty = "")
public class MetaData extends BaseComponent
{
    private String title;
    private String description;
    private String keywords;

    public String getOoLabel()
    {
        return "MetaData (title = " + StringUtils.trim(this.title, 30) + ", description = " + StringUtils.trim(this.description, 30) + ", keywords = " + StringUtils.trim(this.keywords, 30) + ")";
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
    @Property(order = 20, type = PropertyType.TEXT, size = 1000)
    public String getDescription()
    {
        return description;
    }

    /**
     * Sets the meta description.
     * 
     * @param description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    /**
     * Returns meta keywords.
     * 
     * @return
     */
    @Property(order = 30, type = PropertyType.TEXT, size = 1000)
    public String getKeywords()
    {
        return keywords;
    }

    /**
     * Sets the meta keywords.
     * 
     * @param keywords
     */
    public void setKeywords(String keywords)
    {
        this.keywords = keywords;
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((keywords == null) ? 0 : keywords.hashCode());
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
        MetaData other = (MetaData) obj;
        if (description == null)
        {
            if (other.description != null)
                return false;
        }
        else if (!description.equals(other.description))
            return false;
        if (keywords == null)
        {
            if (other.keywords != null)
                return false;
        }
        else if (!keywords.equals(other.keywords))
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
