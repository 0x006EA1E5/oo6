<form id="form" method="post" action="/otherobjects/form/" onsubmit="return ooSubmitForm('${blockReference.id}')">
<#if blockData??>
	<#-- Existing block -->
	<input type="hidden" class="hidden" name="editableId" value="${blockData.id}" />
	<#list blockData.typeDef.properties as prop>
	
	<#if prop.type == "boolean">
	<p>${prop.label}<br/><input type="text" class="text" name="${prop.name}" value="${blockData.get("${prop.name}")?string("true", "false")}" /> </p>
	<#elseif prop.type != "component">
	<p>${prop.label}<br/><input type="text" class="text" name="${prop.name}" value="${blockData.get("${prop.name}")!}" /> </p>
	</#if>
	</#list>
<#else> 
	<#-- New block -->
	<input type="hidden" class="hidden" name="_oo_containerId" value="${location}" />
	<input type="hidden" class="hidden" name="_oo_type" value="${typeDef.name}" />
	<input type="hidden" class="hidden" name="code" value="blockData" />
	<#list typeDef.properties as prop>
	<#if prop.name != "code">
	<p>${prop.label}<br/><input type="text" class="text" name="${prop.name}" value="" /> </p>
	</#if>
	</#list>
</#if> 
<p><input type="submit" class="submit" value="Save" /> </p>
</form>

<script>
ooSetFormFocus();
</script>


