<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<@oo.css "/otherobjects/static/temporary/temporary.css"/>

${daoService.jcr}

<h1>Edit: {object.label}</h1>

<@oo.showFlashMessages />

<#macro renderProperty prop prefix>
<tr>
<td>${prop.label}</td>
<td>
	<#if prop.type == "date" >
  		<@spring.bind "${prefix}${prop.fieldName}"/>
  		<#assign status = springMacroRequestContext.getBindStatus("${prefix}${prop.fieldName}")>
  		<#if status.value?exists && status.value?is_date>
        	<#assign stringStatusValue=status.value?date?string("yyyy-MM-dd")>
    	<#else>
      	  	<#assign stringStatusValue=status.value?default("")>
   		</#if>
  		
  		<input type="text" id="${status.expression}" name="${status.expression}" value="${stringStatusValue}" value="">
	<#elseif prop.type == "component" >
  		
  		<table border="1">
		<#list prop.relatedTypeDef.properties as p2>
		<#if false>
		<@renderProperty p2 "${prefix}${prop.fieldName}."/>	
		<#else>
		<tr>
		<td>${p2.label}</td>
		<td>
		<input name="${prop.fieldName}.${p2.fieldName}"/>
		</td>
		</tr>
		</#if>	
		</#list>
		</table>
  		
	<#elseif prop.type == "text" >
  		<@spring.formTextarea "${prefix}${prop.fieldName}"/>
  	<#else>
  		<@spring.formInput "${prefix}${prop.fieldName}"/>  	
  	</#if>
</td>
<td style="color:red;">
  <@spring.showErrors "<br>"/>
</td>
</tr>
</#macro>


<form action="${oo.url("/otherobjects/form")}" method="post">
<table border="1">
<tr>
<td>ID</td>
<td>
  <input type="text" name="editableId" value="${id}">
</td>
</tr>
<#list typeDef.properties as prop>
<@renderProperty prop "object." />
</#list>
<tr>
<td></td>
<td><input type="submit" value="submit"/></td>
</tr>
</table>
</form>

<div class="content">
<p><a href="${oo.url('/otherobjects/workbench/view/${object.id}')}">View</a></p>
</div>


<p>TODO:<br/>

References<br/>
Component<br/>
Lists<br/>
</p>

<p>VALIDATION:<br/>

Data type [DONE]<br/>
Required [DONE]<br/>
Maxlength<br/>
Regexp<br/>
Pattern<br/>
</p>