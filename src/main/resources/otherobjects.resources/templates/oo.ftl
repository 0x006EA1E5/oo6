<#-- 
Renders an image.
-->
<#macro image image class>
<img src="${cmsImageTool.getOriginal(image).dataFile.externalUrl}" class="${class!}" />
</#macro>

<#-- 
Convenience macro to insert CSS link tag.
-->
<#macro css path media="screen">
<link rel="stylesheet" href="${resourceUrl(path)}" type="text/css" media="${media}" />
</#macro>    

<#-- 
Convenience macro to insert CSS link tag.
-->
<#macro js path>
<script type="text/javascript" src="${resourceUrl(path)}"></script>
</#macro>

<#-- 
Convenience macro to insert favicon link tag.
-->
<#macro favicon path>
</#macro>  

<#-- 
Renders an exception stack trace. Output is not wrapped in a block element.
-->
<#macro renderException exception>
<strong>${exception.class.name} : ${exception.message}</strong><br/>
<#list exception.stackTrace as trace>
${trace}<br/>
</#list>
</#macro>  

<#-- 
Macro to insert block
-->
<#macro block blockReference>
<#attempt>

	<#assign blockName = blockReference.block.code/>
	<#assign blockData = blockReference.blockData!/>
	<#-- If block is global but has no data then render placeholder-->
	<#if blockReference.block.global?has_content && !blockData?has_content>
		<div class="oo-block" id="oo-block-${blockReference.id}">
		<#include "blocks/oo-block-new.ftl">
		</div>
	<#else>
		<#assign blockData = blockReference.blockData! >
		<div class="oo-block" id="oo-block-${blockReference.id}">
		<#include "/site/templates/blocks/${blockName}.ftl">
		</div>
	</#if>
<#recover>
	<div class="oo-block" id="oo-block-${blockReference.id}">
	<#include "blocks/oo-block-error.ftl">
	</div>
</#attempt>
</#macro>  

<#-- 
Macro to insert region
-->
<#macro region template regionCode>

<div class="oo-region" id="oo-region-${regionCode}">
<#list (template.getRegion(regionCode)!).blocks! as block>
<@oo.block block />
</#list>
</div>

</#macro>  


<#-- 
Macro to insert HUD code
-->
<#macro hud>
<@authorize "ROLE_ADMIN"> 
<#include "/otherobjects/templates/legacy/blocks/toolbar.ftl" />
</@authorize>
</#macro>

<#-- 
Renders the contents of the block if the roles match.
-->
<#macro authorize ifAllGranted ifAnyGranted="" ifNoneGranted="">
<#if security.authorize(ifAllGranted,ifAnyGranted,ifNoneGranted) >
<#nested>
</#if>
</#macro>

<#-- 
Displays flash messages.
-->
<#macro showFlashMessages>
<#assign msgs = flash.messages! >
<#if msgs?has_content >
<div class="oo-flash">
<#list msgs as msg>
<p class="oo-flash-${msg.type}">${msg.message}</p>
</#list>
</div>
</#if>
</#macro>

<#--
Displays up to 10 paragraphs of Lipsum.
-->
<#macro lipsum count=1>
<#assign paras = ["Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean pulvinar, magna non congue egestas, purus arcu aliquam est, at scelerisque lorem lectus eget mauris. Morbi nec sapien. Duis hendrerit, turpis non vehicula interdum, enim enim venenatis arcu, eget fermentum magna sem vitae neque. Pellentesque mauris enim, laoreet eu, facilisis et, vulputate non, enim. Pellentesque sollicitudin vehicula massa. Sed eros. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nunc laoreet, ipsum vel lacinia egestas, sem nibh mattis odio, non cursus tortor dolor ut neque. Vivamus non massa. Quisque ac pede sit amet lacus luctus mollis.", 
"Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Fusce fringilla. Sed vulputate, orci dapibus eleifend molestie, massa metus mollis enim, eget gravida augue nunc viverra nisl. Etiam dignissim mauris quis massa. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Donec ac ligula. Quisque vel justo. Phasellus posuere nisl in quam. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Aenean non eros. Ut semper eros et urna.", 
"Vestibulum velit enim, porta mollis, mollis eu, ultrices ac, sapien. Aliquam urna metus, placerat vel, condimentum in, blandit nec, tellus. Aenean ut nibh. Fusce adipiscing massa quis tellus. Phasellus pulvinar nibh id lectus. Morbi sem. Vivamus molestie tempor libero. Integer aliquam mollis sapien. Etiam ante. Sed eu libero. Sed ut quam et purus suscipit eleifend. Sed libero magna, tincidunt ut, venenatis quis, volutpat at, lectus. Aenean convallis. Nunc non lacus.", 
"Mauris faucibus. Cras accumsan pellentesque ipsum. In hac habitasse platea dictumst. Donec aliquet bibendum augue. Cras viverra tincidunt est. Sed iaculis. Donec nulla ligula, ultricies ac, ullamcorper in, tincidunt volutpat, sem. Morbi a quam. Morbi nec odio vitae dui vulputate elementum. Duis lacus sapien, adipiscing at, tempor sit amet, faucibus in, sem.",
"Vestibulum pulvinar bibendum sem. Fusce elementum. Mauris viverra ante non massa. Morbi vel ligula id orci fermentum sagittis. Mauris lorem. Sed convallis. Phasellus quis nibh. Maecenas massa massa, lacinia quis, suscipit vitae, tincidunt quis, massa. Suspendisse interdum tempus enim. Ut sed erat. Vestibulum at urna vitae mauris sollicitudin porttitor. Curabitur vulputate nisl ac turpis. Aenean massa. Nam placerat mauris vel turpis. Aliquam aliquet. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Pellentesque lectus nisi, venenatis eu, elementum vitae, sollicitudin id, leo. Cras sagittis. Aenean non orci at lectus dapibus dapibus.", 
"Nulla tincidunt massa aliquet libero ultrices pretium. Nulla sit amet sapien. Fusce nec ipsum. Pellentesque dapibus, augue quis convallis condimentum, tellus sapien elementum est, ut tincidunt purus nisi quis tellus. Vivamus in lorem. Pellentesque at nisi ac pede condimentum varius. Aenean quis felis. Cras ante est, euismod a, lacinia non, condimentum sed, ipsum. Cras viverra. Sed faucibus enim a massa. Aenean nulla. Fusce lacinia, urna a commodo sollicitudin, urna metus tristique odio, quis lobortis dolor nisl vel diam. Pellentesque molestie mattis lectus. Quisque blandit ante id nisl. In rutrum leo et tortor. Donec tincidunt feugiat tellus. Sed porttitor semper velit. Etiam orci.",
"Vivamus lectus risus, aliquam sit amet, scelerisque ut, dapibus id, lectus. Nam scelerisque. Phasellus in pede. Proin mi. Duis lobortis, lorem a ultricies porta, turpis arcu mollis nunc, ut hendrerit erat sapien sit amet mi. Fusce luctus. Donec vel purus non purus euismod mattis. Aenean vestibulum placerat tortor. Nulla facilisi. Quisque nisl lorem, egestas quis, auctor scelerisque, vehicula et, nunc. Vestibulum rutrum, erat vitae adipiscing feugiat, risus mauris vestibulum orci, id sodales dui turpis at nisi.", 
"Nunc aliquam diam luctus lorem. Morbi a justo eget risus volutpat elementum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Etiam sollicitudin. Sed a nisl vel urna venenatis condimentum. Ut malesuada. Fusce augue libero, ultrices et, viverra vitae, mollis sed, odio. In erat. Nulla ac purus. Nulla facilisi. Nulla volutpat, augue eu eleifend blandit, urna nisl euismod magna, quis porta eros lectus et urna. Quisque sit amet eros. Cras at nulla a nunc aliquet convallis. Fusce pellentesque leo vitae neque. Integer suscipit convallis risus. Nulla sollicitudin arcu a nulla. Nunc eget libero.",
"Nullam nec nisi. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Phasellus mauris. Suspendisse lorem nulla, interdum ac, sodales eu, tincidunt vel, ante. Mauris sapien sem, porta eget, facilisis id, gravida in, lectus. Maecenas tristique neque quis dui. Aenean pulvinar, orci in ultricies elementum, nisl purus rhoncus ipsum, a malesuada arcu erat eu metus. Sed massa. Aliquam sed ipsum eget nisi faucibus pharetra. Donec id erat. Aenean consectetuer erat ut neque. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Morbi congue velit id arcu. Nullam vitae nibh non dolor rutrum convallis. Mauris ligula odio, fermentum in, tempor a, vulputate vel, urna.", 
"In hac habitasse platea dictumst. Fusce ullamcorper posuere lectus. Suspendisse potenti. Quisque aliquet erat nec neque. Aliquam eget dui vel purus vestibulum feugiat. Praesent blandit erat at odio. Nam faucibus pede ut ipsum. Donec lacinia eleifend velit. Pellentesque cursus, sem eu scelerisque malesuada, tellus metus lobortis justo, quis ornare quam tellus sed nibh. Maecenas non urna. Vivamus turpis justo, gravida sed, auctor a, tempus a, sapien. Aliquam erat volutpat. Aliquam erat volutpat. Ut vitae nulla id ipsum laoreet feugiat."]>
<#list paras as para>
<#if (para_index > 9) || (para_index > (count - 1)) >
<#break />
</#if>
${para}
</#list>
</#macro>

<#--
Displays object contents.
-->
<#macro inspect obj>
${objectInspector.toHtml(obj)}
</#macro>

<#--
Format as Textile.
-->
<#macro format text>
${formatTool.formatTextile(text)}
</#macro>

<#--
Functions
-->
<#function action actionName>
<#return url("/_action/${actionName}") >
</#function>

<#function url path>
<#return "${urlTool.getUrl(path)}">
</#function>

<#function resourceUrl path>
<#return "${urlTool.getResourceUrl(path)}">
</#function>

<#function msg message>
<#return "${formatTool.getMessage(message)}">
</#function>





<#--
Form Macros
-->


<#macro renderForm typeDef>
<script>
function addToList()
{
	// Get field
	var f = $('.oo-list-empty-field').at(0);
	
	// Insert copy
	n = $(f.node.cloneNode(true));
	var a = n.children("INPUT").toArray();
	for(var i=0; i<a.length; i++)
		a[i].disabled=false;
	n.setStyle({border:"1px solid red!important", display:"block"});
	console.log(n);
	f.insert(n.node, 'after');
}
function removeFromList()
{
	// Get field
	var all = $('.oo-list-last-field');
	var f = all.at(all.length-1);
	// Insert copy
	f.parents().at(0).node.removeChild(f.node);
}
</script>
<@renderType typeDef "object." />
</#macro>

<#--
Renders the form parts for a typeDef. Used for main form and also for components.
-->
<#macro renderType typeDef prefix empty=false>
<table class="oo-edit">
<thead>
<tr>
<th>Field</th>
<th>Value</th>
<th>Help</th>
</tr>
</thead>
<tbody>
<#list typeDef.properties as prop>
<@renderProperty prop prefix empty />
</#list>
</tbody>
</table>
</#macro>

<#--
Renders a property for the form.
-->
<#macro renderProperty prop prefix empty>
<tr>
<td class="oo-label">${prop.label} <#if prop.required><span class="oo-required">*</span></#if></td>
<td>
<p>
	<@renderField prop prop.type "${prefix}${prop.fieldName}" empty /> 
</p>
</td>
<td style="color:red; font-weight:normal!important;">
<#if status??>
<p><@spring.showErrors "<br>"/></p>
</#if>
</td>
</tr>
</#macro>

<#--
Renders a field inputter by choosing the correct inputter renderer. Also handles lists and components.
-->
<#macro renderField prop type path empty=false>
	<#if type == "list" >
  		<#assign status = springMacroRequestContext.getBindStatus("${path}")>
  		<p><a href="javascript:addToList();">add</a> | <a href="javascript:removeFromList();">remove</a></p>
		<#if status.actualValue?is_enumerable>
			<#list status.actualValue as item>
	  			<p class="oo-list-last-field">@renderField prop prop.collectionElementType "${path}[${item_index}]" /></p>
	  		</#list>
	  		<p style="display:none;" class="oo-list-empty-field">@renderField prop prop.collectionElementType "${path}[${status.actualValue?size}]" true/></p>
	  	<#else>
  			${status.value}
  		</#if>
  	<#elseif type == "component" >
  		<#assign status = springMacroRequestContext.getBindStatus("${path}")>
  		${status.value!}
		<#if status.value??>
		@renderType prop.relatedTypeDef "${path}." false/>
		<#else>
		@renderType prop.relatedTypeDef "${path}." true/>
		</#if>  		
	<#elseif type == "date" >
  		<@formDate "${path}" />
  	<#elseif type == "text" >
  		<@spring.formTextarea "${path}"  "class=\"textarea\""/>
	<#elseif type == "boolean" >
  		<@formCheckbox "${path}"  "class=\"checkbox\""/>
	<#else>
  		<#if empty>
  		<input type="text" name="${path?substring(7)}" class="text" xdisabled="true" />
  		<#else>
  		<@spring.formInput "${path}" "class=\"text\"" />
  		</#if>  	
  	</#if>
</#macro>

<#--
Renders a checkbox for a boolean property.

Note: when values come back they may be strings (and not typed)
-->
<#macro formCheckbox path attributes="">
    <#assign status = springMacroRequestContext.getBindStatus("${path}")>
    <input type="checkbox" id="${status.expression}" name="${status.expression}" value="true"
       <#if status.value?? && ((status.value?is_boolean && status.value) || (!status.value?is_boolean && status.value == "true"))> checked="checked" </#if> ${attributes}
    <@spring.closeTag/>
    <#-- Hidden field to set value to false when checkbox not checked -->
    <input type="hidden" name="_${status.expression}" value="false" <@spring.closeTag/>
</#macro>

<#--
Renders a textbox for a date property.
-->
<#macro formDate path attributes="">
	<@spring.bind "${path}"/>
	<#assign status = springMacroRequestContext.getBindStatus("${path}")>
	<#if status.value?? && status.value?is_date>
    	<#assign stringStatusValue=status.value?date?string("yyyy-MM-dd")>
	<#else>
  	  	<#assign stringStatusValue=status.value?default("")>
	</#if>
	<input type="text" class="text" id="${status.expression}" name="${status.expression}" value="${stringStatusValue}" value="">
</#macro>	
 