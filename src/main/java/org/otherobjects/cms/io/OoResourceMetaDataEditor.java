package org.otherobjects.cms.io;

import java.beans.PropertyEditorSupport;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;

public class OoResourceMetaDataEditor extends PropertyEditorSupport
{

    @Override
    public String getAsText()
    {
        return ((OoResourceMetaData) getValue()).toString();
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        try
        {
            XStream xstream = new XStream(new JettisonMappedXmlDriver());
            xstream.registerConverter(new JsonXstreamDateConverter());
            xstream.alias("metaData", OoResourceMetaData.class);
            OoResourceMetaData ooResourceMetaData = (OoResourceMetaData) xstream.fromXML(text);
            setValue(ooResourceMetaData);
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException("Couldn't deserialize JSON value string", e);
        }
    }

}
