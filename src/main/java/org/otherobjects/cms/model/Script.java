package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.PropertyType;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class Script extends DynaNode
{
    private static final String ICON_PATH = "otherobjects.resources/static/icons/script-gear.png";

    private String label;
    private String language;
    private String scriptPath;
    private String scriptCode;

    @Override
    public String getOoIcon()
    {
        return ICON_PATH;
    }

    @Property(order = 10)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order = 20)
    public String getLanguage()
    {
        return language;
    }

    public void setLanguage(String language)
    {
        this.language = language;
    }

    @Property(order = 30)
    public String getScriptPath()
    {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath)
    {
        this.scriptPath = scriptPath;
    }

    @Property(order = 40, type = PropertyType.TEXT)
    public String getScriptCode()
    {
        return scriptCode;
    }

    public void setScriptCode(String scriptCode)
    {
        this.scriptCode = scriptCode;
    }

}
