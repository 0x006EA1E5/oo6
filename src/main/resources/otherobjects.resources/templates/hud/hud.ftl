<div class="oo-menu" style="display:none;" id="OoMenu"></div>

<div class="oo-edit-zones"></div>

<div class="oo-toolbar oo-text-style">
	<div class="oo-toolbar-icons">
		
		<div class="oo-toolbar-left">
			<div class="oo-toolbar-icon oo-dashboard-icon " onclick="window.location='/otherobjects/';"><div class="oo-small-badge"><div class="oo-small-badge-label">2</div></div></div>
		</div>
		
		<div class="oo-toolbar-center">
			<form action="${oo.url("/otherobjects/workbench/search")}"><input class="oo-search-input" type="search" name="q" results="4" placeholder="Global Search" value="${RequestParameters.q!?html}"/></form>
		</div>
		
		<div class="oo-toolbar-right">
			<div class="oo-toolbar-radio">
				<div id="ooToolbarIconEditMode" class="oo-toolbar-icon oo-edit-icon oo-icon-selected"></div>
				<div class="oo-toolbar-icon oo-stats-icon"></div>
				<div id="ooToolbarIconDesignMode" class="oo-toolbar-icon oo-design-icon" onclick="toggleDesignMode()"></div>
				<div class="oo-toolbar-icon oo-debug-icon"></div>
			</div>
			<div class="oo-toolbar-icon oo-logout-icon" onclick="window.location='/otherobjects/logout.html';"></div>
		</div>
		
	</div>
</div>

<script>
var resourceObjectId = '${(resourceObject.id)!}';
var ooTemplateId = '${(ooTemplate.id)!}';
var ooBaseUrl = '${oo.url("/")}';
var ooBlockInEdit = "";
</script>

<script>
Ojay('.oo-logout-icon').on('click', function() {
	location.href='${oo.url("/otherobjects/logout.html")}';
}); 
</script>