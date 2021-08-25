MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.OnlyOffice = MWF.APPOnlyOffice =  new Class({
    Extends: MWF.APP$Module,
    isActive: false,
    options:{
        "moduleEvents": ["beforeOpen",
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
        if(!layout.serviceAddressList["x_onlyofficefile_assemble_control"]){
            this.node.set("html","<h3><font color=red>请先安装onlyoffice应用</font></h3>");
            return false;
        }
        this.action = o2.Actions.load("x_onlyofficefile_assemble_control");
        if (!this.json.isNotLoadNow){
            this.data = this.getData();
            if(this.data.documentId === ""){
                this.createDocument(function (){
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
            "fileName" : "文件正文." + this.json.officeType,
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

        if (this.json.isReadonly){
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
        this.officeNode = new Element("div#officeNode", {
            "styles": this.form.css.officeAreaNode
        }).inject(this.node);
        if (this.node.getSize().y<800) this.node.setStyle("height", "800px");
        debugger
        this.document.editor.editorConfig.mode = this.mode;

        var lang = layout.session.user.language;
        if(lang != "en"){
            lang = "zh";
        }
        this.document.editor.editorConfig.lang = lang,
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
        docEditor = new DocsAPI.DocEditor("officeNode", this.document.editor);
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
