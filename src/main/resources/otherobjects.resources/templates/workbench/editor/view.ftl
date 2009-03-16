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

<#assign pageTitle = "Viewing: ${object.label!}" />
<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<#include "/otherobjects/templates/workbench/shared/nav-folders.ftl" />

<div id="ooContent"> 
<div class="oo-content">

<h2>${pageTitle}</h2>

<table class="oo-listing"><thead>
<tr>
<th>Field</th>
<th>Value</th>
</tr>
</thead>
<tbody>
<tr>
<td class="oo-label">ID</td>
<td class="oo-field-none">
${object.id!}
</td>
</tr>
<tr>
<td class="oo-label">JCR Path</td>
<td class="oo-field-none">
${object.jcrPath!}
</td>
</tr>
<#list typeDef.properties as prop>
<@renderProperty prop object />
</#list>
<#if object.creationTimestamp??>
<tr>
<td class="oo-label">Created</td>
<td class="oo-field-none">
${object.creationTimestamp!?datetime?string("long")}
by
${object.creator!}
</td>
</tr>
<tr>
<td class="oo-label">Last Modified</td>
<td class="oo-field-none">
${object.modificationTimestamp!?datetime?string("long")}
by
${object.modifier!}
</td>
</tr>
</#if>	
</tbody>
</table>
</div>
</div>

<div id="ooActions" class="oo-panel oo-text-style">
<#-- <li><a href="${oo.url('/otherobjects/workbench/list/${folder.editableId}')}">Back to listing</a></div></div>> -->
<a class="oo-item" href="${oo.url('/otherobjects/workbench/edit/${object.editableId}')}"><div class="oo-arrow">Edit</div></a>
<#if item.published??>
<div class="oo-item"><div class="oo-arrow"><a href="${oo.url('/otherobjects/workbench/history/${object.editableId}')}">View history</a></div></div>
<div class="oo-item"><div class="oo-arrow"><a href="${oo.url('/otherobjects/workbench/publish/${object.editableId}')}">Publish</a></div></div>
</#if>
<div class="oo-item"><div class="oo-arrow"><a href="${oo.url('/otherobjects/workbench/copy/${object.editableId}')}">Make a copy</a></div></div>
<div class="oo-item"><div class="oo-arrow"><a href="${oo.url('/otherobjects/workbench/delete/${object.editableId}')}">Delete</a></div></div>

<div class="oo-item"><div class="oo-arrow"><a href="${oo.url('/otherobjects/debug/jcr?path=${object.jcrPath}')}">Debug</a></div></div>

<#if item.linkPath??>
<div class="oo-item"><div class="oo-arrow"><a href="${oo.url(item.linkPath)}">Preview</a></div></div>
</#if>
</ul>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

