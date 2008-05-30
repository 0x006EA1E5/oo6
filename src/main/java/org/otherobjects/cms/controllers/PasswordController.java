package org.otherobjects.cms.controllers;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.otherobjects.cms.security.PasswordChanger;
import org.otherobjects.cms.security.PasswordService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/password/**")
public class PasswordController
{
    @Resource
    private PasswordService passwordService;

    @RequestMapping(value = {"new"}, method = RequestMethod.GET)
    public ModelAndView showRequestNew(HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mav = new ModelAndView("otherobjects/password/requestNew");
        return mav;
    }

    @RequestMapping(value = {"new"}, method = RequestMethod.POST)
    public ModelAndView doRequestNew(@RequestParam("username")
    String username, HttpServletRequest request, HttpServletResponse response)
    {

        try
        {
            String code = passwordService.getPasswordChangeRequestCode(username);
            System.out.println(code);

        }
        catch (Exception e)
        {
            //noop
        }
        ModelAndView mav = new ModelAndView("otherobjects/password/requestReceived");
        return mav;
    }

    @RequestMapping(value = {"change"}, method = RequestMethod.GET)
    public ModelAndView showChangePassword(@RequestParam("crc")
    String changeRequestCode, HttpServletRequest request, HttpServletResponse response)
    {
        ModelAndView mav = new ModelAndView("otherobjects/password/changePassword");
        mav.addObject("crc", changeRequestCode);
        return mav;
    }

    @RequestMapping(value = {"change"}, method = RequestMethod.POST)
    public ModelAndView doChangePassword(HttpServletRequest request, HttpServletResponse response)
    {
        PasswordChanger pc = new PasswordChanger();
        ServletRequestDataBinder binder = new ServletRequestDataBinder(pc);
        binder.bind(request);

        ModelAndView mav = new ModelAndView("otherobjects/password/changed");

        mav.addObject("success", passwordService.changePassword(pc));

        return mav;
    }
}
