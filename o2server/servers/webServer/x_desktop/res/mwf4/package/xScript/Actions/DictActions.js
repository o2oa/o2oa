MWF.xScript = MWF.xScript || {};
MWF.xScript.Actions = MWF.xScript.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xScript.Actions.DictActions = new Class({
	initialize: function(){
        this.actionPath = "/xScript/Actions/dictAction.json";
		this.action = new MWF.xDesktop.Actions.RestActions(this.actionPath, "x_processplatform_assemble_surface");
    },

    getDict: function(application, dict, path, success, failure, async){
        this.action.invoke({"name": "getDict","async": async, "urlEncode": false, "parameter": {"application": application, "applicationDict": dict, "path": path},	"success": success,	"failure": failure});
    },
    setDict: function(application, dict, path, data, success, failure, async){
        this.action.invoke({"name": "setDict", "data": data, "async": async, "urlEncode": false, "parameter": {"application": application, "applicationDict": dict, "path": path},	"success": success,	"failure": failure});
    },
    addDict: function(application, dict, path, data, success, failure, async){
        this.action.invoke({"name": "addDict", "data": data, "async": async, "urlEncode": false, "parameter": {"application": application, "applicationDict": dict, "path": path},	"success": success,	"failure": failure});
    },
    deleteDict: function(application, dict, path, success, failure, async){
        this.action.invoke({"name": "deleteDict","async": async, "urlEncode": false, "parameter": {"application": application, "applicationDict": dict, "path": path},	"success": success,	"failure": failure});
    }

});