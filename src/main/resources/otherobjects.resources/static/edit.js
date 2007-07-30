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
	
	function buildFormPart(td, prefix, hiddenFields)
	{
		console.log(td);
	
		// Add fields according to typeDef
		for(var i=0; i<td.properties.length; i++)
		{
			var propDef = td.properties[i];
			var fName = '' + propDef.name;
			
			if(propDef.type=="component")
			{
				console.log(propDef.relatedTypeDef);
				buildFormPart(propDef.relatedTypeDef, propDef.name+".", hiddenFields);
			}
			else
			{
				console.log("Creating field: " + fName + " type: " + propDef.type);
				var config = {fieldLabel:propDef.label, name: prefix+propDef.name, allowBlank:true};
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
				else if(propDef.type=="reference")
				{
				    var store = new Ext.data.Store({
				        proxy: new Ext.data.HttpProxy({
				            url: '/go/workbench/data/select/'+propDef.relatedType
				        }),
				
				        // create reader that reads the records
				        reader: new Ext.data.JsonReader({
				            id: 'id'
				        }, [
				            {name: 'id', mapping: 'id'},
				            {name: 'label', mapping: 'label'}
				        ]),
				    });
					var f = new Ext.form.OOComboBox({
						
						fieldLabel: propDef.label,
			            name: propDef.name+":label",
			            hiddenName: propDef.name, 
			            allowBlank:true,
					    store: store,
					    displayField:'label',
					    valueField:'id',
					    typeAhead: true,
					    //mode: 'local',
					    triggerAction: 'all',
					    emptyText:'Select...',
					    valueNotFoundText: '???',
					    selectOnFocus:true
					});
				}
				else
				{
					config.width='300px';
					var f = new Ext.form.TextField(config);
				}
				form.add(f);
			}
		}
	}

	function buildForm(obj)
	{
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
		
		// Add form fields
		buildFormPart(obj.type, "", hiddenFields);
		
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
		form.setValues(flattenObject(obj.data, "", []));
		
	}
	
	function flattenObject(obj,prefix,flat) {
		for(var id in obj) {
	    	if(typeof obj[id] != 'function') {
				if(obj[id] instanceof Object)
				{
					console.log(prefix+id +"="+ obj[id]);
					flat[flat.length]={id:prefix+id, value:obj[id]};
					flattenObject(obj[id],id+".",flat);
				}
				else
				{
					console.log(prefix+id +"="+ obj[id]);
					flat[flat.length]={id:prefix+id, value:obj[id]};
	        	}
			}
	    }
	    return flat;
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

// ------------------------------------------------------------------------------------------------------------------------------
// Custom DateField that supports Java style dates in milliseconds
// ------------------------------------------------------------------------------------------------------------------------------

Ext.form.OODateField = function(config){
    Ext.form.OODateField.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OODateField, Ext.form.DateField,  {
  	setValue : function(date){
		if(date)
			Ext.form.DateField.superclass.setValue.call(this, this.formatDate(new Date(date)));
    }
});

// ------------------------------------------------------------------------------------------------------------------------------
// Json backed combo field
// ------------------------------------------------------------------------------------------------------------------------------

Ext.form.OOComboBox = function(config){
    Ext.form.OOComboBox.superclass.constructor.call(this, config);
};

Ext.extend(Ext.form.OOComboBox, Ext.form.ComboBox,  {

    setValue : function(v){
		console.log("Combo value", v);
    	var text = v;
    	if(v.id)
    	{
    		text = v.label;
    		v = v.id;
    	}
        else if(this.valueField){
            var r = this.findRecord(this.valueField, v);
            if(r){
                text = r.data[this.displayField];
            }else if(this.valueNotFoundText){
                text = this.valueNotFoundText;
            }
        }
        this.lastSelectionText = text;
        if(this.hiddenField){
            this.hiddenField.value = v;
        }
        Ext.form.ComboBox.superclass.setValue.call(this, text);
        this.value = v;
    },
    
     getValue : function(){
        console.log("Combo getValue");
		if(this.valueField){
            return typeof this.value != 'undefined' ? this.value : '';
        }else{
            return Ext.form.ComboBox.superclass.getValue.call(this);
        }
    }
});