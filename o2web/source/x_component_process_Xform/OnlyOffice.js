MWF.xAction.RestActions.Action["x_onlyofficefile_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action
});
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.OnlyOffice = MWF.APPOnlyOffice =  new Class({
    Extends: MWF.APP$Module,
    isActive: false,
    options:{
        "moduleEvents": ["queryLoad","beforeOpen",
            "afterOpen",
            "afterSave"
        ]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.documentId = "";
        this.mode = "edit";
    },
    _loadUserInterface: function(){
        this.node.empty();
        this.node.setStyles({
            "min-height": "100px"
        });
    },
    _afterLoaded: function(){
        this.fireEvent("queryLoad");
        if(!layout.serviceAddressList["x_onlyofficefile_assemble_control"]){
            this.node.set("html","<h3><font color=red>"+MWF.xApplication.process.Xform.LP.onlyoffice.noInstall+"</font></h3>");
            return false;
        }


        if(this.mode !== "read" && this.json.allowUpload){
            this.createUpload();
        }


        this.action = o2.Actions.load("x_onlyofficefile_assemble_control");
        if (!this.json.isNotLoadNow){
            this.data = this.getData();
            if(this.data.documentId === ""){

                if (this.json.officeType === "other" && this.json.templateType === "script"){
                    this.json.template = this.form.Macro.exec(this.json.templeteScript.code, this);
                }

                this[this.json.officeType === "other"&&this.json.template !== ""? "createDocumentByTemplate":"createDocument"](function (){
                    this.loadDocument();
                }.bind(this));
            }else {
                this.documentId = this.data.documentId;
                this.loadDocument();
            }
        }
    },
    createDocument : function (callback){
        var data = {
            "fileName" : MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + this.json.officeType,
            "fileType" : this.json.officeType,
            "relevanceId" : this.form.businessData.work.job
        }
        this.action.OnlyofficeAction.create(data,
            function( json ){
                this.documentId = json.data.id;
                this.setData();
                if (callback) callback();
            }.bind(this),null, false
        );
    },
    createDocumentByTemplate : function (callback){

        this.action.OnlyofficeAction.get(this.json.template).then(function(json) {
            var data = {
                "fileName": MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + json.data.fileModel.document.fileType,
                "fileType": json.data.fileModel.document.fileType,
                "relevanceId": this.form.businessData.work.job,
                "sampleName": this.json.template
            }

            this.action.OnlyofficeAction.create(data,
                function( json ){
                    this.documentId = json.data.id;
                    this.setData();
                    if (callback) callback();
                }.bind(this),null, false
            );

        }.bind(this))
    },
    createUpload : function (){


        this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);

        var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.ofdview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
        uploadBtn.addEvent("click",function (){

            o2.Actions.get("x_onlyofficefile_assemble_control").action.actions = {};
            o2.Actions.get("x_onlyofficefile_assemble_control").action.actions.upload = {
                "enctype": "formData",
                "method": "POST",
                "uri": "/jaxrs/onlyoffice/upload"
            };
            o2.require("o2.widget.Upload", null, false);
            var upload = new o2.widget.Upload(this.content, {
                "action": o2.Actions.get("x_onlyofficefile_assemble_control").action,
                "method": "upload",
                "accept" : ".docx,.xlsx,.pptx",
                "parameter": {
                },
                "data":{
                    "relevanceId" : this.form.businessData.work.job
                },
                "onCompleted": function(data){

                    this.documentId = data.id;
                    this.setData();
                    this.node.empty();
                    this.createUpload();
                    this.loadDocument();

                }.bind(this)
            });

            upload.load();
        }.bind(this));

    },
    loadDocument: function () {
        this.getEditor(function () {
            this.loadApi(function (){
                this.loadEditor();
            }.bind(this));
        }.bind(this));
    },
    loadApi : function (callback){
        this.action.OnlyofficeConfigAction.getConfig(function( json ){
            var data = json.data;
            var docserviceApi = data.docserviceApi;
            o2.load(docserviceApi, function () {
                if (callback) callback();
            }.bind(this));
        }.bind(this),null, false);
    },
    getEditor: function (callback) {
        this.action.OnlyofficeAction.get(this.documentId, function( json ){
            this.document = json.data;
            this.document.editor = this.document.fileModel;
            if (callback) callback();
        }.bind(this),null,false);
    },
    loadEditor: function () {

        this.fireEvent("beforeOpen");

        if (this.isReadonly()){
            this.mode  = "view";
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.mode = "view";
                }
            }
        }
        var docEditor;
        var _self = this;
        var innerAlert = function (message) {
            if (console && console.log)
                console.log(message);
        };
        var onAppReady = function () {
            innerAlert("Document editor ready");

        };
        var onDocumentStateChange = function (event) {

            var title = document.title.replace(/\*$/g, "");
            document.title = title + (event.data ? "*" : "");
            if(event.data){
                _self.fireEvent("afterSave");
            }
        };
        var onRequestEditRights = function () {
            location.href = location.href.replace(RegExp("mode=view\&?", "i"), "");
        };
        var onRequestHistory = function (event) {
            if (this.document.FileHistory[0] === "") {
                docEditor.refreshHistory({
                    currentVersion: null,
                    history: null
                });
            } else {
                var historyArr = JSON.parse(this.document.FileHistory[0]).history;
                var newHistoryArr = [];
                for (var i = 0; i < historyArr.length; i++) {
                    if (historyArr[i].version > 0) {
                        newHistoryArr.push(historyArr[i]);
                    }
                }
                newHistoryArr.sort(function (a, b) {
                    return a.version - b.version;
                });
                var historyObj = newHistoryArr || null;
                docEditor.refreshHistory({
                    currentVersion: JSON.parse(this.document.FileHistory[0]).currentVersion,
                    history: historyObj
                });
            }
        }.bind(this);
        var onRequestHistoryData = function (data) {
            var historyArr = [];
            var history = JSON.parse(this.document.FileHistory[1]);
            for (var key in history) {
                if (key !== "0") {
                    historyArr.push(history[key]);
                }
            }
            var version = data.data;
            var historyData = historyArr || null;
            docEditor.setHistoryData(historyData[version - 1]);
        }.bind(this);
        var onRequestHistoryClose = function (event) {
            document.location.reload();
        };
        var onError = function (event) {
            if (event) innerAlert(event.data);
        };
        var onOutdatedVersion = function (event) {
            location.reload(true);
        };
        var onDocumentReady= function() {
            console.log("Document is loaded");
            this.fireEvent("afterOpen");
        }.bind(this);

        this.document.editor.events = {
            "onAppReady": onAppReady,
            "onDocumentReady":onDocumentReady,
            "onDocumentStateChange": onDocumentStateChange,
            'onRequestEditRights': onRequestEditRights,
            "onError": onError,
            "onOutdatedVersion": onOutdatedVersion,
        }
        if (this.document.FileHistory[0] !== "") {
            this.document.editor.events.onRequestHistory = onRequestHistory;
            this.document.editor.events.onRequestHistoryData = onRequestHistoryData;
            this.document.editor.events.onRequestHistoryClose = onRequestHistoryClose;
        }
        if(layout.mobile){
            this.document.editor.type = "mobile";
        }

        this.officeNode = new Element("div#_" + this.documentId, {
            "styles": this.form.css.officeAreaNode
        }).inject(this.node);

        if (this.node.getSize().y<800) this.node.setStyle("height", "800px");
        debugger
        this.document.editor.editorConfig.mode = this.mode;

        var lang = layout.session.user.language;
        if(lang != "en"){
            lang = "zh";
        }
        this.document.editor.editorConfig.lang = "zh";
        this.document.editor.editorConfig.location = "zh-CN";
        this.document.editor.editorConfig.region = "zh-CN";


        this.document.editor.editorConfig.customization = {
            "chat": this.json.chat,
            "commentAuthorOnly": false,
            "comments": this.json.comments,
            "autosave" : this.json.autosave,
            "compactHeader": this.json.compactHeader,
            "compactToolbar": this.json.compactToolbar,
            "compatibleFeatures": this.json.compatibleFeatures,
            "customer": {
                "address": this.json.address,
                "info": this.json.info,
                "logo": this.json.logo,
                "mail": this.json.mail,
                "name": this.json.companyName,
                "www": this.json.www
            },
            "feedback": {
                "url": this.json.feedback,
                "visible": this.json.feedbackUrl
            },
            "forcesave": this.json.forcesave,
            "help": this.json.help,
            "hideRightMenu": this.json.hideRightMenu,
            "logo": {
                "image": this.json.logoImg,
                "url": this.json.logoUrl
            },
            "macros": true,
            "macrosMode": "warn",
            "mentionShare": true,
            "plugins": this.json.plugins,
            "reviewDisplay": this.json.reviewDisplay,
            "showReviewChanges": this.json.showReviewChanges,
            "spellcheck": this.json.spellcheck,
            "toolbarHideFileName": this.json.toolbarHideFileName,
            "toolbarNoTabs": this.json.toolbarNoTabs,
            "trackChanges": this.json.trackChanges,
            "unit": this.json.unit,
            "zoom": this.json.zoom
        }
        docEditor = new DocsAPI.DocEditor("_" + this.documentId, this.document.editor);
        this.onlyOffice = docEditor;
    },
    hide: function(){
        this.node.hide();
    },
    show: function(){
        this.node.show();
    },
    isEmpty : function(){
        var data = this.getData();
        if(data.documentId === ""){
            return true;
        }else {
            return false;
        }
    },
    getData: function(){
        var data = {
            "documentId" : ""
        }
        if(this.form.businessData.data[this.json.id]){
            data.documentId = this.form.businessData.data[this.json.id].documentId;
        }
        return data;
    },
    setData: function(){
        var data = {
            "documentId" : this.documentId
        }
        this._setBusinessData(data);
    }
});
