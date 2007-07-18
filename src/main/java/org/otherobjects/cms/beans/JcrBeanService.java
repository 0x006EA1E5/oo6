package org.otherobjects.cms.beans;

import java.util.Iterator;
import java.util.Map;

import net.sf.cglib.beans.BeanGenerator;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.model.DynaNode;
import org.otherobjects.cms.types.JcrTypeServiceImpl;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Service class to 
 * @author joerg
 *
 */
public class JcrBeanService {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	private TypeService typeService;

	public void setTypeService(TypeService typeService) {
		this.typeService = typeService;
	}
	
	public DynaNode createCustomDynaNodeBean(DynaNode persistentDynaNode)
    {
		TypeDef type = typeService.getType(persistentDynaNode.getOoType());
		
        BeanGenerator beanGenerator = new BeanGenerator();
        beanGenerator.setSuperclass(DynaNode.class);

        Assert.isInstanceOf(JcrTypeServiceImpl.class, typeService, "Only works if injected typeService is a JcrTypeServiceImpl");

        JcrTypeServiceImpl jcrTypeService = (JcrTypeServiceImpl) typeService;
        Map<String, Class<?>> jcrClassMappings = jcrTypeService.getJcrClassMappings();
        for (Iterator<PropertyDef> it = type.getProperties().iterator(); it.hasNext();)
        {
            PropertyDef propertyDef = it.next();
            Assert.doesNotContain(propertyDef.getName(), ".", "There is currently no mechanism to create nested properties");
            beanGenerator.addProperty(propertyDef.getName(), jcrClassMappings.get(propertyDef.getType()));
        }
        //TODO what do we do about nested properties?
        DynaNode dynaNode = (DynaNode) beanGenerator.create();
        dynaNode.setOoType(type.getName());

        return dynaNode;
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
