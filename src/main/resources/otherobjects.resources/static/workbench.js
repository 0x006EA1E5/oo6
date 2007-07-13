//
// workbench.js
//
// Defines OO.Workbench the main workbench interface class.
//

// Define OO namespace
var OO = OO || {};

// Exm blank image
Ext.BLANK_IMAGE_URL = '/resources/otherobjects.resources/static/extjs/images/default/s.gif';

OO.Workbench = function()
{
	var dh = Ext.DomHelper;
	
    var layout; // Ext.BorderLayout
	var navigator; // OO.Navigator
	var listing; // OO.Listing
	var editForm; // OO.EditForm
	
	var currentContainer;
	var currentItem;
    
    return {
		
		// FIXME Use getters/setters intead?
		currentContainer : currentContainer, 
		currentItem : currentItem, 
		
        init : function()
		{
            // Set up workbench layout
			layout = new Ext.BorderLayout(document.body, {
	            north: {split:false, initialSize: 25, minSize: 60, maxSize: 60, titlebar: false, collapsible: false, animate: false},	                    
				west: {split:true, collapsed: false, initialSize: 200, minSize: 200, maxSize: 500, collapsible: true, animate: false, autoScroll:true},
	            center: {titlebar:false, autoScroll:true, closeOnTab:true, tabPosition:'top', alwaysShowTabs:true}
	            //south: {split:false, initialSize: 25, minSize: 60, maxSize: 60, titlebar: false, collapsible: false, animate: false}	                   
	        });
			
			// Create welcome panel
			var welcomePanel = new Ext.ContentPanel('welcome-panel', {autoCreate:true, title:'Welcome', background:false, closable:true});
			welcomePanel.setUrl('/go/workbench/welcome.html',null,true); //loadOnce

			// Create preview panel
			var previewPanel = new OO.PreviewPanel('preview-panel', {autoCreate:true, src:'/go/workbench/preview-help.html', title:'Preview', background:true, closable:false});
			//previewPanel.setUrl('/go/workbench/preview-help.html',null,true); //loadOnce
			//previewPanel.on("activate", OO.Workbench.showPreview);
	
			// Create listing grid
			listing = OO.ListingGrid;
			var listingPanel = listing.init();
			console.log(listingPanel);
			
			// Create edit panel
			editForm = OO.EditForm;
			var editPanel = new Ext.ContentPanel('edit-panel', {autoCreate:true, title:'Edit', background:true, closable:false});
			editPanel.setUrl('/go/workbench/edit-help.html',null,true); //loadOnce
			editForm.createForm();
			
			// Create navigator
			navigator = OO.Navigator;
			navigator.init();
			
			// Render layout
	        layout.beginUpdate();
			layout.add('north', new Ext.ContentPanel('header'));
	        layout.add('west', new Ext.ContentPanel('select', {title: 'Select'}));
	        layout.add('center', welcomePanel);
	        layout.add('center', listingPanel);
	        layout.add('center', editPanel);
	        layout.add('center', previewPanel);
	        // layout.add('south', new Ext.ContentPanel('status'));
	        layout.endUpdate();
        },
		
		selectContainer : function(node)
		{
			currentContainer = node;
			layout.getRegion("center").showPanel("listing-panel");
	        console.log("Selected container: "+ node.id);
	        listing.load(node.id);
		},
		
		selectItem : function(grid,index)
		{
			var row = grid.dataSource.getAt(index);
			// TODO Move grid specific login to grid.js
			currentItem = row.id;
	        console.log("Selected item: "+ row.id + " => " + row.data.path);
			var previewPanel = layout.getRegion("center").getPanel("preview-panel");
			previewPanel.setSrc(row.data.path);
			var editPanel = layout.getRegion("center").getPanel("edit-panel");
			
			editForm.createForm(row.id);
			
			//layout.getRegion("center").showPanel("preview-panel");
		},
		
		showPreview : function()
		{
			console.log("Showing preview panel");
			var previewPanel = layout.getRegion("center").getPanel("preview-panel");
			
			console.log("Current preview:" + previewPanel.curentPath);
			if(previewPanel.curentPath != previewPanel.linkPath) {
				console.log("Loading new preview:" + previewPanel.linkPath);
				previewPanel.currentPath = previewPanel.linkPath;
				var html = dh.markup({
					tag: 'iframe',
					src: 'http://google.com/',//previewPanel.linkPath,
					style: 'border:0px;'
				});
				previewPanel.setContent(html);
			}			
		},
		
		addPanel : function(panel)
		{
			// TODO Good extension point?
		}
    };
}();

Ext.onReady(OO.Workbench.init, OO.Workbench);