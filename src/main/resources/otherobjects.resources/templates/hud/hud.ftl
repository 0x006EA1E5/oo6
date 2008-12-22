<div class="oo-menu" style="display:none;" id="OoMenu">

</div>


<div class="oo-edit-zones"></div>

<div class="oo-toolbar oo-text-style">
	<div class="oo-toolbar-icons">
		
		<div class="oo-toolbar-left">
			<div class="oo-toolbar-icon oo-dashboard-icon "><div class="oo-small-badge"><div class="oo-small-badge-label">2</div></div></div>
			<div class="oo-toolbar-icon oo-new-icon"></div>
			<div class="oo-toolbar-icon oo-commerce-icon"></div>
			<div class="oo-toolbar-icon oo-users-icon"></div>
		</div>
		
		<div class="oo-toolbar-center">
			<input class="oo-search-input" type="text">
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