MWF.xScript = MWF.xScript || {};
MWF.xScript.Actions = MWF.xScript.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xScript.Actions.CMSDictActions = new Class({
	initialize: function(){
        this.actionPath = "/xScript/Actions/CMSDictAction.json";
		this.action = new MWF.xDesktop.Actions.RestActions(this.actionPath, "x_cms_assemble_control");
    },

    getDictWhole: function(appId, appDictId, success, failure, async){
        this.action.invoke({"name": "getDictWhole","async": async, "urlEncode": false, "parameter": {"appId": appId, "appDictId": appDictId},	"success": success,	"failure": failure});
    },
    getDict: function(appId, appDictId, path, success, failure, async){
        this.action.invoke({"name": "getDict","async": async, "urlEncode": false, "parameter": {"appId": appId, "appDictId": appDictId, "path": path},	"success": success,	"failure": failure});
    },
    setDict: function(appId, appDictId, path, data, success, failure, async){
        this.action.invoke({"name": "setDict", "data": data, "async": async, "urlEncode": false, "parameter": {"appId": appId, "appDictId": appDictId, "path": path},	"success": success,	"failure": failure});
    },
    addDict: function(appId, appDictId, path, data, success, failure, async){
        this.action.invoke({"name": "addDict", "data": data, "async": async, "urlEncode": false, "parameter": {"appId": appId, "appDictId": appDictId, "path": path},	"success": success,	"failure": failure});
    },
    deleteDict: function(appId, appDictId, path, success, failure, async){
        this.action.invoke({"name": "deleteDict","async": async, "urlEncode": false, "parameter": {"appId": appId, "appDictId": dict, "appDictId": path},	"success": success,	"failure": failure});
    }

});