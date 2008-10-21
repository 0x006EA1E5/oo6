<#--
Form Macros
-->

<#macro renderForm typeDef>
<script>
function addToList()
{
	// Get field
	var f = $('.oo-list-empty-field').at(0);
	
	// Insert copy
	n = $(f.node.cloneNode(true));
	console.log(n);
	n.setStyle({display:'block'});
	n.removeClass("oo-list-template");
	f.insert(n.node, 'after');
}
function removeFromList()
{
	// Get field
	var all = $('.oo-list-last-field');
	var f = all.at(all.length-1);
	// Insert copy
	f.parents().at(0).node.removeChild(f.node);
}
</script>
<@renderType typeDef "object." />
</#macro>

<#--
Renders the form parts for a typeDef. Used for main form and also for components.
-->
<#macro renderType typeDef prefix empty=false>
<table class="oo-edit">
<thead>
<tr>
<th>Field</th>
<th>Value</th>
<th>Help</th>
</tr>
</thead>
<tbody>
<#list typeDef.properties as prop>
<@renderProperty prop prefix empty />
</#list>
</tbody>
</table>
</#macro>

<#--
Renders a property for the form.
-->
<#macro renderProperty prop prefix empty>
<tr>
<td class="oo-label">${prop.label} <#if prop.required><span class="oo-required">*</span></#if></td>
<td>
<p>
	<@renderField prop prop.defaultFieldType "${prefix}${prop.fieldName}" empty /> 
</p>
</td>
<td style="color:red; font-weight:normal!important;">
<#if ooStatus??>
<p><@showErrors ooStatus "<br>"/></p>
</#if>
</td>
</tr>
</#macro>

<#--
Renders a field inputter by choosing the correct inputter renderer. Also handles lists and components.
-->
<#macro renderField prop type path empty=false>
	<#if type == "list" >
  		<@bind path />
  		<#assign listOoStatus = ooStatus />
  		<#assign nextIndex = 0 />
  		<p><a href="javascript:addToList();">add</a> | <a href="javascript:removeFromList();">remove</a></p>
		<#if listOoStatus.actualValue?? && listOoStatus.actualValue?is_enumerable>
			<#list listOoStatus.actualValue as item>
	  			<p class="oo-list-last-field"><@renderField prop prop.collectionElementType "${path}[${item_index}]" /></p>
	  		</#list>
	  		<#assign nextIndex = listOoStatus.actualValue?size />
  		</#if>
  		<div style="display:none;" class="oo-list-empty-field oo-list-template">
  		<@renderField prop prop.collectionElementType "${path}[${nextIndex}]" true/>
  		</div>
  	<#elseif type == "component" >	
  		<#if !empty>
	  		<@bind path />
		</#if>
  		<#if !empty && ooStatus.value??>
			<@renderType prop.relatedTypeDef "${path}." false/>
		<#else>
			<@renderType prop.relatedTypeDef "${path}." true/>
		</#if>
	<#elseif type == "date" >
  		<@formDate "${path}" />
  	<#elseif type == "text" >
  		<@formTextarea "${path}" "" empty/>
	<#elseif type == "boolean" >
  		<@formCheckbox "${path}" "" empty/>
	<#elseif type == "string" >
  		<@formInput "${path}" "" "text" empty />  		
	<#elseif type == "none" >
  		-  		
	<#else>
  		<@formInput "${path}" "" "text" empty />
  	</#if>
</#macro>


<#--
Local copy of Spring's bind macro which will correctly set status variables
since it is in the same namespace.

TODO Can we make an empty-compatible version of this?
-->
<#macro bind path>
    <#assign ooStatus = springMacroRequestContext.getBindStatus(path)>
    <#if ooStatus.value?exists && ooStatus.value?is_boolean>
	    <#assign stringStatusValue=ooStatus.value?string>
	<#else>
    	<#assign stringStatusValue=ooStatus.value?default("")>
	</#if>
</#macro>


<#--
Renders a checkbox for a boolean property.

Note: when values come back they may be strings (and not typed)
-->
<#macro formInput path attributes="" fieldType="text" empty=false>
	<#if empty>
		<#assign expression = path?substring(7) />
  		<input type="${fieldType}" name="${expression}" id="${expression}" class="${fieldType}" ${attributes}
	    <@spring.closeTag/>
	<#else>
		<@bind path />
		<input type="${fieldType}" name="${ooStatus.expression}" id="${ooStatus.expression}" class="${fieldType}" value="<#if fieldType!="password">${stringStatusValue}</#if>" ${attributes}
    	<@spring.closeTag/>
	</#if>  	
</#macro>

<#--
Renders a textarea field.
-->
<#macro formTextarea path attributes="" empty=false>
	<#if empty>
		<#assign expression = path?substring(7) />
  		<textarea name="${expression}" id="${expression}" class="textarea" ${attributes}></textarea>
	<#else>
		<@bind path />
  		<textarea name="${ooStatus.expression}" id="${ooStatus.expression}" class="textarea" ${attributes}>${stringStatusValue}</textarea>
	</#if>  	
</#macro>

<#--
Renders a checkbox for a boolean property.

Note: when values come back they may be strings (and not typed)
-->
<#macro formCheckbox path attributes="" empty=false>
    <#if empty>
		<#assign expression = path?substring(7) />
	    <input type="checkbox" class="checkbox" id="${expression}" name="${expression}" value="true"
	    <@spring.closeTag/>
	    <input type="hidden" name="_${expression}" value="false" <@spring.closeTag/>
	<#else>
		<@bind path />
	    <input type="checkbox" class="checkbox" id="${ooStatus.expression}" name="${ooStatus.expression}" value="true"
	    <#if ooStatus.value?? && ((ooStatus.value?is_boolean && ooStatus.value) || (!ooStatus.value?is_boolean && ooStatus.value == "true"))> checked="checked" </#if> ${attributes}
	    <@spring.closeTag/>
	    <#-- Hidden field to set value to false when checkbox not checked -->
	    <input type="hidden" name="_${ooStatus.expression}" value="false" <@spring.closeTag/>
    </#if>
</#macro>

<#--
Renders a textbox for a date property.
-->
<#macro formDate path attributes="" empty=false>
	<#if empty>
		xx
	<#else>
		<@bind path />
		<#assign ooStatus = springMacroRequestContext.getBindStatus("${path}")>
		<#if ooStatus.value?? && ooStatus.value?is_date>
	    	<#assign stringStatusValue=ooStatus.value?date?string("yyyy-MM-dd")>
		<#else>
	  	  	<#assign stringStatusValue=ooStatus.value?default("")>
		</#if>
		<input type="text" class="text" id="${ooStatus.expression}" name="${ooStatus.expression}" value="${stringStatusValue}">
		</#if>
</#macro>	

<#--
Renders form validation error messages.
-->
<#macro showErrors status separator classOrStyle="">
    <#list status.errorMessages as error>
    <#if classOrStyle == "">
        <b>${error}</b>
    <#else>
        <#if classOrStyle?index_of(":") == -1><#assign attr="class"><#else><#assign attr="style"></#if>
        <span ${attr}="${classOrStyle}">${error}</span>
    </#if>
    <#if error_has_next>${separator}</#if>
    </#list>
</#macro>
 