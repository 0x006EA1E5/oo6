<div class="oo-toolbar oo-text-style">
	<div class="oo-toolbar-icons">
		<div class="oo-toolbar-left">
			<div class="oo-toolbar-radio">
				<div class="oo-toolbar-icon oo-new-icon oo-icon-selected"  onclick="window.location='${oo.url("/")}'"></div>
				<div class="oo-toolbar-icon oo-edit-icon" onclick="toggleEditMode(this);"></div>
				<#--<div class="oo-toolbar-icon oo-stats-icon"></div>-->
				<div class="oo-toolbar-icon oo-design-icon" onclick="toggleDesignMode(this);"></div>
				<div class="oo-toolbar-icon oo-debug-icon" onclick="window.location='${oo.url("/otherobjects/debug")}'"></div>
			</div>
		
		</div>
		
		<div class="oo-toolbar-center">
			<form action="${oo.url("/otherobjects/workbench/search")}"><input class="oo-search-input" type="search" name="q" results="4" placeholder="Global Search" value="${RequestParameters.q!?html}"/></form>
		</div>
		
		<div class="oo-toolbar-right">
			<div class="oo-toolbar-icon oo-logout-icon" onclick="window.location='${oo.url("/otherobjects/logout")}'"></div>
			<div class="oo-toolbar-icon oo-users-icon"></div>
			<div class="oo-toolbar-icon oo-new-icon"></div>
			<div class="oo-toolbar-icon oo-dashboard-icon" onclick="window.location='${oo.url("/otherobjects/")}'"><div class="oo-small-badge"><div class="oo-small-badge-label">2</div></div></div>
			<#-- <div class="oo-toolbar-icon oo-commerce-icon"></div> -->
		</div>
		
	</div>
</div>