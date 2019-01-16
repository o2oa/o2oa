MWF.xAction.RestActions.Action["x_file_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    getAttachment: function(id){
        var url= this.designAddress+this.fileActions.getAttachmentData.uri;
        url = url.replace(/{id}/g, id);
        window.open(url);
    },
    saveFolder: function(data, success, failure){
        if (data.id){
            this.updateFolder(data, success, failure);
        }else{
            this.addFolder(data, success, failure);
        }
    },
    getFileUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },
    getFileDownloadUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },

    getBase64Code: function(success, failure, id, width, height, async){
        width = width || 0;
        height = height ||0;
        this.action.invoke({"name": "getBase64Code","async": async,"parameter": {"id": id, "height" : height, "width" : width},"success": success,"failure": failure});
    }
});