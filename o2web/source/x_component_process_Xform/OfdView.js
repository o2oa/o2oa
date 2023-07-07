MWF.xDesktop.requireApp("process.Xform", "$Module", null, false);
MWF.xApplication.process.Xform.OfdView = MWF.APPOfdView =  new Class({
    Extends: MWF.APP$Module,
    options:{
        "moduleEvents": [
            "afterOpen"
        ]
    },
    initialize: function(node, json, form, options){
        this.node = $(node);
        this.node.store("module", this);
        this.json = json;
        this.form = form;
        this.fileTemplate = false;

        if (this.json.isReadonly || this.form.json.isReadonly){
            this.mode  = "read";
        }else{
            if (this.json.readScript && this.json.readScript.code){
                var flag = this.form.Macro.exec(this.json.readScript.code, this);
                if (flag){
                    this.mode = "read";
                }
            }
        }

    },
    _loadUserInterface: function(){
        this.node.empty();
    },
    _afterLoaded: function(){
        if(this.mode !== "read"){
            this.createUpload();
        }
        if(this.json.template && (this.json.templateType === "value" && this.json.template !=="")){
            this.fileTemplate = true;
        }
        if(this.json.templeteScript && (this.json.templateType === "script" && this.json.templeteScript !=="")){
            this.fileTemplate = true;
        }
        this.data = this.getData();
        this.documentId = this.data.documentId;
        if(this.data.documentId === "" && !this.fileTemplate){
            this.createEmpty();
        }else {
            this.loadOfdView();
        }
    },
    createEmpty : function (){
        this.emptyNode = new Element("div").inject(this.node);
        this.emptyNode.set("text",MWF.xApplication.process.Xform.LP.ofdview.nofile);
    },
    createUpload : function (){


        this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);

        var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.ofdview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
        uploadBtn.addEvent("click",function (){
            o2.require("o2.widget.Upload", null, false);

            if(this.documentId === ""){
                var upload = new o2.widget.Upload(this.form.app.content, {
                    "action": o2.Actions.get("x_processplatform_assemble_surface").action,
                    "method": "uploadAttachment",
                    "parameter": {
                        "id": this.form.businessData.work.id
                    },
                    "accept" : ".ofd",
                    "data":{
                        "site": "ofdAttachement"
                    },
                    "onCompleted": function(json){
                        this.documentId = json.data.id;
                        this.setData();
                        this.loadOfdView();
                    }.bind(this)
                });
            }else {
                var upload = new o2.widget.Upload(this.form.app.content, {
                    "action": o2.Actions.get("x_processplatform_assemble_surface").action,
                    "method": "replaceAttachment",
                    "parameter": {
                        "id" : this.documentId,
                        "workid": this.form.businessData.work.id
                    },
                    "accept" : ".ofd",
                    "data":{
                    },
                    "onCompleted": function(json){
                        this.documentId = json.data.id;
                        this.setData();
                        this.loadOfdView();
                    }.bind(this)
                });
            }

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
    setData: function(){

        var data = {
            "documentId" : this.documentId
        };
        this._setBusinessData(data);
    },
    loadOfdView: function(){
        debugger
        this.iframeNode = new Element("div").inject(this.node);
        this.iframeNode.setStyles({
            "height": "100%"
        });
        var fileUrl;
        if(this.json.template && (this.json.templateType === "value" && this.json.template !=="")){
            fileUrl = this.json.template;
        }else if(this.json.templeteScript && (this.json.templateType === "script" && this.json.templeteScript !=="")){
            fileUrl = this.form.Macro.exec(this.json.templeteScript.code, this);
        }else {
            var host = o2.Actions.getHost( "x_processplatform_assemble_surface" );
            fileUrl = o2.filterUrl(host + "/x_processplatform_assemble_surface/jaxrs/attachment/download/" + this.documentId);
        }

        if(this.iframe){
            this.iframe.set("src","../o2_lib/ofdjs/index.html?file=" + fileUrl);
        }else {
            this.iframe = new Element("iframe").inject(this.iframeNode);
            this.iframe.set("src","../o2_lib/ofdjs/index.html?file=" + fileUrl);
            this.iframe.set("scrolling","no");
            this.iframe.set("frameborder",0);
            this.iframe.setStyles({
                "height" : "100%",
                "width" : "100%"
            });
        }
        if(this.emptyNode) this.emptyNode.hide();

        this.fireEvent("afterOpen");
    },
    loadOfdViewByUrl : function (fileUrl){
        if(this.iframe){
            this.iframe.set("src","../o2_lib/ofdjs/index.html?file=" + fileUrl);
        }else {
            this.iframe = new Element("iframe").inject(this.iframeNode);
            this.iframe.set("src","../o2_lib/ofdjs/index.html?file=" + fileUrl);
            this.iframe.set("scrolling","no");
            this.iframe.set("frameborder",0);
            this.iframe.setStyles({
                "height" : "100%",
                "width" : "100%"
            });
        }
        if(this.emptyNode) this.emptyNode.hide();

        this.fireEvent("afterOpen");
    },
    hide: function(){
        this.node.hide();
    },
    show: function(){
        this.node.show();
    },
    isEmpty : function(){
    },
    save: function(){
    },
    validation: function(){return true}
});
