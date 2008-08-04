
YAHOO.util.Event.onDOMReady(function() {
	//ooEnableBlockSelector();
	//ooEnableBlockManagement();
	//ooToggleHud("oo-main-hud");
	ooEnableKeyboardShortcuts();		
	
});

function ooEnableBlockManagement() {

	ooToggleHud("oo-main-hud");
	
	$(".oo-region").insert('<div class="oo-block-add">Add block here</div>','bottom');
	$(".oo-block-add").on('click', function(el,e) {
		ooInsertNewBlock(el);
	});
	$(".oo-block").insert('<div class="oo-block-delete">Delete block above</div>','bottom');
	$(".oo-block-delete").on('click', function(el,e) {
		ooDeleteBlock(el);
	});
	//YAHOO.util.Event.onDOMReady(YAHOO.example.DDApp.init, YAHOO.example.DDApp, true);
}

function ooInsertNewBlock(el) {
	ooToggleHud("oo-chooser-hud");
	
	// Remove previous events 
	$(".oo-chooser-button").forEach( function(ell) {YAHOO.util.Event.purgeElement(ell.node); });	
	
	// Find code of enclosing region
	var regionCode = el.parents(".oo-region")[0].id.substring(10);
	console.log(regionCode);
	
	// Add event to block choice buttons
	$(".oo-chooser-button").on('click', function(el2,e) {
		var blockCode = el2.node.id;
		Ojay.HTTP.GET('/otherobjects/block/create/'+blockCode+'?resourceObjectId='+resourceObjectId+'&templateId='+ooTemplateId+'&regionCode='+regionCode, {}, {
			onSuccess: function(response) {
				el.insert(response.responseText,'before');
				ooSaveTemplateDesign();
        	}
        });
		ooToggleHud("oo-chooser-hud");
		e.stopEvent();	
	});
}


function ooDeleteBlock(el) {
	el.node.parentNode.parentNode.removeChild(el.node.parentNode);
	ooSaveTemplateDesign();
}

function ooSaveTemplateDesign()
{
	// Detects region/blcok arrangement and serialises it to a JSON string 
	// with is then posted to the BlockController to save.
	var regions = new Array();
	Ext.select(".oo-region", true).each(function(e) {
		var blocks = new Array();
		e.select(".oo-block", true).each(function(e) {
			blocks[blocks.length]=e.id.slice(9);
		});
		regions[regions.length]={name:e.id.slice(10), blockIds:blocks};
	});
	var json = YAHOO.lang.JSON.stringify(regions);	
	Ojay.HTTP.POST('/otherobjects/block/saveArrangement?templateId='+ooTemplateId, {arrangement: json}, {});
}

function ooToggleHud(id) {

	// Allow HUD to be dragged out of the way
	// dd = new YAHOO.util.DD(id);

	var vp = Ojay.getViewportSize()
	var l = (vp.width - 825)/2;
	var t = (vp.height - 250)/2;

	var hud = $('#'+id);
	hud.setStyle({left: l+'px'});
	hud.setStyle({top: t+'px'});

	if(hud.getStyle('display')!='block')
		hud.show();
	else
		hud.hide();
	
}

function ooLoadHud(url) 
{
	Ojay.HTTP.GET('/otherobjects/blocks/render/oo-hud').insertInto('#oo-main-hud');//.evalScriptTags();
}


function ooEnableBlockSelector() {
	
	ooToggleHud("oo-main-hud");
	
	// Highlight blocks on mouse over
	$('.oo-block').on('mouseover', function(element, e) {
	
		// Enforce only one overlay
		if(document.getElementById("overlay") != null) return;
		
		var blockReferenceId=element.node.id.substring(9);
		
		// Add overlay div
		element.insert('<div id="overlay" class="oo-block-overlay"></div>', 'top');
		$('#overlay').setStyle({width:element.getWidth()-12+'px', height:element.getHeight()-12+'px'});
	
		// Set click handler
		$('#overlay').on('click', function(el, e) {
		    e.stopDefault();
			var overlay = $('#overlay').node;
			overlay.parentNode.removeChild(overlay);
			
			$('#oo-form-overlay').setStyle({display:"block"});
			Ojay.HTTP.GET('' + ooBaseUrl + '/otherobjects/block/form/'+blockReferenceId+'?resourceObjectId='+resourceObjectId).insertInto('#oo-form-overlay').evalScriptTags();
		});
	
		// Restore on mouse out
		$('#overlay').on('mouseout', function(element, e) {
			var overlay = $('#overlay').node;
			overlay.parentNode.removeChild(overlay);
		});
	
	});
}

function ooSubmitForm(blockId) 
{
	var formObject = document.getElementById('form'); 
	YAHOO.util.Connect.setForm(formObject); 
	var callback = { customevents:{ 
		onSuccess:function(eventType, args) { 
			$('#oo-form-overlay').setStyle({display:"none"});
			Ojay.HTTP.GET('/otherobjects/block/get/'+blockId+'?resourceObjectId='+resourceObjectId).insertInto('#oo-block-'+blockId);
		}
	}};
	var cObj = YAHOO.util.Connect.asyncRequest('POST', '/otherobjects/form/', callback );
	return false;
} 

function ooSetFormFocus()
{
	for(i=0; i<document.forms.length; i++)
	{
		for(j=0; j<document.forms[i].elements.length; j++)
		{
			var thisElement = document.forms[i].elements[j];
			if( thisElement.className == 'text' || thisElement.className == 'textarea')
			{
				thisElement.focus();
				return;
			}
		}
	}
}

// Add in shortcuts
// ?, Alt-1 etc
function ooEnableKeyboardShortcuts() {
	shortcut.add("Esc",function(){ooToggleHud("oo-main-hud");});
}

