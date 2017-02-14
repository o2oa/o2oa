MWF.xApplication.Deployment = MWF.xApplication.Deployment || {};
MWF.xApplication.Deployment.Actions = MWF.xApplication.Deployment.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Deployment.Actions.RestActions = new Class({
    initialize: function(){
        this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_component_assemble_control", "x_component_Deployment");
    },

    createComponent: function(data, success, failure, async){
        this.action.invoke({"name": "createComponent", "async": async, "data": data, "success": success, "failure": failure});
    },
    getComponent: function(id, success, failure, async){
        this.action.invoke({"name": "getComponent", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    updateComponent: function(id, data, success, failure, async){
        this.action.invoke({"name": "updateComponent", "async": async, "parameter": {"id": id}, "data": data, "success": success, "failure": failure});
    },
    removeComponent: function(id, success, failure, async){
        this.action.invoke({"name": "removeComponent", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    listComponent: function(success, failure, async){
        this.action.invoke({"name": "listComponent", "async": async, "success": success, "failure": failure});
    }
});