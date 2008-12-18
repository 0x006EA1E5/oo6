package org.otherobjects.cms.services;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.validation.ValidatorService;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

/**
 * FIXME This is just a test
 *
 * @author rich
 */
@Service
public class FormService
{
    @Resource
    private BindService bindService;

    @Resource
    private ValidatorService validatorService;

    @Resource
    private TypeService typeService;

    public BindingResult bindAndValidate(HttpServletRequest request, Object target) throws Exception
    {
        TypeDef typeDef = typeService.getType(target.getClass());
        BindingResult errors = bindService.bind(target, typeDef, request);
        Validator validator = validatorService.getValidator(target);
        if (validator != null)
            validator.validate(target, errors);
        return errors;
    }
}
