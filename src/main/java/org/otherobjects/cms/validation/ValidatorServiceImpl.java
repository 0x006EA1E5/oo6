package org.otherobjects.cms.validation;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;

/**
 * FIXME Need to implement settable list of custom validators here.
 * 
 * @author rich
 *
 */
@Service("validatorService")
public class ValidatorServiceImpl implements ValidatorService {
    
    @Resource
    private Validator defaultValidator;

    public Validator getValidator(Object objectToValidate)
    {
        return getValidator(objectToValidate.getClass());
    }

    public Validator getValidator(Class<?> classToValidate)
    {
        // 1. Search custom validators
        
        // 2. Use default validator if it supports desired class
        if (defaultValidator.supports(classToValidate))
            return defaultValidator;

        // 3. Return null if no suitable validator found.
        return null;
    }

    public void setDefaultValidator(Validator validator)
    {
        this.defaultValidator = validator;
    }
}
