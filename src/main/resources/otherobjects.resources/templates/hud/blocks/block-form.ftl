<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />
<#import "/forms.ftl" as forms />

<div class="oo-panel oo-text-style" style="width:100%;">

<#if resourceObjectForm>
<form id="OoForm" method="post" action="${oo.url("/otherobjects/form")}">
<#else>
<form id="OoForm" method="post">
</#if>

	<div class="oo-title"><big>Editing: What we do </big></div>

	<div class="oo-listing">
	  	<table style="width:750px;">
	  	<thead>
	  	<tr>
	  	<th colspan="2" width="750"></th>
	  	</tr>
	  	</thead>  	
	  	
	  	<#if blockData??>
			<#-- Existing block -->
			<input type="hidden" class="hidden" name="_oo_id" value="${blockData.id}" />
			<input type="hidden" class="hidden" name="_oo_type" value="${blockData.typeDef.name}" />
			<#if blockGlobal><input type="text" class="text" name="code" value="blockData" /></#if>
			<@renderForm blockData.typeDef/>
		<#else> 
			<#-- New block --> 
			<input type="hidden" class="hidden" name="_oo_containerId" value="${location}" />
			<input type="hidden" class="hidden" name="_oo_type" value="${typeDef.name}" />
			<#-- <#if blockGlobal><input type="text" class="text" name="code" value="blockData" /> </#if> -->
			<@renderForm typeDef true/>
		</#if> 
	
	  	<tbody>
	  	
	   	</tbody>
	  	<tfoot>
	  	<tr class="oo-listing-arrow">
		<td width="200"></td>
	  	<td width="650">
		
		</td>
	  	</tr>
	  	</tfoot>
	  	</table>
	  </div>	
	
	  <div class="oo-actions" style="width:750px;">
		<div class="oo-action"><div class="oo-button oo-button-grey oo-center-text" id="OoFormClose">Close</div></div>
		<div class="oo-action"><div class="oo-button oo-button-red oo-center-text" id="OoFormSave">Save</div></div>
		<div class="oo-action"><div class="oo-button oo-button-green oo-center-text" id="OoFormPublish">Publish</div></div>
		<#--
		 <div class="oo-action"><div class="oo-button oo-button-blue oo-center-text oo-arrow">More</div></div>
		-->
	</div>
</form>	
	
</div>


<script type="text/javascript">

// TODO setFormFocus();

Ojay('#OoFormSave').on('click', function(el,e) {
	ooDisableFormTemplates();
	ooSaveForm('${blockReference.id}', true);
});

Ojay('#OoFormClose').on('click', function(el,e) {
	Ojay('#OoMenu').setStyle({display:"none"});
	Ojay('#OoMenu').node.innerHTML = "";
});
Ojay('#OoFormPublish').on('click', function(el,e) {
	Ojay.HTTP.POST(ooBaseUrl + 'otherobjects/block/publish/${blockReference.id}?resourceObjectId='+resourceObjectId).insertInto(Ojay('#oo-block-${blockReference.id}').parents());
	Ojay('#OoMenu').setStyle({display:"none"});
	
});

Ojay('#OoForm').on('submit', function(element, e) {
	ooDisableFormTemplates();
	ooSaveForm('${blockReference.id}', true);
});


function ooDisableFormTemplates()
{
	// Disable add the elements that are in the list element template
	$('.oo-list-template').descendants("input").set({disabled:true});
	$('.oo-list-template').descendants("textarea").set({disabled:true});
	$('.oo-list-template').descendants("select").set({disabled:true});
	$('.oo-list-template').descendants("checkbox").set({disabled:true});
	$('.oo-list-template').descendants("radio").set({disabled:true});
	$('.oo-list-template').descendants("button").set({disabled:true});
}

function ooSaveForm(blockId, hide) 
{
	var formObject = document.getElementById('OoForm'); 
	YAHOO.util.Connect.setForm(formObject); 
	var callback = { customevents:{ 
		onSuccess:function(eventType, args) { 
			if(hide) 
				Ojay('#OoMenu').setStyle({display:"none"});
			Ojay.HTTP.GET(ooBaseUrl + 'otherobjects/block/get/'+blockId+'?resourceObjectId='+resourceObjectId).insertInto(Ojay('#oo-block-'+blockId).parents());
		}
	}};
	var cObj = YAHOO.util.Connect.asyncRequest('POST', ooBaseUrl + 'otherobjects/workbench/save', callback );
	return false;
} 
</script>

<#macro renderForm typeDef empty=false>
<@renderType typeDef "object." empty/>
</#macro>

<#macro renderType typeDef prefix empty=false>
<#list typeDef.properties as prop>
<@renderProperty prop prefix empty />
</#list>
</#macro>

<#macro renderProperty prop prefix empty>
<tr class="oo-listing-field">
  	<td width="200">
  		${prop.label} <#if prop.required><span class="oo-required">*</span></#if>
  	</td>
  	<td width="600">
  		<@renderField prop prop.defaultFieldType "${prefix}${prop.fieldName}" empty />
  		<#if ooStatus??>
		<p><@forms.showErrors ooStatus "<br>"/></p>
		</#if>
  	</td>
</tr>
</#macro>

<#macro renderField prop type path empty=false>
	<#if type == "list" >
  		<@forms.bind path />
  		<#assign listOoStatus = ooStatus />
  		<#assign nextIndex = 0 />
  		<p><a href="javascript:addToList();">add</a> | <a href="javascript:removeFromList();">remove</a></p>
		<#if listOoStatus.actualValue?? && listOoStatus.actualValue?is_enumerable>
			<#list listOoStatus.actualValue as item>
	  			<p class="oo-list-last-field"><@renderField prop prop.collectionElementType "${path}[${item_index}]" /></p>
	  		</#list>
	  		<#assign nextIndex = listOoStatus.actualValue?size />
  		</#if>
  		<div style="display:none;" class="oo-list-empty-field oo-list-template">
  		<@renderField prop prop.collectionElementType "${path}[${nextIndex}]" true/>
  		</div>
  		<script>var nextIndex = ${nextIndex};</script>
  	<#elseif type == "component" >	
  		<#if !empty>
	  		<@bind path />
		</#if>
  		<#if !empty && ooStatus.value??>
			<@renderType prop.relatedTypeDef "${path}." false/>
		<#else>
			<@renderType prop.relatedTypeDef "${path}." true/>
		</#if>
	<#elseif type == "reference" >
		<#if prop.relatedTypeDef.store =="jackrabbit">
			<#assign options = daoTool.get(prop.relatedType).getAllByType(prop.relatedType) />
		<#else>
			<#assign options = daoTool.get(prop.relatedType).all />
		</#if>
  		<@forms.formSingleSelect "${path}" options "" empty/>
	<#elseif type == "date" >
  		<@forms.formDate "${path}" "" empty/>
	<#elseif type == "time" >
  		<@forms.formTime "${path}" "" empty/>
	<#elseif type == "timestamp" >
  		<@forms.formTimestamp "${path}" "" empty/>
  	<#elseif type == "text" >
  		<@forms.formTextarea "${path}" "" empty/>
	<#elseif type == "boolean" >
  		<@forms.formCheckbox "${path}" "" empty/>
	<#elseif type == "transient" >
		<@forms.formFile "${path}" "" empty/>
	<#elseif type == "string" >
  		<@forms.formInput "${path}" "" "text" empty />  		
	<#elseif type == "none" >
  		-  		
	<#else>
  		<@forms.formInput "${path}" "" "text" empty />
  	</#if>
</#macro>

<#macro bind path>
    <#assign ooStatus = springMacroRequestContext.getBindStatus(path)>
    <#if ooStatus.value?exists && ooStatus.value?is_boolean>
	    <#assign stringStatusValue=ooStatus.value?string>
	<#else>
    	<#assign stringStatusValue=ooStatus.value?default("")>
	</#if>
</#macro>
