OO.Navigator = function(){
	
	var tree;
	var treeEditor;
	
	return {
		
		init : function() {
			
			// Create lazy-loading navigator
			tree = new Ext.tree.TreePanel('navigator-tree', {
                animate:true, 
                loader: new Ext.tree.TreeLoader({dataUrl:'/go/workbench/data/navigator'}),
                enableDD:true,
				enableDrop:true,
				ddGroup : 'NavigatorDD',
                containerScroll: true,
				rootVisible:false
            });
			var root = new Ext.tree.AsyncTreeNode({
                text: 'Root', 
                draggable:false, // disable root node dragging
                id:'source',
				cls:'site-nav-item'
            });
            tree.setRootNode(root);
			tree.render();
			root.expand();
			tree.on("click", function(node){OO.Workbench.selectContainer(node);});
			tree.on('beforenodedrop', function(e) {
				var itemId ;
				if(e.dropNode)
					itemId = e.dropNode.id;
				else
				{
					itemId = e.data.selections[0].data.id;
					// Remove from grid
					console.log("Drop", e.data);
					e.data.grid.getDataSource().remove(e.data.selections[0]);
				}
				console.log("Moving folder from: " + itemId + " to " + e.target.id);
				NavigatorService.moveItem(itemId, e.target.id, e.point);
				return true;
			});
			tree.on('nodedragover', function(e) {
				// Only allow position of tree nodes to be set
				if(!e.dropNode && e.point!="append")
					return false;
			});
			
            
			
			// Add an inline editor for the nodes...
            treeEditor = new Ext.tree.TreeEditor(tree, {
                allowBlank:false,
                blankText:'A name is required'
            });
			treeEditor.ignoreNoChange=true;
			// ... but disable default behaviour. Only allow via context menu.
			tree.un('beforeclick', treeEditor.beforeNodeClick, treeEditor);
            treeEditor.on("complete", function(element, newName){
				console.log("Renaming folder from: " + element.editNode.attributes.folderPath + " to " + newName);
				NavigatorService.renameItem(element.editNode.id, newName);
			});
			
			
			// Add contextual menu to tree
			var navigatorContextMenu = new Ext.menu.Menu();	
			navigatorContextMenu.add({ text: 'Add sub folder', handler:function(item){
					var parent = item.parentMenu.selectedNode;
					// FIXME Need to make sure that the parent has expanded before we add new node.
					parent.expand();
					console.log("Creating sub folder in:"+parent.id);
					NavigatorService.addItem(parent.id,null, function(r){
						console.log(r);
						var node = parent.appendChild(new Ext.tree.TreeNode({id:r.id, text:r.label, cls:'folder', leaf:true, allowDrag:true}));
						node.select();	
					});
        	}});
			navigatorContextMenu.add({ text: 'Rename folder', handler:function(item){
				var node = item.parentMenu.selectedNode;
				console.log(node);
				treeEditor.triggerEdit(node);
        	}});
			tree.on("contextmenu", function(node){
					navigatorContextMenu.selectedNode = node;
					navigatorContextMenu.show(node.ui.node.ui.getAnchor());
			});
		},
		
		selectNode : function(path)
		{
			var node = tree.getRootNode().firstChild
			node.select();
			OO.Workbench.selectContainer(node);
		}
		
	}	
}();