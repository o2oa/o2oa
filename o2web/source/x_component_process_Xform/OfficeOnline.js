
MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.OfficeOnline = MWF.APPOfficeOnline =  new Class({
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
        this.mode = "write";
        this.appToken = "x_processplatform_assemble_surface";
        this.workId = this.form.businessData.work.id;
    },
    _loadUserInterface: function(){
        this.node.empty();
        this.node.setStyles({
            "min-height": "800px"
        });
    },
    _afterLoaded: function(){
        this.fireEvent("queryLoad");
        if(!layout.serviceAddressList["x_officeonline_assemble_control"]){
            this.node.set("html","<h3><font color=red>"+MWF.xApplication.process.Xform.LP.officeonline.noInstall+"</font></h3>");
            return false;
        }

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

        if(this.mode !== "read" && this.json.allowUpload){
            this.createUpload();
        }

        this.action = o2.Actions.load("x_officeonline_assemble_control");
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
            "fileName" : MWF.xApplication.process.Xform.LP.officeonline.filetext + "." + this.json.officeType,
            "fileType" : this.json.officeType,
            "appToken" : this.appToken,
            "workId" : this.workId,
            "site" : "filetext"
        };
        this.action.OnlineAction.createForO2(data,
            function( json ){
                this.documentId = json.data.fileId;
                this.setData();
                if (callback) callback();
            }.bind(this),null, false
        );
    },
    createDocumentByTemplate : function (callback){

        this.action.OnlineAction.getInfo(this.json.template).then(function(json) {
            var data = {
                "fileName": MWF.xApplication.process.Xform.LP.officeonline.filetext + "." + json.data.extension,
                "fileType": json.data.extension,
                "appToken" : this.appToken,
                "workId" : this.workId,
                "site" : "filetext",
                "tempId": this.json.template
            };

            this.action.OnlineAction.createForO2(data,
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
                "action": o2.Actions.get(this.appToken).action,
                "method": "uploadAttachment",
                "accept" : ".docx,.xlsx,.pptx,.pdf",
                "parameter": {
                    "id": this.workId
                },
                "data":{
                    "site": "filetext"
                },
                "onCompleted": function(data){
                    o2.Actions.load(this.appToken).AttachmentAction.delete(this.documentId,function( json ){
                    }.bind(this));

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
        this.createUpload();
        this.loadDocument();
    },
    loadDocument: function () {
        this.loadApi(function () {
            this.getEditor(function (){
                this.loadEditor();
            }.bind(this));
        }.bind(this));
    },
    loadApi : function (callback){

        this.officeAPI = {
            "pdf" : {
                "view" : "/wv/wordviewerframe.aspx?PdfMode=1",
                "write" : "/wv/wordviewerframe.aspx?PdfMode=1"
            },
            "docx" : {
                "view" : "/wv/wordviewerframe.aspx?1=1",
                "write" : "/we/wordeditorframe.aspx?1=1"
            },
            "doc" : {
                "view" : "/wv/wordviewerframe.aspx?1=1",
                "write" : "/we/wordeditorframe.aspx?1=1"
            },
            "xlsx" : {
                "view" : "/x/_layouts/xlviewerinternal.aspx?ui=zh-CN&rs=zh-CN",
                "write" : "/x/_layouts/xlviewerinternal.aspx?edit=1"
            },
            "xls" : {
                "view" : "/x/_layouts/xlviewerinternal.aspx?ui=zh-CN&rs=zh-CN",
                "write" : "/x/_layouts/xlviewerinternal.aspx?edit=1"
            },
            "pptx" : {
                "view" : "/p/PowerPointFrame.aspx?PowerPointView=ReadingView",
                "write" : "/p/PowerPointFrame.aspx?PowerPointView=EditView"
            },
            "ppt" : {
                "view" : "/p/PowerPointFrame.aspx?PowerPointView=ReadingView",
                "write" : "/p/PowerPointFrame.aspx?PowerPointView=EditView"
            }
        };
        this.action.ConfigAction.getCallBackUrl().then(function (json){
            this.WOPISrc = json.data.value;

            if(this.WOPISrc === ""){
                this.WOPISrc = o2.Actions.getHost( "x_officeonline_assemble_control" );
            }

            if (callback) callback();
        }.bind(this));
    },
    getEditor: function (callback) {
        var action = o2.Actions.load(this.appToken);

        action.AttachmentAction.getOnlineInfo(this.documentId, function( json ){
            this.document = json.data;

            this.fileName = this.document.name;
            var extension = this.document.extension;

            var WOPISrc = this.WOPISrc +"/x_officeonline_assemble_control/jaxrs/wopi/files/" + this.documentId + "?mode=" + this.mode;

            console.log(WOPISrc);

            WOPISrc = WOPISrc + "&appToken=" + this.appToken;

            this.action.ConfigAction.getOfficeOnlineUrl().then(function (json){
                console.log(json)
                this.officeOnlineUrl = json.data.value;

                this.fileUrl = this.officeOnlineUrl + this.officeAPI[extension][this.mode] + "&WOPISrc=" + encodeURIComponent(WOPISrc);
                console.log(WOPISrc);
                console.log(this.fileUrl );
                if (callback) callback();
            }.bind(this));


        }.bind(this),null,false);

    },
    loadEditor: function () {

        this.fireEvent("beforeOpen");

        this.officeNode = new Element("div#_" + this.documentId,{"style":"height:100%;overflow:hidden"}).inject(this.node);

        var form = new Element("form",{"target" : "office_frame_"+this.documentId,"action":this.fileUrl,"type":"hidden","method":"post"}).inject(this.officeNode);
        new Element("input",{"name":"access_token","value":layout.session.token,"type":"hidden"}).inject(form);

        var iframe = new Element("iframe#office_frame_"+this.documentId,{"name":"office_frame_"+this.documentId}).inject(this.officeNode);
        // iframe.set("src",this.fileUrl);
        iframe.set("scrolling","no");
        iframe.set("frameborder",0);
        iframe.setStyles({
            "height" : "95%",
            "width" : "100%"
        });

        form.submit();

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
            data = this.form.businessData.data[this.json.id]
        }
        if(!data.documentId) data.documentId = "";
        return data;
    },
    setData: function() {
        var data = {
            "documentId": this.documentId,
            "appToken": this.appToken
        }
        this.data = data;
        this._setBusinessData(data);

        var jsonData = {}
        jsonData[this.json.id] = data;

        o2.Actions.load(this.appToken).DataAction.updateWithJob(this.form.businessData.work.job, jsonData, function (json) {
            data = json.data;
        })
    }
});
