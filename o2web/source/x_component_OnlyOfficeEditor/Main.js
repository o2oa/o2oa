
MWF.xApplication.OnlyOfficeEditor = MWF.xApplication.OnlyOfficeEditor || {};


MWF.xApplication.OnlyOfficeEditor.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "OnlyOfficeEditor",
        "mvcStyle": "style.css",
        "mode" : "view",
        "jars" : "",
        "title": ""
    },
    onQueryLoad: function () {

        this.lp = MWF.xApplication.OnlyOfficeEditor.LP;
        this.documentId = this.options.documentId;
        this.mode = this.options.mode;
        this.version = this.options.version;
        this.jars = this.options.jars;
        this.fileUrl = this.options.url;
        this.fileName = decodeURIComponent(this.options.fileName);

        this.action = MWF.Actions.load("x_onlyofficefile_assemble_control");

        if(this.status){
            this.mode = this.status.mode;
            this.documentId = this.status.documentId;
            this.version = this.status.version;
            this.jars = this.status.jars;
        }
        //if(this.jars === "") this.jars = "template";

    },
    onQueryClose : function (){

    },
    loadApplication: function (callback) {

        this.loadDocument();
    },
    loadDocument: function () {
        this.getEditor(function () {
            this.setTitle(this.document.fileName);
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
        debugger


        if(this.fileUrl){
            this.action.OnlyofficeAction.url({
                "fileName" : this.fileName,
                "url" : decodeURIComponent(this.fileUrl)
            }, function( json ){
                this.document = json.data;
                this.document.editor = this.document.fileModel;
                if (callback) callback();
            }.bind(this),null,false);
        }else if(this.jars === "officeOnline"){

            o2.Actions.load("x_program_center").InvokeAction.execute("officeOnlineSrv",{
                "fun" : "getEditor",
                "data" : {
                    "documentId": this.documentId,
                    "version" : this.version,
                    "mode" : this.mode
                }
            },function(json) {

                this.document = json.data;

                if (callback) callback();
            }.bind(this));

        }else if(this.jars === "" || this.jars === undefined){

            this.action.OnlyofficeAction.getEdit(this.documentId,this.mode, function( json ){
                this.document = json.data;
                this.document.editor = this.document.fileModel;
                if (callback) callback();
            }.bind(this),null,false);

        }else {
            this.action.OnlyofficeAction.appFileEdit({
                "appToken" : this.jars,
                "mode" : this.mode,
                "fileId" : this.documentId
            }, function( json ){
                this.document = json.data;
                this.document.editor = this.document.fileModel;
                if (callback) callback();
            }.bind(this),null,false);
        }

    },
    loadEditor : function (){

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

                var obj = {
                    currentVersion: JSON.parse(this.document.FileHistory[0]).currentVersion,
                    history: historyObj
                }
                console.log(obj)
                docEditor.refreshHistory(obj);
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

            console.log(historyData[version - 1]);

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

        if(this.document.FileHistory){
            if (this.document.FileHistory[0] !== "") {
                this.document.editor.events.onRequestHistory = onRequestHistory;
                this.document.editor.events.onRequestHistoryData = onRequestHistoryData;
                this.document.editor.events.onRequestHistoryClose = onRequestHistoryClose;
            }
        }

        //this.document.editor.editorConfig.mode = this.mode;
        if(layout.mobile){
            this.document.editor.type = "mobile";

            this.document.editor.editorConfig.mobile = {
                forceView: false,
                info: false,
                standardView: true,
            }


        }

        this.wrapNode = new Element("div",{"style":"height:100%;"}).inject(this.content)


        if(this.document.editor.documentType === "cell" && layout.mobile && layout.config.isOnlyofficeEdit && this.mode==="edit"){
            var customNode = new Element("div",{"style":"padding:10px;"}).inject(this.wrapNode);
            var textarea = new Element("oo-textarea",{"auto-size":"true","style":"width:100%"}).inject(customNode);
            var lable = new Element("div",{"style":"width:100%;padding:5px 0;","text":"选择需要编辑的单元格，然后点击编辑单元格按钮"}).inject(customNode);
            var editBtn = new Element("oo-button",{"text":"编辑单元格","style":"margin-right:10px;"}).inject(customNode);
            var saveBtn = new Element("oo-button",{"text":"保存单元格","style":"margin-right:10px;"}).inject(customNode);
            var closeBtn = new Element("oo-button",{"text":"关闭外部编辑"}).inject(customNode);


            customNode.hide();
            var startWrapNode = new Element("div").inject(this.wrapNode);
            var startBtn = new Element("oo-button",{"text":"启用外部编辑","style":"padding:10px;"}).inject(startWrapNode);
            startBtn.addEvent("click",function (){
                customNode.show();
                startBtn.hide();
            })
            closeBtn.addEvent("click",function (){
                customNode.hide();
                startBtn.show();
            })


            editBtn.addEvent("click",function (){

                var connector = this.onlyOffice.createConnector();
                connector.callCommand(function () {
                    var oWorksheet = Api.GetActiveSheet();
                    var oRange = oWorksheet.GetSelection();
                    var address = oRange.GetAddress();
                    console.log("选中单元格的地址：", address);
                    var json = {
                        "cell" : address,
                        "value" : oRange.GetValue()
                    }
                    return json;
                }, function (json) {
                    textarea.set("value",json.value);
                    lable.set("text","当前选中的单元格：" + json.cell)


                    Asc.scope.cell = json.cell;
                    connector.disconnect();
                });
            }.bind(this));
            saveBtn.addEvent("click",function (){


                var connector = this.onlyOffice.createConnector();
                Asc.scope.name = textarea.get("value");

                connector.callCommand(function () {
                    // 获取当前工作表
                    var worksheet = Api.GetActiveSheet();
                    console.log(Asc.scope.cell)
                    console.log(Asc.scope.name)
                    // 获取 A1 单元格范围
                    var range = worksheet.GetRange(Asc.scope.cell);

                    // 设置 A1 单元格的值
                    range.SetValue(Asc.scope.name);

                }, function () {
                    console.log("callback command");
                    // 可选：插入完成后执行其他逻辑
                    connector.disconnect();
                });

                customNode.hide();
                startBtn.show();
            }.bind(this));
        }
        this.document.editor.editorConfig.customization.uiTheme = "theme-white";
        this.officeNode = new Element("div#_" + this.documentId).inject(this.wrapNode);
        console.log(this.document.editor)
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
    recordStatus: function(){
        var status ={
            "documentId": this.documentId,
            "mode": this.mode,
            "jars" : this.jars
        };
        return status;
    },
});
