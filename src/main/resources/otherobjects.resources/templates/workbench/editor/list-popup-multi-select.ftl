<#import "/spring.ftl" as spring />
<#import "/oo.ftl" as oo />

<#-- need to separate into simpler ftls as in OO5 -->

<#assign pageTitle = "Select a ${type.label}" />
<#assign popup = true />

<#include "/otherobjects/templates/workbench/shared/header.ftl" />

<div id="ooContent"> 
<div class="oo-content">

<h2>${pageTitle}</h2>
${RequestParameters.fieldName}

<#if type.imageProperty?? && type.imageProperty == "self">
<#assign viewMode = "thumbnails"/>
<#else>
<#assign viewMode = "list"/>
</#if>

<#-- assign viewMode = RequestParameters.view!"list"/ -->

<#if viewMode == "thumbnails">

	<table class="oo-listing">
	<thead>
	<tr>
	<th colspan="5">Items</th>
	</tr>
	</thead>
	<tbody>
	<tr>
	<#list items as item>
	
	<#if item_index % 5 == 0></tr><tr></#if>
		<td class="oo-image">
	   	 <a href="javascript:window.opener.ooPopupSelect('${RequestParameters.fieldName}','${item.editableId}','${item.ooLabel}','${oo.resourceUrl(item.ooIcon)}','${item.thumbnailPath}');window.close();"> 	   		
   		 
	   		<strong>${item.wikiCode}</strong><br />
	   		<strong>${item.label}</strong><br />
	   		<img src="${item.thumbnailPath!""}" width="100" height="100"><br />Select</a>
	   		<#--<a href="${oo.url('/otherobjects/workbench/edit/${item.editableId}')}">Edit</a>-->
		</td>
	</#list>
	</tr>
	</tbody>
	</table>

<#else>

	<table class="oo-listing">
	<thead>
	<tr>
	<th>Icon</th>
	<th>Name</th>
	<th>Type</th>
	<th>Action</th>
	</tr>
	</thead>
	<tbody>
	<#list items as item>
	<#if ! item.class.name?ends_with("Folder") && ! item.class.name?ends_with("PublishingOptions")>  <!-- FIXME -->
	 <tr>
		<td class="oo-icon">
			<p><img src="${oo.resourceUrl(item.ooIcon!"")}" width="19" height="16"></p>
    	</td>
    	<td><a class="oo-<#if item.published?? && item.published>live<#else>edited</#if>" href="javascript:window.opener.ooPopupSelect('${RequestParameters.fieldName}','${item.editableId}','${item.ooLabel}','${oo.resourceUrl(item.ooIcon)}','');window.close();">${item.ooLabel!}</a></td>
		<#if item.linkPath??>
			<td title="${item.typeDef.name}"><p>${item.typeDef.label}</p></td>
		<#else>
			<td title="${item.class.name}"><p>${item.class.name}</p></td>	
		</#if>
		<td class="oo-action"><#if item.linkPath??><a href="javascript:window.opener.ooPopupSelect('${RequestParameters.fieldName}','${item.editableId}','${item.ooLabel}','${oo.resourceUrl(item.ooIcon)}','');">Select</a></#if></td>
	</tr>
	</#if>
	</#list>
	</tbody>
	</table>

</#if>
</div>



<div class="oo-actions">
<h2>Actions</h2>
<script>
function ooSendSelection() {
	// Copy selected item labels to parent form
	var selectionList = $('OoSortList');
	var originalList = window.opener.document.getElementById('${RequestParameters.fieldName}');
	
	// remove the 'remove' links from each list item
	originalList.innerHTML = selectionList.innerHTML.replace(/<a.*?a>/g, "");
	window.close();
}
</script>
<ul>
<li class="divider"><strong><a href="javascript:ooSendSelection()">Confirm selection</a></strong></li>
</ul>
</div>
<#--
<ul>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${id}?container=${folder.id}')}">New ${type.label} ...</a></li>
<#list folder.allAllowedTypes as type>
<#if type?exists>
<li class="divider"><a href="${oo.url('/otherobjects/workbench/create/${type.name}?container=${folder.id}')}">New ${type.label} ...</a></li>
</#if>
</#list>
</ul>
</div>
-->

<#include "/otherobjects/templates/workbench/shared/footer.ftl" />


<#macro selection>
<#--
#set($basket = $objectsTool.getMultiSelectBasket($parameters.object.reassociate(), $parameters.collectionPropertyName))
#set($collectionType = $parameters.object.getObjectType().getProperty($parameters.collectionPropertyName).type)

<h2>Your Selection</h2>

<p style="clear:both;"><strong>$basket.size() item(s) selected</strong></p>

<form action="$link.setPath("popup-multi-select-add.html")" method="post">
<ul id="OoSortList" class="selection">
#foreach($item in $basket)
#set($i = $velocityCount - 1)
<li id="OoSortList_OoSortList$i">
#if($isImage)
#set($thumbnail = "")
#set($thumbnail = $item.thumbnail)
#if($thumbnail.width)<img src="$thumbnail.url" width="100" height="100" style="margin:5px 0px 2px 0px;" />#end
#end
<a style="position:absolute;clear:none; width:20px; height:16px; padding:3px; left:130px; text-align:center;" href="$link.useCurrentResource().addParameter("optionListProvider",$parameters.optionListProvider).addParameter("object",$parameters.object).addParameter("objectType",$parameters.objectType).addParameter("parentObjectType",$parameters.parentObjectType).addParameter("collectionPropertyName",$parameters.collectionPropertyName).addParameter("collectionFieldName",$parameters.collectionFieldName).setAction("com.otherobjects.cms.actions.MultiSelectActions.doRemove").addActionParameter("index",$i)"><img src="$static.setPath("/otherobjects/static/graphics/close-button.gif")"></a>
<span class="oo-drag" #if ($collectionType == "List" && $basket.size()>1)onmouseover="this.className='oo-drag oo-drag-select'" onmouseout="this.className='oo-drag'"#end>#if($isFile)$item.objectIcon.html #end$item.objectLabel</span>
##Short term fudge - if this object is a set then render links differently
#if ($collectionType == "Set")
<input type="hidden" name="P$parameters.collectionFieldName[]$i" value="$parameters.encode($item)"/>
#else
<input type="hidden" name="P$parameters.collectionFieldName[$i]" value="$parameters.encode($item)"/>
#end
</li>
#end
</ul>
<input type="hidden" name="Pobject" id="Pobject" value="$parameters.encode($parameters.object)"/>
<input type="hidden" name="collectionPropertyName" id="collectionPropertyName" value="$parameters.collectionPropertyName"/>
#if($parameters.parentObjectType.isVersioned())
<input type="hidden" name="action" value="com.othermedia.webkit.hibernate.versioned.VersionedFormActions.doSaveCollection"/>
#else
<input type="hidden" name="action" value="com.othermedia.webkit.forms.FormActions.doSaveCollection"/>
#end
<input type="hidden" id="OoSortOrder" name="order" value=""/>
##<input type="submit" value="Confirm selection"/>
</form>

#if ($collectionType == "List" && $basket.size()>1)
<p style="clear:both;"><br/><strong>Drag the items in the list to reorder them.</strong></p>
#end

<p style="clear:both;"><br/>When you have selected all the objects you
require please click the <strong>Confirm selection</strong>
action button on the right of this window.</p>

#if ($collectionType == "List")

	<script type="text/javascript">
		Sortable.create("OoSortList", {dropOnEmpty:true,constraint:'vertical',onUpdate:listUpdate});
		
		function listUpdate(c)
	    {
			$("OoSortOrder").value = Sortable.serialize(c);
//			ooReorderCollection($("Pobject").value, $("collectionPropertyName").value, Sortable.serialize(c))
		}
		

	</script>
#end

#else

#set($basket = $objectsTool.getMultiSelectBasket($parameters.object.reassociate(), $parameters.collectionPropertyName))
#set($collectionType = $parameters.object.getObjectType().getProperty($parameters.collectionPropertyName).type)

<h2>Your Selection</h2>

<p style="clear:both;"><strong>$basket.size() item(s) selected</strong></p>

<form action="$link.setPath("popup-multi-select-add.html")" method="post">
<ul id="OoSortList" class="selection">
#foreach($item in $basket)
#set($i = $velocityCount - 1)
<li id="OoSortList_OoSortList$i"#if($isImage) style="clear:both;width:150px;padding-bottom:10px;border-bottom:1px solid #999999;"#end>
#if($isImage)
#set($thumbnail = "")
#set($thumbnail = $item.thumbnail)
#if($thumbnail.width)<img src="$thumbnail.url" width="100" height="100" style="margin:5px 0px 2px 0px;" />#end
#end
<a style="position:absolute;clear:none; width:20px; height:16px; padding:3px; left:130px; text-align:center;" href="$link.useCurrentResource().addParameter("object",$parameters.object).addParameter("optionListProvider",$parameters.optionListProvider).addParameter("objectType",$parameters.objectType).addParameter("parentObjectType",$parameters.parentObjectType).addParameter("collectionPropertyName",$parameters.collectionPropertyName).setAction("com.otherobjects.cms.actions.MultiSelectActions.doRemove").addActionParameter("index",$i).addParameter("inverseField", $parameters.inverseField).addParameter("inverse", $parameters.inverse)"><img src="$static.setPath("/otherobjects/static/graphics/close-button.gif")"></a>
<span class="oo-drag" #if ($collectionType == "List" && $basket.size()>1)onmouseover="this.className='oo-drag oo-drag-select'" onmouseout="this.className='oo-drag'"#end>#if($isFile)$item.objectIcon.html #end$item.objectLabel</span>
##Short term fudge - if this object is a set then render links differently
#if ($collectionType == "Set")
<input type="hidden" name="Pcollection[]$i" value="$parameters.encode($item)"/>
#else
<input type="hidden" name="Pcollection[$i]" value="$parameters.encode($item)"/>
#end
</li>
#end
</ul>

<input type="hidden" name="Pobject" id="Pobject" value="$parameters.encode($parameters.object)"/>
<input type="hidden" name="collectionPropertyName" id="collectionPropertyName" value="$parameters.collectionPropertyName"/>
<input type="hidden" name="inverse" id="inverse" value="$parameters.inverse"/>
<input type="hidden" name="inverseField" id="inverseField" value="$parameters.inverseField"/>

#if($parameters.parentObjectType.isVersioned())
<input type="hidden" name="action" value="com.othermedia.webkit.hibernate.versioned.VersionedFormActions.doSaveCollection"/>
#else
<input type="hidden" name="action" value="com.othermedia.webkit.forms.FormActions.doSaveCollection"/>
#end
<input type="hidden" id="OoSortOrder" name="order" value=""/>
##<input type="submit" value="Confirm selection"/>
</form>

#if ($collectionType == "List" && $basket.size()>1)
<p style="clear:both;"><br/><strong>Drag the items in the list to reorder them.</strong></p>
#end

<p style="clear:both;"><br/>When you have selected all the objects you
require please click the <strong>Confirm selection</strong>
action button on the right of this window.</p>

#if ($collectionType == "List")

	<script type="text/javascript">
		Sortable.create("OoSortList", {dropOnEmpty:true,constraint:'vertical',onUpdate:listUpdate});
		
		function listUpdate(c)
	    {
			$("OoSortOrder").value = Sortable.serialize(c);
//			ooReorderCollection($("Pobject").value, $("collectionPropertyName").value, Sortable.serialize(c))
		}
		
	</script>

#end

#end
-->
</#macro>