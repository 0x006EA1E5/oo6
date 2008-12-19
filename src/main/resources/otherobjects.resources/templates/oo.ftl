<#-- 
Renders an image.
-->
<#macro image image class>
<img src="${cmsImageTool.getOriginal(image).dataFile.externalUrl}" class="${class!}" />
</#macro>

<#-- 
Convenience macro to insert a CSS link tag.
-->
<#macro css path media="screen">
<link rel="stylesheet" href="${resourceUrl(path)}" type="text/css" media="${media}" />
</#macro>  

<#-- 
Macro to insert CSS tag using either a combined file or individual ones as appropriate.
-->
<#macro combinableCss paths combinedPath media="all">
<#if ooEnvironment == "dev">
<#list paths as path>
<link rel="stylesheet" href="${resourceUrl(path)}" type="text/css" media="${media}" />
</#list>
<#else>
<link rel="stylesheet" href="${resourceUrl(combinedPath)}" type="text/css" media="${media}" />
</#if>
</#macro>    

<#-- 
Macro to insert link tags for SyndicationFeeds.
-->
<#macro feeds>
<#assign feedList = syndicationFeedTool.getFeeds()>
<#if feedList?has_content>
	<#list syndicationFeedTool.getFeeds() as feed>
	<link rel="alternate" type="${feed.feedMimeType}" title="${feed.label}" href="${feed.feedUrl}">
	</#list>
</#if>
</#macro>    

<#-- 
Convenience macro to insert a JavaScript CSS link tag.
-->
<#macro js path>
<script type="text/javascript" src="${resourceUrl(path)}"></script>
</#macro>

<#-- 
Convenience macro to insert a favicon link tag.
-->
<#macro favicon path>
<link rel="shortcut icon" href="${resourceUrl(path)}" />
</#macro>  

<#-- 
Convenience macro to insert an Apple Touch Icon link tag.
-->
<#macro appleTouchIcon path>
<link rel="apple-touch-icon" href="${resourceUrl(path)}"/>
</#macro>  

<#-- 
Inserts page title, including trail. Allows override via page metaData.
-->
<#macro pageTitle resourceObject default>
<title>
	<#if (resourceObject.data.metaData.title)??>${resourceObject.data.metaData.title}<#else>${resourceObject.ooLabel}</#if> |
	<#list navigationTool.getTrail(resourceObject.ooUrlPath, 1, true)?reverse as trailItem>
		${trailItem.label!} |
	</#list>
	${default}
</title>
</#macro>  

<#-- 
Inserts page metaData.
-->
<#macro metaData resourceObject>
<#if (resourceObject.data.metaData.keywords)??><meta name="keywords" content="${resourceObject.data.metaData.keywords}"/></#if>
<#if (resourceObject.data.metaData.keywords)??><meta name="description" content="${resourceObject.data.metaData.description}"/></#if>
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
		<div class="oo-block" id="oo-block-${blockReference.id}" title="${blockReference.block.label}">
		<#include "blocks/oo-block-new.ftl">
		</div>
	<#else>
		<#assign blockData = blockReference.blockData! >
		<div class="oo-block" id="oo-block-${blockReference.id}" title="${blockReference.block.label}">
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
Renders the contents of the block if the roles match.
-->
<#macro authorize ifAllGranted ifAnyGranted="" ifNoneGranted="">
<#if securityTool.authorize(ifAllGranted,ifAnyGranted,ifNoneGranted) >
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

<#-- 
Workbench: displays property value as string.
-->
<#macro renderPropertyValue prop object>
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
</#macro>

<#--
Displays up to 10 paragraphs of Lipsum.
-->
<#macro lipsum count=1>
<#assign paras = ["Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean pulvinar, magna non congue egestas, purus arcu aliquam est, at scelerisque lorem lectus eget mauris. Morbi nec sapien. Duis hendrerit, turpis non vehicula interdum, enim enim venenatis arcu, eget fermentum magna sem vitae neque. Pellentesque mauris enim, laoreet eu, facilisis et, vulputate non, enim. Pellentesque sollicitudin vehicula massa. Sed eros. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc laoreet, ipsum vel lacinia egestas, sem nibh mattis odio, non cursus tortor dolor ut neque. Vivamus non massa. Quisque ac pede sit amet lacus luctus mollis.", 
"Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Fusce fringilla. Sed vulputate, orci dapibus eleifend molestie, massa metus mollis enim, eget gravida augue nunc viverra nisl. Etiam dignissim mauris quis massa. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec ac ligula. Quisque vel justo. Phasellus posuere nisl in quam. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aenean non eros. Ut semper eros et urna.", 
"Vestibulum velit enim, porta mollis, mollis eu, ultrices ac, sapien. Aliquam urna metus, placerat vel, condimentum in, blandit nec, tellus. Aenean ut nibh. Fusce adipiscing massa quis tellus. Phasellus pulvinar nibh id lectus. Morbi sem. Vivamus molestie tempor libero. Integer aliquam mollis sapien. Etiam ante. Sed eu libero. Sed ut quam et purus suscipit eleifend. Sed libero magna, tincidunt ut, venenatis quis, volutpat at, lectus. Aenean convallis. Nunc non lacus.", 
"Mauris faucibus. Cras accumsan pellentesque ipsum. In hac habitasse platea dictumst. Donec aliquet bibendum augue. Cras viverra tincidunt est. Sed iaculis. Donec nulla ligula, ultricies ac, ullamcorper in, tincidunt volutpat, sem. Morbi a quam. Morbi nec odio vitae dui vulputate elementum. Duis lacus sapien, adipiscing at, tempor sit amet, faucibus in, sem.",
"Vestibulum pulvinar bibendum sem. Fusce elementum. Mauris viverra ante non massa. Morbi vel ligula id orci fermentum sagittis. Mauris lorem. Sed convallis. Phasellus quis nibh. Maecenas massa massa, lacinia quis, suscipit vitae, tincidunt quis, massa. Suspendisse interdum tempus enim. Ut sed erat. Vestibulum at urna vitae mauris sollicitudin porttitor. Curabitur vulputate nisl ac turpis. Aenean massa. Nam placerat mauris vel turpis. Aliquam aliquet. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Pellentesque lectus nisi, venenatis eu, elementum vitae, sollicitudin id, leo. Cras sagittis. Aenean non orci at lectus dapibus dapibus.", 
"Nulla tincidunt massa aliquet libero ultrices pretium. Nulla sit amet sapien. Fusce nec ipsum. Pellentesque dapibus, augue quis convallis condimentum, tellus sapien elementum est, ut tincidunt purus nisi quis tellus. Vivamus in lorem. Pellentesque at nisi ac pede condimentum varius. Aenean quis felis. Cras ante est, euismod a, lacinia non, condimentum sed, ipsum. Cras viverra. Sed faucibus enim a massa. Aenean nulla. Fusce lacinia, urna a commodo sollicitudin, urna metus tristique odio, quis lobortis dolor nisl vel diam. Pellentesque molestie mattis lectus. Quisque blandit ante id nisl. In rutrum leo et tortor. Donec tincidunt feugiat tellus. Sed porttitor semper velit. Etiam orci.",
"Vivamus lectus risus, aliquam sit amet, scelerisque ut, dapibus id, lectus. Nam scelerisque. Phasellus in pede. Proin mi. Duis lobortis, lorem a ultricies porta, turpis arcu mollis nunc, ut hendrerit erat sapien sit amet mi. Fusce luctus. Donec vel purus non purus euismod mattis. Aenean vestibulum placerat tortor. Nulla facilisi. Quisque nisl lorem, egestas quis, auctor scelerisque, vehicula et, nunc. Vestibulum rutrum, erat vitae adipiscing feugiat, risus mauris vestibulum orci, id sodales dui turpis at nisi.", 
"Nunc aliquam diam luctus lorem. Morbi a justo eget risus volutpat elementum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Etiam sollicitudin. Sed a nisl vel urna venenatis condimentum. Ut malesuada. Fusce augue libero, ultrices et, viverra vitae, mollis sed, odio. In erat. Nulla ac purus. Nulla facilisi. Nulla volutpat, augue eu eleifend blandit, urna nisl euismod magna, quis porta eros lectus et urna. Quisque sit amet eros. Cras at nulla a nunc aliquet convallis. Fusce pellentesque leo vitae neque. Integer suscipit convallis risus. Nulla sollicitudin arcu a nulla. Nunc eget libero.",
"Nullam nec nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus mauris. Suspendisse lorem nulla, interdum ac, sodales eu, tincidunt vel, ante. Mauris sapien sem, porta eget, facilisis id, gravida in, lectus. Maecenas tristique neque quis dui. Aenean pulvinar, orci in ultricies elementum, nisl purus rhoncus ipsum, a malesuada arcu erat eu metus. Sed massa. Aliquam sed ipsum eget nisi faucibus pharetra. Donec id erat. Aenean consectetuer erat ut neque. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Morbi congue velit id arcu. Nullam vitae nibh non dolor rutrum convallis. Mauris ligula odio, fermentum in, tempor a, vulputate vel, urna.", 
"In hac habitasse platea dictumst. Fusce ullamcorper posuere lectus. Suspendisse potenti. Quisque aliquet erat nec neque. Aliquam eget dui vel purus vestibulum feugiat. Praesent blandit erat at odio. Nam faucibus pede ut ipsum. Donec lacinia eleifend velit. Pellentesque cursus, sem eu scelerisque malesuada, tellus metus lobortis justo, quis ornare quam tellus sed nibh. Maecenas non urna. Vivamus turpis justo, gravida sed, auctor a, tempus a, sapien. Aliquam erat volutpat. Aliquam erat volutpat. Ut vitae nulla id ipsum laoreet feugiat."]>
<#list paras as para>
<#if (para_index > 9) || (para_index > (count - 1)) >
<#break />
</#if>
${para}
</#list>
</#macro>

<#--
Displays object contents.
-->
<#macro inspect obj>
${objectInspector.toHtml(obj)}
</#macro>

<#--
Format as Textile.
-->
<#macro format text>
${formatTool.formatTextile(text)}
</#macro>

<#--
Functions
-->
<#function action actionName>
<#return url("/_action/${actionName}") >
</#function>

<#function url path>
<#return "${urlTool.getUrl(path)}">
</#function>

<#function resourceUrl path>
<#return "${urlTool.getResourceUrl(path)}">
</#function>

<#function msg message>
<#return "${formatTool.getMessage(message)}">
</#function>









<#--
OO Interface Integration macros
-->

<#-- 
Macro to insert HTML tag to support OO interface
-->
<#macro html id="" class="">
<html <#if id?has_content>id="${id}"</#if><@authorize "ROLE_ADMIN"> class="${class!}"</@authorize>>
</#macro>

<#-- 
Macro to insert OO interface CSS headers
-->
<#macro head>
<@oo.css "/otherobjects/static/hud/hud.css" />
</#macro>

<#-- 
Macro to insert HUD code
-->
<#macro foot>
<@authorize "ROLE_ADMIN">
<div class="oo-icon"></div>
<div class="oo-hud">
</div>
<#include "/otherobjects/templates/hud/hud.ftl" />
<@oo.js "/otherobjects/static/hud/hud.js" />
</@authorize>
</#macro>

<#-- 
Macro to insert legacy toolbar code
-->
<#macro legacyToolbar>
<@authorize "ROLE_ADMIN"> 
<#include "/otherobjects/templates/legacy/blocks/toolbar.ftl" />
</@authorize>
</#macro>








