<#import "/oo.ftl" as oo>
<@oo.image daoTool.get("org.otherobjects.cms.model.CmsImage").getByPath("/libraries/images/${tag.id}") tag.getInteger("width") />