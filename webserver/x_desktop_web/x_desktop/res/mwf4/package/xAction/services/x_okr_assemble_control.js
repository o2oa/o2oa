MWF.xAction.RestActions.Action["x_okr_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    uploadAttachment: function(id, success, failure, formData, file,site){
        this.action.invoke({"name": "uploadAttachment", "parameter": {"id": id,"site":site},"data": formData,"file": file,"success": success,"failure": failure});
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
            window.open(this.actionAttachment.address+url);
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

    uploadReportAttachment: function(id, success, failure, formData, file,site){
        this.action.invoke({"name": "uploadReportAttachment", "parameter": {"id": id,"site":site},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getReportAttachmentData: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getReportAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getReportAttachmentStream: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getReportAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },
    deleteReportAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "deleteReportAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },

    aa:function(){}
});