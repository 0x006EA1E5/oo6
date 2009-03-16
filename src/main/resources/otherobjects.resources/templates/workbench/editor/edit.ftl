<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<#if object.id??>
  <#assign pageTitle = "Editing: ${object.ooLabel!}" />
<#else>
  <#assign pageTitle = "Creating new ${typeDef.label!}" />
</#if>
<#include "/otherobjects/templates/workbench/shared/header.ftl" />
<#include "/otherobjects/templates/workbench/shared/nav-folders.ftl" />

<div id="ooContent">
<div class="oo-content">
<h2>${pageTitle}</h2>

<form id="oo-form" action="${oo.url(typeDef.adminControllerUrl + "/save")}" method="post" enctype="multipart/form-data">

<#if object.id??>
  <input type="hidden" name="_oo_id" value="${id}">
  <input type="hidden" name="_oo_type" value="${typeDef.name}">
<#else>
  <input type="hidden" name="_oo_type" value="${typeDef.name}">
  <input type="hidden" name="_oo_containerId" value="${containerId}">
</#if>

<@forms.renderForm typeDef />

</form>


<script type="text/javascript">
$('#oo-form').on('submit', function(element, e) {
	disableFormTemplates();
});

function formSubmit()
{
	disableFormTemplates();
	$('#oo-form').node.submit();
}

function disableFormTemplates()
{
	// Disable add the elements that are in the list element template
	$('.oo-list-template').descendants("input").set({disabled:true});
	$('.oo-list-template').descendants("textarea").set({disabled:true});
	$('.oo-list-template').descendants("select").set({disabled:true});
	$('.oo-list-template').descendants("checkbox").set({disabled:true});
	$('.oo-list-template').descendants("radio").set({disabled:true});
	$('.oo-list-template').descendants("button").set({disabled:true});
}
</script>


</div>
</div>

<div id="ooActions" class="oo-panel oo-text-style">
<a class="oo-item" href="javascript:formSubmit();"><div class="oo-arrow">Save</div></a>
</ul>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />
