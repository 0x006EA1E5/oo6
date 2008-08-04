<#import "/oo.ftl" as oo>
<div class="title">Editing</div>
<form id="form" method="post" onsubmit="return ooSubmitForm('${blockReference.id}')">
<#if blockData??>
	<#-- Existing block -->
	<input type="hidden" class="hidden" name="editableId" value="${blockData.id}" />
	<#list blockData.typeDef.properties as prop>
	<div class="field">
	<#if prop.type == "boolean">
	<label>${prop.label}<label>
	<input type="radio" class="radio" name="${prop.name}" value="true" <#if blockData.get("${prop.name}")!falset>checked="checked" </#if>/> Yes
	<input type="radio" class="radio" name="${prop.name}" value="false" <#if !blockData.get("${prop.name}")!false>checked="checked" </#if>/> No
	</p>
	<#elseif prop.type == "text">
	<label>${prop.label}</label><textarea class="textarea" name="${prop.name}">${blockData.get("${prop.name}")!}</textarea>
	<#elseif prop.type == "component">
	COMPONENT
	<#elseif prop.type != "component">
	<label>${prop.label}</label><input type="text" class="text" name="${prop.name}" value="${blockData.get("${prop.name}")!}" />
	</#if>
	</div>
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

