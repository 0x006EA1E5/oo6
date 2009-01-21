<#--
TODO Analytics tracking
TODO Custom class and id
TODO Internal link context and absolute links
TODO SSL links
TODO Remove spacing
-->
<#assign tracking = googleAnalyticsTool.getPath(tag.id) />
<a href="${tag.id}" <#if tracking?has_content>onClick="javascript: pageTracker._trackPageview('${tracking}');"</#if> <#if tag.id?starts_with("http")>target="_blank" class="external"</#if>><#if tag.caption?has_content>${tag.caption}<#else>${tag.id}</#if></a>