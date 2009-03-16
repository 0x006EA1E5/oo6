package org.otherobjects.cms.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.Url;
import org.otherobjects.cms.mail.EmailAddress;
import org.otherobjects.cms.mail.FreemarkerMail;
import org.otherobjects.cms.mail.MailService;
import org.otherobjects.cms.security.PasswordService;
import org.otherobjects.cms.tools.FlashMessageTool;
import org.otherobjects.cms.util.FlashMessage;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
public class LoginController
{
    @Resource
    private PasswordService passwordService;

    @Resource
    private MailService mailService;

    /**
     * Shows the login form. If the user is already logged in then redirect them to the home page.
     */
    @RequestMapping(value = {"/login", "/login/", "/login/auth"}, method = RequestMethod.GET)
    public ModelAndView showAuth(HttpServletRequest request, HttpServletResponse response)
    {
        // FIXME If already logged in then redirect

        ModelAndView mav = new ModelAndView("/otherobjects/templates/workbench/user-management/login");
        AuthenticationException authenticationException = (AuthenticationException) WebUtils.getSessionAttribute(request, AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
        if (authenticationException != null)
        {
            FlashMessageTool flashMessageTool = new FlashMessageTool(request);
            flashMessageTool.flashMessage(FlashMessage.ERROR, "Login failed. " + authenticationException.getMessage());
            // Clear message so we do not see it again
            WebUtils.setSessionAttribute(request, AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY, null);
        }
        return mav;
    }

    /**
     * Post-process login request. Actual login is performed by Spring Security's <code>AuthenticationProcessingfilter</code>, this
     * just catches any errors and turns them into flash messages. 
     */
    @RequestMapping(value = "/login/auth", method = RequestMethod.POST)
    public ModelAndView processAuth(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mav = new ModelAndView("otherobjects/login/login");

        AuthenticationException authenticationException = (AuthenticationException) WebUtils.getSessionAttribute(request, AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);
        if (authenticationException != null)
        {
            FlashMessageTool flashMessageTool = new FlashMessageTool(request);
            flashMessageTool.flashMessage(FlashMessage.ERROR, "Login failed. " + authenticationException.getMessage());
            // Clear message so we do not see it again
            WebUtils.setSessionAttribute(request, AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY, null);
        }
        return mav;

        // REDIRECT!
    }

    /**
     * Handles request for a new password.
     * @throws Exception 
     */
    @RequestMapping(value = {"/login/request-password-change"}, method = RequestMethod.POST)
    public ModelAndView requestPasswordChange(@RequestParam("username") String username, HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // Send password change email
        String passwordChangeRequestCode = passwordService.generatePasswordChangeRequestCode(username);

        FreemarkerMail mail = new FreemarkerMail();
        mail.setFromAddress(new EmailAddress("rich@othermedia.com"));
        mail.addToRecipient(new EmailAddress("rich@othermedia.com"));
        mail.setBodyTemplateResourcePath("/otherobjects/templates/emails/password-change.ftl");
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("link", new Url("/otherobjects/password-change").getAbsoluteLink() + "?crc=" + passwordChangeRequestCode);
        mail.setModel(model);
        mail.setSubject("Password change request");
        //mail.setBody("http://localhost:8080/test/otherobjects/password-change?crc=" + passwordChangeRequestCode);
        mailService.send(mail);

        ModelAndView mav = new ModelAndView("/otherobjects/templates/workbench/user-management/login");
        FlashMessageTool flashMessageTool = new FlashMessageTool(request);
        flashMessageTool.flashMessage(FlashMessage.INFO, "You have been sent an email with password change instructions.");
        return mav;

        // REDIRECT!
    }
    /*
        @RequestMapping(value = {"/login/password-change"}, method = RequestMethod.GET)
        public ModelAndView showPasswordChange(@RequestParam("crc") String changeRequestCode, HttpServletRequest request, HttpServletResponse response)
        {
            // Verify change request code. Not essential here but good to provide feedback to user
            // early if the code is wrong (eg if the mail client broke the change password link)
            
            
            
            ModelAndView mav = new ModelAndView("otherobjects/login/change-password");
            mav.addObject("form", new PasswordChanger());
            mav.addObject("crc", changeRequestCode);
            return mav;
        }

        @RequestMapping(value = {"/login/password-change"}, method = RequestMethod.POST)
        public ModelAndView processPasswordChange(HttpServletRequest request, HttpServletResponse response) throws IOException
        {
            PasswordChanger pc = new PasswordChanger();
            ServletRequestDataBinder binder = new ServletRequestDataBinder(pc);
            binder.bind(request);

            FlashMessageTool flashMessageTool = new FlashMessageTool(request);

            // FIXME Handle errors?

            boolean success = passwordService.changePassword(pc);
            
            if(success)
            {
                flashMessageTool.flashMessage(FlashMessage.INFO, "Your password has been changed.");
                response.sendRedirect(new Url("otherobjects/login/auth").toString());
            }
            else
            {
                flashMessageTool.flashMessage(FlashMessage.INFO, "Your password has been changed.");
                response.sendRedirect(new Url("/otherobjects/login/password-change?crc=" + pc.getChangeRequestCode()).toString());
            }
            return null;
        }*/
}
