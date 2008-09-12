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
  		No value
  	</#if>  	
</td>
</tr>
</#macro>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTHERobjects: $!config.getProperty("site.label")</title>
<@oo.css "/otherobjects/static/legacy/workbench.css" />
</head>
<body>
<div class="oo-header">
<h1>Workbench</h1>
<ul>
<li><strong><a href="">Site Editor</a></strong></li>
</ul>
</div>
<div class="oo-main-outer"><div class="oo-main-inner">
<@oo.showFlashMessages />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
Viewing: ${object.label}
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
</ul>
</div>


<hr class="oo-clear" />
</div></div>
</body>
</html>

