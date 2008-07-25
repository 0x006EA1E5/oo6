package org.otherobjects.cms.validation;

import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.otherobjects.cms.jcr.dynamic.DynaNode;
import org.otherobjects.cms.types.PropertyDef;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import org.springmodules.validation.valang.ValangValidator;

public class TypeDefBasedValidator implements Validator
{
    private TypeService typeService;

    public void setTypeService(TypeService typeService)
    {
        this.typeService = typeService;
    }

    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        return typeService.getType(clazz) != null;
    }

    public void validate(Object target, Errors errors)
    {
        validate(target, errors, typeService.getType(target.getClass()));
    }

    @SuppressWarnings("unchecked")
    public void validate(Object target, Errors errors, TypeDef typeDef)
    {
        StringBuffer valangRules = new StringBuffer();
        // iterate all props
        for (PropertyDef propertyDef : typeDef.getProperties())
        {
            // Determine path
            String path = calcPropertyPath(target, propertyDef);
            Object value = errors.getFieldValue(path);

            // deal with lists
            if (propertyDef.getType().equals("list")) //TODO the type should clearly be a constant or enum of sorts
            {
                List<Object> list = null;
                try
                {
                    list = (List<Object>) PropertyUtils.getNestedProperty(target, path); //FIXME shouldn't we just use value
                }
                catch (Exception e)
                {
                    //noop
                }

                if (propertyDef.getCollectionElementType().equals("reference"))
                {
                    //TODO can we validate anything?
                }
                else if (propertyDef.getCollectionElementType().equals("component"))
                {
                    int i = 0;
                    for (Object component : list)
                    {
                        validateComponent(component, path + "[" + i + "]", propertyDef, errors);
                        i++;
                    }
                }

                // Nothing to do for simple properties in lists - use valang

            }
            else if (propertyDef.getType().equals("component"))// deal with components
            {
                validateComponent(value, path, propertyDef, errors);
            }
            else
            // simple props
            {
                if (propertyDef.isRequired())
                    ValidationUtils.rejectIfEmptyOrWhitespace(errors, path, "field.required");

                if (propertyDef.getSize() > -1)
                {
                    if (value != null && value.toString().length() > propertyDef.getSize())
                        errors.rejectValue(path, "field.valueTooLong");
                }

            }

            // if we have a valang property, insert the path into it and append it to the valang rules buffer
            if (StringUtils.hasText(propertyDef.getValang()))
            {
                valangRules.append(propertyDef.getValang().replaceAll("\\?", path));
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

    private void validateComponent(Object component, String path, PropertyDef propertyDef, Errors errors)
    {
        if (component == null)
        {
            if (propertyDef.isRequired())
                errors.rejectValue(path, "field.required");
            else
                return;
        }
        TypeDef componentTypeDef = typeService.getType(component.getClass());
        Assert.notNull(componentTypeDef, "No TypeDef for component of at " + path + ".");
        try
        {
            errors.pushNestedPath(path);
            validate(component, errors, componentTypeDef);
        }
        finally
        {
            errors.popNestedPath();
        }

    }

    private String calcPropertyPath(Object target, PropertyDef propertyDef)
    {
        if (target instanceof DynaNode)
        {
            return DynaNode.DYNA_NODE_DATAMAP_NAME + "[" + propertyDef.getName() + "]";
        }
        else
            return propertyDef.getName();
    }

}
