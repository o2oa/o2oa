MWF.xScript = MWF.xScript || {};
MWF.xScript.Actions = MWF.xScript.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xScript.Actions.CMSScriptActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control");
        this.action.getActions = function(callback){
            this.actions = {
                //"getScriptByName": {"uri": "/jaxrs/script/{uniqueName}/app/{appId}","method": "POST"},
                "getScriptByName": {"uri": "/jaxrs/script/{uniqueName}/app/{appId}/imported"}
            };
            if (callback) callback();
        }
    },
    getScriptByName: function(appId, uniqueName, included, success, failure, async){
        this.action.invoke({"name": "getScriptByName", "async": async, "parameter": {"appId": appId, "uniqueName": uniqueName},	"success": success,	"failure": failure});
    },
    getScript: function(id, success, failure, async){
        this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    //getScriptTextDepend: function(id, text, included, success, failure){
    //    if (included.indexOf(id)==-1){
    //        this.getScript(id, function(json){
    //            included.push(json.data.name);
    //            included.push(json.data.id);
    //
    //            text = json.data.text+"\n"+text;
    //            if (json.data.dependScriptList.length){
    //                var count = json.data.dependScriptList.length;
    //                var current = 0;
    //
    //                json.data.dependScriptList.each(function(id){
    //                    this.getScriptTextDepend
    //                }.bind(this))
    //            }
    //
    //        }, failure);
    //    }
    //},
    //
    //getScriptTextByName: function(application, name, success, included, failure, async){
    //    var text = "";
    //    if (included.indexOf(name)==-1){
    //        this.getScriptByName(application, name, function(json){
    //            included.push(json.data.name);
    //            included.push(json.data.id);
    //            if (json.data.dependScriptList.length){
    //                json.data.dependScriptList.each(function(id){
    //
    //                }.bind(this))
    //            }
    //
    //
    //        }.bind(this));
    //    }else{
    //
    //    }
    //}

});