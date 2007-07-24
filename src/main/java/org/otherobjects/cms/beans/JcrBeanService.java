package org.otherobjects.cms.beans;

import java.util.Iterator;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;

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
 * defined in TypeDef for a given DynaNode {@link JcrBeanService#createCustomDynaNodeClass(DynaNode)}. 
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
    
    /**
     * Uses CGLIB to generate a custom DynaNode with bean style properties as defined by the given typeDef.
     * Used for its side effect of actually generating the bytecode for this dynamic class and injecting it into the current classloader.
     * @param typeDef - typeDef to serve as a template for the dynamic DynaNode to be created
     * 
     * @return String - Class name of the generated dynamic class
     */
    public String createCustomDynaNodeClass(TypeDef typeDef)
    {

        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(DynaNode.class);

        JcrTypeServiceImpl jcrTypeService = (JcrTypeServiceImpl) typeService;
        Map<String, Class<?>> jcrClassMappings = jcrTypeService.getJcrClassMappings();
        for (Iterator<PropertyDef> it = typeDef.getProperties().iterator(); it.hasNext();)
        {
            PropertyDef propertyDef = it.next();
            Assert.doesNotContain(propertyDef.getName(), ".", "There is currently no mechanism to create nested properties");

            // FIXME Move this somewhere better
            if (propertyDef.getType().equals("reference") || propertyDef.getType().equals("component"))
            {
                throw new OtherObjectsException("No support for reference or component proerties at the moment: " + typeDef.getName());

                //TypeDef type2 = typeService.getType(propertyDef.getRelatedType());
                //beanGenerator.addProperty(propertyDef.getName(), Class.forName(type2.getClassName()));
            }
            else
                beanGenerator.addProperty(propertyDef.getName(), jcrClassMappings.get(propertyDef.getType()));
        }

        //TODO what do we do about nested properties?
        DynaNode dynaNode = (DynaNode) beanGenerator.create();

        logger.info("Created bean class for {}: {}", typeDef.getName(), dynaNode.getClass().getName());

        return dynaNode.getClass().getName();
    }
}
