package org.otherobjects.cms.scripting;

import org.springframework.core.io.Resource;

/**
 * Interface to be implemented by objects that can resolve scripts to Resources by name
 * 
 * 
 * @author joerg
 *
 */
public interface ScriptResourceResolver
{
    /**
     * 
     * @param scriptName - symbolic name of the script to be resolved
     * @return  Resource that is identified by scriptName if no matching resource exists an error is thrown
     * @throws Exception
     */
    Resource resolveScriptName(String scriptName) throws Exception;

}
