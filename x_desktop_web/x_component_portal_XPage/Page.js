MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.XPage = MWF.xApplication.portal.XPage || {};
MWF.xDesktop.requireApp("portal.XPage", "Package", null, false);
MWF.xApplication.portal.XPage.Page = MWF.APPPage =  new Class({
	Implements: [Options, Events],
	Extends: MWF.APPForm,
	options: {
		"style": "default",
        "readonly": false,
		"cssPath": "",
        "moduleEvents": ["postLoad", "afterLoad", "beforeSave", "afterSave", "beforeClose", "beforeProcess", "afterProcess"]
	},
	initialize: function(node, data, options){
		this.setOptions(options);

		this.container = $(node);
        this.container.setStyle("-webkit-user-select", "text");
		this.data = data;
		this.json = data.json;
		this.html = data.html;
		
		this.path = "/x_component_portal_XPage/$Page/";
		this.cssPath = this.options.cssPath || "/x_component_portal_XPage/$Page/"+this.options.style+"/css.wcss";
		this._loadCss();
		
		this.modules = [];
        this.all = {};
        this.forms = {};

        if (!this.personActions) this.personActions = new MWF.xAction.org.express.RestActions();
	}
});