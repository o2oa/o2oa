MWF.xDesktop.requireApp("process.Xform", "OfficeOnline", null, false);
MWF.xApplication.cms.Xform.OfficeOnline = MWF.CMSOfficeOnline =  new Class({
    Extends: MWF.APPOfficeOnline,
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.documentId = "";
        this.mode = "write";
        this.appToken = "x_cms_assemble_control";
        this.workId = this.form.businessData.document.id;
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
            "appToken": this.appToken
        }
        this.data = data;
        this._setBusinessData(data);

        var jsonData = {}
        jsonData[this.json.id] = data;

        o2.Actions.load(this.appToken).DataAction.updateWithDocument(this.form.businessData.document.id, jsonData, function (json) {
            data = json.data;
        })
    },
    getEditor: function (callback) {
        var action = o2.Actions.load(this.appToken);

        action.FileInfoAction.getOnlineInfo(this.documentId, function( json ){
            this.document = json.data;

            this.fileName = this.document.name;
            var extension = this.document.extension;

            var WOPISrc = this.WOPISrc +"/x_officeonline_assemble_control/jaxrs/wopi/files/" + this.documentId + "?mode=" + this.mode;

            console.log(WOPISrc);

            WOPISrc = WOPISrc + "&appToken=" + this.appToken;

            this.action.ConfigAction.getOfficeOnlineUrl().then(function (json){
                this.officeOnlineUrl = json.data.value;

                this.fileUrl = this.officeOnlineUrl + this.officeAPI[extension][this.mode] + "&WOPISrc=" + encodeURIComponent(WOPISrc);
                console.log(WOPISrc);
                console.log(this.fileUrl );
                if (callback) callback();
            }.bind(this));


        }.bind(this),null,false);

    },
});
