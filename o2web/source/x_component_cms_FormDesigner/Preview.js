MWF.xDesktop.requireApp("process.FormDesigner", "Preview", null, false);
MWF.xApplication.cms.FormDesigner.Preview = MWF.CMSFCPreview = new Class({
	Extends: MWF.FCPreview,
	Implements: [Options, Events],
	options: {
		"style": "default",
        "previewPath": "../x_desktop/cmspreview.html",
        "size": null
	},
	
	initialize: function(form, options){
		this.setOptions(options);
        var href = window.location.href;
        if (href.indexOf("debugger")!=-1) this.options.previewPath = "../x_desktop/cmspreview.html?debugger";

        this.path = "../x_component_process_FormDesigner/$Preview/";
        this.cssPath = "../x_component_process_FormDesigner/$Preview/"+this.options.style+"/css.wcss";

        this._loadCss();

        this.form = form;
		this.data = form._getFormData();
	},
    loadFormData: function(node){
        MWF.getJSON("../x_desktop/res/preview/cmsdoc.json", function(json){
            MWF.xDesktop.requireApp("cms.Xform", "Form", function(){
                this.appForm = new MWF.CMSForm(node, this.data);

                this.dataStr = JSON.stringify(this.data);

                this.appForm.formDataText = this.dataStr;
                this.appForm.app = this.form.designer;
                this.appForm.load();
            }.bind(this));
        }.bind(this));
    }
});