
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Datagrid", null, false);

MWF.xApplication.portal.PageDesigner.Module.Datagrid = MWF.PCDatagrid = new Class({
    Extends: MWF.FCDatagrid,
    options: {
        "style": "default",
        "propertyPath": "../x_component_portal_PageDesigner/Module/Datagrid/datagrid.html"
    },

    initialize: function(form, options){
        this.setOptions(options);

        this.path = "../x_component_portal_PageDesigner/Module/Datagrid/";
        this.cssPath = "../x_component_portal_PageDesigner/Module/Datagrid/"+this.options.style+"/css.wcss";

        this._loadCss();
        this.moduleType = "component";
        this.moduleName = "datagrid";

        this.form = form;
        this.container = null;
        this.containerNode = null;
        this.containers = [];
        this.elements = [];
        this.selectedMultiTds = [];
    },

});