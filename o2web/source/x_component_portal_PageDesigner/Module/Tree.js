MWF.xApplication.portal.PageDesigner.Module.Tree = MWF.PCTree = new Class({
	Extends: MWF.FCTree,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Tree/tree.html"
	},
	
	initialize: function(form, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Tree/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Tree/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "tree";

		this.form = form;
		this.container = null;
		this.containerNode = null;
	}
});
