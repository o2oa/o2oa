MWF.xApplication.cms.Index = MWF.xApplication.cms.Index || {};
MWF.xApplication.cms.Index.Actions = MWF.xApplication.cms.Index.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.cms.Index.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_cms_assemble_control", "x_component_cms_Index");
        this.actionPerson = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_personal", "x_component_cms_Index");
        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_cms_Index");
        this.actionProcess = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_processplatform_assemble_surface", "x_component_cms_Index");
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


    listDocumentAll: function(id, count, categoryId, success, failure, async){
        this.action.invoke({"name": "listDocumentAll","async": async, "parameter": {"id": id, "count": count, "categoryId": categoryId}, "success": success,	"failure": failure});
    },
    listDocumentFilterNext: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDocumentFilterNext","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDocumentFilterPrev: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDocumentFilterPrev","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listViewDataNext: function(id, count, data, success, failure, async){
        this.action.invoke({"name": "listViewDataNext","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDraftNext: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDraftNext","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDraftPrev: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDraftPrev","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDraftFilterAttribute: function( success, failure, async){
        this.action.invoke({"name": "listDraftFilterAttribute","async": async, "success": success,	"failure": failure});
    },
    listPublishFilterAttribute: function( success, failure, async){
        this.action.invoke({"name": "listPublishFilterAttribute","async": async, "success": success,	"failure": failure});
    },
    listArchiveFilterAttribute: function( success, failure, async){
        this.action.invoke({"name": "listArchiveFilterAttribute","async": async, "success": success,	"failure": failure});
    },

    listCategoryDraftFilterAttribute: function( categoryId, success, failure, async){
        this.action.invoke({"name": "listCategoryDraftFilterAttribute","async": async,  "parameter": {"categoryId": categoryId }, "success": success,	"failure": failure});
    },
    listCategoryPublishFilterAttribute: function(categoryId, success, failure, async){
        this.action.invoke({"name": "listCategoryPublishFilterAttribute","async": async,  "parameter": {"categoryId": categoryId }, "success": success,	"failure": failure});
    },
    listCategoryArchiveFilterAttribute: function( categoryId, success, failure, async){
        this.action.invoke({"name": "listCategoryArchiveFilterAttribute","async": async,  "parameter": {"categoryId": categoryId }, "success": success,	"failure": failure});
    },

    getDocument: function(docId, data, success, failure){
        this.action.invoke({"name": "getDocument", "parameter": {"id": docId },"success": success,"failure": failure});
    },
    saveDocument: function(documentData, success, failure){
        if (!documentData.isNewDocument){
            this.updateDocument(documentData, success, failure);
        }else{
            this.addDocument(documentData, success, failure);
        }
    },
    addDocument: function(documentData, success, failure){
        this.action.invoke({"name": "addDocument","data": documentData,"success": success,"failure": failure});
    },
    updateDocument: function(documentData, success, failure){
        this.action.invoke({"name": "updateDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    removeDocument: function(id, success, failure, async){
        this.action.invoke({"name": "removeDocument", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    publishDocument: function(documentData, success, failure){
        this.action.invoke({"name": "publishDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    cancelPublishDocument: function(documentData, success, failure){
        this.action.invoke({"name": "cancelPublishDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    achiveDocument: function(documentData, success, failure){
        this.action.invoke({"name": "achiveDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },
    redraftDocument: function(documentData, success, failure){
        this.action.invoke({"name": "redraftDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure});
    },

    //listColumnController: function(appId,success, failure, async){
    //    this.action.invoke({"name": "listColumnController","async": async, "parameter": {"appId": appId}, "success": success,	"failure": failure});
    //},

    listColumn: function( success, failure, async){
        this.action.invoke({"name": "listColumn","async": async, "success": success,	"failure": failure});
    },
    listColumnByPublish: function( success, failure, async){
        this.action.invoke({"name": "listColumnByPublish","async": async, "success": success,	"failure": failure});
    },
    listColumnByAdmin: function( success, failure, async){
        this.action.invoke({"name": "listColumnByAdmin","async": async, "success": success,	"failure": failure});
    },
    getColumn: function(columnData, success, failure){
        this.action.invoke({"name": "getColumn","parameter": {"id": columnData.id},"success": success,"failure": failure});
    },

    listCategory: function( columnId, success, failure, async){
        var _self = this;
        this.action.invoke({"name": "listCategory","parameter": {"appId": columnId },"async": async, "success": function(json){
            _self.transCategoryData(json);
            success.call(this,json);
        },"failure": failure});
    },
    listCategoryByPublisher: function( columnId, success, failure, async){
        var _self = this;
        this.action.invoke({"name": "listCategoryByPublisher","parameter": {"appId": columnId },"async": async, "success": function(json){
            _self.transCategoryData(json);
            success.call(this,json);
        },"failure": failure});
    },
    getCategory: function(categoryData, success, failure){
        var _self = this;
        this.action.invoke({"name": "getCategory","data": categoryData,"parameter": {"id": categoryData },"success": function(json){
            _self.transCategoryData(json);
            success.call(this,json);
        },"failure": failure});
    },
    transCategoryData : function( json ){
        var trans = function(category){
            if(!category.name)category.name = category.categoryName;
            if(!category.alias)category.alias = category.categoryAlias;
            if(!category.categoryName)category.categoryName = category.name;
            if(!category.categoryAlias)category.categoryAlias = category.alias;
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

	getForm: function(form, success, failure, async){
		this.action.invoke({"name": "getForm","async": async, "parameter": {"id": form},	"success": success,	"failure": failure});
	},
    //getDictionary: function(id, success, failure, async){
    //    this.action.invoke({"name": "getDictionary","async": async, "parameter": {"applicationDict": id},	"success": success,	"failure": failure});
    //},
    //getScript: function(id, success, failure, async){
    //    this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    //},
    //getScriptByName: function(name, application, success, failure, async){
    //    this.action.invoke({"name": "getScriptByName","async": async, "parameter": {"name": name, "applicationId": application},	"success": success,	"failure": failure});
    //}

    listViewByCategory: function(categoryId, success, failure, async){
        this.action.invoke({"name": "listViewByCategory","async": async, "parameter": {"categoryId": categoryId}, "success": success,	"failure": failure});
    },
    getView: function(id, success, failure){
        this.action.invoke({"name": "getView", "parameter": {"id": id },"success": success,"failure": failure});
    },
    listCategoryViewByCategory: function(categoryId, success, failure, async){
        this.action.invoke({"name": "listCategoryViewByCategory","async": async, "parameter": {"categoryId": categoryId}, "success": success,	"failure": failure});
    },

    listWorkFilter: function(id, count, application, data, success, failure, async){
        this.actionProcess.invoke({"name": "listWorkFilter","async": async, "data": data, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },
    startWork: function(success, failure, id, data, async){
        this.actionProcess.invoke({"name": "startWork","data": data,"async": async, "parameter": {"processId": id},	"success": success,	"failure": failure});
    },
    saveWorkData: function(success, failure, id, data, async){
        this.actionProcess.invoke({"name": "saveWorkData","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    getCurrentPerson : function(success, failure, async){
        this.actionPerson.invoke({"name": "getCurrentPerson","async": async,"success": success,	"failure": failure});
    },
    listDepartmentWithIdentity : function(success, failure, data, async ){
        this.actionOrg.invoke({"name": "listDepartmentWithIdentity","async": async,"success": success, data : data, "failure": failure});
    }

});