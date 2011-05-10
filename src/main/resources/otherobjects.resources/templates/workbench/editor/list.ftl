<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/hud/hud.ftl" as hud />

<#assign pageTitle = "Listing: ${oo.msg(folder.label)}" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<#include "/otherobjects/templates/workbench/shared/nav-folders.ftl" />

<div id="ooContent"> 
<div class="oo-content">

<h2>Listing: ${folder.jcrPath}/</h2>

<#assign viewMode = folder.view />

<#-- assign viewMode = RequestParameters.view!"list"/ -->

<#if viewMode == "thumbnails">

	<table class="oo-listing">
	<thead>
	<tr>
	<th colspan="5">Items</th>
	</tr>
	</thead>
	<tbody>
	<tr>
	<#list items as item>
	
	<#if item_index % 5 == 0></tr><tr></#if>
		<td class="oo-image">
	   		<a href="${oo.url('/otherobjects/workbench/view/${item.editableId}')}"><strong>${item.wikiCode!}</strong><br />
	   		<strong>${item.label}</strong><br />
	   		<img src="${item.ooImage.thumbnailPath!""}" width="100" height="100"><br />View</a>
	   		<a href="${oo.url('/otherobjects/workbench/edit/${item.editableId}')}">Edit</a>
	   		<#if item.typeDef.imageProperty == "self"><a href="javascript:ooEditWithPicnik('${item.editableId}','${item.ooUrl}')">Picnik!</a></#if>
		</td>
	</#list>
	</tr>
	</tbody>
	</table>

<#else>

	<table class="oo-listing">
	<thead>
	<tr>
	<th>Icon</th>
	<th><a href="?sortOrder=name">Name</a></th>
	<th><a href="?sortOrder=type">Type</a></th>
	<th>Action</th>
	<th>Action</th>
	</tr>
	</thead>
	<tbody>
	<#list items as item>
	<#if ! item.class.name?ends_with("Folder") && ! item.class.name?ends_with("PublishingOptions")>  <!-- FIXME -->
	 <tr>
	 	<td class="oo-icon">
			<p><img src="${oo.resourceUrl(item.ooIcon!"")}" <!-- FIXME width="16" height="16" --></p>
    	</td>
		<td><a class="oo-<#if item.published?? && item.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${item.editableId}')}">${item.ooLabel!}</a></td>
		<#if item.linkPath??>
			<td title="${item.typeDef.name}"><p>${item.typeDef.label}</p></td>
		<#else>
			<td title="${item.class.name}"><p>${item.class.name}</p></td>	
		</#if>
		<td class="oo-action"><#if item.linkPath??><a href="${item.ooUrl}">Preview</a></#if></td>
		<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/edit/${item.editableId}')}">Edit</a></td>
	</tr>
	</#if>
	</#list>
	</tbody>
	</table>
</#if>

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

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

