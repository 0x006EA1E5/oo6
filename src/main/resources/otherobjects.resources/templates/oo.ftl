<#-- 
Renders an image.
-->
<#macro image image class>
<img src="${cmsImageTool.getOriginal(image).dataFile.externalUrl}" class="${class!}"/>
</#macro>

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

<#-- 
Macro to insert block
-->
<#macro block blockReference>
<#attempt>

	<#assign blockName = blockReference.block.code/>
	<#assign blockData = blockReference.blockData!/>
	<#-- If block is global but has no data then render placeholder-->
	<#if blockReference.block.global?has_content && !blockData?has_content>
		<div class="oo-block" id="oo-block-${blockReference.id}">
		<#include "/blocks/oo-block-new.ftl">
		</div>
	<#else>
		<#assign blockData = blockReference.blockData! >
		<div class="oo-block" id="oo-block-${blockReference.id}">
		<#include "/blocks/${blockName}.ftl">
		</div>
	</#if>
<#recover>
	<div class="oo-block" id="oo-block-${blockReference.id}">
	<#include "/blocks/oo-block-error.ftl">
	</div>
</#attempt>
</#macro>  

<#-- 
Macro to insert region
-->
<#macro region template regionCode>

<div class="oo-region" id="oo-region-${regionCode}">
<#list (template.getRegion(regionCode)!).blocks! as block>
<@oo.block block />
</#list>
</div>

</#macro>  


<#-- Directives-->
<#assign format = "org.otherobjects.cms.tools.FormatDirective"?new()>  
