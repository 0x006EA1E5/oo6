<#import "/oo.ftl" as oo>

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" 
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>OTHERobjects Workbench</title>
<@oo.js "/otherobjects/static/libs/firebug/firebug.js"/>
<@oo.js "/otherobjects/static/libs/extjs/src/ext-base.js"/>
<@oo.js "/otherobjects/static/libs/extjs/src/ext-all-debug.js"/>
<@oo.css "/otherobjects/static/libs/extjs/src/ext-all.css"/>
</head>

<body>

<div style="width:300px; margin:100px 0px 0px 100px;">
    <div class="x-box-tl"><div class="x-box-tr"><div class="x-box-tc"></div></div></div>
    <div class="x-box-ml"><div class="x-box-mr"><div class="x-box-mc">
        <h3 style="margin-bottom:20px;">OTHERobjects Login</h3>
        <div id="form">

        </div>
    </div></div></div>
    <div class="x-box-bl"><div class="x-box-br"><div class="x-box-bc"></div></div></div>
</div>

<script>Ext.onReady(
function(){
	
	
	 form = new Ext.Form({ 
                labelAlign: 'right', 
                labelWidth: 75, 
                buttonAlign: 'right', 
                id: "loginFormId",
                method: "POST",
                baseParams:{module:'login'} 
        }); 
        form.add( 
            new Ext.form.TextField({ 
                fieldLabel: 'Username', 
                name: 'j_username',
               	allowBlank:false
                
            }),         
            new Ext.form.TextField({ 
                fieldLabel: 'Password', 
                name: 'j_password', 
                allowBlank:false,
                defaultAutoCreate : {tag: "input", type: "password", size: "20", autocomplete: "off"} 
            }),         
            new Ext.form.Checkbox({ 
                fieldLabel: 'Remember', 
                name: '_spring_security_remember_me'
            }) 
        ); 
        form.addButton('Login', function(){ 
			form.el.dom.action='/otherobjects/j_spring_security_check';
        	form.el.dom.submit();
        }, form); 
		
        form.render("form"); 

});
</script>


</body>

