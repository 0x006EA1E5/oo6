<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign pageTitle = "History: ${oo.msg(item.label)}" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />
<#include "/otherobjects/templates/workbench/shared/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>


<table class="oo-listing">
<thead>
<tr>
<th>Label</th>
<th>Last Modified</th>
<th>User</th>
<th>Action</th>
<th>Action</th>
</tr>
</thead>
<tbody>
<#list versions as version>
 <tr>
	<td><a class="oo-<#if version.published?? && version.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${version.editableId}')}">${version.label!}</a></td>
	<td><p>${item.modificationTimestamp?datetime?string("HH:mm 'on' d MMM yyyy")}</p></td>	
	<td><p>${item.modifier}</p></td>	
	<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/diff/${item.editableId}?version=${version.version}')}">Diff</a></td>
	<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/restore/${item.editableId}?version=${version.version}')}">Restore</a></td>
</tr>
</#list>
</tbody>
</table>
</div>



<div class="oo-actions">
<h2>Actions</h2>

<ul>
<li><a href="${oo.url('/otherobjects/workbench/view/${item.editableId}')}">Back to view</a></li>
 </ul>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

