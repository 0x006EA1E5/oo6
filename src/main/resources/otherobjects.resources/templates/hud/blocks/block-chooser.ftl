<div class="oo-panel oo-text-style" style="width:100%;">

	<div class="oo-title"><big>Choose a block to insert </big></div>

	<div class="oo-listing">
	  	<table style="width:750px;">
		  	<thead><tr><th width="750"></th></tr></thead>  	
		  	<tbody>
				<#list daoTool.get("baseNode").getAllByJcrExpression("/jcr:root/designer//element(*) [@ooType = 'org.otherobjects.cms.model.TemplateBlock'] order by @label") as block>
				<tr><td width="750" xclass="oo-row-button">
				<div id="${block.code}" class="oo-row-button">${block.label}<br>
				<small>${block.code!}</small>
				</div>
				</td></tr>
				</#list>	  	
		   	</tbody>
		   	<tfoot><tr><td width="750"></td></tr></tfoot>
	  	</table>
	</div>	
	
	<div class="oo-actions" style="width:750px;">
		<div class="oo-action"><div class="oo-button oo-button-grey oo-center-text" id="OoFormClose">Close</div></div>
	</div>

</div>

<script>
Ojay('#OoFormClose').on('click', function(el,e) {
	Ojay('#OoMenu').setStyle({display:"none"});
	Ojay('#OoMenu').node.innerHTML = "";
});

// Add event to block choice buttons
Ojay(".oo-row-button").on('click', function(el2,e) {
	var regionCode ='${regionCode}';
	var blockCode = el2.node.id;
	Ojay.HTTP.GET('/otherobjects/block/create/'+blockCode+'?resourceObjectId='+resourceObjectId+'&templateId='+ooTemplateId+'&regionCode='+regionCode, {}, {
		onSuccess: function(response) {
			Ojay('#oo-region-'+regionCode).insert(response.responseText,'top');
			ooSaveTemplateDesign();
    	}
    });
	Ojay('#OoMenu').setStyle({display:"none"});
	Ojay('#OoMenu').node.innerHTML = "";
	e.stopEvent();	
});
</script>