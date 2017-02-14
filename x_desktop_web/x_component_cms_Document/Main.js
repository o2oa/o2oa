MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Document = MWF.xApplication.cms.Document || {};
MWF.xApplication.cms.Document.options.multitask = true;
MWF.xApplication.cms.Document.Main = new Class({
	Extends: MWF.xApplication.Common.Main,
	Implements: [Options, Events],

	options: {
		"style": "default",
		"name": "cms.Document",
		"icon": "icon.png",
		"width": "1200",
		"height": "800",
		"title": MWF.xApplication.cms.Document.LP.title,
        "documentId": "",
        "isControl": false,
        "readonly": false,
        "autoSave" : false,
        "saveOnClose" : false,
        "postPublish" : null
	},
	onQueryLoad: function(){
		this.lp = MWF.xApplication.cms.Document.LP;
        if (this.status){
            this.options.documentId = this.status.documentId;
            this.options.readonly = (this.status.readonly=="true" || this.status.readonly==true) ? true : false;
            this.options.autoSave = (this.status.autoSave=="true" || this.status.autoSave==true) ? true : false;
            this.options.saveOnClose = (this.status.saveOnClose=="true" || this.status.saveOnClose==true) ? true : false;
        }
        if( this.options.documentId && this.options.documentId!=""){
            this.options.appId = "cms.Document"+this.options.documentId;
        }
	},
	loadApplication: function(callback){

        this.node = new Element("div", {"styles": this.css.content}).inject(this.content)

        MWF.require("MWF.widget.Mask", function(){
            this.mask = new MWF.widget.Mask({"style": "desktop"});

            this.formNode = new Element("div", {"styles": {"min-height": "100%"}}).inject(this.node);
            MWF.xDesktop.requireApp("cms.Document", "Actions.RestActions", function(){
                this.action = new MWF.xApplication.cms.Document.Actions.RestActions();
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
            }.bind(this));

        }.bind(this));

        this.addEvent("queryClose", function(){
            this.refreshTaskCenter();
        }.bind(this))

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
    loadDocument: function(){
        var id = this.options.documentId;
        this.action.getDocument(id, function(json){
            //if (this.mask) this.mask.hide();
            //this.openDocument();
            this.loadController( json.data.document.appId, function(){
                json.data = json.data || [];
                this.parseData(json.data);
                this.action.getCategory( json.data.document.catagoryId, function( js ){
                    this.categoryData = js.data;
                    var formId = this.categoryData.formId || this.categoryData.readFormId;
                    if( this.readonly == true && this.categoryData.readFormId && this.categoryData.readFormId != "" ){
                        formId = this.categoryData.readFormId
                    }
                    if( !formId || formId=="" ){
                        this.notice(  json.data.document.categoryName + this.lp.formNotSetted , "error");
                    }else{
                        this.loadForm( formId );
                    }
                }.bind(this))
            }.bind(this))
        }.bind(this), function( error ){
            //debugger;
            this.notice(  this.lp.documentGettedError + ":" + error.responseText , "error");
            this.close();
        }.bind(this));
    },
    loadController: function(appId, callback){
        this.controllers =[];
        this.action.listColumnController(appId, function( json ){
            json.data = json.data || [];
            json.data.each(function(item){
                this.controllers.push(item.adminUid);
            }.bind(this))
            if(callback)callback(json);
        }.bind(this), function(error){
            this.notice(  this.lp.controllerGettedError + ":" + error.responseText , "error");
            this.close();
        }.bind(this));
    },
    errorDocument: function(){
        if (this.mask) this.mask.hide();
        this.node.set("text", "openError");
    },
    loadForm : function( formId ){
        this.action.getForm(formId, function( json ){
            //if (this.mask) this.mask.hide();
            this.form = (json.data.data) ? JSON.decode(MWF.decodeJsonString(json.data.data)): null;
            this.listAttachment();
        }.bind(this), function(error){
            this.notice(  this.lp.formGettedError + ":" + error.responseText , "error");
            this.close();
        }.bind(this));
    },
    listAttachment: function(){
        if( this.document.attachmentList && this.document.attachmentList.length > 0 ){
            this.action.listAttachment(this.options.documentId, function( json ){
                if (this.mask) this.mask.hide();
                this.attachmentList = json.data;
                this.attachmentList.each(function(att){
                    att.lastUpdateTime = att.updateTime;
                    att.person = att.creatorUid;
                })
                this.openDocument();
            }.bind(this), function(error){
                this.notice(  this.lp.attachmentGettedError  + ":" + error.responseText, "error");
                this.close();
            }.bind(this));
        }else{
            if (this.mask) this.mask.hide();
            this.attachmentList = [];
            this.openDocument();
        }
    },
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

        if( this.isEmptyObject(this.data) ){
            this.data.isNew = true;
        }else{
            this.data.isNew = false;
        }

        this.document = data.document;
        var isAdmin = false;

        //系统管理员
        if( MWF.AC.isAdministrator() ){
            this.options.isControl = true;
            isAdmin = true;
        }
        //栏目管理员
        if(this.controllers && this.controllers.contains(this.desktop.session.user.name) ){
            this.options.isControl = true;
            isAdmin = true;
        }
        //文档创建人
        if( this.desktop.session.user.name==this.document.creatorPerson ){
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

        if(!this.readonly){
            this.options.autoSave = true;
            this.options.saveOnClose = true;
        }

        //this.attachmentList = data.attachmentList;

        //this.inheritedAttachmentList = data.inheritedAttachmentList;
        var isControl = this.options.isControl;
        this.control = data.control ||  {
                "allowRead": true,
                "allowPublishDocument": isControl && this.document.docStatus == "draft",
                "allowArchiveDocument" : isControl && this.document.docStatus == "published",
                "allowRedraftDocument" : isControl && this.document.docStatus == "published",
                "allowSave": isControl && this.document.docStatus == "published",
                "allowPopularDocument": isAdmin && this.document.docStatus == "published",
                "allowEditDocument":  isControl,
                "allowDeleteDocument":  isControl
            };
       // this.form = (data.form) ? JSON.decode(MWF.decodeJsonString(data.form.data)): null;
    },
    setPopularDocument: function(){
        MWF.xDesktop.requireApp("cms.Document", "HotLinkForm", null, false);

        var form = new MWF.xApplication.cms.Document.HotLinkForm(this, this.document, {
            documentId : this.options.documentId,
            onPostOk : function( id ){

            }.bind(this)
        },{
            app : this, lp : this.lp, css : this.css, actions : this.action
        })
        form.create()
    },
    openDocument: function(){
        if (this.form){
            MWF.xDesktop.requireApp("cms.Xform", "Form", function(){
                this.appForm = new MWF.CMSForm(this.formNode, this.form, {
                    "readonly": this.readonly,
                    "autoSave" : this.options.autoSave,
                    "saveOnClose" : this.options.saveOnClose,
                    "onPostPublish" : this.options.postPublish
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