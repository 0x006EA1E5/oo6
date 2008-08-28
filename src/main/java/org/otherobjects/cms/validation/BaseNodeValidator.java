package org.otherobjects.cms.validation;

import java.util.List;

import javax.annotation.Resource;

import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.model.BaseNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springmodules.validation.valang.ValangValidator;

/**
 * Validator is a simple {@link Validator } implementation to validate BaseNodes (usually happens after binding by {@link BindService}).
 * Uses the following three properties specified in {@link PropertyDef} for validation:
 * <ul>
 *  <li>{@link PropertyDef#isRequired()}</li>
 *  <li>{@link PropertyDef#getSize()}</li>
 *  <li>{@link PropertyDef#getValang()}</li>
 * </ul>
 * 
 * @author joerg
 */
public class BaseNodeValidator implements Validator
{

    @Resource
    private TypeService typeService;
    
    public BaseNodeValidator()
    {
    }
    
    public BaseNodeValidator(TypeService typeService)
    {
        this.typeService = typeService;
    }

    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        return BaseNode.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings("unchecked")
    public void validate(Object target, Errors errors)
    {
        Assert.isInstanceOf(BaseNode.class, target, "Can only validate BaseNodes");

        BaseNode valObj = (BaseNode) target;
        TypeDef typeDef = typeService.getType(valObj.getOoType());
        StringBuffer valangRules = new StringBuffer();

        for (PropertyDef propertyDef : typeDef.getProperties())
        {
            String fieldName = propertyDef.getName();
            if(target instanceof DynaNode)
                fieldName = "data['"+fieldName+"']";
            Object value = errors.getFieldValue(fieldName);
            

            if (propertyDef.getType().equals(PropertyDef.LIST))
            {
                String collectionElementType = propertyDef.getCollectionElementType();
                Assert.isTrue(collectionElementType != null, "If this property is a collection the collectionElementType needs to have been set");
                if (collectionElementType.equals(PropertyDef.COMPONENT))
                {
                    if (value != null && List.class.isAssignableFrom(value.getClass()))
                    {
                        int i = 0;
                        List<BaseNode> baseNodeList = (List<BaseNode>) value;
                        for (BaseNode baseNode : baseNodeList)
                        {
                            validateComponent(fieldName + "[" + i + "]", propertyDef, baseNode, errors);
                            i++;
                        }
                    }
                }
            }
            else if (propertyDef.getType().equals(PropertyDef.COMPONENT))
            {
                validateComponent(fieldName, propertyDef, value, errors);
            }
            else
            {

                if (propertyDef.isRequired())
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, fieldName, "field.required");

                if (propertyDef.getSize() > -1)
                {
                    if (value != null && value.toString().length() > propertyDef.getSize())
                        errors.rejectValue(fieldName, "field.valueTooLong");
                }

                // if we have a valang property, insert the fieldName into it and append it to the valang rules buffer
                if (StringUtils.hasText(propertyDef.getValang()))
                {
                    valangRules.append(propertyDef.getValang().replaceAll("\\?", fieldName));
                }
            }
        }

        // if there were any valang rules create a valang validator from those
        if (valangRules.length() > 0)
        {
            ValangValidator val = new ValangValidator();
            val.setValang(valangRules.toString());
            try
            {
                //FIXME this is not nice as it is implementation specific
                val.afterPropertiesSet();
                val.validate(target, errors);
            }
            catch (Exception e)
            {
                // noop
            }
        }

    }

    private void validateComponent(String fieldName, PropertyDef propertyDef, Object baseNode, Errors errors)
    {
        if (baseNode == null)
        {
            if (propertyDef.isRequired())
                errors.rejectValue(fieldName, "field.required");
            else
                return;
        }
        Assert.isTrue(BaseNode.class.isAssignableFrom(baseNode.getClass()), "Value of property " + fieldName + " is not a BaseNode. Perhaps there is a conflicting parameter in the request?");
        try
        {
            errors.pushNestedPath(fieldName);
            ValidationUtils.invokeValidator(this, baseNode, errors);
        }
        finally
        {
            errors.popNestedPath();
        }
    }

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

}
