MWF.xDesktop.requireApp("process.Xform", "OnlyOffice", null, false);
MWF.xApplication.cms.Xform.OnlyOffice = MWF.CMSOnlyOffice =  new Class({
    Extends: MWF.APPOnlyOffice,
    createDocument : function (callback){
        var data = {
            "fileName" : MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + this.json.officeType,
            "fileType" : this.json.officeType,
            "appToken" : "x_cms_assemble_control",
            "workId" : this.form.businessData.document.id,
            "site" : "filetext"
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
                "fileName": MWF.xApplication.process.Xform.LP.onlyoffice.filetext + "." + json.data.fileModel.document.fileType,
                "fileType": json.data.fileModel.document.fileType,
                "appToken" : "x_cms_assemble_control",
                "workId" : this.form.businessData.document.id,
                "site" : "filetext",
                "tempId": this.json.template
            };

            this.action.OnlyofficeAction.createForO2(data,
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
                "method": "replaceAttachment",
                "accept" : ".docx,.xlsx,.pptx",
                "parameter": {
                    "id" : this.documentId,
                    "documentid" : this.form.businessData.document.id,
                },
                "data":{
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
    getData: function(){
        debugger
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
                    "appToken": "x_cms_assemble_control"
                };
            }

        }

        return data;
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
