<Ûimport "spring.ftl" as spring/>

<#--
Supported fields:

* TODO component
* TODO reference
* TODO list

* boolean -> checkbox
* text -> textarea
* default -> text

-->

<#macro renderField prop value="">
<div class="field">
<#if prop.type == ".boolean">
	<label>${prop.label}<label>
	<input type="radio" class="radio" name="${prop.name}" value="true" <#if value!false>checked="checked" </#if>/> Yes
	<input type="radio" class="radio" name="${prop.name}" value="false" <#if !value!false>checked="checked" </#if>/> No
<#elseif prop.type == "text">
	<label>${prop.label}</label><textarea class="textarea" name="${prop.name}">${value!}</textarea>
<#elseif prop.type == "component">
	<@renderFormPart prop.relatedTypeDef />
<#else>
	<label>${prop.label}</label> 
	<input type="text" class="text" name="${prop.name}" value="${value!}" />
</#if>
</div>
</#macro>

<#macro renderFormPart typeDef data>
<#list typeDef.properties as prop>
<@renderField prop data.get(prop.name) />
</#list>
</#macro>


<#import "/oo.ftl" as oo>
<div class="title">Editing</div>
<#if resourceObjectForm>
<form id="form" method="post" action="${oo.url("/otherobjects/form")}">
<#else>
<form id="form" method="post" onsubmit="return ooSubmitForm('${blockReference.id}')">
</#if>

<#if blockData??>
	<#-- Existing block -->
	<input type="hidden" class="hidden" name="editableId" value="${blockData.id}" />
	<@renderFormPart blockData.typeDef blockData />
<#else> 
	<#-- New block --> New
	<input type="hidden" class="hidden" name="_oo_containerId" value="${location}" />
	<input type="hidden" class="hidden" name="_oo_type" value="${typeDef.name}" />
	<!-- <input type="text" class="hidden" name="code" value="blockData" /> -->
	<@renderFormPart typeDef />
</#if> 
<p><input type="submit" class="submit" value="Save" /> </p>
</form>

<script>
ooSetFormFocus();
</script>

