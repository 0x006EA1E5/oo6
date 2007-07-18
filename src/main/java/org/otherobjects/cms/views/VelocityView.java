package org.otherobjects.cms.views;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.context.Context;
import org.springframework.web.servlet.support.RequestContext;

public class VelocityView extends
		org.springframework.web.servlet.view.velocity.VelocityView {

	@Override
	protected void exposeHelpers(Context velocityContext,
			HttpServletRequest request) throws Exception {
		velocityContext.put("ctxTool", new RequestContext(request));
	}
	
}
