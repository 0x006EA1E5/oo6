package org.otherobjects.cms.events;

import org.otherobjects.cms.model.CmsNode;
import org.springframework.context.ApplicationEvent;

public class PublishEvent extends ApplicationEvent {
	
	private static final long serialVersionUID = -6396327287329531404L;
	
	private CmsNode cmsNode;
	
	public PublishEvent(Object source, CmsNode cmsNode) {
		super(source);
		this.cmsNode = cmsNode;
	}

	public CmsNode getCmsNode() {
		return cmsNode;
	}
}
