<#-- 
Resolves resource and returns the external url.

FIXME Temporary implementation until our resource handling is finalised.
-->
<#macro url internalPath>
<#if internalPath?contains("site")>
http://localhost:8080/static/site.resources/static/${internalPath?replace("/site/static/","")}<#t>
</#if>
<#if internalPath?contains("otherobjects")>
http://localhost:8080/static/otherobjects.resources/static/${internalPath?replace("/otherobjects/static/","")}<#t>
</#if>
</#macro>

<#-- 
Convenience macro to insert CSS link tag.
-->
<#macro css internalPath media="screen">
<link rel="stylesheet" href="<@oo.url internalPath/>" type="text/css" media="${media}">
</#macro>    

<#-- 
Convenience macro to insert CSS link tag.
-->
<#macro js internalPath>
<script type="text/javascript" src="<@oo.url internalPath/>"></script>
</#macro>

<#-- 
Convenience macro to insert favicon link tag.
-->
<#macro favicon internalPath>
<link rel="shortcut icon" href="<@oo.url internalPath/>" type="image/x-icon">
</#macro>  

<#-- 
Renders an exception stack trace. Output is not wrapped in a block element.
-->
<#macro renderException exception>
<strong>${exception.class.name} : ${exception.message}</strong><br/>
<#list exception.stackTrace as trace>
${trace}<br/>
</#list>
</#macro>  