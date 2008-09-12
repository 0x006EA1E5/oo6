<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

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
  		No value
  	</#if>  	
</td>
</tr>
</#macro>

<#assign object = daoService.getDao("baseNode").get(id) />

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
Listing: ${oo.msg(folder.label)}
</h2>

<table class="oo-listing">
<thead>
<tr>
<th>Name</th>
<th>Action</th>
<th>Action</th>
</tr>
</thead>
<tbody>
<#list items as item>
<tr>
	<td><a class="$cssClass" href="${oo.url('/otherobjects/workbench/view/${item.id}')}">${item.label}</a></td>
	<td class="oo-action"><a href="${oo.url(item.linkPath)}">Preview</a></td>
	<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/edit/${item.id}')}">Edit</a></td>
</tr>
</#list>
</tbody>
</table>
</div>



<div class="oo-actions">
<h2>Actions</h2>

<ul>
<#list folder.allAllowedTypes as type>
<li><a href="${oo.url('/otherobjects/workbench/edit/{object.id}')}">New ${(type.label)!} ...</a></li>
</#list>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

