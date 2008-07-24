package org.otherobjects.cms.controllers;

import org.otherobjects.cms.security.PasswordChanger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class PasswordChangerValidator implements Validator
{

    @SuppressWarnings("unchecked")
    public boolean supports(Class clazz)
    {
        return PasswordChanger.class.isAssignableFrom(clazz);

    }

    public void validate(Object target, Errors errors)
    {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPassword", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "newPasswordRepeated", "field.required");

        PasswordChanger p = (PasswordChanger) target;
        if (!p.getNewPassword().equals(p.getNewPasswordRepeated()))
            errors.rejectValue("newPasswordRepeated", "password.does.not.match");

        if (p.getNewPassword().length() < 6)
            errors.rejectValue("newPassword", "password.too.short", new Object[]{6}, "password.too.short");
    }

}
