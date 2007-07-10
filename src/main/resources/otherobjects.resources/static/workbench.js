// Define OO namespace
var OO = OO || {};

// Exm blank image
Ext.BLANK_IMAGE_URL = '/resources/otherobjects.resources/static/extjs/images/default/s.gif';


OO.Workbench = function()
{
    var layout, navigator, tabs;
	var listing;
    
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
			
			var folderContextMenu = new Ext.menu.Menu();
			
			var i1 = folderContextMenu.add({ text: 'Add sub folder...' });
			i1.on('click', function(item){
            	Ext.Msg.alert('Date Selected', 'You chose {0}.', item);
        	});
			folderContextMenu.add({ text: 'Rename folder...' });

			
			navigator.on("click", function(node){OO.Workbench.selectContainer(node);});
			navigator.on("contextmenu", function(node){folderContextMenu.show(node.ui.elNode);});
			
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



			var welcomePanel = new Ext.ContentPanel('welcome-panel', {autoCreate:true, title:'Welcome', background:true});
			welcomePanel.setUrl('/go/workbench/welcome.html');
	        layout.add('center', welcomePanel);
	
	        layout.beginUpdate();
			layout.add('north', new Ext.ContentPanel('header'));
	        //layout.add('east', editPanel);
	        layout.add('west', new Ext.ContentPanel('select', {title: 'Select'}));
	        layout.endUpdate();
			
			listing = OO.ListingGrid;
			listing.init();
			
        },
		
		selectContainer : function(node)
		{
			layout.getRegion("center").showPanel("listing-grid");
	        console.log(node);
	        listing.load(node.id);
		},
		
		addPanel : function(panel)
		{
	        layout.add('center', panel);
		}
    };
}();
Ext.onReady(OO.Workbench.init, OO.Workbench);