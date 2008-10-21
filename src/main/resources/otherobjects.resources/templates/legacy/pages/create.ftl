<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<#assign pageTitle = "Creating new ${oo.msg(typeDef.label)}" />
<#include "/otherobjects/templates/legacy/blocks/header.ftl" />
<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">

<h2>
${pageTitle}
</h2>


<form id="oo-form" action="${oo.url("/otherobjects/form")}" method="post">
<input type="hidden" name="_oo_type" value="${typeDef.name}">
<input type="hidden" name="_oo_containerId" value="${RequestParameters.container}">

<@forms.renderForm typeDef />

<input type="submit" />

</form>

<script type="text/javascript">
$('#oo-form').on('submit', function(element, e) {
	// Disable add the elements that are in the list element template
	$('.oo-list-template').descendants("INPUT").set({disabled:true});
});

function formSubmit()
{
	$('.oo-list-template').descendants("INPUT").set({disabled:true});
	$('#oo-form').node.submit();
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
