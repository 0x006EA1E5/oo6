<#import "/oo.ftl" as oo />

<#-- 
Workbench: displays property value as string.
-->
<#macro renderPropertyValue prop object>
<#if beanTool.getPropertyValue(object, prop.propertyPath)?? >
	<#if prop.type == "component" >
		<table class="oo-listing field-component">
		<#list prop.relatedTypeDef.properties as p>
			<tr <#if !p_has_next>class="last-row"</#if>><td class="oo-label">${p.label}</td><td class="oo-value"><@renderPropertyValue p beanTool.getPropertyValue(object, prop.propertyPath)/></td></tr> 
		</#list>
		</table>
	<#elseif prop.type == "date" >
		${beanTool.getPropertyValue(object, prop.propertyPath)?date?string("d MMM yyyy")}
	<#elseif prop.type == "time" >
		${beanTool.getPropertyValue(object, prop.propertyPath)?time?string("HH:mm")}
	<#elseif prop.type == "timestamp" >
		${beanTool.getPropertyValue(object, prop.propertyPath)?datetime?string("HH:mm 'on' d MMM yyyy")}
	<#elseif prop.type == "boolean" >
		${beanTool.getPropertyValue(object, prop.propertyPath)?string("Yes", "No")}	
	<#elseif prop.type == "text" >
		${formatTool.summarize(beanTool.getPropertyValue(object, prop.propertyPath), 500)}	
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
</#macro>

