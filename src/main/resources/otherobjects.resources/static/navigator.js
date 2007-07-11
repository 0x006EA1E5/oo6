OO.Navigator = function(){
	
	var tree;
	var treeEditor;
	
	return {
		
		init : function() {
			
			// Create lazy-loading navigator
			tree = new Ext.tree.TreePanel('navigator-tree', {
                animate:true, 
                loader: new Ext.tree.TreeLoader({dataUrl:'/go/workbench/data/navigator'}),
                //enableDD:true,
                containerScroll: true,
                dropConfig: {appendOnly:true}
            });
			var root = new Ext.tree.AsyncTreeNode({
                text: 'Site', 
                draggable:false, // disable root node dragging
                id:'source',
				cls:'site-nav-item'
            });
            tree.setRootNode(root);
			tree.render();
			root.expand();
			tree.on("click", function(node){OO.Workbench.selectContainer(node);});
			
			// Add an inline editor for the nodes...
            treeEditor = new Ext.tree.TreeEditor(tree, {
                allowBlank:false,
                blankText:'A name is required'
                //selectOnFocus:true,
            });
			treeEditor.ignoreNoChange=true;
			// ... but disable default behaviour. Only allow via context menu.
			tree.un('beforeclick', treeEditor.beforeNodeClick, treeEditor);
            treeEditor.on("complete", function(element, newName){
				console.log("Renaming folder from: " + element.editNode.attributes.folderPath + " to " + newName);
				NavigatorService.moveItem(element.editNode.id, newName);
			});
			
			
			// Add contextual menu to tree
			var navigatorContextMenu = new Ext.menu.Menu();	
			var i1 = navigatorContextMenu.add({ text: 'Add sub folder' });
			i1.on('click', function(item){
					var parent = item.parentMenu.selectedNode.id;
					console.log("Creating sub folder in:"+parent);
					NavigatorService.addItem(parent,null);
        	});
			
			var i2 = navigatorContextMenu.add({ text: 'Rename folder' });
			i2.on('click', function(item){
				var node = item.parentMenu.selectedNode;
				console.log(node);
				treeEditor.triggerEdit(node);
        	});
			
			tree.on("contextmenu", function(node){
					navigatorContextMenu.selectedNode = node;
					navigatorContextMenu.show(node.ui.node.ui.getAnchor());
			});
			
		
          
		}	
	}	
}();