MWF.xAction.RestActions.Action["x_bbs_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    getUUID: function(success){
        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0];
            if (success) success(id);
        },	"failure": null});
        return id;
    },
    uploadSectionIcon: function(id, success, failure, formData, file){
        this.action.invoke({"name": "uploadSectionIcon", "data": formData,"file": file, "parameter": {"id": id}, "success": success,"failure": failure});
    },
    uploadAttachment: function(documentid, success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "parameter": {"documentid": documentid},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    deleteAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    getAttachmentData: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },
    getAttachmentStream: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },

    getAttachmentUrl: function(id, documentid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },
    convertLocalImageToBase64: function(size, success, failure, formData, file){
        this.action.invoke({"name": "convertLocalImageToBase64", "parameter": {"size": size},"data": formData,"file": file,"success": success,"failure": failure});
    }
});