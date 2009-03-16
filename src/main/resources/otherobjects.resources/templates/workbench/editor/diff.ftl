<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#assign pageTitle = "Comparing: ${oo.msg(item1.label)} and ${oo.msg(item2.label)}" />

<#include "/otherobjects/templates/legacy/blocks/header.ftl" />

<#include "/otherobjects/templates/legacy/blocks/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>


<table class="oo-listing">
<thead>
<tr>
<th>Version 1</th>
<th>Version 2</th>
</tr>
</thead>
<tbody>
<#list typeDef.properties as property>
 <tr <#if ! beanTool.equals(beanTool.getPropertyValue(item1, property.propertyPath), beanTool.getPropertyValue(item2, property.propertyPath))>class="oo-diff"</#if>>
	<td><p><@oo.renderPropertyValue property item1 /></p></td>	
	<td><p><@oo.renderPropertyValue property item2 /></p></td>	
</tr>
</#list>
</tbody>
</table>
</div>



<div class="oo-actions">
<h2>Actions</h2>

<ul>
<li><a href="${oo.url('/otherobjects/workbench/history/${item.editableId}')}">Back to history</a></li>
 </ul>
</div>

<#include "/otherobjects/templates/legacy/blocks/footer.ftl" />

