<#import "/oo.ftl" as oo>

<style type="text/css">
</style>

<div class="no-print">

<div id="OoMenuIcon">
<h2>OTHER Objects</h2>
</div>

<div id="OoToolbar" class="oo-toolbar">

<h3><a id="OoLogoButton" class="oo-button-up" href="${oo.url("/otherobjects/workbench/")}"></a></h3>

<#if resourceObject??>
<p class="oo-divider">
    <a href="${oo.url("/otherobjects/workbench/")}">Back to workbench</a>
</p>

<p class="oo-divider">
	<a id="oo-toolbar-object" class="oo-<#if resourceObject.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${resourceObject.id}')}">${resourceObject.typeDef.label}: ${formatTool.trim(resourceObject.ooLabel,50)}</a>
</p>

<ul>
	<li><a href="${oo.url('/otherobjects/workbench/edit/${resourceObject.id}')}">Edit page</a></li>	
	<li><a href="javascript:ooToggleEditMode()">Edit blocks</a></li>	
	<li><a href="javascript:ooToggleDesignMode()">Arrange blocks<#-- (${ooTemplate.ooLabel} : ${ooTemplate.layout.ooLabel})--></a></li>	
	<#if ! ooTemplate.published>
	<li id="oo-publish-template-button"><a href="javascript:ooPublishTemplate('${ooTemplate.id}')">Publish template (${ooTemplate.ooLabel})</a></li>
	</#if>	
	<#if ! resourceObject.published>
	<li id="oo-publish-button"><a href="javascript:ooPublish('${resourceObject.id}')">Publish</a></li>
	</#if>	
</ul>
<#else>
<p class="oo-divider">
    <a href="${oo.url("/")}">Back to site</a>
</p>
<#if ooTemplate??>

</#if>
</#if>
<#--
<ul>
	<li><a href="javascript:ooToggle('.oo-stats')">${performanceInfo.events?size} queries</a></li>	
</ul>
-->

<a id="OoLogoutButton" href="${oo.url("/otherobjects/logout.html")}"></a>

<#--
<div class="oo-stats">
<h2><em>Page performance statistics</em></h2>
<table>
<#list performanceInfo.events as event>
<tr><td>${event.details}</td><td>${event.time}ms</td></tr>
</#list>
</table>
</div>
-->

</div>


<div class="oo-menu" id="OoMenu"></div>



<div class="oo-edit-zones"></div>
<div class="oo-design-tools" style="display:none;">
<div id="OoDesignTrash" class="oo-design-trash oo-panel">Trash</div>
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
<#list daoTool.get("baseNode").getAllByJcrExpression("/jcr:root/designer//element(*) [@ooType = 'org.otherobjects.cms.model.TemplateBlock']") as block>
<a class="oo-chooser-button" id="${block.code}">${block.label} &rarr;</a>
</#list>
</div>
</div>