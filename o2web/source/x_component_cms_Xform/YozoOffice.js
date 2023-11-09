MWF.xDesktop.requireApp("process.Xform", "YozoOffice", null, false);
MWF.xApplication.cms.Xform.YozoOffice = MWF.CMSYozoOffice =  new Class({
    Extends: MWF.APPYozoOffice,
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.mode = "write";
        this.appToken = "x_cms_assemble_control";

    },
    createDocument : function (callback){
        var data = {
            "fileName" : MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + this.getFileType(this.json.officeType),
            "appToken" : this.appToken,
            "workId" : this.form.businessData.document.id,
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
    createDocumentByTemplate : function (callback){

        this.action.CustomAction.getInfo(this.json.template).then(function(json) {
            var data = {
                "fileName": MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + json.data.extension,
                "fileType": json.data.extension,
                "appToken" : "x_cms_assemble_control",
                "workId" : this.form.businessData.document.id,
                "site" : "filetext",
                "tempId": this.json.template
            };

            this.action.CustomAction.createForO2(data,
                function( json ){
                    this.documentId = json.data.fileId;
                    this.setData();
                    if (callback) callback();
                }.bind(this),null, false
            );

        }.bind(this));
    },
    createUpload : function (){

        this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);
        var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.ofdview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
        uploadBtn.addEvent("click",function (){
            o2.require("o2.widget.Upload", null, false);
            var upload = new o2.widget.Upload(this.content, {
                "action": o2.Actions.get("x_cms_assemble_control").action,
                "method": "uploadAttachment",
                "accept" : ".docx,.xlsx,.pptx",
                "parameter": {
                    "id" : this.form.businessData.document.id,
                },
                "data":{
                },
                "onCompleted": function(data){
                    o2.Actions.load(this.appToken).FileInfoAction.delete(this.documentId,function( json ){
                    }.bind(this));
                    this.documentId = data.id;

                    this.reload();
                }.bind(this)
            });

            upload.load();
        }.bind(this));

    },
    setData: function() {
        var data = {
            "documentId": this.documentId,
            "appToken": "x_cms_assemble_control"
        };
        this.data = data;
        this._setBusinessData(data);

        var jsonData = {}
        jsonData[this.json.id] = data;

        o2.Actions.load("x_cms_assemble_control").DataAction.updateWithDocument(this.form.businessData.document.id, jsonData, function (json) {
            data = json.data;
        });
    }
});
