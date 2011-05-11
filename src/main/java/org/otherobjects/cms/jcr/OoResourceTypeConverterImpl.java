package org.otherobjects.cms.jcr;

import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.jackrabbit.ocm.manager.atomictypeconverter.AtomicTypeConverter;
import org.otherobjects.cms.io.OoResource;
import org.otherobjects.cms.io.OoResourceLoader;
import org.otherobjects.framework.SingletonBeanLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link AtomicTypeConverter} implementation that converts between {@link OoResource OoResource's} virtual path strings to real OoResources and back to allow
 * JCR persisted Entities to carry Resource/file attachments.
 *  
 * @author joerg
 *
 */
public class OoResourceTypeConverterImpl implements AtomicTypeConverter
{
    private final Logger logger = LoggerFactory.getLogger(OoResourceTypeConverterImpl.class);

    public Object getObject(Value value)
    {
        String resourcePath = null;
        try
        {
            resourcePath = value.getString();
            return ((OoResourceLoader) SingletonBeanLocator.getBean("ooResourceLoader")).getResource(resourcePath);
        }
        catch (Exception e)
        {
            logger.warn("Couldn't convert path " + resourcePath + " into an OoResource", e);
            return null;
        }
    }

    public Value getValue(ValueFactory valueFactory, Object propValue)
    {
        if (propValue == null)
        {
            return null;
        }

        return valueFactory.createValue(((OoResource) propValue).getPath());
    }

    public String getXPathQueryValue(ValueFactory valueFactory, Object object)
    {
        return object.toString();
    }

}
