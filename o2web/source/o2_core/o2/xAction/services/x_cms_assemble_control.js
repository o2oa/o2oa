MWF.xAction.RestActions.Action["x_cms_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    getUUID: function(success){
        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0];
            if (success) success(id);
        },	"failure": null});
        return id;
    },
    saveColumn: function(columnData, success, failure){
        if (!columnData.isNewColumn){
            this.updateColumn(columnData, success, failure);
        }else{
            this.addColumn(columnData, success, failure); 
        }
    },
    addColumn: function(columnData, success, failure){
        this.action.invoke({"name": "addColumn","data": columnData,"success": success,"failure": failure});
    },
    updateColumn: function(columnData, success, failure){
        this.action.invoke({"name": "updateColumn","data": columnData,"parameter": {"id": columnData.id},"success": success,"failure": failure});
    },
    updataColumnIcon: function(columnId, success, failure, formData, file){
        this.action.invoke({"name": "updataColumnIcon", "parameter": {"id": columnId},"data": formData,"file": file,"success": success,"failure": failure});
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
    getCategory: function(categoryId, success, failure, async){
        var _self = this;
        this.action.invoke({"name": "getCategory", "parameter": {"id": categoryId },"success": function(json){
            _self.transCategoryData(json);
            success.call(this,json);
        },"failure": failure, "async": async});
    },
    saveCategory: function(categoryData, success, failure){
        if (!categoryData.isNew){
            this.updateCategory(categoryData, success, failure);
        }else{
            this.addCategory(categoryData, success, failure);
        }
    },
    addCategory: function(categoryData, success, failure){
        this.action.invoke({"name": "addCategory","data": categoryData,"success": success,"failure": failure});
    },
    updateCategory: function(categoryData, success, failure){
        this.action.invoke({"name": "updateCategory","data": categoryData,"parameter": {"id": categoryData.id},"success": success,"failure": failure});
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

    saveFile: function(data, success, failure, async){
        if (data.id){
            this.updataFile(data.id, data, success, failure, async);
        }else{
            this.getUUID(function(id){
                data.id = id;
                this.addFile(data, success, failure, async);
            }.bind(this), false);

        }
    },

    saveForm: function(formData, mobileData, fieldList, success, failure){
        if (!formData.isNewForm){
            this.updateForm(formData, mobileData, fieldList, success, failure);
        }else{
            this.addForm(formData, mobileData, fieldList, success, failure);
        }
    },
    updateForm: function(formData, mobileData, fieldList, success, failure){ 
        var data, mobileData;
        if (formData) data = MWF.encodeJsonString(JSON.encode(formData));
        if (mobileData) mobileData = MWF.encodeJsonString(JSON.encode(mobileData));
        var json = {
            "id": formData.json.id,
            "name": formData.json.name,
            "alias": formData.json.name,
            "description": formData.json.description,
            "appId": formData.json.application,
            "formFieldList": fieldList
        };
        if (formData) json.data = data;
        if (mobileData) json.mobileData = mobileData;

        this.action.invoke({"name": "updataForm","data": json,"parameter": {"id": formData.json.id},"success": success,"failure": failure});
    },
    addForm: function(formData, mobileData, fieldList, success, failure){
        var data, mobileData;
        if (!formData.json.id){
            this.getUUID(function(id){
                formData.json.id = id;

                if (formData) data = MWF.encodeJsonString(JSON.encode(formData));
                if (mobileData) mobileData = MWF.encodeJsonString(JSON.encode(mobileData));

                var json = {
                    "id": formData.json.id,
                    "name": formData.json.name,
                    "alias": formData.json.name,
                    "description": formData.json.description,
                    "appId": formData.json.application,
                    "formFieldList": fieldList
                };
                if (formData) json.data = data;
                if (mobileData) json.mobileData = mobileData;
                this.action.invoke({"name": "addForm","data": json, "parameter": {"id": formData.json.id }, "success": success,"failure": failure});
            }.bind(this));
        }else{
            if (formData) data = MWF.encodeJsonString(JSON.encode(formData));
            if (mobileData) mobileData = MWF.encodeJsonString(JSON.encode(mobileData));

            var json = {
                "id": formData.json.id,
                "name": formData.json.name,
                "alias": formData.json.name,
                "description": formData.json.description,
                "appId": formData.json.application,
                "formFieldList": fieldList
            };
            if (formData) json.data = data;
            if (mobileData) json.mobileData = mobileData;
            this.action.invoke({"name": "addForm","data": json, "parameter": {"id": formData.json.categoryId}, "success": success,"failure": failure});
        }
    },
    addFormTemplate: function(formData, mobileData, templateData, success, failure){
        var data, mobileDataStr;
        this.getUUID(function(id){
            //formData.json.id = id;
            templateData.id = id;
            if (formData){
                var tFormData = Object.clone(formData);
                tFormData.json.id = id;
                tFormData.json.name = templateData.name;
                data = MWF.encodeJsonString(JSON.encode(tFormData));
            }
            if (mobileData){
                var tMobileData = Object.clone(mobileData);
                tMobileData.json.id = id;
                tMobileData.json.name = templateData.name;
                mobileDataStr = MWF.encodeJsonString(JSON.encode(tMobileData));
            }

            //templateData.name
            //templateData.category
            //templateData.description

            if (formData) templateData.data = data;
            if (mobileData) templateData.mobileData = mobileDataStr;
            this.action.invoke({"name": "addFormTemplate","data": templateData, "success": success,"failure": failure});
        }.bind(this));
    },
    saveDictionary: function(data, success, failure){
        if (data.id || data.appDictId ){
            this.updateDictionary(data, success, failure);
        }else{
            this.addDictionary(data, success, failure);
        }
    },
    updateDictionary: function(data, success, failure){
        if(!data.alias || data.alias=="")data.alias=data.name;
        this.action.invoke({"name": "updataDictionary","data": data,"parameter": {"appDictId": data.id },"success": success,"failure": failure});
    },
    addDictionary: function(data, success, failure){
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                data.appDictId = id;
                if(!data.alias || data.alias=="")data.alias=data.name;
                this.action.invoke({"name": "addDictionary","data": data, "parameter": {"appDictId": data.id },"success": success,"failure": failure});
            }.bind(this));
        }
    },
    getScript: function(id, success, failure, async){
        var _self = this;
        this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": function(json){
            _self.transScriptData(json);
            success.call(this,json);
        },	"failure": failure});
    },
    getScriptByName: function(name, appId, success, failure, async){
        var _self = this;
        this.action.invoke({"name": "getScriptByName","async": async, "parameter": {"name": name, "appId": appId},	"success": function(json){
            _self.transScriptData(json);
            success.call(this,json);
        },	"failure": failure});
    },
    saveScript: function(data, success, failure){
        if (!data.isNewScript){
            this.updateScript(data, success, failure);
        }else{
            this.addScript(data, success, failure);
        }
    },
    updateScript: function(data, success, failure){
        this.transScriptData(data,"send");
        this.action.invoke({"name": "updataScript","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    addScript: function(data, success, failure){
        this.transScriptData(data,"send");
        var _self = this;
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "addScript","data": data,"success": success,"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "addScript","data": data,"success": success,"failure": failure});
        }
    },
    transScriptData : function( json, type ){
        var trans = function(json){
            if( type && type == "send" ){
                if(!json.appId )json.appId = json.application;
                if(!json.appName )json.appName = json.applicationName;
                if( json.application )delete json.application;
                if( json.applicationName )delete json.applicationName;
            }else{
                if(!json.appId )json.appId = json.application;
                if(!json.appName )json.appName = json.applicationName;
                if(!json.application )json.application = json.appId;
                if(!json.applicationName )json.application = json.appName;
            }
        };
        if( typeOf(json) == "array" ){
            json.each( function(json){
                trans(json)
            })
        }else{
            trans(json)
        }
    },
    saveView: function(viewData, success, failure){
        if (!viewData.isNew){
            this.updateView(viewData, success, failure);
        }else{
            this.addView(viewData, success, failure);
        }
    },
    addView: function(viewData, success, failure){
        this.action.invoke({"name": "addView","data": viewData,"success": success,"failure": failure});
    },
    updateView: function(viewData, success, failure){
        this.action.invoke({"name": "updateView","data": viewData,"parameter": {"id": viewData.id},"success": success,"failure": failure});
    },

    saveViewColumn: function(data, success, failure,async){
        if (!data.isNew){
            this.updateViewColumn(data, success, failure,async);
        }else{
            this.addViewColumn(data, success, failure,async);
        }
    },

    saveCategoryView: function(data, success, failure,async){
        if (!data.isNew){
            this.updateViewColumn(data, success, failure,async);
        }else{
            this.addViewColumn(data, success, failure,async);
        }
    },
    saveQueryView: function(data, success, failure,async){
        if ( data.isNewView ){
            data.isNewView = false;
            this.addQueryView(data, success, failure,async);
        }else{
            this.updateQueryView(data, success, failure,async);
        }
    },
    addQueryView: function(viewData, success, failure, async){
        var data =viewData.data;
        var dataStr = JSON.encode(viewData.data);
        viewData.data = dataStr;
        if (!data.id){
            this.getUUID(function(id){
                viewData.id = id;
                this.action.invoke({"name": "addQueryView","data": viewData, "success": success,"failure": failure,"async": async});
                viewData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addQueryView","data": viewData, "success": success,"failure": failure,"async": async});
            viewData.data = data;
        }
    },
    updateQueryView: function(viewData, success, failure, async){
        var data =viewData.data;
        var dataStr = JSON.encode(viewData.data);
        viewData.data = dataStr;
        this.action.invoke({"name": "updateQueryView","data": viewData,"async": async,"parameter": {"id": viewData.id},"success": success,"failure": failure});
        viewData.data = data;
    },

    //----document---
    saveDocument: function(documentData, success, failure, async){
        if (!documentData.isNewDocument){
            this.updateDocument(documentData, success, failure, async);
        }else{
            this.addDocument(documentData, success, failure, async);
        }
    },
    addDocument: function(documentData, success, failure, async){
        delete documentData.attachmentList;
        this.action.invoke({"name": "addDocument","data": documentData,"success": success,"failure": failure,"async": async});
    },
    updateDocument: function(documentData, success, failure, async){
        delete documentData.attachmentList;
        this.action.invoke({"name": "updateDocument","data": documentData,"parameter": {"id": documentData.id},"success": success,"failure": failure,"async": async});
    },
    saveData: function(success, failure, id, data, async){
        if( !data.isNew ) {
            this.updateData(success, failure, id, data, async);
        }else{
            this.addData(success, failure, id, data, async);
        }
    },
    uploadAttachment: function(id, success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "parameter": {"id": id},"data": formData,"file": file,"success": success,"failure": failure});
    },
    replaceAttachment: function(id, documentid, success, failure, formData, file){
        this.action.invoke({"name": "replaceAttachment", "parameter": {"documentid": documentid, "id": id},"data": formData,"file": file,"success": success,"failure": failure});
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
    },
    getImageUrl: function(id, documentid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getImage.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },
    //--index--
    listDocumentFilterNext: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDocumentFilterNext","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDocumentFilterPrev: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDocumentFilterPrev","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDraftNext: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDraftNext","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listDraftPrev: function(id, count, data, success, failure, async){
        if( data && !data.documentType )data.documentType = "全部";
        this.action.invoke({"name": "listDraftPrev","async": async, "data": data, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    //--module--
    importDocumentFormExcel: function(categoryId, success, failure, formData, file){
        this.action.invoke({"name": "importDocumentFormExcel", "parameter": {"categoryId": categoryId},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getCategoryByAlias: function(alias, success, failure){
        var _self = this;
        this.action.invoke({"name": "getCategoryByAlias","parameter": {"alias": alias },"success": function(json){
            _self.transCategoryData(json);
            success.call(this,json);
        },"failure": failure});
    },
    batchModifyData: function(docIds, dataChanges, success, failure){
        this.action.invoke({"name": "batchModifyData","data": { docIds : docIds, dataChanges : dataChanges },"success": success,"failure": failure});
    }
});