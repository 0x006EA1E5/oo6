package org.otherobjects.cms.controllers;

import org.otherobjects.cms.security.PasswordChanger;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Validates new passwords to ensure the comply with security policy.
 * 
 * @author rich
 */
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

        // Don't show more specific warnings if fields are null
        if(errors.hasErrors())
            return;
        
        PasswordChanger p = (PasswordChanger) target;
        if (!p.getNewPassword().equals(p.getNewPasswordRepeated()))
            errors.rejectValue("newPasswordRepeated", "password.does.not.match");
        if (p.getNewPassword().length() < 6)
            errors.rejectValue("newPassword", "password.too.short", new Object[]{6}, "password.too.short");
    }

}
