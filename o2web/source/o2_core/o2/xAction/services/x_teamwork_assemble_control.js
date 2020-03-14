MWF.xAction.RestActions.Action["x_teamwork_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    attachmentTaskUpload: function(id, success, failure, formData, file,site){
        this.action.invoke({"name": "attachmentTaskUpload", "parameter": {"id": id,"site":site},"data": formData,"file": file,"success": success,"failure": failure});
    },
    attachmentProjectUpload: function(id, success, failure, formData, file,site){
        this.action.invoke({"name": "attachmentProjectUpload", "parameter": {"id": id,"site":site},"data": formData,"file": file,"success": success,"failure": failure});
    },
    attachmentGet: function(id, success, failure, async){
        this.action.invoke({"name": "attachmentGet","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    attachmentRemove: function(id, success, failure, async){
        this.action.invoke({"name": "attachmentRemove","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    attachmentDownload: function(id,documentid){
        this.action.getActions(function(){
            var url = this.action.actions.attachmentDownload.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    attachmentDownloadStream: function(id,documentid,callback){
        this.action.getActions(function(){
            var url = this.action.actions.attachmentDownloadStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);

        }.bind(this));
    },

    attachmentDownloadUrl: function(id,documentid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.attachmentDownload.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },

    aa:function(){}
});