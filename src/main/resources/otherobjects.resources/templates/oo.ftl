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
http://localhost:8080/${internalPath?replace("/site/","")}<#t>
</#if>
<#if internalPath?contains("otherobjects")>
http://localhost:8080/classpath/otherobjects.resources/static/${internalPath?replace("/otherobjects/static/","")}<#t>
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
		<#include "/otherobjects/templates/blocks/oo-block-new.ftl">
		</div>
	<#else>
		<#assign blockData = blockReference.blockData! >
		<div class="oo-block" id="oo-block-${blockReference.id}">
		<#include "/site/templates/blocks/${blockName}.ftl">
		</div>
	</#if>
<#recover>
	<div class="oo-block" id="oo-block-${blockReference.id}">
	<#include "blocks/oo-block-error.ftl">
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


<#-- 
Macro to insert HUD code
-->
<#macro hud>

<#-- HUD Elements -->
<script type="text/javascript" src="http://cdn.othermedia.com/ojay-yui/2.5.1.js"></script>
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.1.2/all-min.js"></script>
<script type="text/javascript" src="http://yui.yahooapis.com/2.5.1/build/dragdrop/dragdrop-min.js" ></script>
<@oo.js "/otherobjects/static/javascript/keyboard.js"/>
<@oo.js "/otherobjects/static/javascript/ext-base.js"/>
<@oo.js "/otherobjects/static/javascript/ext-core.js"/>
<@oo.js "/otherobjects/static/javascript/hud/drag-drop.js"/>
<@oo.js "/otherobjects/static/javascript/hud/hud.js"/>
<@oo.css "/otherobjects/static/css/hud.css"/>

<div id="oo-form-overlay" style="display:none;"></div>

<div  id="oo-main-hud" class="oo-hud" style="display:none;">
<#include "/blocks/oo-hud.ftl">
</div>

<div id="oo-chooser-hud" class="oo-hud" style="display:none;">
<#include "/blocks/oo-block-chooser.ftl">
</div>

<script>
var resourceObjectId = '${resourceObject.id}';
var ooTemplateId = '${ooTemplate.id}';
</script>

</#macro>

<#-- Directives-->
<#assign format = "org.otherobjects.cms.tools.FormatDirective"?new()>  



