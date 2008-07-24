package org.otherobjects.cms.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandler;
import org.mortbay.jetty.handler.ContextHandler.SContext;
import org.otherobjects.cms.security.PasswordChanger;
import org.otherobjects.cms.security.PasswordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@Controller
@RequestMapping("/password-change")
@SessionAttributes("passwordChanger")
public class PasswordChangeController
{
    @Autowired
    private PasswordService passwordService;

    @RequestMapping(method = RequestMethod.GET)
    public String setupForm(Model model, HttpServletRequest request) throws IOException
    {
        //        System.exit(0);
        System.in.read();
        System.exit(0);

        PasswordChanger passwordChanger = new PasswordChanger();

        // Validate change request code
        String crc = request.getParameter("crc");
        if (passwordService.validateChangeRequestCode(crc))
        {
            // Pre-fill in form since valid
            model.addAttribute("crcFail", true);
            passwordChanger.setChangeRequestCode(crc);
        }
        else
        {
            // Could not detect valid CRC
            model.addAttribute("crcFail", false);
        }
        model.addAttribute("passwordChanger", passwordChanger);
        return "otherobjects/login/change-password";
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute("passwordChanger")
    PasswordChanger passwordChanger, BindingResult result, SessionStatus status)
    {

        new PasswordChangerValidator().validate(passwordChanger, result);

        boolean success = passwordService.changePassword(passwordChanger);

        if (result.hasErrors())
        {
            return "otherobjects/login/change-password";
        }
        else
        {
            //this.clinic.storePet(PasswordChanger);
            status.setComplete();
            return "redirect:/otherobjects/login/auth";
        }
    }

}
