//
// edit.js
//
// Defines OO.EditForm providing a dynamic data editor.
//

OO.EditForm = function(){

	var form;

	function showFailureMessage(form, scope)
	{
      	Ext.Msg.alert('Warning', 'An error ocured whilst saving. Please try again.');
	}

	function buildForm(typeDef)
	{
		console.log("Test", typeDef);
		console.log(typeDef);
		// TODO Find out about QuickTips
		// Ext.QuickTips.init();

		// Create map to store hidden fields
		var hiddenFields = {
			put : function(foo,bar) {this[foo] = bar;},
			get : function(foo) {return this[foo];}
		}
		
		// Create form
	    form = new Ext.form.Form({
			labelAlign: 'left',
			buttonAlign: 'left',
			monitorValid: true
			//labelWidth: 75
	    });

		// Add essential form processing support fields (TODO make hidden)		
		form.add(new Ext.form.TextField({fieldLabel:'ID', name:'id', width:'200px', allowBlank:true}));
		form.add(new Ext.form.TextField({fieldLabel:'Type', name:'ooType', width:'200px', allowBlank:false}));
		
		// Add fields according to typeDef
		for(var i=0; i<typeDef.properties.length; i++)
		{
			var propDef = typeDef.properties[i];
			var fName = '' + propDef.name;
			console.log("Creating field: " + fName + " type: " + propDef.type);
			var config = {fieldLabel:propDef.label, name: propDef.name, width:'500px', allowBlank:true, format: 'd/m/y'};
			if(propDef.type=="date")
			{
				var f = new Ext.form.DateField(config);
			}
			else if(propDef.type=="html")
			{
				config.height='300px';
				var f = new Ext.form.TextArea(config);
			}
			else if(propDef.type=="text")
			{
				config.grow=true;
				config.growMax=500;
				var f = new Ext.form.TextArea(config);
			}
			else if(propDef.type=="boolean")
			{
				var f = new Ext.form.Checkbox(config);
				hiddenFields.put(propDef.name+':BOOLEAN', '');
			}
			else
			{
				var f = new Ext.form.TextField(config);
			}
			form.add(f);
		}


		// FIXME Does this work?	    
//	    form.addButton('Save and continue editing', function(){
//			form.submit({url:'/go/form', bindForm:true, waitMsg:'Saving Data...', params: hiddenFields });   
//		});
		
	    form.addButton('Save', function() {
			form.submit({url:'/go/form', bindForm:true, waitMsg:'Saving Data...', params: hiddenFields });   
		});
		
		form.on('actioncomplete', function() {Workbench.activateTab("listing-tab");}, this);
		form.on('actionfailed', showFailureMessage, this);
		
		// Remove previous form and render new one
		Ext.get('edit-panel').dom.innerHTML='';
		console.log(Ext.get('edit-panel'));
	    form.render('edit-panel');
	    
	}

	function loadJsonObject(url, callback)
	{
		Ext.Ajax.request({url:url, success:function(r){
			var o = Ext.util.JSON.decode(r.responseText);
			callback(o);
		}});
	}

 	return {
		
    	createForm : function(typeDef) {
		loadJsonObject("/go/workbench/data/types/Article", buildForm);
		//buildForm(td);
//      		console.log(resource);
//      		form.setValues({ooType:resource.type});
//      		form.setValues(resource);
//      		form.setValues(resource.data);
//      		//form.on('actionfailed', showFailureMessage, this);
//      		//showFailureMessage();
//      		Workbench.enableTab("edit-tab");
    	},
		
		setFormValues : function(data){
			console.log(data);
      		form.setValues(data);
    	}
   
	}

}();