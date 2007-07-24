package org.otherobjects.cms.beans;

import java.util.Iterator;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.JcrTypeServiceImpl;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Service class to beanify DynaNodes. Dynamically adds bean style getters and setters for all (simple) properties
 * defined in TypeDef for a given DynaNode {@link JcrBeanService#createCustomDynaNodeBean(DynaNode)}. Can then populate these bean properties 
 * with the values contained in the JcrMapper managed data map {@link DynaNode#getData()} by calling {@link JcrBeanService#copyDynamicProperties(DynaNode, DynaNode)}.
 * And those bean properties can then be copied back to the data map by calling {@link JcrBeanService#copyBeanProperties(DynaNode, DynaNode)}
 *  
 * @author joerg
 *
 */
public class JcrBeanService
{

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private TypeService typeService;

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    public DynaNode createCustomDynaNodeBean(TypeDef type)
    {

        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(DynaNode.class);

        JcrTypeServiceImpl jcrTypeService = (JcrTypeServiceImpl) typeService;
        Map<String, Class<?>> jcrClassMappings = jcrTypeService.getJcrClassMappings();
        for (Iterator<PropertyDef> it = type.getProperties().iterator(); it.hasNext();)
        {
            PropertyDef propertyDef = it.next();
            Assert.doesNotContain(propertyDef.getName(), ".", "There is currently no mechanism to create nested properties");

            // FIXME Move this somewhere better
            if (propertyDef.getType().equals("reference") || propertyDef.getType().equals("component"))
            {
                throw new OtherObjectsException("No support for reference or component proerties at the moment: " + type.getName());

                //TypeDef type2 = typeService.getType(propertyDef.getRelatedType());
                //beanGenerator.addProperty(propertyDef.getName(), Class.forName(type2.getClassName()));
            }
            else
                beanGenerator.addProperty(propertyDef.getName(), jcrClassMappings.get(propertyDef.getType()));
        }

        //TODO what do we do about nested properties?
        DynaNode dynaNode = (DynaNode) beanGenerator.create();
//        dynaNode.setOoType(type.getName());

        logger.info("Created bean class for {}: {}", type.getName(), dynaNode.getClass().getName());

        return dynaNode;

    }

    public DynaNode createCustomDynaNodeBean(DynaNode persistentDynaNode)
    {
        String ooType = persistentDynaNode.getOoType();
        return createCustomDynaNodeBean(typeService.getType(ooType));
    }

    public void copyBeanProperties(DynaNode fromNode, DynaNode toNode)
    {
        TypeDef type = typeService.getType(toNode.getOoType());
        for (Iterator<PropertyDef> it = type.getProperties().iterator(); it.hasNext();)
        {
            PropertyDef propertyDef = it.next();

            try
            {
                toNode.set(propertyDef.getName(), PropertyUtils.getNestedProperty(fromNode, propertyDef.getName()));
            }
            catch (Exception e)
            {
                logger.warn("Couldn't copy property " + propertyDef.getName(), e);
            }
        }
    }

    public void copyDynamicProperties(DynaNode fromNode, DynaNode toBean)
    {
        for (Iterator<String> it = fromNode.getData().keySet().iterator(); it.hasNext();)
        {
            String property = it.next();
            try
            {
                PropertyUtils.setNestedProperty(toBean, property, fromNode.get(property));
            }
            catch (Exception e)
            {
                logger.warn("Couldn't copy property " + property, e);
            }
        }
        toBean.setId(fromNode.getId());
        toBean.setPath(fromNode.getPath());
        toBean.setCode(fromNode.getCode());
    }
}
