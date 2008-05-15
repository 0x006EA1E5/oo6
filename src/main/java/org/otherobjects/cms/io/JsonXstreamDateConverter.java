package org.otherobjects.cms.io;

import java.util.Date;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class JsonXstreamDateConverter implements Converter
{

    public boolean canConvert(Class clazz)
    {
        return Date.class.isAssignableFrom(clazz);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context)
    {
        Date date = (Date) value;
        writer.setValue("" + date.getTime());
    }

    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context)
    {
        Date date;
        String value = null;
        try
        {
            value = reader.getValue();
            date = new Date(new Long(value));
        }
        catch (Exception e)
        {
            throw new ConversionException("Couldn't parse " + value + " into a date", e);
        }
        return date;
    }
}