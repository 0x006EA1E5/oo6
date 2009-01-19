<#import "/oo.ftl" as oo>
<span class="oo-image-${tag.get("align", "default")!}">
<#if tag.get("link")??><a href="${tag.get("link")}"></#if>
<@oo.image daoTool.get("org.otherobjects.cms.model.CmsImage").getByPath("/libraries/images/${tag.id}") tag.getInteger("width") />
<#if tag.get("link")??></a></#if>
</span>