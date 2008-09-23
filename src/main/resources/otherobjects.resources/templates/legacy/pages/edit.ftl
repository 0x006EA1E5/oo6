<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#macro renderProperty prop prefix>
<tr>
<td class="oo-label">${prop.label} <#if prop.required><span class="oo-required">*</span></#if></td>
<td>
<p>
	<#if prop.type == "date" >
  		<@spring.bind "${prefix}${prop.fieldName}"/>
  		<#assign status = springMacroRequestContext.getBindStatus("${prefix}${prop.fieldName}")>
  		<#if status.value?exists && status.value?is_date>
        	<#assign stringStatusValue=status.value?date?string("yyyy-MM-dd")>
    	<#else>
      	  	<#assign stringStatusValue=status.value?default("")>
   		</#if>
  		
  		<input type="text" class="text" id="${status.expression}" name="${status.expression}" value="${stringStatusValue}" value="">
	<#elseif prop.type == "component" >
  		
  		<table border="1">
		<#list prop.relatedTypeDef.properties as p2>
		<#if false>
		<@renderProperty p2 "${prefix}${prop.fieldName}."/>	
		<#else>
		<tr>
		<td>${p2.label}</td>
		<td>
		<input name="${prop.fieldName}.${p2.fieldName}"/>
		</td>
		</tr>
		</#if>	
		</#list>
		</table>
  		
	<#elseif prop.type == "text" >
  		<@spring.formTextarea "${prefix}${prop.fieldName}"  "class=\"textarea\""/>
  	<#else>
  		<@spring.formInput "${prefix}${prop.fieldName}" "class=\"text\"" />  	
  	</#if>
</p>
</td>
<td style="color:red; font-weight:normal!important;">
<p><@spring.showErrors "<br>"/></p>
</td>
</tr>
</#macro>

<#assign pageTitle = "Editing: ${oo.msg(object.label)}" />
<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">

<h2>
${pageTitle}
</h2>

<form action="${oo.url("/otherobjects/form")}" method="post">
<input type="hidden" name="editableId" value="${id}">
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
<@renderProperty prop "object." />
</#list>
</tbody>
</table>
</form>
</div>



<div class="oo-actions">
<h2>Actions</h2>
<ul>
<li><a href="javascript:document.forms[0].submit();">Save</a></li>
</ul>
</div>


<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />
