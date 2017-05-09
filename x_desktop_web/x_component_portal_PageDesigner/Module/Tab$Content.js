MWF.xApplication.portal.PageDesigner.Module.Tab$Content = MWF.PCTab$Content = new Class({
	Extends: MWF.FCTab$Content,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "/x_component_portal_PageDesigner/Module/Tab$Content/tab$Content.html",
		"actions": []
	},
	
	initialize: function(tab, page, options){
		this.setOptions(options);
		
		this.path = "/x_component_portal_PageDesigner/Module/Tab$Content/";
		this.cssPath = "/x_component_portal_PageDesigner/Module/Tab$Content/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "container";
		this.moduleName = "tab$Content";
		
		this.form = tab.form;
		this.tab = tab;
		this.page = page;
	}
});
