MWF.xAction.RestActions.Action["x_processplatform_assemble_surface"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    exportView: function(app, view, filter){
        this.action.invoke({"name": "exportView","async": true, "data": filter, "parameter": {"flag": view, "applicationFlag": app},	"success": function(json){
            var url = this.action.actions["exportViewResult"].uri;
            //url = url.replace("{applicationFlag}", app);
            url = url.replace("{flag}", json.data.id);
            window.open(o2.filterUrl(this.action.address+url, "_blank"));
        }.bind(this)});
    },
    saveDictionary: function(data, success, failure){
        if (data.id){
            this.updateDictionary(data, success, failure);
        }else{
            this.addDictionary(data, success, failure);
        }
    },
    updateDictionary: function(data, success, failure){
        this.action.invoke({"name": "updataDictionary","data": data,"parameter": {"applicationDictFlag": data.id, "applicationFlag": data.application},"success": success,"failure": failure});
    },
    addDictionary: function(data, success, failure){
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "addDictionary","data": data,"success": success,"failure": failure});
            }.bind(this));
        }
    },
    removeWork: function(id, application, all, success, failure, async){
        if (all){
            this.action.invoke({"name": "removeAllWork","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "removeWork","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
        }
    },

    getAttachmentData: function(id, workid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workid}", encodeURIComponent(workid));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getWorkcompletedAttachmentData: function(id, workid){
        this.action.getActions(function(){
            var url = this.action.actions.getWorkcompletedAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workCompletedId}", encodeURIComponent(workid));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getAttachmentStream: function(id, workid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workid}", encodeURIComponent(workid));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getWorkcompletedAttachmentStream: function(id, workid){
        this.action.getActions(function(){
            var url = this.action.actions.getWorkcompletedAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workCompletedId}", encodeURIComponent(workid));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },

    getAttachmentUrl: function(id, workid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workid}", encodeURIComponent(workid));
            if (callback) callback(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getAttachmentWorkcompletedUrl: function(id, workid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getWorkcompletedAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workCompletedId}", encodeURIComponent(workid));
            if (callback) callback(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getWorkDataByPath: function(id, path, success, failure, async){
        var p = path.replace(/\./g, "/");
        if (id.workCompleted){
            this.action.invoke({"name": "getWorkcompletedDataByPath","async": async, "parameter": {"id": id.workCompleted, "path": p},	"success": success,	"failure": failure, "urlEncode":false});
        }else{
            this.action.invoke({"name": "getWorkDataByPath","async": async, "parameter": {"id": id.work, "path": p},	"success": success,	"failure": failure, "urlEncode":false});
        }

    },
    getJobDataByPath: function(id, path, success, failure, async){
        var p = path.replace(/\./g, "/");
        this.action.invoke({"name": "getJobDataByPath","async": async, "parameter": {"id": id, "path": p},	"success": success,	"failure": failure, "urlEncode":false});
    },

    startWork: function(par1, par2, par3, par4, async){
        var process = par3, data = par4, success = par1, failure = par2;
        if (o2.typeOf(par1)==="string"){
            process = par1;
            data = par2;
            success = par3;
            failure = par4;
        }
        this.getProcess(process, function(json){
            if (json.data.defaultStartMode=="draft"){
                this.draw(process, data, success, failure, async)
            }else{
                this.createtWork(process, data, success, failure, async)
            }
        }.bind(this), failure, async);
    }
});