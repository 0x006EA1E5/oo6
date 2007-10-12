package org.otherobjects.cms.model;

import org.otherobjects.cms.types.annotation.Property;
import org.otherobjects.cms.types.annotation.Type;

@Type
public class ScriptResource extends BaseNode
{
    private static final String ICON_PATH = "otherobjects.resources/static/icons/page-white-gear.png";
    private String label;
    private Script script;

    @Override
    public String getOoIcon()
    {
        return ICON_PATH;
    }
     
    @Property(order=10)
    public String getLabel()
    {
        return label;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    @Property(order=20)
    public Script getScript()
    {
        return script;
    }

    public void setScript(Script script)
    {
        this.script = script;
    }


}
