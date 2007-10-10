package org.otherobjects.cms.site;

import java.util.List;

import org.otherobjects.cms.workbench.NavigatorService;

public class SiteNavigatorServiceImpl implements SiteNavigatorService {

	private NavigatorService navigatorService;
	
	
	public void setNavigatorService(NavigatorService navigatorService) {
		this.navigatorService = navigatorService;
	}


	public List<SiteItem> getSiteItems(SiteItem siteItem) {
		return null;
	}


	public List<SiteItem> getSiteItems(SiteItem siteItem, int minLevel) {
		// TODO Auto-generated method stub
		return null;
	}

}
