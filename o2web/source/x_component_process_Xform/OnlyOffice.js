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
    getFileName : function (){
        if (this.json.fileNameType === "value"){
            return this.json.fileName;
        }else if(this.json.fileNameType === "script"){
            return this.form.Macro.exec(this.json.fileNameScript.code, this);
        }else{
            return MWF.xApplication.process.Xform.LP.onlyoffice.filetext;
        }
    },
    createDocument : function (callback){
        var data = {
            "fileName" : this.getFileName() + "." + this.json.officeType,
            "fileType" : this.json.officeType,
            "appToken" : "x_processplatform_assemble_surface",
            "workId" : this.form.businessData.work.id,
            "site" : this.json.fileSite?this.json.fileSite:"filetext"
        };
        this.action.OnlyofficeAction.createForO2(data,
            function( json ){
                this.documentId = json.data.fileId;
                this.setData();
                if (callback) callback();
            }.bind(this),null, false
        );
    },
    createDocumentByTemplate : function (callback){

        this.action.OnlyofficeAction.get(this.json.template).then(function(json) {
            var data = {
                "fileName": this.getFileName() + "." + json.data.fileModel.document.fileType,
                "fileType": json.data.fileModel.document.fileType,
                "appToken" : "x_processplatform_assemble_surface",
                "workId" : this.form.businessData.work.id,
                "site" : this.json.fileSite?this.json.fileSite:"filetext",
                "tempId": this.json.template
            };

            this.action.OnlyofficeAction.createForO2(data,
                function( json ){
                    this.documentId = json.data.fileId;
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
            o2.require("o2.widget.Upload", null, false);
            var upload = new o2.widget.Upload(this.content, {
                "action": o2.Actions.get("x_processplatform_assemble_surface").action,
                "method": "replaceAttachment",
                "accept" : ".docx,.xlsx,.pptx",
                "parameter": {
                    "id" : this.documentId,
                    "workid" : this.form.businessData.work.id,
                },
                "data":{
                },
                "onCompleted": function(data){

                    this.documentId = data.id;

                    this.reload();
                }.bind(this)
            });

            upload.load();
        }.bind(this));

    },
    reload : function (){
        this.setData();
        this.node.empty();
        if(this.mode !== "read" && this.json.allowUpload){
            this.createUpload();
        }
        this.loadDocument();
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

        if(this.data.appToken){

            this.action.OnlyofficeAction.appFileEdit({
                "appToken" : this.data.appToken,
                "mode" : this.mode,
                "fileId" : this.documentId
            }, function( json ){
                this.document = json.data;
                this.document.editor = this.document.fileModel;
                if (callback) callback();
            }.bind(this),null,false);


        }else{
            this.action.OnlyofficeAction.get(this.documentId, function( json ){
                this.document = json.data;
                this.document.editor = this.document.fileModel;
                if (callback) callback();
            }.bind(this),null,false);
        }

    },
    loadEditor: function () {

        this.fireEvent("beforeOpen");


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
            //document.location.reload();
            _self.reload();
        };
        var onError = function (event) {
            if (event) innerAlert(event.data);
        };
        var onOutdatedVersion = function (event) {
            location.reload(true);
        };
        var onDocumentReady= function() {
            console.log("Document is loaded");
        }.bind(this);
        var onPluginsReady= function() {
            console.log("Plugins is loaded");
            this.fireEvent("afterOpen");
        }.bind(this);

        this.document.editor.events = {
            "onAppReady": onAppReady,
            "onDocumentReady":onDocumentReady,
            "onPluginsReady":onPluginsReady,
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
            "zoom": this.json.zoom,
            "review" : {
                "trackChanges" : this.json.trackChanges,
                "reviewDisplay" : this.json.reviewDisplay,
                "showReviewChanges" : this.json.showReviewChanges
            }
        }

        if (window.CXO_API && window.CXO_API.CXEditor) {
            docEditor = new window.CXO_API.CXEditor("_" + this.documentId, this.document.editor);
        } else if (window.DocsAPI && window.DocsAPI.DocEditor) {
            docEditor = new window.DocsAPI.DocEditor("_" + this.documentId, this.document.editor);
        } else {
            console.error("Editor API not found");
            throw new Error("Editor API not found");
        }
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
        };
        var site = this.json.fileSite?this.json.fileSite:"filetext";
        if(this.form.businessData.data[this.json.id] && this.form.businessData.data[this.json.id].documentId){
            data = this.form.businessData.data[this.json.id];
        }else {

            //判断对应的site里有没有值
            var attachmentList = this.form.businessData.attachmentList;
            attachmentList = attachmentList.filter(function(att) {
                return att.site === site;
            });
            if(attachmentList.length>0){
                data = {
                    "documentId": attachmentList[0].id,
                    "appToken": "x_processplatform_assemble_surface"
                };
            }

        }

        return data;
    },
    setData: function() {
        var data = {
            "documentId": this.documentId,
            "appToken": "x_processplatform_assemble_surface"
        }
        this.data = data;
        this._setBusinessData(data);

        var jsonData = {}
        jsonData[this.json.id] = data;

        o2.Actions.load("x_processplatform_assemble_surface").DataAction.updateWithJob(this.form.businessData.work.job, jsonData, function (json) {
            data = json.data;
        })
    },
    setBookmark : function (name , value){

        var connector = this.onlyOffice.createConnector();
        Asc.scope.name = name;
        Asc.scope.text = value;
        connector.callCommand(function() {
            var oDocument = Api.GetDocument();
            var oRange = oDocument.GetBookmarkRange(Asc.scope.name);
            var oRangeParagraph = oRange.GetParagraph(0);
            var aSearch = oRangeParagraph.Search(oRange.GetText());
            try {
                oRange.AddText(Asc.scope.text, 'after');
                aSearch[0].Delete();
            } catch (err) {
                oRange.AddText(Asc.scope.text, 'before');
                aSearch = oRangeParagraph.Search(oRange.GetText());
                aSearch[0].AddBookmark(Asc.scope.name);
            }
        }, function() { console.log("callback command"); });
        connector.disconnect();
    },
    save : function (){
        var connector = this.onlyOffice.createConnector();
        connector.callCommand(function() {
            Api.Save();
        }, function() { console.log("callback command"); });
        connector.disconnect();
    },
    startRevisions : function (){
        //开启修订
        var connector = this.onlyOffice.createConnector();
        connector.callCommand(function() {
            var oDocument = Api.GetDocument();
            oDocument.SetTrackRevisions(true);
        }, function() { console.log("callback command"); });
        connector.disconnect();
    },
    stopRevisions : function (){
        //关闭修订
        var connector = this.onlyOffice.createConnector();
        connector.callCommand(function() {
            var oDocument = Api.GetDocument();
            oDocument.SetTrackRevisions(false);
        }, function() { console.log("callback command"); });
        connector.disconnect();
    },
    acceptAllRevisions : function (){
        var connector = this.onlyOffice.createConnector();
        connector.callCommand(function() {
            var oDocument = Api.GetDocument();
            oDocument.AcceptAllRevisionChanges();
        }, function() { console.log("callback command"); });
        connector.disconnect();

    },
    rejectAllRevisions : function (){
        var connector = this.onlyOffice.createConnector();
        connector.callCommand(function() {
            var oDocument = Api.GetDocument();
            oDocument.RejectAllRevisionChanges();
        }, function() { console.log("callback command"); });
        connector.disconnect();
    },
});
