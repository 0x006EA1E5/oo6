<#--
TODO Remove nasty path hacks
-->
<#import "/oo.ftl" as oo>
<#assign file = daoTool.get("baseNode").getByPath("/libraries/files/${tag.id}") />
<div class="oo-file">
<a href="${oo.resourceUrl("/data/files/"+file.code)}"><img src="${oo.resourceUrl("/site/static/graphics/file-icons/${file.extension}-32.png")}" width="32" height="32"></a>
<#-- <#if tag.caption?has_content>${tag.caption}<#else>${file.label}</#if><br> -->
<a onClick="javascript: pageTracker._trackPageview('/downloads/${tag.id}');" href="${oo.resourceUrl("/data/files/"+file.code)}">${file.code}</a><br> 
<small>${formatTool.formatFileSize(file.fileSize)}</small>
</div>