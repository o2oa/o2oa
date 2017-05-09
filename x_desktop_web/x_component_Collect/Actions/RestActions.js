MWF.xApplication.Collect = MWF.xApplication.Collect || {};
MWF.xApplication.Collect.Actions = MWF.xApplication.Collect.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Collect.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_program_center", "x_component_Collect");
	},

    collectConnected: function(success, failure, async){
        this.action.invoke({"name": "collectConnected","async": async, "success": success,	"failure": failure});
    },
    getCollectConfig: function(success, failure, async){
        this.action.invoke({"name": "getCollectConfig","async": async, "success": success,	"failure": failure});
    },
    collectValidate: function(success, failure, async){
        this.action.invoke({"name": "collectValidate","async": async, "success": success,	"failure": failure});
    },
    collectValidateInput: function(data, success, failure, async){
        this.action.invoke({"name": "collectValidateInput","async": async, "data": data, "success": success,	"failure": failure});
    },

    updateCollect: function(data, success, failure, async){
        this.action.invoke({"name": "updateCollect","async": async, "data": data, "success": success,	"failure": failure});
    },
    createCollect: function(data, success, failure, async){
        this.action.invoke({"name": "createCollect","async": async, "data": data, "success": success,	"failure": failure});
    },
    getCode: function(mobile, success, failure, async){
        this.action.invoke({"name": "getCode","async": async,"parameter": {"mobile": mobile},"success": success,"failure": failure});
    },
    codeValidate: function(data, success, failure, async){
        this.action.invoke({"name": "codeValidate","async": async, "data": data, "success": success,	"failure": failure});
    },
    resetPassword: function(data, success, failure, async){
        this.action.invoke({"name": "resetPassword","async": async, "data": data, "success": success,	"failure": failure});
    },
    nameExist: function(name, success, failure, async){
        this.action.invoke({"name": "nameExist","async": async,"parameter": {"name": name},"success": success,"failure": failure});
    },
    passwordValidate: function(data, success, failure, async){
        this.action.invoke({"name": "passwordValidate","async": async, "data": data, "success": success,	"failure": failure});
    }
});