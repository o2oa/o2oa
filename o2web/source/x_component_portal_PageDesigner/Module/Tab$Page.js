MWF.xApplication.portal.PageDesigner.Module.Tab$Page = MWF.PCTab$Page = new Class({
	Extends: MWF.FCTab$Page,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Tab$Page/tab$Page.html"
	},
	initialize: function(tab, page, options){
		this.setOptions(options);
		
		this.path = "../x_component_portal_PageDesigner/Module/Tab$Page/";
		this.cssPath = "../x_component_portal_PageDesigner/Module/Tab$Page/"+this.options.style+"/css.wcss";

		this._loadCss();
		this.moduleType = "element";
		this.moduleName = "tab$Page";

		this.form = tab.form;
		this.tab = tab;
		this.page = page;
	}
});
