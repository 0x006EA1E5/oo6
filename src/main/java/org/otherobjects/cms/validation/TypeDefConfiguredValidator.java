package org.otherobjects.cms.validation;

import java.util.List;

import javax.annotation.Resource;

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
 * This is a generalisation of {@link BaseNodeValidator} that tries to only rely on an existing {@link TypeDef}
 * 
 * @author joerg
 *
 */
public class TypeDefConfiguredValidator implements Validator
{
    @Resource
    private TypeService typeService;

    public boolean supports(Class clazz)
    {
        return typeService.getType(clazz) != null;
    }

    public void validate(Object target, Errors errors)
    {
        TypeDef typeDef = typeService.getType(target.getClass());
        StringBuffer valangRules = new StringBuffer();

        for (PropertyDef propertyDef : typeDef.getProperties())
        {
            String fieldName = propertyDef.getName();
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
                        List<Object> objectList = (List<Object>) value;
                        for (Object object : objectList)
                        {
                            validateComponent(fieldName + "[" + i + "]", propertyDef, object, errors);
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

    private void validateComponent(String fieldName, PropertyDef propertyDef, Object valueObject, Errors errors)
    {
        if (valueObject == null)
        {
            if (propertyDef.isRequired())
                errors.rejectValue(fieldName, "field.required");
            else
                return;
        }
        Assert.notNull(typeService.getType(valueObject.getClass()), "No TypeDef for valueObject of property " + fieldName + ". Perhaps there is a conflicting parameter in the request?");
        try
        {
            errors.pushNestedPath(fieldName);
            ValidationUtils.invokeValidator(this, valueObject, errors);
        }
        finally
        {
            errors.popNestedPath();
        }
    }

}
