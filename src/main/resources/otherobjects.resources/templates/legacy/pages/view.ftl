<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#assign object = daoService.getDao("baseNode").get(id) />

<#macro renderProperty prop object>
<tr>
<td class="oo-label">${prop.label}</td>
<td class="oo-field-none">
	<#if object.getPropertyValue(prop.propertyPath)?? >
		<#if prop.type == "component" >
			${object.getPropertyValue(prop.propertyPath)!}
		<#elseif prop.type == "date" >
			
			${object.getPropertyValue(prop.propertyPath)?string("d MMM yyyy")}
		<#elseif prop.type == "boolean" >
			${object.getPropertyValue(prop.propertyPath)?string("Yes", "No")}	
	  	<#else>
			${object.getPropertyValue(prop.propertyPath)!}
	  	</#if>
  	<#else>
  		<span style="color:#888">No value</span>
  	</#if>  	
</td>
</tr>
</#macro>

<#assign pageTitle = "Viewing: ${oo.msg(object.label!)}" />
<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>
<table class="oo-listing">
<thead>
<tr>
<th>Field</th>
<th>Value</th>
</tr>
</thead>
<tbody>
<#list object.typeDef.properties as prop>
<@renderProperty prop object />
</#list>
</tbody>
</table>
</div>

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<li><a href="${oo.url('/otherobjects/workbench/edit/${object.id}')}">Edit</a></li>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/publish/${object.id}')}">Publish</a></li>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/delete/${object.id}')}">Delete</a></li>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

