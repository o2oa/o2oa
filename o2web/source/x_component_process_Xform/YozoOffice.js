MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.YozoOffice = MWF.APPYozoOffice =  new Class({
    Extends: MWF.APP$Module,
    options:{

        "moduleEvents": [
            "afterOpen",
            "afterCreate",
            "beforeSave",
            "afterSave"
        ]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.mode = "write";
        this.appToken = "x_processplatform_assemble_surface";

    },
    _loadUserInterface: function(){
        this.node.empty();
        this.node.setStyles({
            "min-height": "100px"
        });
    },
    _afterLoaded: function(){
        if(!layout.serviceAddressList["x_yozofile_assemble_control"]){
            this.node.set("html","<h3><font color=red>please install weboffice !!!</font></h3>");
            return false;
        }

        if(this.mode !== "read" && this.json.allowUpload){
            this.createUpload();
        }

        this.action = o2.Actions.load("x_yozofile_assemble_control");
        if (!this.json.isNotLoadNow){

            this.data = this.getData();
            if(this.data.documentId === ""){

                if (this.json.officeType === "other" && this.json.templateType === "script"){
                    this.json.template = this.form.Macro.exec(this.json.templeteScript.code, this);
                }


                this[this.json.officeType === "other"&&this.json.template !== ""? "createDocumentByTemplate":"createDocument"](function (){


                    this.loadOffice();
                }.bind(this));


            }else {
                this.documentId = this.data.documentId;
                this.loadOffice();
            }
        }
    },
    reload : function (){
        this.officeLoaded = false;
        this.setData();
        this.node.empty();
        if(this.mode !== "read" && this.json.allowUpload){
            this.createUpload();
        }
        this.loadOffice();
    },
    createDocumentByTemplate : function (callback){

        this.action.CustomAction.getInfo(this.json.template).then(function(json) {
            var data = {
                "fileName": MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + json.data.extension,
                "fileType": json.data.extension,
                "appToken" : "x_processplatform_assemble_surface",
                "workId" : this.form.businessData.work.id,
                "site" : "filetext",
                "tempId": this.json.template
            };

            this.action.CustomAction.createForO2(data, function( json ){
                    debugger
                    this.documentId = json.data.fileId;
                    this.setData();
                    if (callback) callback();
                }.bind(this),null, false
            );

        }.bind(this))
    },

    createDocument : function (callback){
        var data = {
            "fileName" : MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + this.getFileType(this.json.officeType),
            "appToken" : this.appToken,
            "workId" : this.form.businessData.work.id,
            "site" : "filetext"
        };
        this.action.CustomAction.createForO2(data,
            function( json ){
                debugger
                this.documentId = json.data.fileId;
                this.setData();
                if (callback) callback();
            }.bind(this),null, false
        );
    },
    createUpload : function (){

        this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);
        var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.ofdview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
        uploadBtn.addEvent("click",function (){
            o2.require("o2.widget.Upload", null, false);
            var upload = new o2.widget.Upload(this.content, {
                "action": o2.Actions.get("x_processplatform_assemble_surface").action,
                "method": "uploadAttachment",
                "accept" : ".docx,.xlsx,.pptx",
                "parameter": {
                    "id" : this.form.businessData.work.id
                },
                "data":{
                },
                "onCompleted": function(data){
                    o2.Actions.load("x_processplatform_assemble_surface").AttachmentAction.delete(this.documentId,function( json ){
                    }.bind(this));
                    this.documentId = data.id;

                    this.reload();
                }.bind(this)
            });

            upload.load();
        }.bind(this));

    },
    getData: function(){
        var data = {
            "documentId" : ""
        };
        if(this.form.businessData.data[this.json.id]){
            data.documentId = this.form.businessData.data[this.json.id].documentId;
        }
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
    },
    loadOffice: function(){
        if (!this.officeLoaded){

            this.loadOfficeContorl();
            this.officeLoaded = true;
        }
    },
    loadOfficeContorl: function(file){
        if (this.node.getSize().y<800) this.node.setStyle("height", "800px");

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
        this.loadOfficeEditor();
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
    getFileType: function(){
        var ename = "docx";
        switch (this.json.officeType){
            case "word":
                ename = "docx";
                break;
            case "excel":
                ename = "xlsx";
                break;
            case "ppt":
                ename = "pptx";
        }
        return ename;
    },
    //书签赋值
    setBookMarkValue : function(name,value){

        var YozoOffice = this.iframe.contentWindow.YozoOffice;

        if (YozoOffice.Application.ActiveDocument.Bookmarks.Exists(name)) {
            YozoOffice.Application.Selection.GoTo(-1, null, null, name);
            YozoOffice.Application.Selection.Text = value;
        }
    },
    loadOfficeEditor: function(){

        this.action.CustomAction.getFileUrl(this.documentId,{"mode":this.mode,"appToken":this.appToken}, function( json ){
            var iframe = new Element("iframe").inject(this.node);
            iframe.set("src",json.data.fileUrl);
            iframe.set("id","_" + this.documentId);
            iframe.set("scrolling","no");
            iframe.set("frameborder",0);
            iframe.setStyles({
                "height" : "100%",
                "width" : "100%"
            });

            this.iframe = iframe;
        }.bind(this),null, false);
    }
});
