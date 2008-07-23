package org.otherobjects.cms.controllers;

import org.otherobjects.cms.security.PasswordChanger;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;

@RequestMapping("/login/change")
public class PasswordChangeController extends SimpleFormController
{

    public PasswordChangeController()
    {
        setSuccessView("redirect:otherobjects/login/auth");
        setFormView("otherobjects/login/password-change");
        setCommandClass(PasswordChanger.class);
        
        /*
        <property name="sessionForm"><value>true</value></property>
        <property name="commandName"><value>credentials</value></property>
        <property name="commandClass"><value>test.business.Credentials</value></property>
        <property name="validator"><ref bean="logonValidator"/></property>
        <property name="formView"><value>logon.jsp</value></property>
        <property name="successView"><value>sucess.jsp</value></property>
        */
    }
 
    
    @Override
    protected ModelAndView onSubmit(Object command, BindException errors) throws Exception
    {
        // TODO Auto-generated method stub
        return super.onSubmit(command, errors);
    }
    
    
}
