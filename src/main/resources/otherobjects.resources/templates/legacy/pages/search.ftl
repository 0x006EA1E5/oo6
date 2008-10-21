<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign pageTitle = "Search results for: ${oo.msg(RequestParameters.q)}" />

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
<#list results as item>
<tr>
	<td><a class="oo-<#if item.published>live<#else>edited</#if>" href="${oo.url('/otherobjects/workbench/view/${item.id}')}">${item.label!}</a></td>
	<td title="${item.typeDef.name}"><p>${item.typeDef.label}</p></td>
	<td class="oo-action"><a href="${oo.url(item.linkPath)}">Preview</a></td>
	<td class="oo-action"><a href="${oo.url('/otherobjects/workbench/edit/${item.id}')}">Edit</a></td>
</tr>
</#list>
</tbody>
</table>
</div>


<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

