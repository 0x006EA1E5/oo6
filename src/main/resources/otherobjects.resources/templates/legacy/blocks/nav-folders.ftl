<#macro ooFolderNavList treeItem>
<#if treeItem.children?has_content>
<ul>
<#list treeItem.children as f>
	<#--
	#if($parameters.folder.isChildOf($f) || $parameters.folder == $f)
		#if($f.allChildren.size() >0)
			class="oo-folder-full-open"
		#else
			class="oo-folder-empty-open"
		#end
	#end>
	#else
	-->
	<#if f.children?has_content>
	<li class="oo-folder-open">
	<#else>
	<li class="oo-folder-empty">
	</#if>
	<span></span><a href="${oo.url("/otherobjects/workbench/list/"+f.item.id)}">${oo.msg(f.item.label)}</a>	
	<@ooFolderNavList f/>
	</li>

</#list>
</ul>
</#if>
</#macro>

<div class="oo-navigation">
<ul class="oo-folders">
<li class="oo-folder-full-open"><span onclick="ooExpandContract(this)"></span><a href="$path.addParameter("folder", $root)">Home</a>
<@ooFolderNavList daoService.getDao("folder").folderTree />
</li>
</ul>
</div>


