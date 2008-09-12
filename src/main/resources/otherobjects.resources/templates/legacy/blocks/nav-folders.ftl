<div class="oo-navigation">
<ul class="oo-folders">
<#list dao.folder.folders as f>
<li class="oo-folder-empty"><#if folder! == f><strong></#if><span></span>
<a href="${oo.url("/otherobjects/workbench/list/${f.id}")}">${oo.msg(f.label)}</a>
<#if folder! == f></strong></#if></li>
</#list>
</ul>
</div>