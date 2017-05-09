MWF.xDesktop.requireApp("portal.PageDesigner", "Module.Package", null, false);

MWF.xApplication.portal.SourceDesigner = MWF.xApplication.portal.SourceDesigner || {};
MWF.xApplication.portal.SourceDesigner.Module = MWF.xApplication.portal.SourceDesigner.Module || {};

MWF.xApplication.portal.SourceDesigner.Module.Page = MWF.PSCPage =  new Class({
    Extends: MWF.PCPage,
    options: {
        "propertyPath": "/x_component_portal_SourceDesigner/Module/Page/page.html"
    },
    initializeBase: function(options){
        this.setOptions(options);
        this.path = "/x_component_portal_SourceDesigner/Module/Page/";
        this.cssPath = "/x_component_portal_SourceDesigner/Module/Page/"+this.options.style+"/css.wcss";
        this._loadCss();
    }
});