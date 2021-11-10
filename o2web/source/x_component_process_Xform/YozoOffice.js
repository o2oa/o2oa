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
        this.field = true;
        this.openedAttachment = null;
        this.mode = "write";
    },
    _loadUserInterface: function(){
        this.node.empty();
        this.node.setStyles({
            "min-height": "100px"
        });
        this.file = null;
        if (!this.form.officeList) this.form.officeList=[];
        this.form.officeList.push(this);
    },
    _afterLoaded: function(){
        if(!layout.serviceAddressList["x_yozofile_assemble_control"]){
            this.node.set("html","<h3><font color=red>please install weboffice !!!</font></h3>");
            return false;
        }

        this.action = o2.Actions.load("x_yozofile_assemble_control");
        if (!this.json.isNotLoadNow){

            this.data = this.getData();
            if(this.data.documentId === ""){
                this.createDocument(function (){
                    this.loadOffice();
                }.bind(this));
            }else {
                this.documentId = this.data.documentId;
                this.loadOffice();
            }
        }
        if (!this.json.isNotLoadNow){
            this.loadOffice();
        }
    },
    createDocument : function (callback){
        this.action.CustomAction.createFileBlank(this.json.officeType,{"userId":layout.user.distinguishedName,"fileName":"文件正文." + this.getFileType()}, function( json ){
            this.fireEvent("afterCreate");
            this.documentId = json.data.docId;
            this.setData();
            if (callback) callback();
        }.bind(this),null, false);
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
    setData: function(){
        var data = {
            "documentId" : this.documentId
        };
        this._setBusinessData(data);
    },
    loadOffice: function(){
        if (!this.officeLoaded){

            this.loadOfficeContorl();
            this.officeLoaded = true;
        }
    },
    loadOfficeContorl: function(file){
        if (this.node.getSize().y<800) this.node.setStyle("height", "800px");

        if (this.readonly || this.json.isReadonly){
            this.mode  = "read";
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.mode = "read";
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
    loadOfficeEditor: function(){

        this.action.CustomAction.getFileUrl(this.documentId,{"userId":layout.user.distinguishedName,"permission":this.mode}, function( json ){
            var iframe = new Element("iframe").inject(this.node);
            iframe.set("src",json.data.redirectUrl);
            iframe.set("scrolling","no");
            iframe.set("frameborder",0);
            iframe.setStyles({
                "height" : "100%",
                "width" : "100%"
            });
        }.bind(this),null, false);
    },
    isEmpty : function(){
    },
    save: function(){
        if (!this.readonly){
            this.fireEvent("beforeSave");
            this.fireEvent("afterSave");
        }
    },
    validation: function(){return true}
});
