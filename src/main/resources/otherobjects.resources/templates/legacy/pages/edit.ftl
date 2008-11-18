<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<#if object.id??>
  <#assign pageTitle = "Editing: ${oo.msg(object.ooLabel!)}" />
<#else>
  <#assign pageTitle = "Creating new ${oo.msg(typeDef.label)}" />
</#if>
<#include "/otherobjects/templates/legacy/blocks/header.ftl" />
<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">

<h2>
${pageTitle}
</h2>

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

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<li><a href="javascript:formSubmit();">Save</a></li>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />
