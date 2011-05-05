/**
 * 
 */
package org.otherobjects.cms.types.convert;

import javax.annotation.Resource;

import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * <hr/>
 * Created 11 Jun 2010, Last Committed $Date $
 * @author geales
 * @author Last committed by $Author $
 * @version $Revision $
 */
public final class StringToTypeDefConverter implements Converter<String, TypeDef>
{

    @Resource
    private TypeService typeService;

    public TypeDef convert(String typeName) {
        return (TypeDef) typeService.getType(typeName);

    }

    /**
     * @param typeService the typeService to set
     */
    public void setTypeService(TypeService typeService) {
        this.typeService = typeService;
    }
}

