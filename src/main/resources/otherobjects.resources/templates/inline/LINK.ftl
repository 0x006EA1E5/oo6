<#--
TODO Analytics tracking
TODO Custom class and id
TODO Internal link context and absolute links
TODO SSL links
-->
<a href="${tag.id}" onClick="javascript: pageTracker._trackPageview('${googleAnalyticsTools.getPath(tag.id)}');"<#if tag.id?starts_with("http")>target="_blank" class="external"</#if>><#if tag.caption?has_content>${tag.caption}<#else>${tag.id}</#if></a>