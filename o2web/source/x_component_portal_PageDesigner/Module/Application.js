MWF.xApplication.portal.PageDesigner.Module.Application = MWF.PCApplication = new Class({
	Extends: MWF.FC$Element,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Application/application.html",
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Application/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Application/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "component";
		this.moduleName = "application";
		
		this.Node = null;
		this.form = form;
		this.page = form;
	},
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "application",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_createNode: function(callback){
		this.node = new Element("div", {
			"id": this.json.id,
			"MWFType": "application",
			"styles": this.css.moduleNode,
			"events": {
				"selectstart": function(e){
					e.preventDefault();
				}
			}

		}).inject(this.form.node);


	},
});
