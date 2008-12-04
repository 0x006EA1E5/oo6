<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#assign object = item />

<#macro renderProperty prop object>
<tr>
<td class="oo-label">${prop.label}</td>
<td class="oo-field-none">
<@oo.renderPropertyValue prop object />
</td>
</tr>
</#macro>

<#assign pageTitle = "Viewing: ${oo.msg(object.label!)}" />
<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>
<table class="oo-listing">
<thead>
<tr>
<th>Field</th>
<th>Value</th>
</tr>
</thead>
<tbody>
<#list typeDef.properties as prop>
<@renderProperty prop object />
</#list>
</tbody>
</table>
</div>

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<#-- <li><a href="${oo.url('/otherobjects/workbench/list/${folder.editableId}')}">Back to listing</a></li> -->
<li class="divider"><a href="${oo.url('/otherobjects/workbench/edit/${object.editableId}')}">Edit</a></li>
<#if item.published??>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/history/${object.editableId}')}">View history</a></li>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/publish/${object.editableId}')}">Publish</a></li>
</#if>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/delete/${object.editableId}')}">Delete</a></li>

<#if item.linkPath??>
<li class="divider"><a href="${oo.url(item.linkPath)}">Preview</a></li>
</#if>
</ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

