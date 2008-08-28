package org.otherobjects.cms.validation;

import javax.annotation.Resource;

import org.otherobjects.cms.model.BaseNode;
import org.springframework.validation.Validator;

public class ValidatorServiceImpl implements ValidatorService
{
    @Resource
    private BaseNodeValidator baseNodeValidator;

    @Resource
    private TypeDefConfiguredValidator typeDefConfiguredValidator;

    public Validator getValidator(Object objectToValidate)
    {
        return getValidator(objectToValidate.getClass());
    }

    public Validator getValidator(Class<?> classToValidate)
    {
        if (BaseNode.class.isAssignableFrom(classToValidate))
            return baseNodeValidator;
        else if (typeDefConfiguredValidator.supports(classToValidate))
            return typeDefConfiguredValidator;

        return null;
    }

    public void setBaseNodeValidator(BaseNodeValidator baseNodeValidator)
    {
        this.baseNodeValidator = baseNodeValidator;
    }

    public void setTypeDefConfiguredValidator(TypeDefConfiguredValidator typeDefConfiguredValidator)
    {
        this.typeDefConfiguredValidator = typeDefConfiguredValidator;
    }

}
