<#import "/oo.ftl" as oo />

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" class="oo">
<head>
<title>${pageTitle!} | ${oo.msg("site.name")} | OTHERobjects</title>
<@oo.css "/otherobjects/static/hud/workbench.css" />
<@oo.js "/otherobjects/static/legacy/workbench.js" />
<@oo.js "/otherobjects/static/legacy/yui-2.6.0-all-min.js" />
<@oo.js "/otherobjects/static/legacy/ojay-r307-all-min.js" />

<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/fonts/fonts-min.css" />
<link rel="stylesheet" type="text/css" href="http://yui.yahooapis.com/2.6.0/build/treeview/assets/skins/sam/treeview.css" />

<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/yahoo-dom-event/yahoo-dom-event.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.6.0/build/treeview/treeview-min.js"></script>
</head>
<body>

<@oo.showFlashMessages />

<div class="oo-toolbar oo-text-style">
	<div class="oo-toolbar-icons">
		
		<div class="oo-toolbar-left">
			<div class="oo-toolbar-icon oo-dashboard-icon "><div class="oo-small-badge"><div class="oo-small-badge-label">2</div></div></div>
			<div class="oo-toolbar-icon oo-new-icon"></div>
			<div class="oo-toolbar-icon oo-commerce-icon"></div>
			<div class="oo-toolbar-icon oo-users-icon"></div>
		</div>
		
		<div class="oo-toolbar-center">
			<form action="${oo.url("/otherobjects/workbench/search")}"><input class="oo-search-input" type="search" name="q" results="4" placeholder="Global Search" value="${RequestParameters.q!?html}"/></form>
		</div>
		
		<div class="oo-toolbar-right">
			<div class="oo-toolbar-radio">
				<div class="oo-toolbar-icon oo-edit-icon oo-icon-selected"></div>
				<div class="oo-toolbar-icon oo-stats-icon"></div>
				<div class="oo-toolbar-icon oo-design-icon"></div>
				<div class="oo-toolbar-icon oo-debug-icon"></div>
			</div>
			<div class="oo-toolbar-icon oo-logout-icon"></div>
		</div>
		
	</div>
</div>
