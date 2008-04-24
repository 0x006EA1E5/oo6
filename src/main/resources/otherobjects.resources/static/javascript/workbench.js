//
// workbench.js
//
// Defines OO.Workbench the main workbench interface class.
//

// Define OO namespace
var OO = OO || {};

// Ext blank image
Ext.BLANK_IMAGE_URL = '/static/otherobjects.resources/static/libs/extjs/images/default/s.gif';

OO.Workbench = function()
{
	var dh = Ext.DomHelper;
	
    var layout; // Ext.BorderLayout
	var navigator; // OO.Navigator
	var listing; // OO.Listing
	var editForm; // OO.EditForm
	
	var contextPath = "";
	
	var currentContainer = "/site";
	var currentItem;
	
    return {
		
		// FIXME Use getters/setters intead?
		currentContainer : currentContainer, 
		currentItem : currentItem, 
		currentNode : null, 
		navigator : navigator, 
		contextPath : contextPath, 
		
		
		getCurrentNode : function() {
			// FIXME Temp hack to lazy set root node
			if(!this.currentNode)
				OO.Navigator.selectNode("/site/");
			return this.currentNode;
		},
		
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
			var welcomePanel = new Ext.ContentPanel('welcome-panel', {autoCreate:true, title:'Welcome', background:false, closable:true, url:'/go/workbench/welcome.html'});
			//welcomePanel.setUrl('/go/workbench/welcome.html',null,true); //loadOnce

			// Create preview panel
			var previewPanel = new OO.PreviewPanel('preview-panel', {autoCreate:true, src:'/otherobjects/workbench/preview-help.html', title:'Preview', background:true, closable:false});
			//previewPanel.setUrl('/go/workbench/preview-help.html',null,true); //loadOnce
			//previewPanel.on("activate", OO.Workbench.showPreview);
	
			// Create listing grid
			listing = OO.ListingGrid;
			var listingPanel = listing.init();
			listingPanel.on("activate", function(){
				if(listing.currentContainerId != currentContainer) {
	       			listing.load(currentContainer);
					listing.currentContainerId = currentContainer;
				}
			});
			console.log(listingPanel);
			
			// Create edit panel
			editForm = OO.EditForm;
			var editPanel = new Ext.ContentPanel('edit-panel', {autoCreate:true, title:'Edit', background:true, closable:false});
			editPanel.on("activate", function() {
				
				if(editForm.newDataType)
					console.log("Activating editor", editForm.newDataType);
				else
					console.log("Activating editor", editForm.dataId);
				if(editForm.dataId || editForm.newDataType)
					editForm.renderForm();
				else if(!editForm.loaded)
				{
					editPanel.load('/go/workbench/edit-help.html'); //loadOnce
					editForm.loaded = true;
				}
			});
					
			// Create version history tab
			var versionHistoryPanel = new Ext.ContentPanel('version-history-panel', {autoCreate:true, title:'Version history', background:true, closable:true, scripts:true, url:'/go/workbench/version-history.html'});
			versionHistoryPanel.on("activate", function(t) {	
				var updater = t.getUpdateManager();
		        updater.update({
		        	url: "/go/workbench/version-history.html?id="+currentItem,
		        	scripts:true,
					params:{}
		        });
			}, this);
			
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
	        layout.add('center', versionHistoryPanel);
	        // layout.add('south', new Ext.ContentPanel('status'));
	        layout.endUpdate();
			
			layout.getRegion("center").showPanel("welcome-panel");
//			layout.getRegion("center").showPanel("listing-panel");

        },
		
		selectContainer : function(node)
		{
			this.currentNode = node;
			currentContainer = node.id;
	        console.log("Selected container: "+ currentContainer, node);
			if(listing.currentContainerId != currentContainer)
	       		listing.load(currentContainer);
			listing.currentContainerId = currentContainer;
			layout.getRegion("center").showPanel("listing-panel");
			editForm.createForm(currentContainer);
		},
		
		selectItem : function(grid,index)
		{
			var row = grid.dataSource.getAt(index);
			// TODO Move grid specific login to grid.js
			currentItem = row.data.editableId;
			console.log("Current item in selectItem: " + currentItem);
	        console.log("Selected item: "+ currentItem + " => " + row.data.linkPath);
			var previewPanel = layout.getRegion("center").getPanel("preview-panel");
			previewPanel.setSrc(row.data.linkPath);
			var editPanel = layout.getRegion("center").getPanel("edit-panel");
			
			//editForm.setUrl(null);
			editForm.createForm(currentItem);
			
			//layout.getRegion("center").showPanel("preview-panel");
			//layout.getRegion("center").showPanel("edit-panel");
		},
		
		activatePanel : function(name)
		{
			layout.getRegion("center").showPanel(name);
		},
		
		getPanel : function(name)
		{
			return layout.getRegion("center").getPanel(name);
		},
		
		activateEditor : function()
		{
			layout.getRegion("center").showPanel("edit-panel");
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
		},
		
        setPath : function(path)
        {
            console.log("Setting context path: " + path);
            contextPath = path;
        },

        getPath : function(path)
        {
            return contextPath + path;
        }
    };
}();

// Configure dwr error handling
function errorHandler(msg) {
	Ext.Msg.alert("Oops. Something bad has happened.", "Sorry, whatever you tried to do didn't work. The workbench may be in an inconsistent state.<br/>Please refresh the page now.");
}

Ext.onReady(OO.Workbench.init, OO.Workbench);



