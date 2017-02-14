MWF.xApplication.cms.FormDesigner.Module = MWF.xApplication.cms.FormDesigner.Module || {};
MWF.xDesktop.requireApp("cms.FormDesigner", "Module.$Container", null, false);
MWF.xApplication.cms.FormDesigner.Module.Div = MWF.CMSFCDiv = new Class({
	Extends: MWF.CMSFC$Container,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_cms_FormDesigner/Module/Div/div.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "/x_component_cms_FormDesigner/Module/Div/";
		this.cssPath = "/x_component_cms_FormDesigner/Module/Div/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "div";
		
		this.Node = null;
		this.form = form;
	},
	
	_createMoveNode: function(){
		this.moveNode = new Element("div", {
			"MWFType": "div",
			"id": this.json.id,
			"styles": this.css.moduleNodeMove,
			"events": {
				"selectstart": function(){
					return false;
				}
			}
		}).inject(this.form.container);
	},
	_setEditStyle_custom: function(name){
		
	}
	
});
