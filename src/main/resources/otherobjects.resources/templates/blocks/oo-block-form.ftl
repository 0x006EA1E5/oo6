<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<#if resourceObjectForm>
<form id="form" method="post" action="${oo.url("/otherobjects/form")}">
<#else>
<form id="form" method="post" onsubmit="return ooSubmitForm('${blockReference.id}')">
</#if>

<#if blockData??>
	<#-- Existing block -->
	<input type="hidden" class="hidden" name="_oo_id" value="${blockData.id}" />
	<@forms.renderForm blockData.typeDef/>
<#else> 
	<#-- New block --> 
	<input type="hidden" class="hidden" name="_oo_containerId" value="${location}" />
	<input type="hidden" class="hidden" name="_oo_type" value="${typeDef.name}" />
	<!-- <input type="text" class="hidden" name="code" value="blockData" /> -->
	<@forms.renderForm typeDef true/>
</#if> 
<p><input type="submit" class="submit" value="Save" /> </p>
</form>



<script type="text/javascript">

ooSetFormFocus();

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

