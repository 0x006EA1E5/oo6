<#-- 
Renders an image.
-->
<#macro image image class>
<img src="${cmsImageTool.getOriginal(image).dataFile.externalUrl}" class="${class!}"/>
</#macro>

<#-- 
Convenience macro to insert CSS link tag.
-->
<#macro css path media="screen">
<link rel="stylesheet" href="${resourceUrl(path)}" type="text/css" media="${media}">
</#macro>    

<#-- 
Convenience macro to insert CSS link tag.
-->
<#macro js path>
<script type="text/javascript" src="${resourceUrl(path)}"></script>
</#macro>

<#-- 
Convenience macro to insert favicon link tag.
-->
<#macro favicon path>
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
		<#include "blocks/oo-block-new.ftl">
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
<@oo.authorize ifAllGranted="ROLE_ADMIN">
<!-- Start HUD Elements -->
<script type="text/javascript" src="http://cdn.othermedia.com/ojay-yui/2.5.1.js"></script>
<script type="text/javascript" src="http://cdn.othermedia.com/ojay/0.1.2/all-min.js"></script>
<@oo.js "/otherobjects/static/javascript/keyboard.js"/>
<@oo.js "/otherobjects/static/javascript/ext-base.js"/>
<@oo.js "/otherobjects/static/javascript/ext-core.js"/>
<@oo.js "/otherobjects/static/javascript/hud/hud.js"/>
<@oo.css "/otherobjects/static/css/hud.css"/>

<div id="oo-form-overlay" style="display:none;"></div>

<div  id="oo-main-hud" class="box-oo second-oo" style="display:none;">
<#include "/blocks/oo-hud.ftl">
</div>

<div id="oo-chooser-hud" class="oo-hud" style="display:none;">
<#include "/blocks/oo-block-chooser.ftl">
</div>

<div id="logo-oo" class="logo-oo" onclick="ooToggleHud('oo-main-hud');"></div>

<script>
var resourceObjectId = '${resourceObject.id}';
var ooTemplateId = '${ooTemplate.id}';
</script>
</@oo.authorize>
<!-- End HUD Elements -->

</#macro>

<#-- 
Renders the contents of the block if the roles match.
-->
<#macro authorize ifAllGranted ifAnyGranted="" ifNoneGranted="">
<#if security.authorize(ifAllGranted,ifAnyGranted,ifNoneGranted) >
<#nested>
</#if>
</#macro>

<#-- 
Displays flash messages.
-->
<#macro showFlashMessages>
<#assign msgs = flash.messages! >
<#if msgs?has_content >
<div class="oo-flash">
<#list msgs as msg>
<p class="oo-flash-${msg.type}">${msg.message}</p>
</#list>
</div>
</#if>
</#macro>

<#-- Functions-->
<#function action actionName>
<#return url("/_action/${actionName}") >
</#function>

<#function url path>
<#return "${urlTool.getUrl(path)}">
</#function>

<#function resourceUrl path>
<#return "${urlTool.getResourceUrl(path)}">
</#function>


<#-- Directives-->
<#assign format = "org.otherobjects.cms.tools.FormatDirective"?new()>  

