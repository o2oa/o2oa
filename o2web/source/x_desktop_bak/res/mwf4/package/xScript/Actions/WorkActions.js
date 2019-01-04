MWF.xScript = MWF.xScript || {};
MWF.xScript.Actions = MWF.xScript.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xScript.Actions.ScriptActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface");
        this.action.getActions = function(callback){
            this.actions = {
                "setTitle": {"uri": "/jaxrs/work/{id}/processing","method": "PUT"}
            }
            if (callback) callback();
        }
    },
    setTitle: function(id, data, success, failure, async){
        this.action.invoke({"name": "getScriptByName", "data": data, "async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    }
});