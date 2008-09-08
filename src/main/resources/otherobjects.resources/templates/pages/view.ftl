<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign object = daoService.getDao("baseNode").get(id) />


<style>
.navigation {float:left; border:1px solid red;}
</style>

<div class="navigation"> 
<ul>
  <li>One</li>
  <li>Two</li>
  <li>Three</li>
  <ul>
  <li>One</li>
  <li>Two</li>
  <li>Three</li>
</ul>
</ul>
</div>

<h1>View: ${object.label}</h1>

<#macro renderProperty prop object>
<tr>
<td>${prop.name}</td>
<td>
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

<table border="1">
<#list object.typeDef.properties as prop>
<@renderProperty prop object />
</#list>
</table>

<div class="content">
<p><a href="${oo.url('/otherobjects/workbench/edit/${object.id}')}">Edit</a></p>
</div>
