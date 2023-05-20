MWF.xDesktop.requireApp("process.Xform", "PdfView", null, false);
MWF.xApplication.cms.Xform.PdfView = MWF.CMSPdfView =  new Class({
    Extends: MWF.APPPdfView,
    createUpload : function (){


        this.uploadNode = new Element("div",{"style":"margin:10px;"}).inject(this.node);

        var uploadBtn = new Element("button",{"text":MWF.xApplication.process.Xform.LP.pdfview.upload,"style":"margin-left: 15px; color: rgb(255, 255, 255); cursor: pointer; height: 26px; line-height: 26px; padding: 0px 10px; min-width: 40px; background-color: rgb(74, 144, 226); border: 1px solid rgb(82, 139, 204); border-radius: 15px;"}).inject(this.uploadNode);
        uploadBtn.addEvent("click",function (){
            o2.require("o2.widget.Upload", null, false);

            if(this.documentId === ""){
                var upload = new o2.widget.Upload(this.form.app.content, {
                    "action": o2.Actions.get("x_cms_assemble_control").action,
                    "method": "uploadAttachment",
                    "parameter": {
                        "id": this.form.businessData.document.id
                    },
                    "accept" : "application/pdf",
                    "data":{
                        "site": "pdfAttachement"
                    },
                    "onCompleted": function(json){
                        this.documentId = json.data.id;
                        this.setData();
                        this.loadPdfView();
                    }.bind(this)
                });
            }else {
                var upload = new o2.widget.Upload(this.form.app.content, {
                    "action": o2.Actions.get("x_cms_assemble_control").action,
                    "method": "replaceAttachment",
                    "parameter": {
                        "id" : this.documentId,
                        "documentid" : this.form.businessData.document.id
                    },
                    "accept" : "application/pdf",
                    "data":{
                    },
                    "onCompleted": function(json){
                        this.documentId = json.data.id;
                        this.setData();
                        this.loadPdfView();
                    }.bind(this)
                });
            }

            upload.load();
        }.bind(this));

    },

    loadPdfView: function(){

        this.iframeNode = new Element("div").inject(this.node);
        this.iframeNode.setStyles({
            "height": "100%"
        });

        var host = o2.Actions.getHost( "x_cms_assemble_control" );
        var fileUrl = o2.filterUrl(host + "/x_cms_assemble_control/jaxrs/fileinfo/download/document/" + this.documentId);

        if(this.iframe){
            this.iframe.set("src","../o2_lib/pdfjs/web/viewer.html?file=" + fileUrl);
        }else {
            this.iframe = new Element("iframe").inject(this.iframeNode);
            this.iframe.set("src","../o2_lib/pdfjs/web/viewer.html?file=" + fileUrl);
            this.iframe.set("scrolling","no");
            this.iframe.set("frameborder",0);
            this.iframe.setStyles({
                "height" : "100%",
                "width" : "100%"
            });
        }
        if(this.emptyNode) this.emptyNode.hide();
        this.fireEvent("afterOpen");
    }
});
