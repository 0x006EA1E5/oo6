<#import "/oo.ftl" as oo />

<#--
#set($toolsClasses = "oo-edit-tools ${toolbarPos}")
#if($config.checkProperty("otherobjects.wiki.toolbar.scrollto"))
	#set($toolsClasses = "${toolsClasses} scrollto")
#end
-->

<div class="oo-edit-tools">


<p>
<span class="oo-toolstitle">Formatting:</span>
<a class="oo-format-bold" href="javascript:ooFormating('${expression}','*','*')" title="Insert bold text">Bold</a>
<a class="oo-format-italic" href="javascript:ooFormating('${expression}','_','_')" title="Insert italic text">Italic</a>
<a class="oo-format-heading4" href="javascript:ooFormating('${expression}','\n! ','\n')" title="Insert a large heading">Large Heading</a>
<a class="oo-format-heading5" href="javascript:ooFormating('${expression}','\n!! ','\n')" title="Insert a medium heading">Medium Heading</a>
<a class="oo-format-heading6" href="javascript:ooFormating('${expression}','\n!!! ','\n')" title="Insert a small heading">Small Heading</a>
<a class="oo-format-superscript" href="javascript:ooFormating('${expression}','^','^')" title="Insert superscript text">Superscript</a>
<a class="oo-format-subscript" href="javascript:ooFormating('${expression}','~','~')" title="Insert subscript text">Subscript</a>



<#-- <#import "/otherobjects-ng/templates/workbench/editor/edit-insert-buttons.tfl" /> -->

			<span class="oo-toolstitle">Insert:</span>
	<#--		<a class="oo-format-image" target="ooPopup" onclick="ooPopup();" href="${oo.url("/workbench/otherobjects/workbench/editor/list-popup-select.ftl")}.addParameter("objectType",{$objectsTool.getObjectType("CmsImage")} ).addParameter("fieldName","${expression}")" title="Insert an image...">Insert image...</a>  -->
		
	<a class="oo-format-image" target="ooPopup" onClick="ooPopup();" href="/otherobjects/workbench/list-popup-select/org.otherobjects.cms.model.CmsImage?fieldName=${expression}" title="Insert an image...">Insert image...</a>
	<a class="oo-format-file" target="ooPopup" onclick="ooPopup();" href="/otherobjects/workbench/list-popup-select/org.otherobjects.cms.model.CmsFile?fieldName=${expression}" title="Insert a file...">Insert file...</a>
	<a class="oo-format-linkexternal" target="ooPopup" onclick="ooPopup();" href="/otherobjects/workbench/list-popup-select/org.otherobjects.cms.model.CmsExternalLink?fieldName=${expression}" title="Insert a link...">Insert link...</a>






<span class="oo-toolstitle">Tables:</span>
<a class="oo-format-convert" href="javascript:ooWordConvert('${expression}');" title="Convert pasted tables from Word or Excel">Convert pasted tables from Word or Excel</a>	
<a id="tabler-button-${expression}" class="oo-format-table" href="javascript:ooShowHideGrid('${expression}')" title="Insert a table">Insert Table</a><span id="tabler-panel-${expression}" class="oo-create-table"><span id="tabler-text-${expression}"> </span><br /><img id="tabler-grid-${expression}" src="${oo.url("/otherobjects/static/graphics/create-table-blank.gif")} width="151" height="121" border="0" alt="" usemap="#tabler-map-${expression}">
<br />
<input id="tabler-caption-${expression}" type="checkbox" /><span class="oo-table-create-text">Add Caption</span><br />
<input id="tabler-headings-${expression}" type="checkbox" /><span class="oo-table-create-text">Add Column Headings</span></span>
<map name="tabler-map-${expression}">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',1,1)" coords="0,0,30,30" href="javascript:ooMakeTable('${expression}',1,1)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',1,2)" coords="0,31,30,60" href="javascript:ooMakeTable('${expression}',1,2)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',1,3)" coords="0,61,30,90" href="javascript:ooMakeTable('${expression}',1,3)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',1,4)" coords="0,91,30,120" href="javascript:ooMakeTable('${expression}',1,4)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',2,1)" coords="31,0,60,30" href="javascript:ooMakeTable('${expression}',2,1)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',2,2)" coords="31,31,60,60" href="javascript:ooMakeTable('${expression}',2,2)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',2,3)" coords="31,61,60,90" href="javascript:ooMakeTable('${expression}',2,3)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',2,4)" coords="31,91,60,120" href="javascript:ooMakeTable('${expression}',2,4)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',3,1)" coords="61,0,90,30" href="javascript:ooMakeTable('${expression}',3,1)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',3,2)" coords="61,31,90,60" href="javascript:ooMakeTable('${expression}',3,2)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',3,3)" coords="61,61,90,90" href="javascript:ooMakeTable('${expression}',3,3)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',3,4)" coords="61,91,90,120" href="javascript:ooMakeTable('${expression}',3,4)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',4,1)" coords="91,0,120,30" href="javascript:ooMakeTable('${expression}',4,1)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',4,2)" coords="91,31,120,60" href="javascript:ooMakeTable('${expression}',4,2)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',4,3)" coords="91,61,120,90" href="javascript:ooMakeTable('${expression}',4,3)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',4,4)" coords="91,91,120,120" href="javascript:ooMakeTable('${expression}',4,4)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',5,1)" coords="121,0,150,30" href="javascript:ooMakeTable('${expression}',5,1)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',5,2)" coords="121,31,150,60" href="javascript:ooMakeTable('${expression}',5,2)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',5,3)" coords="121,61,150,90" href="javascript:ooMakeTable('${expression}',5,3)">
<area shape="rect" onmouseover="ooSlideGrid('${expression}',5,4)" coords="121,91,150,120" href="javascript:ooMakeTable('${expression}',5,4)">
</map>
</p>

<#--
#if($config.checkProperty("otherobjects.wiki.quicklinks"))
-->


<#--
#end
-->

</div>