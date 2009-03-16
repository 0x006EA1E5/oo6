<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign object = daoTool.get("baseNode").get(id) />

<#assign pageTitle = "Listing: ${oo.msg(folder.label)}" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<#include "/otherobjects/templates/workbench/shared/nav-folders.ftl" />

<div id="ooContent"> 
<div class="oo-content">

<h2>${pageTitle}</h2>

<table class="oo-listing">
<thead>
<tr>
<th>Name</th>
<th>Type</th>
<th>Action</th>
<th>Action</th>
</tr>
</thead>
<tbody>
<#list items as item>
<#if ! item.class.name?ends_with("Folder") && ! item.class.name?ends_with("PublishingOptions")> <!--FIXME -->
 <tr>
	<td><a class="oo-<#if item.published?? && item.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${item.editableId}')}">${item.ooLabel!}</a></td>
	<#if item.linkPath??>
		<td title="${item.typeDef.name}"><p>${item.typeDef.label}</p></td>
	<#else>
		<td title="${item.class.name}"><p>${item.class.name}</p></td>	
	</#if>
	<td class="oo-action"><#if item.linkPath??><a href="${oo.url(item.linkPath)}">Preview</a></#if></td>
	<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/edit/${item.editableId}')}">Edit</a></td>
</tr>
</#if>
</#list>
</tbody>
</table>
</div>



<div id="ooActions" class="oo-panel oo-text-style">

<#--<div class="oo-title"><big>Actions</big></div>--> 
<a class="oo-item" href="${oo.url('/otherobjects/workbench/view/'+folder.id)}"><div class="oo-arrow">View folder details</div></a>
<a class="oo-item" href="${oo.url('/otherobjects/workbench/create/org.otherobjects.cms.model.SiteFolder?container='+folder.id)}"><div class="oo-arrow">New sub folder</div></a>
<a class="oo-item" href="${oo.url('/otherobjects/workbench/create/org.otherobjects.cms.model.DbFolder?container='+folder.id)}"><div class="oo-arrow">New database folder</div></a>
<#list folder.allAllowedTypes as type>
<#if type?exists>
<a class="oo-item" href="${oo.url('/otherobjects/workbench/create/${type.name}?container=${folder.id}')}"><div class="oo-arrow">New ${type.label}</div></a>
</#if>
</#list>
<#--
<ul>
<li></li>
<li></li>
<li></li>

</ul>
-->
</div>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

