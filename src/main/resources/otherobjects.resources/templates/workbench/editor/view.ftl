<#import "/spring.ftl" as spring />
<#import "/workbench.ftl" as workbench />
<#import "/oo.ftl" as oo />
<#assign object = item />

<#macro renderProperty prop object>
<tr>
<td class="oo-label">${prop.label}</td>
<td class="oo-field-none">
<@workbench.renderPropertyValue prop object />
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
${object.id!} <#if object.version?has_content>(version ${object.version!})</#if>
</td>
</tr>
<#if item.published??>
<tr>
<td class="oo-label">JCR Path</td>
<td class="oo-field-none">
${object.jcrPath!}
</td>
</tr>
</#if>
<#list typeDef.properties as prop>
<@renderProperty prop object />
</#list>
<#if object.creationTimestamp??>
<tr>
<td class="oo-label">Created</td>
<td class="oo-field-none">
<img src="${gravatarTool.getUrl(object.creator,32,ssl)}"/ style="float:left; padding:0 5px 0 0;" width="32" height="32">
<abbr class="relative-time" title="${object.creationTimestamp!?datetime?string("long")}">${formatTool.formatRelativeTimestamp(object.creationTimestamp)}</abbr>
<br>by <strong>${object.creator!}</strong>
</td>
</tr>
<tr>
<td class="oo-label">Last Modified</td>
<td class="oo-field-none">
<abbr class="relative-time" title="${object.modificationTimestamp!?datetime?string("long")}">${formatTool.formatRelativeTimestamp(object.modificationTimestamp)}</abbr>
<img src="${gravatarTool.getUrl(object.modifier,32,ssl)}"/ style="float:left; padding:0 5px 0 0;" width="32" height="32">
<br>by <strong>${object.modifier!}</strong>
</td>
</tr>
</#if>	
</tbody>
</table>



</div>
</div>

<div class="oo-actions">
<h2>Actions</h2>
<ul>
<#if object.path??><li class="divider"><a href="${oo.url('/otherobjects/workbench/list${object.path}')}">Back to listing</a></li></#if>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/edit/${object.editableId}')}">Edit</a></li>
<#if item.published??>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/history/${object.editableId}')}">View history</a></li>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/publish/${object.editableId}')}">Publish</a></li>
</#if>
<#--
<li class="divider"><a href="${oo.url('/otherobjects/workbench/copy/${object.editableId}')}">Make a copy</a></li>
-->
<li class="divider"><a href="${oo.url('/otherobjects/workbench/delete/${object.editableId}')}">Delete</a></li>

<#-- if item.published??>
<li class="divider"><a href="${oo.url('/otherobjects/debug/jcr?path=${object.jcrPath}')}">Debug</a></li>
</#if> -->

<#if item.linkPath??>
<li class="divider"><a href="${item.ooUrl}">Preview</a></li>
</#if>
</ul>
</div>

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

