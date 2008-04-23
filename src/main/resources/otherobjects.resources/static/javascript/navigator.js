OO.Navigator = function(){
	
	var tree;
	var treeEditor;
	
	return {
		
		init : function() {
			
			// Create lazy-loading navigator
			tree = new Ext.tree.TreePanel('navigator-tree', {
                animate:true, 
                loader: new Ext.tree.TreeLoader({dataUrl:'/otherobjects/data/navigator'}),
                enableDD:true,
				enableDrop:true,
				ddGroup : 'NavigatorDD',
                containerScroll: true,
				rootVisible:false
            });
			var root = new Ext.tree.AsyncTreeNode({
                text: 'Root', 
                draggable:false, // disable root node dragging
                allowDrop:false, // no items to be dragged here either
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
				//console.log("Moving: " + e.dropNode + " / " + e.point);
				// Only allow position of tree nodes to be set
				if(e.dropNode == null && e.point!="append")
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
            treeEditor.on("complete", function(editor, newName, oldName){
				console.log("Renaming folder from: " + editor.editNode.attributes.label + " to " + newName);
				NavigatorService.renameItem(editor.editNode.id, newName);
				//editor.setValue(oldName);
				//editor.cancelEdit(true);
			});
			
			
			// Add contextual menu to tree
			var navigatorContextMenu = new Ext.menu.Menu();	
			navigatorContextMenu.add({ text: 'Add sub folder', icon:'/resources/otherobjects.resources/static/extjs/images/default/tree/drop-add.gif', handler:function(item){
					var parent = item.parentMenu.selectedNode;
					// FIXME Need to make sure that the parent has expanded before we add new node.
					parent.expand();
					console.log("Creating sub folder in:"+parent.id);
					NavigatorService.addItem(parent.id,null, function(r){
						console.log(r);
						var node = parent.appendChild(new Ext.tree.TreeNode({id:r.id, text:r.label, cls:'folder', leaf:false, allowDrag:true}));
						node.select();	
					});
        	}});
			navigatorContextMenu.add({ text: 'Publish folder', icon:'/resources/otherobjects.resources/static/icons/tick.png',handler:function(item) {
				var id = item.parentMenu.selectedNode.id;
				console.log("Publishing folder: " + id);
				ContentService.publishItem(id);
        	}});
			navigatorContextMenu.add({ text: 'Edit folder', icon:'/resources/otherobjects.resources/static/icons/pencil.png', handler:function(item){
				var node = item.parentMenu.selectedNode;
				OO.Workbench.selectContainer(node);
				OO.Workbench.activateEditor();
        	}});
//			navigatorContextMenu.add({ text: 'Rename folder', icon:'/resources/otherobjects.resources/static/icons/pencil.png', handler:function(item){
//				var node = item.parentMenu.selectedNode;
//				console.log(node);
//				if(node.draggable) // Don't allow locked nodes to be renamed
//					treeEditor.triggerEdit(node);
//        	}});
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