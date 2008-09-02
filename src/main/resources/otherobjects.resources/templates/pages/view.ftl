<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign object = daoService.getDao("baseNode").get(id) />

<h1>View: ${object.label}</h1>

<#macro renderProperty prop object>
<tr>
<td>${prop.name}</td>
<td>
	<#if prop.type == "component" >
		${object.get(prop.name)!}
	<#elseif prop.type == "date" >
		${object.get(prop.name)?string("d MMM yyyy")}
	<#elseif prop.type == "boolean" >
		${object.get(prop.name)?string("Yes", "No")}	
  	<#else>
		${object.get(prop.name)!}
  	</#if>
</td>
</tr>
</#macro>

<table border="1">
<#list object.typeDef.properties as prop>
<@renderProperty prop object />
</#list>
</table>

<div class="content">
<p><a href="${oo.url('/otherobjects/workbench/edit/${object.id}')}">Edit</a></p>
</div>
