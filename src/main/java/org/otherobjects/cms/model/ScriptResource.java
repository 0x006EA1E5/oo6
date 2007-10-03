package org.otherobjects.cms.model;

public class ScriptResource extends DynaNode
{
    private static final String ICON_PATH = "otherobjects.resources/static/icons/page-white-gear.png";
    private String label;
    private Script script;

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


    public Script getScript()
    {
        return script;
    }


    public void setScript(Script script)
    {
        this.script = script;
    }


}
