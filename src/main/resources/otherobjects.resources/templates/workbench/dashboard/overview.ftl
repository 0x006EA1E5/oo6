<#assign pageTitle = "Welcome" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<div id="ooContent">
<div class="oo-content">

<#--
<p>Drag this link: 
<a href="javascript:location.href='http://del.icio.us/post?v=4;url='+encodeURIComponent(location.href)+';title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>



#--
<div class="welcome-block">
<h1>Bookmarklets</h1>
<p>Drag this link: 
<a href="javascript:location.href='http://localhost:8080/go/workbench/bookmarklet?v=1&url='+encodeURIComponent(location.href)+'&title='+encodeURIComponent(document.title.replace(/^\s*|\s*$/g,''))" 
title="post to del.icio.us" onclick="window.alert('Drag this link to your bookmarks bar, or right-click it and choose Add Link to Bookmarks...');return false;"
class="bookmarklet">Add to OTHERobjects</a> up to your Bookmarks Toolbar.</p>
</div>
--

-->


<h2>Current items in progress</h2>

<#assign edits = daoTool.get("baseNode").pageByJcrExpression("/jcr:root/site//(*, oo:node) [@published = 'false'] order by @modificationTimestamp descending",10,1) >

<@list edits.items />


<h2>Recently edited pages</h2>
<p>Here are the last few changes to pages across the site:</p>
<#assign latestChanges = daoTool.get("baseNode").pageByJcrExpression("/jcr:root/site//element(*, oo:node) order by @modificationTimestamp descending",10,1) >

<@list latestChanges.items />

</div>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />



<#macro list items>
	<table class="oo-listing">
	<thead>
	<tr>
	<th>Icon</th>
	<th>Name</th>
	<th style="width:120px;">Modifer</th>
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
		<td class="">
		<img src="${gravatarTool.getUrl(item.modifier,32,ssl)}" style="float:left; padding:0 5px 0 0;" width="32" height="32">
<abbr class="relative-time" title="${item.modificationTimestamp!?datetime?string("long")}">${formatTool.formatRelativeTimestamp(item.modificationTimestamp)}</abbr>
<br>by <strong>${item.modifier!}</strong>
</td>
		<td class="oo-action"><#if item.linkPath??><a href="${item.ooUrl}">Preview</a></#if></td>
		<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/edit/${item.editableId}')}">Edit</a></td>
	</tr>
	</#if>
	</#list>
	</tbody>
	</table>
</#macro>