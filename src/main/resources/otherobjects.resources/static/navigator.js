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
                containerScroll: true
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
			tree.on('beforenodedrop', function(e) {
				console.log("Moving folder from: " + e.dropNode.id + " to " + e.target.id);
				NavigatorService.moveItem(e.dropNode.id, e.target.id, e.point);
				return true;
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
		}	
	}	
}();