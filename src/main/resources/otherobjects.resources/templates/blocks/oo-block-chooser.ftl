<h1>Choose a block</h1>
<#list daoService.getDao("baseNode").getAllByJcrExpression("/jcr:root//element(*) [@ooType = 'org.otherobjects.cms.model.TemplateBlock']") as block>
<a class="oo-hud-button oo-chooser-button" id="${block.code}">${block.label}</a>
</#list>
