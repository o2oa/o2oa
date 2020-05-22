MWF.xApplication.portal.PageDesigner.Module.Stat = MWF.PCStat = new Class({
	Extends: MWF.FCStat,
	Implements: [Options, Events],
	options: {
		"style": "default",
		"propertyPath": "../x_component_portal_PageDesigner/Module/Stat/stat.html"
	},
    initialize: function(form, options){
        this.setOptions(options);

        this.path = "../x_component_portal_PageDesigner/Module/Stat/";
        this.cssPath = "../x_component_portal_PageDesigner/Module/Stat/"+this.options.style+"/css.wcss";

        this._loadCss();
        this.moduleType = "element";
        this.moduleName = "stat";

        this.form = form;
        this.container = null;
        this.containerNode = null;
    }
});
