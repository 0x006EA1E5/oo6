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
	var navigator; // Ext.tree.Treepanel
	var listing; // OO.Listing
	var editForm; // OO.EditForm
	
	var currentContainer;
	var currentItem;
    
    return {
		
		layout : layout, 
		
        init : function()
		{
            // Set up workbench layout
			layout = new Ext.BorderLayout(document.body, {
	            north: {split:false, initialSize: 25, minSize: 60, maxSize: 60, titlebar: false, collapsible: false, animate: false},	                    
				//east: {split:true, collapsed: false, hidden: true, initialSize: 350, minSize: 350, maxSize: 500, titlebar: true, collapsible: true, animate: false},
				west: {split:true, collapsed: false, initialSize: 200, minSize: 200, maxSize: 500, collapsible: true, animate: false, autoScroll:true},
	            center: {titlebar:false, autoScroll:true, closeOnTab:true, tabPosition:'top', alwaysShowTabs:true}
	            //south: {split:false, initialSize: 25, minSize: 60, maxSize: 60, titlebar: false, collapsible: false, animate: false}	                   
	        });

			// Create lazy-loading navigator
			navigator = new Ext.tree.TreePanel('navigator-tree', {
                animate:true, 
                loader: new Ext.tree.TreeLoader({dataUrl:'/go/workbench/data/navigator'}),
                //enableDD:true,
                containerScroll: true,
                dropConfig: {appendOnly:true}
            });
			var root = new Ext.tree.AsyncTreeNode({
                text: 'Site', 
                draggable:false, // disable root node dragging
                id:'source'
            });
            navigator.setRootNode(root);
			navigator.render();
			root.expand();
			navigator.on("click", function(node){OO.Workbench.selectContainer(node);});
			
//			// Add contextual menu to tree
//			var folderContextMenu = new Ext.menu.Menu();	
//			var i1 = folderContextMenu.add({ text: 'Add sub folder...' });
//			i1.on('click', function(item){
//            	Ext.Msg.alert('Date Selected', 'You chose {0}.', item);
//        	});
//			folderContextMenu.add({ text: 'Rename folder...' });
//			navigator.on("contextmenu", function(node){folderContextMenu.show(node.ui.elNode);});
			
			// Create welcome panel
			var welcomePanel = new Ext.ContentPanel('welcome-panel', {autoCreate:true, title:'Welcome', background:false, closable:true});
			welcomePanel.setUrl('/go/workbench/welcome.html');

			// Create preview panel
			var previewPanel = new Ext.ContentPanel('preview-panel', {autoCreate:true, title:'Preview', background:true, closable:false});
			previewPanel.setUrl('/go/workbench/preview-help.html');
	
			// Create listing grid
			listing = OO.ListingGrid;
			var listingPanel = listing.init();
			console.log(listingPanel);
			
			// Create edit panel
			editForm = OO.EditForm;
			var editPanel = new Ext.ContentPanel('edit-panel', {autoCreate:true, title:'Edit', background:true, closable:false});
//			editPanel.setUrl('/go/workbench/edit-help.html');
			editForm.createForm();
			
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
	        console.log("Selected container: "+ node);
	        listing.load(node.id);
		},
		
		selectItem : function(grid,index)
		{
			var row = grid.dataSource.getAt(index);
			// TODO Move grid specific login to grid.js
			currentItem = row.id;
	        console.log("Selected item: "+ row.id + " => " + row.data.path);
			var previewPanel = layout.getRegion("center").getPanel("preview-panel");
			var linkPath = row.data.path;
			previewPanel.setUrl(null);
			dh.overwrite(previewPanel.getEl(), {
					tag: 'iframe',
					src: linkPath,
					style: 'border:0px;',
					width: '100%',
					height: '100%'
				});
			
			var editPanel = layout.getRegion("center").getPanel("edit-panel");
			editPanel.setUrl(null);
			editForm.createForm(null);
			
			//layout.getRegion("center").showPanel("preview-panel");
		},
		
		addPanel : function(panel)
		{
			// TODO Good extension point?
		}
    };
}();
Ext.onReady(OO.Workbench.init, OO.Workbench);