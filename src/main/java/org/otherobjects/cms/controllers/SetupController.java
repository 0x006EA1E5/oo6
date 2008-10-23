package org.otherobjects.cms.controllers;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.security.PasswordChanger;
import org.otherobjects.cms.security.PasswordService;
import org.otherobjects.cms.security.SecurityUtil;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.ActionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ValidationUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;

/**
 * SetupController handles initial configuration of the site, including setting
 * administrator information.
 * 
 * @author rich
 */
@Controller
@SessionAttributes(types = {SetupController.SetupAdminUserCommand.class})
public class SetupController
{
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private TypeService typeService;

    @Resource
    private DaoService daoService;

    @Resource
    private PasswordService passwordService;

    /**
     * Returns setup page containing admin user form. If admin user is already created then
     * a warning should be shown instead.
     */
    @RequestMapping(method = RequestMethod.GET)
    public String showForm(Model model)
    {
        User currentUser = SecurityUtil.getCurrentUser();
        if (!currentUser.getUsername().equals("admin"))
            return null;

        UserDao dao = (UserDao) daoService.getDao(User.class);
        User adminUser = (User) dao.loadUserByUsername("admin");

        SetupAdminUserCommand setupAdminUserCommand = new SetupAdminUserCommand();
        setupAdminUserCommand.setEmail(adminUser.getEmail());
        setupAdminUserCommand.setPasswordHint(adminUser.getPasswordHint());
        model.addAttribute("command", setupAdminUserCommand);
        model.addAttribute("typeDef", typeService.getType("org.otherobjects.cms.model.User"));
        return "/otherobjects/templates/otherobjects/login/setup";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute(value = "command") SetupAdminUserCommand setupAdminUserCommand, BindingResult bindingResult, SessionStatus status, HttpServletRequest request,
            HttpServletResponse response) throws IOException
    {
        ActionUtils actionUtils = new ActionUtils(request, response, null, null);

        User currentUser = SecurityUtil.getCurrentUser();
        if (!currentUser.getUsername().equals("admin"))
        {
            return null;
        }


        // Validate 
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "email", "field.required");
        ValidationUtils.rejectIfEmptyOrWhitespace(bindingResult, "oldPassword", "field.required");
        // Check old password
        if (!bindingResult.hasErrors() && !passwordService.isPasswordMatch(currentUser, setupAdminUserCommand.getOldPassword()))
        {
            bindingResult.rejectValue("oldPassword", "password.incorrect");
        }

        bindingResult.pushNestedPath("passwordChanger");
        new PasswordChangerValidator().validate(setupAdminUserCommand.getPasswordChanger(), bindingResult);
        bindingResult.popNestedPath();

        if (bindingResult.hasErrors())
        {
            actionUtils.flashWarning("Your object could not be saved. See below for errors.");
            return "/otherobjects/templates/otherobjects/login/setup";
        }
        else
        {
            passwordService.changePassword(currentUser, setupAdminUserCommand.getOldPassword(), setupAdminUserCommand.getPasswordChanger().getNewPassword());
            UserDao userDao = (UserDao) daoService.getDao(User.class);
            currentUser.setEmail(setupAdminUserCommand.getEmail());
            currentUser.setPasswordHint(setupAdminUserCommand.getPasswordHint());
            userDao.save(currentUser);
            actionUtils.flashInfo("Admin user sucessfully configured.");
            status.setComplete();
            return "redirect:/otherobjects/";
        }
    }

    public class SetupAdminUserCommand
    {
        private String email;
        private String oldPassword;
        private PasswordChanger passwordChanger = new PasswordChanger();
        private String passwordHint;

        public String getEmail()
        {
            return email;
        }

        public void setEmail(String email)
        {
            this.email = email;
        }

        public String getOldPassword()
        {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword)
        {
            this.oldPassword = oldPassword;
        }

        public PasswordChanger getPasswordChanger()
        {
            return passwordChanger;
        }

        public void setPasswordChanger(PasswordChanger passwordChanger)
        {
            this.passwordChanger = passwordChanger;
        }

        public String getPasswordHint()
        {
            return passwordHint;
        }

        public void setPasswordHint(String passwordHint)
        {
            this.passwordHint = passwordHint;
        }
    }
}
