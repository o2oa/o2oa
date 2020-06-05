MWF.xAction.RestActions.Action["x_meeting_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,


    saveBuilding: function(data, success, failure, async){
        if (data.id){
            this.updateBuilding(data, success, failure, async);
        }else{
            this.addBuilding(data, success, failure, async);
        }
    },
    updateBuilding: function(data, success, failure, async){
        this.action.invoke({"name": "updateBuilding", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },
    addBuilding: function(data, success, failure, async){
        this.action.invoke({"name": "addBuilding", "async": async, "data": data, "success": success, "failure": failure});
    },
    saveRoom: function(data, success, failure, async){
        if (data.id){
            this.updateRoom(data, success, failure, async);
        }else{
            this.addRoom(data, success, failure, async);
        }
    },
    updateRoom: function(data, success, failure, async){
        this.action.invoke({"name": "updateRoom", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },
    addRoom: function(data, success, failure, async){
        this.action.invoke({"name": "addRoom", "async": async, "data": data, "success": success, "failure": failure});
    },

    getFileUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getFileDownloadUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getFile: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getFileDownload: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },

    saveMeeting: function(data, success, failure, async){
        if (data.id){
            this.updateMeeting(data, success, failure, async);
        }else{
            this.addMeeting(data, success, failure, async);
        }
    },
    updateMeeting: function(data, success, failure, async){
        this.action.invoke({"name": "updateMeeting", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },
    addMeeting: function(data, success, failure, async){
        this.action.invoke({"name": "addMeeting", "async": async, "data": data, "success": success, "failure": failure});
    }

});