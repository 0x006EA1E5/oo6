var hudVisible = false;
var siteMinWidth = 1016;
var siteBodyOverflow = "auto"; // FIXME This needs to be recorded before entering hud.
var bodyWidth = Ojay('body').setStyle({'width' : YAHOO.util.Dom.getViewportWidth()})

Ojay.Keyboard.listen(document, 'ESCAPE', toggleHud);
Ojay('div.oo-icon').on('click',toggleHud); 


function toggleHud(el, e) {
	if(hudVisible) {
		// Animate hud closed
		Ojay('div.oo-edit-zones').node.innerHTML = "";
		Ojay('.oo-toolbar').animate({top:{from: 0, to: -60}}, 0.5);
		Ojay('html').wait(0.1).animate({paddingTop:{from: 100, to: 0}, paddingBottom:{from: 200, to: 0}}, 0.5).removeClass('oo');
		Ojay('body').
			wait(0.1).
			setStyle({'overflowX' : 'hidden'}).
			animate({left: {to:0}, width: {to: YAHOO.util.Dom.getViewportWidth()}}, 0.5).
			setStyle({'overflowX' : siteBodyOverflow});
		hudVisible = false;
	} else {
		// Animate hud open
		Ojay('html').addClass('oo').animate({paddingTop:{from: 0, to: 100}, paddingBottom:{from: 0, to: 200}}, 0.3);
		Ojay('body').
			setStyle({'overflowX' : 'hidden'}).
			animate({left: {to: Math.floor((YAHOO.util.Dom.getViewportWidth()-siteMinWidth)/2)}, width: {from:YAHOO.util.Dom.getViewportWidth(), to: siteMinWidth}}, 0.3).
			setStyle({'overflowX' : siteBodyOverflow});
		Ojay('.oo-toolbar')
			.wait(0.1)
			.animate({top:{from: -60, to: 0},opacity:{from: 0, to: 1}}, 0.2)
			._('div.oo-block').forEach(function(el,i) {
				// Get Regions Dimensions
				var area = el.getRegion();
				// Create Edit Zone HTML
				var zoneHtml = Ojay.HTML.div({id: 'OoEditZone' + i , className: 'oo-edit-zone', title:el.node.id});
				Ojay('div.oo-edit-zones').insert(zoneHtml,'top');
				
				// Set click handler
				Ojay('#OoEditZone'+i).on('click', function(el, e) {
				    e.stopDefault();
					//var overlay = $('.oo-menu').node;
				    ooBlockInEdit = el.node.getAttribute('title').substring(9);
					$('.oo-menu').setStyle({display:"block"});
					var url = '' + ooBaseUrl + 'otherobjects/block/form/'+ooBlockInEdit+'?resourceObjectId='+resourceObjectId;
					Ojay.HTTP.GET(url).insertInto('#OoMenu').evalScripts();
				});			
				
				//  Create Edit Label HTML
				var labelHtml = Ojay.HTML.div({id: 'OoEditLabel' + i , className: 'oo-edit-label'}, function(HTML) {
					// Insert Actions Arrow
					HTML.div({id: 'OoEditActions' + i , className: 'oo-edit-label-actions'}, function(HTML) {
						// Edit Status Gem and Label Text
						HTML.div({className:'oo-text-style oo-edit-state oo-edit-state-' + el.node.getAttribute('editstate').toLowerCase(), title:'Status: ' + el.node.getAttribute('editstate') },el.node.getAttribute('editlabel'));
					});
				});
				Ojay('#OoEditZone' + i).insert(labelHtml,'top');
				
				Ojay('#OoEditZone' + i).setStyle({opacity: '0', top:area.top + 'px', left:area.left + 'px', width:(area.getWidth()-4) + 'px', height:(area.getHeight()-4) + 'px'}).animate({opacity:  {from: 0, to: 1}}, 0.1);
			});
		
		hudVisible = true;
	}
}




/*
('.oo-editregion').forEach(function(el,i) {
		// Loop create editregions
	var area = el.getRegion();
	var htmlIns = Ojay.HTML.div({id: 'editzone' + i , className: 'oo-editzone'});
	Ojay('div.oo-editzones').insert(htmlIns,'top');
		// Insert Drop item
		var htmlEditIns = Ojay.HTML.div({id: 'draghandle' + i , className: 'oo-draghandle oo-text-style'});
		Ojay('#editzone' + i).insert(htmlEditIns,'top');
		
	Ojay('#editzone' + i).setStyle({opacity: '0', top:area.top + 'px', left:area.left + 'px', width:(area.getWidth()-4) + 'px', height:(area.getHeight()-4) + 'px'}).animate({opacity:  {from: 0, to: 1}}, 0.5);
		Ojay('#draghandle' + i).setContent('<span>' + el.node.title + '</span>');
		
		var dd2 = new YAHOO.util.DD('editzone' + i);
		dd2.setHandleElId('draghandle' + i); 
})
*/

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
	Ojay(".oo-region").forEach(function(e) {
		var blocks = new Array();
		e.descendants(".oo-block").forEach(function(e) {
			blocks[blocks.length]=e.node.id.slice(9);
		});
		regions[regions.length]={name:e.node.id.slice(10), blockIds:blocks};
	});
	var json = YAHOO.lang.JSON.stringify(regions);
	console.log(json);
	Ojay.HTTP.POST('/otherobjects/designer/saveArrangement?templateId='+ooTemplateId, {arrangement: json}, {});
}


YAHOO.namespace("OO");

YAHOO.OO.DDBlock = function(id, sGroup, config) {
    YAHOO.OO.DDBlock.superclass.constructor.apply(this, arguments);
    this.initBlock(id, sGroup, config);
};

YAHOO.lang.extend(YAHOO.OO.DDBlock, YAHOO.util.DDProxy, {

    initBlock: function(id, sGroup, config) {
        if (!id) { 
            return; 
        }
        this.isTarget = false;
    },

    endDrag: function(e) {
    },

    
    onDragDrop: function(e, id) {
        // get the drag and drop object that was targeted
        var oDD;
        
        if ("string" == typeof id) {
            oDD = YAHOO.util.DDM.getDDById(id);
        } else {
            oDD = YAHOO.util.DDM.getBestMatch(id);
        }

        var el = this.getEl();

        Ojay(oDD.getEl()).insert(this.getEl(),'top');

		ooSaveTemplateDesign();
    }
});


function toggleDesignMode(el, e) {
	
	// Change icon selected state
	Ojay('#ooToolbarIconDesignMode').addClass('oo-icon-selected');
	Ojay('#ooToolbarIconEditMode').removeClass('oo-icon-selected');
	
	Ojay('html').addClass('oo-design-mode');
	
	// Add labels to regions
	Ojay('div.oo-region').forEach(function(el,i) {

		//  Create Add button
		var labelHtml = Ojay.HTML.div({className: 'oo-region-label'}, function(HTML) {
			// Insert Actions Arrow
			HTML.div({className: 'oo-region-label-actions'}, function(HTML) {
				// Edit Status Gem and Label Text
				HTML.div({className:'oo-text-style oo-edit-state oo-edit-state-none', title:'Status: ' + el.node.getAttribute('editstate') },'Add');
			});
		});
		el.insert(labelHtml,'bottom');
	});

	
	// Set click handler
	Ojay('.oo-region-label').on('click', function(el, e) {
	    e.stopDefault();
		//var overlay = $('.oo-menu').node;
	    $('.oo-menu').setStyle({display:"block"});
		var url = '' + ooBaseUrl + 'otherobjects/block/choose/' + el.ancestors('.oo-region').node.id.slice(10);
		Ojay.HTTP.GET(url).insertInto('#OoMenu').evalScripts();
	});	
	
	// Enable drag drop
	Ojay('.oo-block').forEach(function(e) {
		new YAHOO.OO.DDBlock(e.node.id);	
	});
	
	Ojay('.oo-region').forEach(function(e) {
		new YAHOO.util.DDTarget(e.node.id);	
	});
	
	function ooMoveToTrash(el) {}
	function ooPutOnShelf(el) {}
	function ooAddToLibrary(el) {}
	     
	var oBlockContextMenuItemData = [
	        { text: "Move to trash", onclick: { fn: ooMoveToTrash } },
	        { text: "Put on shelf", onclick: { fn: ooPutOnShelf } },
	        { text: "Add to library", onclick: { fn: ooAddToLibrary } },  
	    ];

	var oFieldContextMenu = new YAHOO.widget.ContextMenu(
        "blockContextMenu",
        {
            trigger:  Ojay('.oo-block').toArray(),
            itemdata: oBlockContextMenuItemData,
            lazyload: true
		}
	);
	
	
}
