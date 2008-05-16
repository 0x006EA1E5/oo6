package org.otherobjects.cms.io;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.io.IOException;

import org.springframework.util.Assert;

/**
 * {@link PropertyEditor} which if registered in the application context and injected with an {@link OoResourceLoader} instance 
 * allows to set {@link OoResource}s on beans in the context by just specifying their virtual path as a string.
 * @author joerg
 *
 */
public class OoResourceEditor extends PropertyEditorSupport
{
    private OoResourceLoader ooResourceLoader;

    public void setOoResourceLoader(OoResourceLoader ooResourceLoader)
    {
        this.ooResourceLoader = ooResourceLoader;
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        Assert.notNull(ooResourceLoader, "No instance of OoResourceLoader available. You are probably not using the application context provided instance of OoResourceEditor");
        try
        {
            setValue(ooResourceLoader.getResource(text));
        }
        catch (IOException e)
        {
            throw new IllegalArgumentException("Path " + text + " couldn't be resolved as a OoResource", e);
        }
    }

    @Override
    public String getAsText()
    {
        OoResource ooResource = (OoResource) getValue();
        return ooResource.getPath();
    }

}
