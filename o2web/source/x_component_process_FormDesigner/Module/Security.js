MWF.xApplication.process.FormDesigner.Module = MWF.xApplication.process.FormDesigner.Module || {};
MWF.xDesktop.requireApp("process.FormDesigner", "Module.Select", null, false);
MWF.xApplication.process.FormDesigner.Module.Security = MWF.FCSecurity = new Class({
    Extends: MWF.FCSelect,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "type": "security",
        "path": "../x_component_process_FormDesigner/Module/Security/",
        "propertyPath": "../x_component_process_FormDesigner/Module/Security/security.html"
    },
    _getNewId: function(){
        var id = "objectSecurityClearance";
        return (this.form.json.moduleList[id]) ? "" : id;
    },
    create: function(data, e, group){
        data.moduleGroup = group;
        this.json = data;
        this.json.id = this._getNewId();
        if (this.json.id){
            this._createMoveNode();
            this._setNodeMove(e, "create");
        }else{
            this.form.designer.notice(this.form.designer.lp.modules.securityInfo, "error", this.form.node);
        }
    }
});
