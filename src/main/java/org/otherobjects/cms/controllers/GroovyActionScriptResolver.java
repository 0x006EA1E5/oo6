package org.otherobjects.cms.controllers;

import org.otherobjects.cms.scripting.AbstractScriptResourceResolver;

public class GroovyActionScriptResolver extends AbstractScriptResourceResolver
{
    public static final String GROOVY_SCRIPT_SUFFIX = ".script";

    public GroovyActionScriptResolver()
    {
        super("scripts/actions", GROOVY_SCRIPT_SUFFIX);
    }

}
