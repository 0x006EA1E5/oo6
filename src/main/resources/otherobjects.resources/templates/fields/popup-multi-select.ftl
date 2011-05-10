<#if empty>
	<#assign expression = path?substring(7) />
<#else>
	<@bind path />
	<#assign expression = ooStatus.expression />
</#if>  	

<p>
<#--TODO, add support for other collection types 
<#if fieldProperty.type.equals("list") >
    <#-- need to get the actual list, from expression? 
	<#list expression as itemL>
		<@listItem itemToList=itemL/>
	</#list>
<#else>
	<@listItem itemToList=expression/>
</#if>
-->

<@listItem itemToList=expression/>

</p>
 <a class="arrow" target="ooPopup" onClick="ooPopup()" href="/otherobjects/workbench/list-popup-multi-select/${fieldProperty.relatedType}?fieldName=${expression}&parentObject=${object}">Choose...</a>
<#-- <a class="arrow" href="javascript:ooPopupRemove('${expression}')">Remove</a>



<#-- <a target="ooPopup" onClick="ooPopup()" href="com.otherobjects.cms.model.structure.Folder-L-1&amp;TobjectType=com.otherobjects.cms.model.library.CmsImage">Choose...</a> -->
<!-- <span id="com.othermedia.tom.model.TomArticleVersion.image:LABEL"><em>Nothing selected</em></span> -->

<#macro listItem itemToList>
	<#if ! ooStatus.value??>
		<input type="hidden" name="${itemToList}" value="" id="${itemToList}" />
		<#if fieldProperty.relatedTypeDef.imageProperty?? && fieldProperty.relatedTypeDef.imageProperty=="self">
		<a target="ooPopup" onClick="ooPopup()" href="/otherobjects/workbench/list-popup-multi-select/${fieldProperty.relatedType}?fieldName=${itemToList}">
		<img class="thumbnail" id="${itemToList}:IMAGE" width="100" height="100" src="${oo.resourceUrl('/otherobjects/static/workbench/assets/thumbnail-blank.png')}" />
		</a>
		<#else>
		<img id="${itemToList}:ICON" width="19" height="16" src="${oo.resourceUrl('/otherobjects/static/icons/blank.png')}" />
		<span id="${itemToList}:LABEL">Nothing selected</span>
		</#if>
	<#else>
		<input type="hidden" name="${itemToList}" value="${ooStatus.actualValue.editableId}" id="${itemToList}" />
		${fieldProperty.relatedTypeDef}
		<#if fieldProperty.relatedTypeDef.imageProperty=="self">
		<img id="${itemToList}:IMAGE" width="100" height="100" src="${ooStatus.actualValue.thumbnailPath}" />
		<#else>
		<img id="${itemToList}:ICON" width="19" height="16" src="${oo.resourceUrl(ooStatus.actualValue.ooIcon)}" />
		<span id="${itemToList}:LABEL">${ooStatus.actualValue.ooLabel}</span>
		</#if>
	</#if>
</#macro>  


