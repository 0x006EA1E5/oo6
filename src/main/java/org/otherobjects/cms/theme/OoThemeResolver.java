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

package org.otherobjects.cms.theme;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ThemeResolver;

/**
 * OTHERobjects theme resolver that resolves themes based on a parameter 'theme'. If the parameter
 * is not found then a theme of 'default' is returned.
 * 
 * @author joerg
 */
public class OoThemeResolver implements ThemeResolver
{
    public static final String THEME_PARAMETER_NAME = "theme";
    public static final String DEFAULT_THEME = "default";

    /**
     * Resolve the current theme name via the given request based on a 'theme=' parameter.
     * Returns 'default' if not theme specified.
     * 
     * @param request request to be used for resolution
     * @return the current theme name
     */
    public String resolveThemeName(HttpServletRequest request)
    {
        String theme = request.getParameter(THEME_PARAMETER_NAME);
        if (theme != null)
            return theme;

        return DEFAULT_THEME;
    }

    /**
     * Set the current theme name to the given one. Not supported.
     * 
     * @param request request to be used for theme name modification
     * @param response response to be used for theme name modification
     * @param themeName the new theme name
     * @throws UnsupportedOperationException if the ThemeResolver implementation
     * does not support dynamic changing of the theme
     */
    public void setThemeName(HttpServletRequest request, HttpServletResponse response, String themeName)
    {
        throw new UnsupportedOperationException();
    }

}
