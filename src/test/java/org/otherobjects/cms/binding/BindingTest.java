package org.otherobjects.cms.binding;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import junit.framework.TestCase;

import org.otherobjects.cms.model.Comment;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.support.BindStatus;
import org.springframework.web.servlet.support.RequestContext;

public class BindingTest extends TestCase
{
    private static final String COMMENT = "test";

    public void testBinding()
    {
        Comment c = new Comment();
        c.setComment(COMMENT);
        
        ApplicationContext ac = new ApplicationContextMock();
        
        
        Map model = new ModelMap();
        
        model.put("comment", c);
        
        HttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute(RequestContext.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);
        RequestContext rc = new RequestContext(request, model);
        BindStatus bindStatus = rc.getBindStatus("comment.comment");
        
        assertEquals(COMMENT, bindStatus.getValue());
        
    }

}
