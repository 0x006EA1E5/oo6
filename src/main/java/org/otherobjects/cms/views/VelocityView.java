package org.otherobjects.cms.views;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;

/**
 * An otherobjects specific subclass of Springs {@link org.springframework.web.servlet.view.velocity.VelocityView VelocityView} that
 * just before rendering stuffs another object into the context that allows template designers to find out more about the context at runtime.
 *  
 * @author joerg
 *
 */
public class VelocityView extends
		org.springframework.web.servlet.view.velocity.VelocityView {
	
	@Override
	protected void doRender(Context context, HttpServletResponse response)
			throws Exception {
		context.put("ctxInfo", new ContextInspector(context));
		super.doRender(context, response);
	}
	
	public class ContextInspector{
		private Context context;
		
		private ContextInspector(Context context)
		{
			this.context = context;
		}
		
		public String describeContext()
		{
			StringBuffer buf = new StringBuffer();
			buf.append("The current model context contains these objects: ");
			buf.append("<ul>");
			for(int i = 0; i < context.getKeys().length; i++)
			{
				buf.append("<li>");
				buf.append(context.getKeys()[i].toString());
				buf.append("</li>");
				
			}
			buf.append("</ul>");
			return buf.toString();
		}
		

	}
	
}
