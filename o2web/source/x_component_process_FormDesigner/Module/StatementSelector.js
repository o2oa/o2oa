MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.$Element", null, false);
MWF.xApplication.process.FormDesigner.Module.StatementSelector = MWF.FCStatementSelector = new Class({
	Extends: MWF.xApplication.process.FormDesigner.Module.Button,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_process_FormDesigner/Module/StatementSelector/StatementSelector.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_process_FormDesigner/Module/StatementSelector/";
		this.cssPath = "../x_component_process_FormDesigner/Module/StatementSelector/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "statementSelector";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	},
	_checkView: function(callback){
		if( this.property && this.property.viewFilter ){
			if (this.json["queryStatement"] && this.json["queryStatement"]!="none"){
				this.property.viewFilter.resetStatementData( this.json["queryStatement"].id );
			}else{
				this.property.viewFilter.resetStatementData();
			}
		}
	}

});
