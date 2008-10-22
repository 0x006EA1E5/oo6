package org.otherobjects.cms.controllers;

import java.io.IOException;
import java.util.Enumeration;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.binding.BindService;
import org.otherobjects.cms.dao.DaoService;
import org.otherobjects.cms.dao.UserDao;
import org.otherobjects.cms.model.User;
import org.otherobjects.cms.types.TypeDef;
import org.otherobjects.cms.types.TypeService;
import org.otherobjects.cms.util.ActionUtils;
import org.otherobjects.cms.validation.ValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.ModelAndView;

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
    private BindService bindService;

    @Resource
    private ValidatorService validatorService;

    @RequestMapping(method = RequestMethod.GET)
    public String showForm(Model model)
    {
        UserDao dao = (UserDao) daoService.getDao(User.class);
        User adminUser = (User) dao.loadUserByUsername("admin");

        SetupAdminUserCommand setupAdminUserCommand = new SetupAdminUserCommand();
        setupAdminUserCommand.setUser(adminUser);
        model.addAttribute("command", setupAdminUserCommand);
        model.addAttribute("typeDef", typeService.getType("org.otherobjects.cms.model.User"));
        return "/otherobjects/templates/otherobjects/login/setup";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processForm(@ModelAttribute(value = "command") SetupAdminUserCommand setupAdminUserCommand, BindingResult bindingResult, SessionStatus status)
    {
//        User user = SecurityUtils.getCurrentUser();
//        if (!passwordUtils.isPasswordMatch(user, setupAdminUserCommand.getOldPassword()))
//        {
//            bindingResult.rejectValue("oldPassword", "wrong.password");
//        }
//        if (!setupAdminUserCommand.newPasswordsMatch())
//        {
//            bindingResult.rejectValue("newPassword", "new.password.error");
//        }
//        if (bindingResult.hasErrors())
//        {
//            return User.PATH_PREFIX + "/passwordChangeForm";
//        }
//        user.setPassword(passwordUtils.encodePassword(setupAdminUserCommand.getNewPassword()));
//        userDao.save(user);
//        status.setComplete();
//        return User.PATH_PREFIX + "/passwordChanged";
        return "/otherobjects/templates/otherobjects/login/setup";
    }

    /**
     * Returns setup page containing admin user form. If admin user is already created then
     * a warning should be shown instead.
     * @param request
     * @param response
     * @return
     * @throws ServletException
     * @throws IOException
     */
    /*
    @RequestMapping(value = "setup", method = RequestMethod.GET)
    public ModelAndView setup(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        UserDao dao = (UserDao) daoService.getDao(User.class);
        UserDetails adminUser = dao.loadUserByUsername("admin");

        ModelAndView mav = new ModelAndView("/otherobjects/templates/otherobjects/login/setup");
        mav.addObject("object", adminUser);
        mav.addObject("typeDef", typeService.getType("org.otherobjects.cms.model.User"));
        return mav;
    }
    */
/*
    @RequestMapping(value = "setup", method = RequestMethod.POST)
    public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        Enumeration parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements())
        {
            String name = (String) parameterNames.nextElement();
            logger.info("Parameter: {} = {}", name, request.getParameter(name));
        }

        ActionUtils actionUtils = new ActionUtils(request, response, null, null);

        UserDao userDao = (UserDao) daoService.getDao(User.class);
        User item = (User) userDao.loadUserByUsername("admin");
        TypeDef typeDef = typeService.getType(item.getClass());

        // Perform binding and validation
        BindingResult errors = bindService.bind(item, typeDef, request);
        Validator validator = validatorService.getValidator(item);
        if (validator != null)
            validator.validate(item, errors);
        else
            logger.warn("No validator for item of class " + item.getClass() + " found");

        if (!(errors != null && errors.hasErrors()))
        {
            // Save new object and redirect
            userDao.save(item, false);
            actionUtils.flashInfo("Admin user sucessfully configured.");
            actionUtils.redirectTo("/otherobjects/");
            return null;
        }
        else
        {
            // Return to form
            actionUtils.flashWarning("Your object could not be saved. See below for errors.");
            ModelAndView view = new ModelAndView("/otherobjects/templates/otherobjects/login/setup");
            view.addObject("typeDef", typeDef);
            view.addObject("object", item);
            view.addObject("org.springframework.validation.BindingResult.object", errors);
            return view;
        }
    }*/

    
    public class SetupAdminUserCommand
    {
        private User user;
        private String oldPassword;
        private String newPassword;
        private String newPasswordRepeated;

        public User getUser()
        {
            return user;
        }
        
        public void setUser(User user)
        {
            this.user = user;
        }
        public String getOldPassword()
        {
            return oldPassword;
        }


        public void setOldPassword(String oldPassword)
        {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword()
        {
            return newPassword;
        }

        public void setNewPassword(String newPassword)
        {
            this.newPassword = newPassword;
        }

        public String getNewPasswordRepeated()
        {
            return newPasswordRepeated;
        }

        public void setNewPasswordRepeated(String newPasswordRepeated)
        {
            this.newPasswordRepeated = newPasswordRepeated;
        }

        public boolean newPasswordsMatch()
        {
            try
            {
                return newPassword.equals(newPasswordRepeated);
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }
}
