
YAHOO.util.Event.onDOMReady(function() {
	//ooEnableBlockSelector();
	//ooEnableBlockManagement();
	//ooToggleHud("oo-main-hud");
	//ooEnableKeyboardShortcuts();		
	//ooSaveTemplateDesign();
	
});

/**
 * ooEnableBlockSelector
 * 
 * Attaches rollover events to blocks which cause them to highlight
 * and edit when clicked.
 */
function ooEnableBlockSelector() {
	
	//ooToggleHud("oo-main-hud");
	
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
			ooBlockInEdit = blockReferenceId;
			
			$('#oo-form-overlay').setStyle({display:"block"});
			Ojay.HTTP.GET('' + ooBaseUrl + 'otherobjects/block/form/'+blockReferenceId+'?resourceObjectId='+resourceObjectId).insertInto('#oo-form-overlay').evalScripts();
		});
	
		// Restore on mouse out
		$('#overlay').on('mouseout', function(element, e) {
			var overlay = $('#overlay').node;
			overlay.parentNode.removeChild(overlay);
		});
	
	});
}

/**
 * ooEnableBlockManagement
 * 
 * Attaches add/delete block controls and events.
 */
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

/**
 * ooDeleteBlock
 * 
 * Deletes selected block.
 */
function ooDeleteBlock(el) {
	el.node.parentNode.parentNode.removeChild(el.node.parentNode);
	ooSaveTemplateDesign();
}

/**
 * ooInsertNewBlock
 * 
 * Overlays list of allowed blocks and inserts the chosen one.
 */
function ooInsertNewBlock(el) {
	ooToggleHud("oo-chooser-hud");
	
	// Remove previous events 
	$(".oo-chooser-button").forEach( function(ell) {YAHOO.util.Event.purgeElement(ell.node); });	
	
	// Find code of enclosing region
	var regionCode = el.parents(".oo-region")[0].id.substring(10);
	//console.log(regionCode);
	
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

/**
 * ooSaveTemplateDesign
 * 
 * Encodes arrangement of regions and blocks as JSON string and posts to backend
 * to save against template.
 */
function ooSaveTemplateDesign()
{
	// Detects region/blcok arrangement and serialises it to a JSON string 
	// with is then posted to the BlockController to save.
	var regions = new Array();
	$(".oo-region").forEach(function(e) {
		var blocks = new Array();
		e.descendants(".oo-block").forEach(function(e) {
			blocks[blocks.length]=e.node.id.slice(9);
		});
		regions[regions.length]={name:e.node.id.slice(10), blockIds:blocks};
	});
	var json = YAHOO.lang.JSON.stringify(regions);
	console.log(regions);
	Ojay.HTTP.POST('/otherobjects/block/saveArrangement?templateId='+ooTemplateId, {arrangement: json}, {});
}




























function ooSubmitForm(blockId) 
{
	return ooQuickSaveForm(blockId, true);
} 

function ooShowCreateForm(type, location) 
{
	$('#oo-form-overlay').setStyle({display:"block"});
	Ojay.HTTP.GET('' + ooBaseUrl + 'otherobjects/block/form/'+type+'?location='+location).insertInto('#oo-form-overlay').evalScripts();
} 

function ooQuickSaveForm(blockId, hide) 
{
	var formObject = document.getElementById('form'); 
	YAHOO.util.Connect.setForm(formObject); 
	var callback = { customevents:{ 
		onSuccess:function(eventType, args) { 
			if(hide) 
				$('#oo-form-overlay').setStyle({display:"none"});
			Ojay.HTTP.GET(ooBaseUrl + 'otherobjects/block/get/'+blockId+'?resourceObjectId='+resourceObjectId).insertInto('#oo-block-'+blockId);
		}
	}};
	var cObj = YAHOO.util.Connect.asyncRequest('POST', ooBaseUrl + 'otherobjects/form/', callback );
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

	Ojay.Keyboard.listen(document, 'ESCAPE', function() {
		
		var formObject = document.getElementById('form'); 
		if(formObject) {
			ooQuickSaveForm(ooBlockInEdit, false);
		}
		ooFadeHud()
    });
	
}


function ooFadeHud() {
	
	var hud = $('#oo-hud');
	
	if(hud.getStyle('opacity')=='0.1') {
		
		hud.animate({opacity: {to: '1.0'}}, 0.5);
	}
	else {
		hud.animate({opacity: {to: '0.1'}}, 0.5);//.hide();
	}


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
