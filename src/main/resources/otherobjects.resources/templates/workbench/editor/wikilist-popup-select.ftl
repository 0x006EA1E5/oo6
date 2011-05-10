<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />


<#assign pageTitle = "Select a ${type.label}" />
<#assign popup = true />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<div id="ooContent"> 
<div class="oo-content">

<h2>${pageTitle}</h2>


<#if type.imageProperty?? && type.imageProperty == "self">
<#assign viewMode = "thumbnails"/>
<#else>
<#assign viewMode = "list"/>
</#if>

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
	   		<a href="javascript:window.opener.ooPopupSelect('${RequestParameters.fieldName}','${item.editableId}','${item.ooLabel}','${oo.resourceUrl(item.ooIcon)}','${item.thumbnailPath}');window.close();">
	   		<strong>${item.wikiCode}</strong><br />
	   		<strong>${item.label}</strong><br />
	   		<img src="${item.thumbnailPath!""}" width="100" height="100"><br />Select</a>
	   		<#--<a href="${oo.url('/otherobjects/workbench/edit/${item.editableId}')}">Edit</a>-->
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
	<th>Name</th>
	<th>Type</th>
	<th>Action</th>
	</tr>
	</thead>
	<tbody>
	<#list items as item>
	<#if ! item.class.name?ends_with("Folder") && ! item.class.name?ends_with("PublishingOptions")>  <!-- FIXME -->
	 <tr>
		<td class="oo-icon">
			<p><img src="${oo.resourceUrl(item.ooIcon!"")}" width="19" height="16"></p>
    	</td>
    	<td><a class="oo-<#if item.published?? && item.published>live<#else>edited</#if>" href="javascript:window.opener.ooPopupSelect('${RequestParameters.fieldName}','${item.editableId}','${item.ooLabel}','${oo.resourceUrl(item.ooIcon)}','');window.close();">${item.ooLabel!}</a></td>
		<#if item.linkPath??>
			<td title="${item.typeDef.name}"><p>${item.typeDef.label}</p></td>
		<#else>
			<td title="${item.class.name}"><p>${item.class.name}</p></td>	
		</#if>
		<td class="oo-action"><#if item.linkPath??><a href="javascript:window.opener.ooPopupSelect('${RequestParameters.fieldName}','${item.editableId}','${item.ooLabel}','${oo.resourceUrl(item.ooIcon)}','');window.close();">Select</a></#if></td>
	</tr>
	</#if>
	</#list>
	</tbody>
	</table>

</#if>

</div>



<#--
<div class="oo-actions">
<h2>Actions</h2>

<ul>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${id}?container=${folder.id}')}">New ${type.label} ...</a></li>
<#list folder.allAllowedTypes as type>
<#if type?exists>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${type.name}?container=${folder.id}')}">New ${type.label} ...</a></li>
</#if>
</#list>
</ul>
</div>
-->

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

