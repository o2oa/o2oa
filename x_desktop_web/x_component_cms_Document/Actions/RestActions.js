MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Document = MWF.xApplication.cms.Document || {};
MWF.xApplication.cms.Document.Actions = MWF.xApplication.cms.Document.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.cms.Document.Actions.RestActions = new Class({
    initialize: function(){
        this.actionPath = "/x_component_cms_Document/Actions/action.json";

        this.actionDocument = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "x_component_cms_Document");
        this.actionDocument.getActions = function(callback){
            this.getActionActions(this.actionDocument, callback);
        }.bind(this);

        this.actionData = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "x_component_cms_Document");
        this.actionData.getActions = function(callback){
            this.getActionActions(this.actionData, callback);
        }.bind(this);

        this.actionAttachment = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "x_component_cms_Document");
        this.actionAttachment.getActions = function(callback){
            this.getActionActions(this.actionAttachment, callback);
        }.bind(this);

        this.actionHotPic = new MWF.xDesktop.Actions.RestActions("", "x_hotpic_assemble_control", "x_component_cms_Document");
        this.actionHotPic.getActions = function(callback){
            this.getActionActions(this.actionHotPic, callback);
        }.bind(this);


    },
    getActionActions: function(action, callback){
        if (!action.actions){
            this.getActions(function(json){
                action.actions = this.actions;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getActions: function(callback){
        if (!this.actions){
            MWF.getJSON(this.actionPath, function(json){
                this.actions = json;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },

    getId: function(count, success, failure, async){
        this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
    },
    getUUID: function(success){

        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0];
            if (success) success(id);
        },	"failure": null});
        return id;
    },

    listColumnController: function(appId,success, failure, async){
        this.actionDocument.invoke({"name": "listColumnController","async": async, "parameter": {"appId": appId}, "success": success,	"failure": failure});
    },

    getDocument: function(docId, success, failure){
        this.actionDocument.invoke({"name": "getDocument", "parameter": {"id": docId },"success": success,"failure": failure});
    },
    saveDocument: function(documentData, success, failure, async){
        if (!documentData.isNewDocument){
            this.updateDocument(documentData, success, failure, async);
        }else{
            this.addDocument(documentData, success, failure, async);
        }
    },
    addDocument: function(documentData, success, failure, async){
        delete documentData.attachmentList;
        this.actionDocument.invoke({"name": "addDocument","data": documentData,"success": success,"failure": failure,"async": async});
    },
    updateDocument: function(documentData, success, failure, async){
        delete documentData.attachmentList;
        this.actionDocument.invoke({"name": "updateDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure,"async": async});
    },
    removeDocument: function(id, success, failure, async){
        this.actionDocument.invoke({"name": "removeDocument", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    publishDocument: function(documentData, success, failure){
        this.actionDocument.invoke({"name": "publishDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    cancelPublishDocument: function(documentData, success, failure){
        this.actionDocument.invoke({"name": "cancelPublishDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    archiveDocument: function(documentData, success, failure){
        this.actionDocument.invoke({"name": "archiveDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    redraftDocument: function(documentData, success, failure){
        this.actionDocument.invoke({"name": "redraftDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },


    getCategory: function(id, success, failure){
        var _self = this;
        this.actionDocument.invoke({"name": "getCategory","parameter": {"id": id },"success": function(json){
            _self.transCategoryData(json);
            success.call(this,json);
        },"failure": failure});
    },
    transCategoryData : function( json ){
        var trans = function(category){
            if(!category.name)category.name = category.catagoryName;
            if(!category.alias)category.alias = category.catagoryAlias;
            if(!category.catagoryName)category.catagoryName = category.name;
            if(!category.catagoryAlias)category.catagoryAlias = category.alias;
        };
        if( json.data ){
            if( typeOf(json.data) == "array" ){
                json.data.each( function(category){
                    trans(category)
                })
            }else{
                trans(json.data)
            }
        }else{
            json.data = [];
        }
    },

    getForm: function(formId, success, failure, async){
        this.actionDocument.invoke({"name": "getForm","async": async, "parameter": {"id": formId},	"success": success,	"failure": failure});
    },

    saveData: function(success, failure, id, data, async){
        if( !data.isNew ) {
            this.updateData(success, failure, id, data, async);
        }else{
            this.addData(success, failure, id, data, async);
        }
    },
    addData: function(success, failure, id, data, async){
        this.actionData.invoke({"name": "addData","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    updateData: function(success, failure, id, data, async){
        this.actionData.invoke({"name": "updateData","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    listAttachment: function(documentid, success, failure, async){
        this.actionDocument.invoke({"name": "listAttachment","async": async, "parameter": {"documentid": documentid},	"success": success,	"failure": failure});
    },
    uploadAttachment: function(id, success, failure, formData, file){
        this.actionAttachment.invoke({"name": "uploadAttachment", "parameter": {"id": id},"data": formData,"file": file,"success": success,"failure": failure});
    },
    replaceAttachment: function(id, documentid, success, failure, formData, file){
        this.actionAttachment.invoke({"name": "replaceAttachment", "parameter": {"documentid": documentid, "id": id},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getAttachment: function(id, documentid, success, failure, async){
        this.actionAttachment.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    deleteAttachment: function(id, documentid, success, failure, async){
        this.actionAttachment.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },

    getAttachmentData: function(id, documentid){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getAttachmentStream: function(id, documentid){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },

    getAttachmentUrl: function(id, documentid, callback){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.actionAttachment.address+url);
        }.bind(this));
    },

    getHotPic: function(application, infoId , success, failure, async){
        this.actionHotPic.invoke({"name": "getHotPic", "parameter": {"application": application, "infoId" : infoId },"success": success,"failure": failure, "async": async});
    },
    saveHotPic: function(data, success, failure, async){
        this.actionHotPic.invoke({"name": "saveHotPic", data : data, "success": success,"failure": failure, "async": async});
    },
    removeHotPic: function(id, success, failure, async){
        this.actionHotPic.invoke({"name": "removeHotPic", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    removeHotPicByInfor: function(application, infoId , success, failure){
        this.actionHotPic.invoke({"name": "removeHotPicByInfor", "parameter": {"application": application, "infoId" : infoId },"success": success,"failure": failure});
    },
    listHotPicFilterPage : function(page, count,  filterData, success,failure, async){
        this.actionHotPic.invoke({"name": "listHotPicFilterPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    getInternetImageBaseBase64: function(data, success, failure, async){
        this.actionAttachment.invoke({"name": "getInternetImageBaseBase64", data : data, "success": success,"failure": failure, "async": async});
    },
    convertLocalImageToBase64: function(size, success, failure, formData, file){
        this.actionAttachment.invoke({"name": "convertLocalImageToBase64", "parameter": {"size": size},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getSubjectAttachmentBase64: function(id, size , success, failure, async){
        this.actionAttachment.invoke({"name": "getSubjectAttachmentBase64", "parameter": {"id": id, "size" : size },"success": success,"failure": failure, "async": async});
    }

});