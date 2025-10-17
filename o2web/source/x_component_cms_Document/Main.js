MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Document = MWF.xApplication.cms.Document || {};
MWF.xApplication.cms.Document.options = MWF.xApplication.cms.Document.options || Object.clone(o2.xApplication.Common.options);
MWF.xApplication.cms.Document.options.multitask = true;
MWF.xApplication.cms.Document.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],

    options: {
        "style": "default",
        "name": "cms.Document",
        "icon": "icon.png",
        "width": "1200",
        "height": "680",
        "title": "",
        "documentId": "",
        "isControl": false,
        "readonly": true,
        "autoSave" : true,
        "saveOnClose" : null,
        "postPublish" : null,
        "postDelete" : null,
        "forceFormId" : null, //不管编辑还是阅读都用此表单打开，优先使用
        "printFormId" : null, //打印表单，不管编辑还是阅读都用此表单打开，仅此于forceFormId
        "readFormId" : null, //强制的阅读表单，优先于表单的readFormId
        "editFormId" : null, //强制的编辑表单，优先于表单的formId
        "useProcessForm": false //表单ID参数是流程的
    },
    onQueryLoad: function(){
        if (!this.options.title && !layout.mobile) this.setOptions({"title": MWF.xApplication.cms.Document.LP.title})
        this.lp = MWF.xApplication.cms.Document.LP;
        if (this.status && Object.keys(this.status).length > 0){
            this.options.documentId = this.status.documentId || this.options.documentId;
            this.options.readonly = (this.status.readonly==="true" || this.status.readonly===true) ? true : false;
            this.options.autoSave = (this.status.autoSave==="true" || this.status.autoSave===true) ? true : false;
            this.options.saveOnClose = (this.status.saveOnClose==="true" || this.status.saveOnClose===true) ? true : false;

            this.options.formId = this.status.formId;
            this.options.formEditId = this.status.formEditId;

            this.options.printFormId = this.status.printFormId;
            this.options.forceFormId = this.status.forceFormId;
            this.options.readFormId = this.status.readFormId;
            this.options.editFormId = this.status.editFormId;
            this.options.useProcessForm = this.status.useProcessForm;
        }

        //兼容之前的 formEditId 和 formId
        if( this.options.formId && !this.options.readFormId )this.options.readFormId = this.options.formId;
        if( this.options.formEditId && !this.options.editFormId )this.options.editFormId = this.options.formEditId;

        if( this.options.readonly === "false" )this.options.readonly = false;
        if( this.options.readonly === "true" )this.options.readonly = true;
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
                    // this.loadDocument();
                    this.loadDocumentV2();
                }.bind(this));
            }else{
                this.mask.loadNode(this.content);
                // this.loadDocument();
                this.loadDocumentV2();
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
        if ( this.desktop.apps && this.desktop.apps["cms.Explorer"]){
            if(this.desktop.apps["cms.Explorer"].content){
                this.desktop.apps["cms.Explorer"].content.unmask();
            }
            if( this.desktop.apps["cms.Explorer"] ){
                this.desktop.apps["cms.Explorer"].refreshAll();
            }
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
    reload: function(){
        this.fireEvent("queryReload");
        this.fireAppEvent("queryReload");
        this.formNode.empty();
        if (this.appForm){
            MWF.release(this.appForm);
            this.appForm = null;
            this.form = null;
            this.$events = {};
        }
        // this.parseDocumentV2(data);
        // this.openDocument();
        this.loadDocumentV2();
    },

    loadDocumentV2 : function( callback ){

        this.loadFormFlag = false;
        this.loadDocumentFlag = false;
        this.loadModuleFlag = false;
        this.checkSaveOnCloseFlag = false;

        this.json_document = null;
        this.json_form = null;

        //只读或者匿名查看
        var readonly = this.options.readonly !== false || this.options.anonymousAccess || this.options.anonymous;

        var formId = "";
        if( this.options.forceFormId ) { //有确定的id
            formId = this.options.forceFormId;
        }else if( this.options.printFormId ){ //有确定的id
            formId = this.options.printFormId;
        }else if( readonly && this.options.readFormId ){ //只读，并且有只读表单id
            formId = this.options.readFormId;
        }

        if( formId ){
            this.useProcessForm = this.options.useProcessForm;
            //编辑状态要先获取document再判断有没有权限编辑
            if( this.options.readonly !== false || this.options.anonymousAccess || this.options.anonymous ){
                this.getFormV2(formId, null, false);
            }
            this.getDocumentV2();
        }else{
            if( readonly ){ //只读情况，不需要判断是否有阅读权限
                this.lookupFormV2( true );
            }
            this.getDocumentV2();
        }

        var cl = "$all";
        MWF.xDesktop.requireApp("cms.Xform", cl, function(){
            this.loadModuleFlag = true;
            this.checkLoad();
        }.bind(this));
    },
    checkLoad : function ( toLoadForm ) {
        if( toLoadForm ){
            if( this.json_document ){
                this.getFormV2( this.formId );
            }else{
                this.needLoadForm = true;
            }
        }

        if( this.needLoadForm && !this.loadFormFlag && this.json_document ){
            this.needLoadForm = false;
            this.getFormV2( this.formId );
        }

        if( this.loadFormFlag && this.loadDocumentFlag && this.loadModuleFlag && this.checkSaveOnCloseFlag ){
            this.parseFormV2( this.json_form.data );
            if (layout.session && layout.session.user){
                this.openDocument();
                if (this.mask) this.mask.hide();
            }else{
                if (layout.sessionPromise){
                    layout.sessionPromise.then(function(){
                        this.openDocument();
                        if (this.mask) this.mask.hide();
                    }.bind(this), function(){});
                }
            }
        }

    },
    getDocumentV2 : function(){
        var id = this.options.documentId || this.options.id;
        var readonly = this.options.readonly !== false;

        var documentMethod;
        if( this.options.anonymousAccess || this.options.anonymous ){
            documentMethod = "getDocumentByAnonymous";
        }else if( readonly && !this.options.printFormId){
            documentMethod = "viewDocument";
        }else{
            documentMethod = "getDocument";
        }

        var attachmentMethod = ( this.options.anonymousAccess || this.options.anonymous ) ? "listAttachmentByAnonymous" : "listAttachment";

        o2.Actions.invokeAsync([
            {"action": this.action, "name": documentMethod},
            {"action": this.action, "name": attachmentMethod },
        ], {"success": function(jsonDocument, jsonAtt){
                if (jsonDocument ){
                    if( jsonAtt && typeOf( jsonAtt.data ) === "array" ){
                        jsonDocument.data.attachmentList = jsonAtt.data ;
                    }else{
                        jsonDocument.data.attachmentList = [];
                    }
                    this.json_document = jsonDocument;
                    this.loadDocumentFlag = true;

                    this.parseDocumentV2(this.json_document.data);

                    if( this.categoryFormWaitingDocument ){
                        this.getFormByCategory();
                    }else{
                        //编辑状态要先获取document再判断有没有权限编辑
                        var toLoadForm = !(this.options.readonly !== false || this.options.anonymousAccess || this.options.anonymous );
                        // var toLoadForm = this.options.readonly !== true && !this.options.anonymousAccess;
                        this.checkLoad( toLoadForm );
                    }
                    this.checkSaveOnClose();
                }else{
                    this.errorLoadingV2();
                }
            }.bind(this), "failure": function(error){
                this.errorLoadingV2( error );
            }.bind(this)}, id, id);
    },
    lookupFormV2 : function ( isReadonly ) {
        var id = this.options.documentId || this.options.id;

        var lookupMethod;
        if( this.options.anonymousAccess || this.options.anonymous ){
            lookupMethod =  layout.mobile ? "lookupFormWithDocMobileAnonymousV2" : "lookupFormWithDocAnonymousV2";
        }else{
            lookupMethod = layout.mobile ? "lookupFormWithDocMobileV2" : "lookupFormWithDocV2";
        }

        this.action[lookupMethod](id, function(json){
            var formId;
            if( json.data.ppFormId ){
                formId = json.data.ppFormId;
                this.useProcessForm = true;
            }else if( isReadonly ){
                formId = json.data.readFormId || json.data.formId;
            }else{
                formId = json.data.formId || json.data.readFormId;
            }
            if (json.data.form){
                this.json_form = json;
                this.loadFormFlag = true;
                this.checkLoad();
            }else{
                var cacheTag = json.data.cacheTag || "";
                this.getFormV2( formId, cacheTag, false )
            }

        }.bind(this), function(){
            this.checkLoad( true );
        }.bind(this));
    },
    getFormV2 : function( formId, cacheTag, ignoreFromCategory ){
        if( this.useProcessForm ){
            this.getProcessForm( formId, cacheTag, ignoreFromCategory );
        }else{
            this.getCMSForm( formId, cacheTag, ignoreFromCategory );
        }
    },
    getCMSForm: function(formId, cacheTag, ignoreFromCategory){
        var formMethod;
        if( this.options.anonymousAccess || this.options.anonymous ){
            formMethod =  layout.mobile ? "getFormMobileAnonymousV2" : "getFormAnonymousV2";
        }else{
            formMethod = layout.mobile ? "getFormMobileV2" : "getFormV2";
        }
        this.action[formMethod](
            formId,

            cacheTag || "",

            function( jsonForm ){
                this.json_form = jsonForm;
                this.loadFormFlag = true;
                this.checkLoad();
            }.bind(this),

            function(error){
                //没有表单，重新获取分类表单
                if( ignoreFromCategory ){
                    this.errorLoadingV2( error , "form" );
                }else if( this.document && this.document.categoryId ){
                    this.getFormByCategory();
                }else{
                    this.categoryFormWaitingDocument = true;
                }
                return true;
            }.bind(this)
        )
    },
    getProcessForm: function(formId, cacheTag, ignoreFromCategory){
        var formMethod;
        // if( this.options.anonymousAccess || this.options.anonymous ){
        //     formMethod =  layout.mobile ? "getFormAnonymousV2Mobile" : "getFormAnonymousV2";
        // }else{
        formMethod = layout.mobile ? "getFormV2Mobile": "getFormV2";
        // }
        MWF.Actions.get("x_processplatform_assemble_surface")[formMethod](
            formId,

            cacheTag || "",

            function( jsonForm ){
                this.json_form = jsonForm;
                this.loadFormFlag = true;
                this.checkLoad();
            }.bind(this),

            function(error){
                //没有表单，重新获取分类表单
                if( ignoreFromCategory ){
                    this.errorLoadingV2( error , "form" );
                }else if( this.document && this.document.categoryId ){
                    this.getFormByCategory();
                }else{
                    this.categoryFormWaitingDocument = true;
                }
            }.bind(this)
        )
    },
    getFormByCategory: function(){
        this.action.getCategory( this.document.categoryId, function(json){
            var d = json.data;
            if( this.readonly === true && d.readFormId && d.readFormId != "" ){
                this.formId  = d.readFormId;
            }else{
                this.formId = d.formId || d.readFormId;
            }
            this.useProcessForm = false;
            this.getFormV2(this.formId, null, true);
        }.bind(this));
    },
    checkSaveOnClose: function(){
        if( this.readonly || this.document.docStatus !== "draft" || typeOf( this.options.saveOnClose ) !== "null" ){
            this.checkSaveOnCloseFlag =  true;
            this.checkLoad();
        }else{
            this.action.getColumn( this.document.appId, function (json) {
                var config = JSON.parse( json.data.config || {} );
                this.options.saveOnClose = typeOf( config.saveDraftOnClose ) === "boolean" ? config.saveDraftOnClose : true;
                this.checkSaveOnCloseFlag =  true;
                this.checkLoad();
            }.bind(this));
        }
    },
    parseFormV2: function( json ){
        if (json.form){
            this.formDataText = (json.form.data) ? MWF.decodeJsonString(json.form.data): "";
            this.form = (this.formDataText) ? JSON.decode(this.formDataText): null;

            this.relatedFormMap = json.relatedFormMap;
            this.relatedScriptMap = json.relatedScriptMap;
            if( json.form.data )delete json.form.data;
            this.formInfor = json.form;
        }else{
            if( layout.mobile ){
                var formDataStr = json.data.mobileData || json.data.data;
                this.formDataText = (formDataStr) ? MWF.decodeJsonString(formDataStr): "";
                this.form = (this.formDataText) ? JSON.decode(this.formDataText): null;
                // if( !this.form ){
                //     this.form = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
                // }
            }else{
                this.formDataText = (json.data.data) ? MWF.decodeJsonString(json.data.data): "";
                this.form = (this.formDataText) ? JSON.decode(this.formDataText): null;
            }
            if( json.data.data )delete json.data.data;
            if( json.data.mobileData )delete json.data.mobileData;
            this.formInfor = json.form;
        }
    },
    parseDocumentV2 : function( data ){
        var title = "";
        title = data.document.title;

        this.setTitle(title);

        data.document.subject = data.document.title;

        this.data =  data.data;
        this.extend = {
            "isCommend" : data.isCommend
        };

        this.attachmentList = data.attachmentList || [];
        this.attachmentList.each(function(att){
            att.lastUpdateTime = att.updateTime;
            att.person = att.creatorUid;
        });

        this.data.isNew = this.isEmptyObject(this.data) ? true : false;

        this.document = data.document;

        var isAdmin = false;

        if( MWF.AC.isCMSManager() || data.isAppAdmin || data.isCategoryAdmin || data.isManager){
            this.options.isControl = true;
            this.isAdmin = true;
        }

        //文档创建人
        if( data.isCreator || this.desktop.session.user.distinguishedName==this.document.creatorPerson ){
            this.options.isControl = true;
        }

        //作者权限
        if( data.isEditor ){
            this.options.isControl = true;
        }

        if( this.options.readonly ){ //强制只读
            this.readonly = true;
        }else{
            if(this.options.isControl && this.document.docStatus != "archived"){ //有编辑权限并且不是归档状态
                this.readonly = false;
            }else{
                this.readonly = true;
            }
        }

        var formId;
        if( this.readonly === true ){
            formId = this.options.forceFormId || this.options.printFormId  || this.options.readFormId;
            if( formId ){
                this.useProcessForm = this.options.useProcessForm;
            }
            if( !formId && this.document.ppFormId ){
                formId = this.document.ppFormId;
                this.useProcessForm = true;
            }
            if( !formId && this.document.readFormId ){
                formId = this.document.readFormId;
                this.useProcessForm = false;
            }
            if( !formId && this.options.editFormId ){
                formId = this.options.editFormId;
                this.useProcessForm = this.options.useProcessForm;
            }
            if( !formId && this.document.form ){
                formId = this.document.form;
                this.useProcessForm = false;
            }
        }else{
            formId = this.options.forceFormId || this.options.printFormId || this.options.editFormId;
            if( formId ){
                this.useProcessForm = this.options.useProcessForm;
            }
            if( !formId && this.document.ppFormId ){
                formId = this.document.ppFormId;
                this.useProcessForm = true;
            }
            if( !formId && this.document.form ){
                formId = this.document.form;
                this.useProcessForm = false;
            }
            if( !formId && this.options.readFormId ){
                formId = this.options.readFormId;
                this.useProcessForm = this.options.useProcessForm;
            }
            if( !formId && this.document.readFormId ){
                formId = this.document.readFormId;
                this.useProcessForm = false;
            }
        }
        this.formId = formId;

        if(this.readonly || this.document.docStatus == "published"){
            this.options.autoSave = false;
            this.options.saveOnClose = false;
        }

        var isControl = this.options.isControl;
        this.control = data.control ||  {
            "allowRead": true,
            "allowPublishDocument": isControl && ["draft","waitPublish"].contains( this.document.docStatus ),
            "allowPublishDocumentDelayed": isControl && ["draft","waitPublish"].contains( this.document.docStatus ),
            "allowSave": isControl && this.document.docStatus == "published",
            "allowPopularDocument": MWF.AC.isHotPictureManager() && this.document.docStatus == "published",
            "allowEditDocument":  isControl && !this.document.wf_workId,
            "allowDeleteDocument":  isControl && !this.document.wf_workId,
            "allowSetTop": this.isAdmin && this.document.docStatus == "published" && !this.document.isTop,
            "allowCancelTop": this.isAdmin && this.document.docStatus == "published" && this.document.isTop
        };
    },
    errorLoadingV2 : function( error, type ){
        var text;
        if( type === "form" ){
            text = this.lp.formGettedError;
        }else{
            text = this.lp.documentGettedError;
        }
        if( error )text = text + ":" + error.responseText;
        this.notice( text , "error");
        layout.sessionPromise.then(function(){
            if (this.mask) this.mask.hide();
            this.close();
        }.bind(this), function(){});
    },

    isEmptyObject: function( obj ) {
        var name;
        for ( name in obj ) {
            return false;
        }
        return true;
    },

    setPopularDocument: function(){
        MWF.xDesktop.requireApp("cms.Document", "HotLinkForm", null, false);
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
            // MWF.xDesktop.requireApp("cms.Xform", "Form", function(){
            MWF.xDesktop.requireApp("cms.Xform", "$all", function(){
                this.appForm = new MWF.CMSForm(this.formNode, this.form, {
                    "readonly": this.readonly,
                    "autoSave" : this.options.autoSave,
                    "saveOnClose" : this.options.saveOnClose,
                    "onPostPublish" : this.options.postPublish,
                    "onAfterPublish" : this.options.afterPublish,
                    "onAfterSave" : this.options.afterSave,
                    "onBeforeClose" : this.options.beforeClose,
                    "onPostDelete" : this.options.postDelete,
                    "useProcessForm": this.useProcessForm
                });

                this.appForm.businessData = {
                    "data": this.data,
                    "originalData" : Object.clone( this.data ),
                    "document": this.document,
                    "extend" : this.extend,
                    "work": this.data.$work || {}, //兼用流程发布到内容管理
                    "control": this.control,
                    "attachmentList": this.attachmentList,
                    "formInfor": this.formInfor,
                    "status": {
                        //"readonly": (this.options.readonly) ? true : false
                        "readonly": this.readonly
                    }
                };
                if( this.useProcessForm &&  this.data.$work ){
                    this.appForm.businessData.work = {
                        id : this.data.$work.workId
                    }
                }
                this.appForm.formDataText = this.formDataText;
                this.appForm.documentAction = this.action;
                this.appForm.app = this;

                if( this.$events && this.$events.queryLoadForm ){
                    this.appForm.addEvent( "queryLoad", function () {
                        this.fireEvent("queryLoadForm", [this]);
                    }.bind(this));
                }

                this.appForm.load(function(){
                    if (window.o2android && window.o2android.postMessage) {
                        layout.appForm = this.appForm;
                    } else if (window.o2android && window.o2android.cmsFormLoaded){
                        layout.appForm = this.appForm;
                    } else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.cmsFormLoaded){
                        layout.appForm = this.appForm;
                    }

                    this.fireEvent("postLoadForm", [this]);
                }.bind(this));
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
        if( this.options.readFormId )status.readFormId = this.options.readFormId;
        if( this.options.editFormId )status.editFormId = this.options.editFormId;
        if( this.options.printFormId )status.printFormId = this.options.printFormId;
        if( this.options.forceFormId )status.forceFormId = this.options.forceFormId;
        if(this.options.appId && this.options.appId!="")status.appId = this.options.appId;
        if( this.options.useProcessForm )status.useProcessForm = true;
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
