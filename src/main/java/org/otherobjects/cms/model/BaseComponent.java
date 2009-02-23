package org.otherobjects.cms.model;

import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.OtherObjectsException;
import org.otherobjects.cms.SingletonBeanLocator;
import org.otherobjects.cms.Url;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.StringUtils;
import org.springframework.util.Assert;

/**
 * Document those property names that can't be used. Better still name space them?
 * 
 * FIXME Our standard props should be namespaced eg ooLabel?
 * 
 * @author rich
 */
public abstract class BaseComponent
{
    private TypeDef typeDef;

    public BaseComponent()
    {
    }
    
}
