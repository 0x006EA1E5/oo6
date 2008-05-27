package org.otherobjects.cms.validation;

import org.springframework.validation.Validator;

public interface ValidatorService
{
    /**
     * 
     * @param objectToValidate
     * @return a {@link Validator} or null if no validator for given object can be found
     */
    Validator getValidator(Object objectToValidate);

    Validator getValidator(Class<?> classToValidate);
}
