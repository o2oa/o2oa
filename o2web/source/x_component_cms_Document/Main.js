MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Document = MWF.xApplication.cms.Document || {};
MWF.xApplication.cms.Document.options.multitask = true;
MWF.xDesktop.requireApp("cms.Document", "HotLinkForm", null, false);
MWF.xApplication.cms.Document.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Document",
		"icon": "icon.png",
		"width": "1200",
		"height": "680",
		"title": MWF.xApplication.cms.Document.LP.title,
        "documentId": "",
        "isControl": false,
        "readonly": true,
        "autoSave" : true,
        "saveOnClose" : true,
        "postPublish" : null,
        "postDelete" : null,
        "formId" : null,
        "formEditId" : null
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Document.LP;
        if (this.status){
            this.options.documentId = this.status.documentId;
            this.options.readonly = (this.status.readonly=="true" || this.status.readonly==true) ? true : false;
            this.options.autoSave = (this.status.autoSave=="true" || this.status.autoSave==true) ? true : false;
            this.options.saveOnClose = (this.status.saveOnClose=="true" || this.status.saveOnClose==true) ? true : false;
            this.options.formId = this.status.formId;
            this.options.printFormId = this.status.printFormId;
        }
        if( this.options.documentId && this.options.documentId!=""){
            this.options.appId = "cms.Document"+this.options.documentId;
        }
	},
	loadApplication: function(callback){

        this.node = new Element("div", {"styles": this.css.content}).inject(this.content);

        MWF.require("MWF.widget.Mask", function(){
            this.mask = new MWF.widget.Mask({"style": "desktop"});

            this.formNode = new Element("div", {"styles": {"min-height": "100%"}}).inject(this.node);
           // MWF.xDesktop.requireApp("cms.Document", "Actions.RestActions", function(){
                this.action = MWF.Actions.get("x_cms_assemble_control"); //new MWF.xApplication.cms.Document.Actions.RestActions();
                if (!this.options.isRefresh){
                    this.maxSize(function(){
                        this.mask.loadNode(this.content);
                        this.loadDocument();
                    }.bind(this));
                }else{
                    this.mask.loadNode(this.content);
                    this.loadDocument();
                }
                if (callback) callback();
            //}.bind(this));

        }.bind(this));

        this.addEvent("queryClose", function(){
            this.refreshTaskCenter();
        }.bind(this));

        this.addKeyboardEvents();

    },
    refreshTaskCenter: function(){
        if (this.desktop.apps["cms.Explorer"]){
            this.desktop.apps["cms.Explorer"].content.unmask();
            this.desktop.apps["cms.Explorer"].refreshAll();
        }
    },
    addKeyboardEvents: function(){
        this.addEvent("keySave", function(e){
            this.keySave(e);
        }.bind(this));
    },
    keySave: function(e){
        if (this.appForm){
            if (!this.readonly){
                this.appForm.saveDocument();
                e.preventDefault();
            }
        }
    },
    reload: function(data){
        if (this.form){
            this.formNode.empty();
            MWF.release(this.form);
            this.form = null;
        }
        this.parseData(data);
        this.openDocument();
    },
    getDocument : function( callback ){
        var id = this.options.documentId;
        //if( this.options.anonymousAccess ){
        //    this.action.getDocumentByAnonymous(id, function(json){
        //        callback(json)
        //    }.bind(this), function( error ){
        //        this.notice(  this.lp.documentGettedError + ":" + error.responseText , "error");
        //        this.close();
        //    }.bind(this));
        //}else if( this.options.readonly ){
        //    this.action.viewDocument(id, function(json){
        //        callback(json)
        //    }.bind(this), function( error ){
        //        this.notice(  this.lp.documentGettedError + ":" + error.responseText , "error");
        //        this.close();
        //    }.bind(this));
        //}else{
        //    this.action.getDocument(id, function(json){
        //        callback(json)
        //    }.bind(this), function( error ){
        //        this.notice(  this.lp.documentGettedError + ":" + error.responseText , "error");
        //        this.close();
        //    }.bind(this));
        //}

        var documentMethod = "getDocument";
        if( this.options.anonymousAccess ){
            documentMethod = "getDocumentByAnonymous"
        }else if( this.options.readonly && !this.options.printFormId){
            documentMethod = "viewDocument";
        }

        var attachmentMethod = "listAttachment";
        if( this.options.anonymousAccess ){
            attachmentMethod = "listAttachmentByAnonymous"
        }

        o2.Actions.invokeAsync([
            {"action": this.action, "name": documentMethod},
            {"action": this.action, "name": attachmentMethod }
        ], {"success": function(json_document, json_att){
            if (json_document ){
                if( json_att && typeOf( json_att.data ) === "array" ){
                    json_document.data.attachmentList = json_att.data ;
                }else{
                    json_document.data.attachmentList = [];
                }
                callback(json_document)
            }else{
                this.notice(  this.lp.documentGettedError + ":" + error.responseText , "error");
                this.close();
            }
        }.bind(this), "failure": function(){
            this.notice(  this.lp.documentGettedError + ":" + error.responseText , "error");
            this.close();
        }.bind(this)}, id);
    },
    loadDocument: function(){
        this.getDocument( function(json){
            json.data = json.data || [];
            this.parseData(json.data);
            this.loadForm( this.formId );
        }.bind(this) );
    },
    errorDocument: function(){
        if (this.mask) this.mask.hide();
        this.node.set("text", "openError");
    },
    loadForm : function( formId, flag ){
        var success = function(json){
            if( layout.mobile ){
                this.form = (json.data.mobileData) ? JSON.decode(MWF.decodeJsonString(json.data.mobileData)): null;
                if( !this.form ){
                    this.form = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                }
            }else{
                this.form = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
            }
            //this.listAttachment();
            this.openDocument();
            if (this.mask) this.mask.hide();
        }.bind(this);
        var failure = function(error){
            //没有表单，重新获取分类表单
            if( !flag ){
                this.action.getCategory( this.document.categoryId, function(json){
                    var d = json.data;
                    this.formId = d.formId || d.readFormId;
                    if( this.readonly == true && d.readFormId && d.readFormId != "" ){
                        this.formId  = d.readFormId;
                    }
                    this.loadForm( this.formId, true );
                }.bind(this));
            }else{
                this.notice(  this.lp.formGettedError + ":" + error.responseText , "error");
                this.close();
            }
        }.bind(this);
        if( this.options.printFormId){
            this.action.getForm(this.options.printFormId, function( json ){
                success(json);
            }.bind(this), function(error){
                failure(error)
            }.bind(this));
        }else{
            if( this.options.anonymousAccess ){
                this.action.getFormByAnonymous(formId, function( json ){
                    success(json);
                }.bind(this), function(error){
                    failure(error)
                }.bind(this));
            }else{
                this.action.getForm(formId, function( json ){
                    success(json);
                }.bind(this), function(error){
                    failure(error)
                }.bind(this));
            }
        }
    },
    //listAttachment: function(){
    //    if( this.document.attachmentList && this.document.attachmentList.length > 0 ){
    //        this.action.listAttachment(this.options.documentId, function( json ){
    //            if (this.mask) this.mask.hide();
    //            this.attachmentList = json.data;
    //            this.attachmentList.each(function(att){
    //                att.lastUpdateTime = att.updateTime;
    //                att.person = att.creatorUid;
    //            })
    //            this.openDocument();
    //        }.bind(this), function(error){
    //            this.notice(  this.lp.attachmentGettedError  + ":" + error.responseText, "error");
    //            this.close();
    //        }.bind(this));
    //    }else{
    //        if (this.mask) this.mask.hide();
    //        this.attachmentList = [];
    //        this.openDocument();
    //    }
    //},
    isEmptyObject: function( obj ) {
        var name;
        for ( name in obj ) {
            return false;
        }
        return true;
    },
    parseData: function(data){

        var title = "";
        title = data.document.title;

        this.setTitle(title);

        data.document.subject = data.document.title;

        this.data =  data.data;

        this.attachmentList = data.attachmentList || [];
        this.attachmentList.each(function(att){
            att.lastUpdateTime = att.updateTime;
            att.person = att.creatorUid;
        });

        if( this.isEmptyObject(this.data) ){
            this.data.isNew = true;
        }else{
            this.data.isNew = false;
        }

        this.document = data.document;

        var isAdmin = false;

        if( MWF.AC.isCMSManager() ){
            this.options.isControl = true;
            isAdmin = true;
        }

        if( data.isAppAdmin ){
            this.options.isControl = true;
            isAdmin = true;
        }
        if( data.isCategoryAdmin ){
            this.options.isControl = true;
            isAdmin = true;
        }
        if( data.isManager ){
            this.options.isControl = true;
            isAdmin = true;
        }
        this.isAdmin = isAdmin;
        ////系统管理员
        //if( MWF.AC.isAdministrator() ){
        //    this.options.isControl = true;
        //    isAdmin = true;
        //}
        ////栏目管理员
        //if(this.controllers && this.controllers.contains(this.desktop.session.user.name) ){
        //    this.options.isControl = true;
        //    isAdmin = true;
        //}
        //文档创建人
        if( data.isCreator || this.desktop.session.user.distinguishedName==this.document.creatorPerson ){
            this.options.isControl = true;
        }

        if( data.isEditor ){ //作者权限
            this.options.isControl = true;
        }

        if( this.options.readonly ){ //强制只读
            this.readonly = true;
        }else{
            this.readonly = true;
            if(this.options.isControl && this.document.docStatus != "archived"){
                this.readonly = false;
            }
        }

        this.formId = this.document.form || this.document.readFormId;
        if( this.readonly == true && this.document.readFormId && this.document.readFormId != "" ){
            this.formId  = this.document.readFormId;
            if(this.options.formId){
                this.formId = this.options.formId
            }
        }else {
            if(this.options.formEditId){
                this.formId = this.options.formEditId
            }
        }

        if(this.readonly || this.document.docStatus == "published"){
            this.options.autoSave = false;
            this.options.saveOnClose = false;
        }

        //this.attachmentList = data.attachmentList;

        //this.inheritedAttachmentList = data.inheritedAttachmentList;
        var isControl = this.options.isControl;
        this.control = data.control ||  {
                "allowRead": true,
                "allowPublishDocument": isControl && this.document.docStatus == "draft",
                "allowSave": isControl && this.document.docStatus == "published",
                "allowPopularDocument": MWF.AC.isHotPictureManager() && this.document.docStatus == "published",
                "allowEditDocument":  isControl && !this.document.wf_workId,
                "allowDeleteDocument":  isControl && !this.document.wf_workId
            };
    },
    setPopularDocument: function(){
        var form = new MWF.xApplication.cms.Document.HotLinkForm(this, this.document, {
            documentId : this.options.documentId,
            summary :  this.data.explain || "",
            onPostOk : function( id ){

            }.bind(this)
        },{
            app : this, lp : this.lp, css : this.css, actions : this.action
        });
        form.create();
    },
    openDocument: function(){
        if (this.form){
            MWF.xDesktop.requireApp("cms.Xform", "Form", function(){
                this.appForm = new MWF.CMSForm(this.formNode, this.form, {
                    "readonly": this.readonly,
                    "autoSave" : this.options.autoSave,
                    "saveOnClose" : this.options.saveOnClose,
                    "onPostPublish" : this.options.postPublish,
                    "onPostDelete" : this.options.postDelete
                });
                this.appForm.businessData = {
                    "data": this.data,
                    "document": this.document,
                    "control": this.control,
                    "attachmentList": this.attachmentList,
                    "status": {
                        //"readonly": (this.options.readonly) ? true : false
                        "readonly": this.readonly
                    }
                };
                this.appForm.documentAction = this.action;
                this.appForm.app = this;
                this.appForm.load();
            }.bind(this));
        }
    },

    //errorDocument: function(){
    //
    //},

    recordStatus: function(){
        var status ={
            "documentId": this.options.documentId,
            "readonly": this.options.readonly,
            "autoSave" : this.options.autoSave,
            "saveOnClose" : this.options.saveOnClose
        };
        if( this.options.formId )status.formId = this.options.formId;
        if( this.options.printFormId )status.printFormId = this.options.printFormId;
        if(this.options.appId && this.options.appId!="")status.appId = this.options.appId;
        return status;
    },
    onPostClose: function(){
        if (this.appForm){
            this.appForm.modules.each(function(module){
                MWF.release(module);
            });
            MWF.release(this.appForm);
        }
    }

});