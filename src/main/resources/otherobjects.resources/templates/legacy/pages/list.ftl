<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign object = daoTool.get("baseNode").get(id) />

<#assign pageTitle = "Listing: ${oo.msg(folder.label)}" />

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>


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
<#if ! item.class.name?ends_with("Folder")> <!--FIXME -->
 <tr>
	<td><a class="oo-<#if item.published?? && item.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${item.editableId}')}">${item.label!}</a></td>
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



<div class="oo-actions">
<h2>Actions</h2>

<ul>
<li><a href="${oo.url('/otherobjects/workbench/view/'+folder.id)}">View folder details ...</a></li>
<li><a href="${oo.url('/otherobjects/workbench/create/org.otherobjects.cms.model.SiteFolder?container='+folder.id)}">New sub folder ...</a></li>
<li><a href="${oo.url('/otherobjects/workbench/create/org.otherobjects.cms.model.DbFolder?container='+folder.id)}">New database folder ...</a></li>
<#list folder.allAllowedTypes as type>
<#if type?exists>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${type.name}?container=${folder.id}')}">New ${type.label} ...</a></li>
</#if>
</#list>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

