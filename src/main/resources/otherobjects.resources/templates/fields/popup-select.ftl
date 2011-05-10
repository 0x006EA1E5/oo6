<#if empty>
	<#assign expression = path?substring(7) />
<#else>
	<@bind path />
	<#assign expression = ooStatus.expression />
</#if>  	

<p>
<#if ! ooStatus.value??>
	<input type="hidden" name="${expression}" value="" id="${expression}" />
	<#if fieldProperty.relatedTypeDef.imageProperty?? && fieldProperty.relatedTypeDef.imageProperty=="self">
	<a target="ooPopup" onClick="ooPopup()" href="/otherobjects/workbench/list-popup-select/${fieldProperty.relatedType}?fieldName=${expression}">
	<img class="thumbnail" id="${expression}:IMAGE" width="100" height="100" src="${oo.resourceUrl('/otherobjects/static/workbench/assets/thumbnail-blank.png')}" />
	</a>
	<#else>
	<img id="${expression}:ICON" width="19" height="16" src="${oo.resourceUrl('/otherobjects/static/icons/blank.png')}" />
	<span id="${expression}:LABEL">Nothing selected</span>
	</#if>
<#else>
	<input type="hidden" name="${expression}" value="${ooStatus.actualValue.editableId}" id="${expression}" />
	<#if fieldProperty.relatedTypeDef.imageProperty=="self">
	<img id="${expression}:IMAGE" width="100" height="100" src="${ooStatus.actualValue.thumbnailPath}" />
	<#else>
	<img id="${expression}:ICON" width="19" height="16" src="${oo.resourceUrl(ooStatus.actualValue.ooIcon)}" />
	<span id="${expression}:LABEL">${ooStatus.actualValue.ooLabel}</span>
	</#if>
</#if>
</p>
<a class="arrow" target="ooPopup" onClick="ooPopup()" href="/otherobjects/workbench/list-popup-select/${fieldProperty.relatedType}?fieldName=${expression}">Choose...</a>
<a class="arrow" href="javascript:ooPopupRemove('${expression}')">Remove</a>



<#-- <a target="ooPopup" onClick="ooPopup()" href="com.otherobjects.cms.model.structure.Folder-L-1&amp;TobjectType=com.otherobjects.cms.model.library.CmsImage">Choose...</a> -->
<!-- <span id="com.othermedia.tom.model.TomArticleVersion.image:LABEL"><em>Nothing selected</em></span> -->




