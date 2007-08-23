package org.otherobjects.cms.views;

import org.apache.velocity.context.Context;

/**
 * Simple interface to be used in a view class' e.g. doRender method to populate the context with a standard set of tools.
 * @author joerg
 *
 */
public interface ViewToolsService {
	public void populateContext(Context context) throws Exception;
}
