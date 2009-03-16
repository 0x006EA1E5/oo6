<div class="oo-menu" style="display:none;" id="OoMenu"></div>

<div class="oo-edit-zones"></div>
<div class="oo-design-tools">
<div id="OoDesignTrash" class="oo-design-trash oo-panel">Trash</div>
</div>

<#include "/otherobjects/templates/hud/toolbar.ftl" />

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