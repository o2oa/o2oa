MWF.xApplication.VsettanMail = MWF.xApplication.VsettanMail || {};
MWF.xApplication.VsettanMail.Actions = MWF.xApplication.VsettanMail.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.VsettanMail.Actions.RestActions = new Class({
    initialize: function(){
        this.actionPath = "/x_component_VsettanMail/Actions/action.json";

        this.action = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_personal", "x_component_VsettanMail");
        this.action.getActions = function(callback){
            this.getActionActions(this.action, callback);
        }.bind(this);
    },
    getActionActions: function(action, callback){
        if (!action.actions){
            action.actions = {"getPassword": {"uri": "/jaxrs/password/decrypt"}};
        }
        if (callback) callback();
    },
    getPassword: function(success, failure, async){
        this.action.invoke({"name": "getPassword","async": async, "success": success, "failure": failure});
    }
});