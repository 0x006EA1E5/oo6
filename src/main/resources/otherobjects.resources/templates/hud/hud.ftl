<@panel "oo-panel oo-toolbar">
	<div class="oo-toolbar-item oo-dashboard"></div>
	<div class="oo-toolbar-item oo-new"></div>
	<div class="oo-toolbar-item oo-user-management"></div>
	<div class="oo-toolbar-item oo-more"></div>
	<div class="oo-search">
		<input type="text" class="oo-search-input" value="Search OTHERobjects">
	</div>
	<div class="oo-modes">
		<div class="oo-mode oo-mode-edit"></div>
		<div class="oo-mode oo-mode-design"></div>
		<div class="oo-mode oo-mode-debug"></div>
	</div>
	<div class="oo-toolbar-item oo-log-out"></div>
</@panel>

<#macro panel classes>
<#if false>
<div class="panel ${classes}"><div class="panel-c"><div class="panel-t"></div>
	<#nested>
</div><div class="panel-b"><div></div></div></div>
<#else>
<div class="panel ${classes}">
	<#nested>
</div>
</#if>
</#macro>

