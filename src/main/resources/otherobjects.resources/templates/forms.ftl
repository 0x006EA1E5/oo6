<#--
Form Macros
-->

<#macro renderForm typeDef empty=false>
<@renderType typeDef "object." empty/>
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
<td class="oo-label"><p>${prop.label} <#if prop.required><span class="oo-required">*</span></#if></p></td>
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
  		<script>var nextIndex = ${nextIndex};</script>
  	<#elseif type == "component" >	
  		<#if !empty>
	  		<@bind path />
		</#if>
  		<#if !empty && ooStatus.value??>
			<@renderType prop.relatedTypeDef "${path}." false/>
		<#else>
			<@renderType prop.relatedTypeDef "${path}." true/>
		</#if>
	<#elseif type == "reference" >
		<#if prop.relatedTypeDef.store =="jackrabbit">
			<#assign options = daoTool.get(prop.relatedType).getAllByType(prop.relatedType) />
		<#else>
			<#assign options = daoTool.get(prop.relatedType).all />
		</#if>
  		<@formSingleSelect "${path}" options "" empty/>
	<#elseif type == "date" >
  		<@formDate "${path}" "" empty/>
	<#elseif type == "time" >
  		<@formTime "${path}" "" empty/>
	<#elseif type == "timestamp" >
  		<@formTimestamp "${path}" "" empty/>
  	<#elseif type == "text" >
  		<@formTextarea "${path}" "" empty/>
	<#elseif type == "boolean" >
  		<@formCheckbox "${path}" "" empty/>
	<#elseif type == "transient" >
		<@formFile "${path}" "" empty/>
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
Renders a standard input field with cusomisable type.
-->
<#macro formInput path attributes="" fieldType="text" empty=false>
	<#if empty>
		<#assign expression = path?substring(7) />
  		<input type="${fieldType}" name="${expression}" id="${expression}" class="${fieldType}" ${attributes}
	    <@spring.closeTag/>
	<#else>
		<@bind path />
		<input type="${fieldType}" name="${ooStatus.expression}" id="${ooStatus.expression}" class="${fieldType}<#if ooStatus.errorMessages?size &gt; 0> errors</#if>" value="<#if fieldType!="password">${stringStatusValue}</#if>" ${attributes}
    	<@spring.closeTag/>
	</#if>  	
</#macro>

<#--
Renders a password field.
-->
<#macro formPassword path attributes="" fieldType="text" empty=false>
	<@formInput path attributes "password" empty/>
</#macro>

<#--
Renders a file field.
-->
<#macro formFile path attributes="" fieldType="text" empty=false>
	<@formInput path attributes "file" empty/>
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
  		<textarea name="${ooStatus.expression}" id="${ooStatus.expression}" class="textarea<#if ooStatus.errorMessages?size &gt; 0> errors</#if>" ${attributes}>${stringStatusValue}</textarea>
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
		<@formInput path attributes "text" empty/>
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
	<input type="button" value="Today" onclick="document.getElementById('${ooStatus.expression}').value=formatDate(new Date());"/>
</#macro>	

<#--
Renders a textbox for a date property.
-->
<#macro formTime path attributes="" empty=false>
	<#if empty>
		<@formInput path attributes "text" empty/>
	<#else>
		<@bind path />
		<#assign ooStatus = springMacroRequestContext.getBindStatus("${path}")>
		<#if ooStatus.value?? && ooStatus.value?is_date>
	    	<#assign stringStatusValue=ooStatus.value?time?string("HH:mm:ss")>
		<#else>
	  	  	<#assign stringStatusValue=ooStatus.value?default("")>
		</#if>
		<input type="text" class="text" id="${ooStatus.expression}" name="${ooStatus.expression}" value="${stringStatusValue}">
	</#if>
	<input type="button" value="Now" onclick="document.getElementById('${ooStatus.expression}').value=formatTime(new Date());"/>
</#macro>

<#--
Renders a textbox for a date property.
-->
<#macro formTimestamp path attributes="" empty=false>
	<#if empty>
		<@formInput path attributes "text" empty/>
		<input type="button" value="Now" onclick="document.getElementById('${path}').value=formatTimestamp(new Date());"/>
	<#else>
		<@bind path />
		<#assign ooStatus = springMacroRequestContext.getBindStatus("${path}")>
		<#if ooStatus.value?? && ooStatus.value?is_date>
	    	<#assign stringStatusValue=ooStatus.value?datetime?string("yyyy-MM-dd HH:mm:ss")>
		<#else>
	  	  	<#assign stringStatusValue=ooStatus.value?default("")>
		</#if>
		<input type="text" class="text" id="${ooStatus.expression}" name="${ooStatus.expression}" value="${stringStatusValue}">
		<input type="button" value="Now" onclick="document.getElementById('${ooStatus.expression}').value=formatTimestamp(new Date());"/>
	</#if>
</#macro>

<#macro formSingleSelect path options attributes="" empty=false>
    <#if empty>
    	<#assign expression = path?substring(7) />
  		
	    <select id="${expression}" name="${expression}" ${attributes}>
	        <option value="">-</option>
	    	<#list options as option>
	        <option value="${option.editableId?html}">${option.ooLabel?html}</option>
	        </#list>
	    </select>
	<#else>
		<@bind path/>
	    <select id="${ooStatus.expression}" name="${ooStatus.expression}" ${attributes}>
	        <option value="">-</option>
	    	<#list options as option>
	        <option value="${option.editableId?html}"<@checkSelected option.editableId/>>${option.ooLabel?html}</option>
	        </#list>
	    </select>
    </#if>
</#macro>

<#--
 * checkSelected
 *
 * FIXME Need to support non-nodes.
 -->
<#macro checkSelected value>
    <#if (ooStatus.actualValue.editableId)! == value>selected="selected"</#if>
</#macro>

<#--
Renders form validation error messages.
-->
<#macro showErrors separator classOrStyle="">
    <#list ooStatus.errorMessages as error>
    <#if classOrStyle == "">
        <b>${error}</b>
    <#else>
        <#if classOrStyle?index_of(":") == -1><#assign attr="class"><#else><#assign attr="style"></#if>
        <span ${attr}="${classOrStyle}">${error}</span>
    </#if>
    <#if error_has_next>${separator}</#if>
    </#list>
</#macro>
 