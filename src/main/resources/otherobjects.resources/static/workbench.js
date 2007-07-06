// Define OO namespace
var OO = OO || {};

// Exm blank image
Ext.BLANK_IMAGE_URL = '/resources/otherobjects.resources/static/extjs/images/default/s.gif';


OO.Workbench = function()
{
    var layout, navigator;
    
    return {
        init : function()
		{
            // Set up workbench layout
			layout = new Ext.BorderLayout(document.body, {
	            north: {split:false, initialSize: 25, minSize: 60, maxSize: 60, titlebar: false, collapsible: false, animate: false},	                    
				//east: {split:true, collapsed: false, hidden: true, initialSize: 350, minSize: 350, maxSize: 500, titlebar: true, collapsible: true, animate: false},
				west: {split:true, collapsed: false, initialSize: 200, minSize: 200, maxSize: 500, collapsible: true, animate: false, autoScroll:true},
	            center: {titlebar: false, autoScroll:true, closeOnTab: true}
	        });

			// Add tree
//            navigator = new Ext.tree.TreePanel('navigator-tree', {animate:true, enableDD:true, containerScroll: true, ddGroup: 'organizerDD', rootVisible:false});
            //var navigatorRoot = new Ext.tree.TreeNode({text: 'Root', allowDrag:false, allowDrop:false, folderPath:"/", id:'root'});
            //navigator.setRootNode(navigatorRoot);  
//            ///navigator.on('beforenodedrop', this.moveFolder);
//            //navigator.on('click', this.selectFolder);
//			navigatorRoot.appendChild(new Ext.tree.TreeNode({text:'Site', allowChildren:true, cls:'site-nav-item', allowDrag:true}));
//            navigator.render();
//			navigatorRoot.expand();

			navigator = new Ext.tree.TreePanel('navigator-tree', {
                animate:true, 
                loader: new Ext.tree.TreeLoader({dataUrl:'/go/workbench/data/navigator'}),
                //enableDD:true,
                containerScroll: true,
                dropConfig: {appendOnly:true}
            });
			
//			var navigatorRoot = new Ext.tree.TreeNode({text: 'Root', allowDrag:false, allowDrop:false, folderPath:"/", id:'root'});
//            navigator.setRootNode(navigatorRoot); 
            
            // set the root node
            var root = new Ext.tree.AsyncTreeNode({
                text: 'Site', 
                draggable:false, // disable root node dragging
                id:'source'
            });
            navigator.setRootNode(root);
 navigator.render();
//			navigatorRoot.expand();

			// Add tabs	
			tabs = new Ext.TabPanel('content');
			tabs.addTab('listing-tab', "Listing");
			tabs.addTab('edit-tab', "Edit");
			tabs.getTab('edit-tab').disable();
			
			var previewTab = tabs.addTab('preview-tab', "Preview");
			//previewTab.on('activate', activatePreviewTab, previewTab);
			previewTab.disable();
			
	
			tabs.activate('listing-tab');
	
	        layout.beginUpdate();
			layout.add('north', new Ext.ContentPanel('header'));
	        //layout.add('east', editPanel);
	        layout.add('west', new Ext.ContentPanel('select', {title: 'Select'}));
	        layout.add('center', new Ext.ContentPanel('content', {title: 'Center Panel', closable: false}));
	        layout.endUpdate();
        }
    };
}();
Ext.onReady(OO.Workbench.init, OO.Workbench);