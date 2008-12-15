<#import "/oo.ftl" as oo>

<style type="text/css">
#OoMenu {display:none;}
#OoMenu H2 {font:70%/600% Arial, sans-serif; width:195px; height:120px; left:0px; position:fixed; bottom:25px; z-index:11; color:#FFFFFF; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/menu.png")});}
.oo-menu {position:fixed; font:70% Arial, sans-serif; width:180px; height:100px; left:0px; bottom:25px; z-index:12;}
.oo-menu A {display:block; color:#000000; height:14px;  width:155px; padding:5px 0px 5px 25px; text-decoration:none;}
.oo-menu A:hover {background-color:#dddddd;}
.oo-menu LI {display:block; width:180px; background:#FFFFFF;}
.oo-menu LI.oo-menudivider {background:#FFFFFF url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/dots.gif")}) repeat-x bottom left; padding:0px 0px 1px 0px;}
.oo-menu LI LI {padding:0px;}
.oo-menu LI LI A {padding:5px 0px 6px 25px;}

/* Toolbar */
.oo-toolbar {height:25px; width:100%; left:0px; position:fixed; z-index:10; font:70% Arial, sans-serif; overflow:hidden; bottom:0px; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/toolbar.png")}); padding:15px 0px 0px 0px;}

/* Menu Button */
#OoLogoButton {display:block; overflow:hidden; text-indent:50px; width:27px; height:25px; text-decoration:none;}
.oo-button-up {background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-oo-logo.gif")}) no-repeat 5px 5px;}
.oo-button-up:hover {background:#CCCCCC url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-oo-logo.gif")}) no-repeat 5px 5px;}
.oo-button-down, .oo-button-hold {background:#FFFFFF url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-oo-logo.gif")}) no-repeat 5px 5px;}
#OoLogoutButton {display:block; float:right; width:27px; height:25px; text-decoration:none; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/log-out.png")}) no-repeat 5px 5px;}

/* Icons */
.oo-icon-overview {background:#FFFFFF url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon_overview.gif")}) no-repeat 3px 4px;}
.oo-icon-user {background:#FFFFFF url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon_user.gif")}) no-repeat 3px 4px;}
.oo-icon-favourites {background:#FFFFFF url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon_favourites.gif")}) no-repeat 3px 4px;}

.oo-toolbar H3 {float:left;}
.oo-toolbar P {float:left;}
.oo-toolbar .oo-divider {background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/divider.gif")}) no-repeat; padding:0px 0px 0px 1px;}
.oo-toolbar P A {color:#000000!important; text-decoration:none; height:12px; float:left; padding:6px 10px 7px 10px;}
.oo-toolbar P A:hover {background-color:#cccccc;}
.oo-toolbar P .oo-edited, .oo-toolbar P .oo-new {background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-in-progress.gif")}) no-repeat 10px 9px; padding:6px 10px 7px 24px;}
.oo-toolbar P .oo-live {background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-live.gif")}) no-repeat 10px 9px; padding:6px 10px 7px 24px;}
.oo-toolbar P .oo-neutral {background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-neutral.gif")}) no-repeat 10px 9px; padding:6px 10px 7px 24px;}
.oo-toolbar P .oo-locked {background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/icon-locked.gif")}) no-repeat 10px 9px; padding:6px 10px 7px 24px;}.oo-toolbar .oo-version {float:right; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/divider.gif")}) no-repeat; padding:0px 0px 0px 1px;}
.oo-toolbar P EM {display:none;}
.oo-toolbar UL {float:left; display:table;}
.oo-toolbar LI {float:left; display:table-cell;}
.oo-toolbar LI A {color:#000000!important; background:url(${oo.resourceUrl("/otherobjects/static/legacy/graphics/arrow-right.gif")}) no-repeat 10px 9px; text-decoration:none; height:12px; float:left; padding:6px 10px 7px 20px;}
.oo-toolbar LI A:hover {background-color:#cccccc;}
.oo-toolbar LI STRONG A {background-color:#ffffff;}
.oo-toolbar LI STRONG A:hover {background-color:#ffffff;}

/* Stats */
.oo-stats {position:fixed; top:100px; left:100px;text-align:left; background:#FFF; border:1px solid black; display:none;}
.oo-stats H2 {background:#000; color:#FFF; width:100%; font-size:120%; padding-bottom:5px; margin:0px;}
.oo-stats H2 EM {padding:2px;}
.oo-stats TD {text-align:left; padding:5px;}
.oo-stats TD {text-align:left; padding:5px;}
.oo-stats TR {border-top:1px solid #666;}
</style>

<div class="no-print">

<div id="OoMenu">
<h2>OTHER Objects</h2>
</div>

<div id="OoToolbar" class="oo-toolbar">

<h3><a id="OoLogoButton" class="oo-button-up" href="${oo.url("/otherobjects/workbench/")}"></a></h3>

<#if resourceObject??>
<p class="oo-divider">
    <a href="${oo.url("/otherobjects/workbench/")}">Back to workbench</a>
</p>

<p class="oo-divider">
	<a class="oo-<#if resourceObject.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${resourceObject.id}')}">${resourceObject.typeDef.label}: ${resourceObject.ooLabel}</a>
</p>

<ul>
	<li><a href="${oo.url('/otherobjects/workbench/edit/${resourceObject.id}')}">Edit</a></li>	
	<li><a href="javascript:ooEnableBlockSelector()">Edit blocks</a></li>	
	<li><a href="javascript:ooEnableBlockManagement()">Arrange blocks<#-- (${ooTemplate.ooLabel} : ${ooTemplate.layout.ooLabel})--></a></li>	
</ul>
<#else>
<p class="oo-divider">
    <a href="${oo.url("/")}">Back to site</a>
</p>
<#if ooTemplate??>
<ul>
	<li><a href="javascript:ooEnableBlockSelector()">Edit blocks</a></li>	
	<li><a href="javascript:ooEnableBlockManagement()">Arrange blocks<#-- (${ooTemplate.ooLabel} : ${ooTemplate.layout.ooLabel})--></a></li>	
</ul>
</#if>
</#if>
<ul>
	<li><a href="javascript:ooToggle('.oo-stats')">${performanceInfo.events?size} queries</a></li>	
</ul>

<a id="OoLogoutButton" href="${oo.url("/otherobjects/logout.html")}"></a>

<div class="oo-stats">
<h2><em>Page performance statistics</em></h2>
<table>
<#list performanceInfo.events as event>
<tr><td>${event.details}</td><td>${event.time}ms</td></tr>
</#list>
</table>
</div>

</div>


<script>
var resourceObjectId = '${(resourceObject.id)!}';
var ooTemplateId = '${(ooTemplate.id)!}';
var ooBaseUrl = '${oo.url("/")}';
var ooBlockInEdit = "";
</script>

<div id="oo-form-overlay" style="display:none;"></div>
<div id="oo-chooser-hud" style="display:none;">
<h1>Choose a block to insert:</h1>
<#list daoService.getDao("baseNode").getAllByType("org.otherobjects.cms.model.TemplateBlock") as block>
<a class="oo-chooser-button" id="${block.code}">${block.label} &rarr;</a>
</#list>
</div>
</div>