<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/workbench.ftl" as workbench />

<#assign pageTitle = "Comparing: ${oo.msg(item1.label)} and ${oo.msg(item2.label)}" />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<#include "/otherobjects/templates/workbench/shared/nav-folders.ftl" />

<div class="oo-content">
<h2>
${pageTitle}
</h2>


<table class="oo-listing">
<thead>
<tr>
<th>Property</th>
<th>Version ${item1.version}</th>
<th>Version ${item2.version}</th>
</tr>
</thead>
<tbody>
<#list typeDef.properties as property>
 <tr <#if ! beanTool.equals(beanTool.getPropertyValue(item1, property.propertyPath), beanTool.getPropertyValue(item2, property.propertyPath))>class="oo-diff"</#if>>
	<td><p>${property.label} </p> </td>	
	<td><p><@workbench.renderPropertyValue property item1 /></p></td>	
	<td><p><@workbench.renderPropertyValue property item2 /></p></td>	
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

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />

