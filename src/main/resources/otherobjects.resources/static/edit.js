//
// edit.js
//
// Defines OO.EditForm providing a dynamic data editor.
//

OO.EditForm = function(){

	var form;
	var dataId;

	function showFailureMessage(form, scope)
	{
      	Ext.Msg.alert('Warning', 'An error ocured whilst saving. Please try again.');
	}

	function buildForm(obj)
	{
		var typeDef = obj.type;
		
		// TODO Find out about QuickTips
		Ext.QuickTips.init();
		
		// Show form validation warnings next to each field
		Ext.form.Field.prototype.msgTarget = 'side';

		// Create map to store hidden fields
		var hiddenFields = {};
		hiddenFields["id"] = obj.data.id;
		
		// Create form
	    form = new Ext.form.Form({
			labelAlign: 'left',
			buttonAlign: 'left',
			monitorValid: true,
			labelWidth: 120
	    });

		// Add essential form processing support fields	
		form.add(new Ext.form.TextField({fieldLabel:'ID', name:'id', width:'200px', allowBlank:true, disabled:true}));
		//form.add(new Ext.form.TextField({fieldLabel:'Type', name:'ooType', width:'200px', allowBlank:false}));
		
		// Add fields according to typeDef
		for(var i=0; i<typeDef.properties.length; i++)
		{
			var propDef = typeDef.properties[i];
			var fName = '' + propDef.name;
			console.log("Creating field: " + fName + " type: " + propDef.type);
			var config = {fieldLabel:propDef.label, name: propDef.name, allowBlank:true};
			if(propDef.type=="date")
			{
				config.format='d/m/y';
				var f = new Ext.form.OODateField(config);
				f.on("change", function(f,n,o){console.log("Change",n);});
			}
			else if(propDef.type=="html")
			{
				config.width=null; // FIXME Ext bug: can't set width of HtmlEditor
				// config.growMax=500;// FIXME Ext bug: can't grow HtmlEditor
				var f = new Ext.form.HtmlEditor(config);
			}
			else if(propDef.type=="text")
			{
				config.width='500px';
				config.grow=true;
				config.growMax=500;
				var f = new Ext.form.TextArea(config);
			}
			else if(propDef.type=="boolean")
			{
				var f = new Ext.form.Checkbox(config);
				hiddenFields["_"+propDef.name] = "";
			}
			else
			{
				config.width='300px';
				var f = new Ext.form.TextField(config);
			}
			form.add(f);
		}


		// FIXME Does this work?	    
//	    form.addButton('Save and continue editing', function(){
//			form.submit({url:'/go/form', bindForm:true, waitMsg:'Saving Data...', params: hiddenFields });   
//		});
		
	    form.addButton('Save', function() {
			form.submit({url:'/go/workbench/form', bindForm:true, waitMsg:'Saving Data...', params:hiddenFields });   
		});
		
		form.on('actioncomplete', function(event,action) {
			// Update listing panel
			console.log("Form saved correctly.");
			OO.ListingGrid.updateItem(action.result.formObject);
			OO.Workbench.getPanel("preview-panel").setDirty(true);
			OO.Workbench.activatePanel("listing-panel");
		}, this);
		//form.on('actionfailed', showFailureMessage, this);
		
		// Remove previous form and render new one
		Ext.get('edit-panel').dom.innerHTML='';
		console.log(hiddenFields);
	    form.render('edit-panel');
		
		// FIXME Better way to set form values without conflict danger?
		form.setValues(obj.data);
		form.setValues(obj.data.data);
	    
	}

	function loadJsonObject(url, callback) {
		Ext.Ajax.request({url:url, success:function(r){
			var o = Ext.util.JSON.decode(r.responseText);
			callback(o);
		}});
	}

 	return {
		
    	createForm : function(id) {
			this.dataId = id;
    	},
		
		renderForm : function()
		{
			loadJsonObject("/go/workbench/data/item/" + this.dataId, buildForm);
		}
	}

}();




// Custom DateField that supports Java style dates in milliseconds
Ext.form.OODateField = function(config){
    Ext.form.OODateField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OODateField, Ext.form.DateField,  {
  	setValue : function(date){
		if(date)
			Ext.form.DateField.superclass.setValue.call(this, this.formatDate(new Date(date)));
    }
});