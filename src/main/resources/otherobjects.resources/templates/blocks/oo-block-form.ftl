<form id="form" method="post" action="/otherobjects/form/" onsubmit="return ooSubmitForm('${blockReference.id}')">
<#if blockData??>
	<#-- Existing block -->
	<input type="hidden" class="hidden" name="editableId" value="${blockData.id}" />
	<#list blockData.typeDef.properties as prop>
	
	<#if prop.type == "boolean">
	<p>${prop.label} ${blockData.get("${prop.name}")?has_content?string("true", "false")} <br/>
	<input type="radio" class="radio" name="${prop.name}" value="true" <#if blockData.get("${prop.name}")?has_content>checked="checked" </#if>/> Yes
	<input type="radio" class="radio" name="${prop.name}" value="false" <#if !blockData.get("${prop.name}")?has_content>checked="checked" </#if>/> No
	</p>
	<#elseif prop.type == "text">
	<p>${prop.label}<br/><textarea class="textarea" name="${prop.name}">${blockData.get("${prop.name}")!}</textarea></p>
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
	<#if prop.name == "code">
	<#elseif prop.type == "boolean">
	<p>${prop.label} <br/>
	<input type="radio" class="radio" name="${prop.name}" value="true" /> Yes
	<input type="radio" class="radio" name="${prop.name}" value="false" checked="checked" /> No
	</p>
	<#elseif prop.type == "text">
	<p>${prop.label}<br/><textarea class="textarea" name="${prop.name}"></textarea></p>
	<#elseif prop.type != "component">
	<p>${prop.label}<br/><input type="text" class="text" name="${prop.name}" value="" /> </p>
	</#if>
	</#list>
</#if> 
<p><input type="submit" class="submit" value="Save" /> </p>
</form>

<script>
ooSetFormFocus();
</script>

