package org.otherobjects.cms.model;

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
    
    public String getLabel()
    {
        return label;
    }
    public void setLabel(String label)
    {
        this.label = label;
    }
    public String getLanguage()
    {
        return language;
    }
    public void setLanguage(String language)
    {
        this.language = language;
    }
    public String getScriptPath()
    {
        return scriptPath;
    }
    public void setScriptPath(String scriptPath)
    {
        this.scriptPath = scriptPath;
    }
    public String getScriptCode()
    {
        return scriptCode;
    }
    public void setScriptCode(String scriptCode)
    {
        this.scriptCode = scriptCode;
    }
    
}
