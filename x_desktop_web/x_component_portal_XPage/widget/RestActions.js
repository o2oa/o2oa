MWF.xApplication.process.Xform.widget = MWF.xApplication.process.Xform.widget || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.process.Xform.widget.RestActions = new Class({
    Extends: MWF.xDesktop.Actions.RestActions,
    initialize: function(serviceName){
        this.serviceName = serviceName;
        this.getAddress();
    },
    getActions: function(callback){
        this.actions = {
            "getProcess": {"uri": "/jaxrs/process/{id}/complex"}
            //"getProcess": {"uri": "/jaxrs/process/{id}"}
        };
        if (callback) callback();
    },
    getProcess: function(success, failure, id, async){
        this.invoke({"name": "getProcess", "async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    }
});