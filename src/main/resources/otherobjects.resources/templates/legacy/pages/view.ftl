<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#assign object = item />

<#macro renderProperty prop object>
<tr>
<td class="oo-label">${prop.label}</td>
<td class="oo-field-none">
	<#if beanTool.getPropertyValue(object, prop.propertyPath)?? >
		<#if prop.type == "component" >
			${beanTool.getPropertyValue(object, prop.propertyPath)!}
		<#elseif prop.type == "date" >
			${beanTool.getPropertyValue(object, prop.propertyPath)?date?string("d MMM yyyy")}
		<#elseif prop.type == "time" >
			${beanTool.getPropertyValue(object, prop.propertyPath)?time?string("HH:mm")}
		<#elseif prop.type == "timestamp" >
			${beanTool.getPropertyValue(object, prop.propertyPath)?datetime?string("HH:mm 'on' d MMM yyyy")}
		<#elseif prop.type == "boolean" >
			${beanTool.getPropertyValue(object, prop.propertyPath)?string("Yes", "No")}	
		<#elseif prop.type == "list" >
			<ul>
			<#list beanTool.getPropertyValue(object, prop.propertyPath) as item>
			<li>${item}</li>
			</#list>
			</ul>	
	  	<#else>
			${beanTool.getPropertyValue(object, prop.propertyPath)!}
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
<#list typeDef.properties as prop>
<@renderProperty prop object />
</#list>
</tbody>
</table>
</div>

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<li><a href="${oo.url('/otherobjects/workbench/edit/${object.editableId}')}">Edit</a></li>
<#if item.published??>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/publish/${object.editableId}')}">Publish</a></li>
</#if>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/delete/${object.editableId}')}">Delete</a></li>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

