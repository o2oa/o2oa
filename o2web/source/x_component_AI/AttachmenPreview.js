MWF.xApplication.AI.AttachmenPreview = new Class({
    Implements: [Options, Events],

    initialize : function(att,app ){
        this.att = att;
        this.app = app;
        this.fileUrl = o2.filterUrl(o2.Actions.getHost("x_ai_assemble_control") +  "/x_ai_assemble_control/jaxrs/file/"+this.att.id+"/download" );

        this.load();

    },
    load:function(){

        var extension = this.att.extension;
        if(extension === "ofd"){
            this.previewOfd();
        }else if(extension === "pdf"){
            this.previewPdf();
        }else if(["doc","docx","xls","xlsx","ppt","pptx"].contains(extension)){
            // this.previewOffice();
            window.open(this.fileUrl);
        }else if(["png","jpg","bmp","jpeg","gif"].contains(extension)){
            this.previewImage();
        }else{
            window.open(this.fileUrl);
        }

    },
    previewPdf : function(){
        if(layout.mobile){
            location.href = "../o2_lib/pdfjs/web/viewer.html?file=" + this.fileUrl;
        }else{
            window.open("../o2_lib/pdfjs/web/viewer.html?file=" + this.fileUrl);
        }
    },
    previewOffice : function(){


        switch (this.app.json.officeTool) {
            case "LibreOffice":
                this.previewLibreOffice();
                break;
            case "OfficeOnline":
                this.previewOfficeOnline();
                break;
            case "OnlyOffice":
                this.previewOnlyOffice();
                break;
            case "YozoOffice":
                this.previewYozoOffice();
                break;
            case "WpsOffice":
                this.previewWpsOffice();
                break;
            default :
                this.previewLibreOffice();

        }


    },
    previewOfficeOnline : function (){
        var att = this.att;
        var jars ;
        if(att.data.activity){
            jars = "x_processplatform_assemble_surface";
        }
        if(att.data.categoryId){
            jars = "x_cms_assemble_control";
        }

        var options = {
            "documentId": att.data.id,
            "mode":"view",
            "jars" : jars,
            "appId":  "OfficeOnlineEditor" + att.data.id
        };
        layout.openApplication(null, "OfficeOnlineEditor", options);
    },
    previewOnlyOffice : function (){
        var att = this.att;
        var jars ;
        if(att.data.activity){
            jars = "x_processplatform_assemble_surface";
        }
        if(att.data.categoryId){
            jars = "x_cms_assemble_control";
        }

        var options = {
            "documentId": att.id,
            "mode":"view",
            "jars" : jars,
            "appId":  "OnlyOfficeEditor" + att.id
        };
        layout.openApplication(null, "OnlyOfficeEditor", options);
    },
    previewYozoOffice : function (){
        var att = this.att;
        var jars ;
        if(att.data.activity){
            jars = "x_processplatform_assemble_surface";
        }
        if(att.data.categoryId){
            jars = "x_cms_assemble_control";
        }

        var options = {
            "documentId": att.data.id,
            "mode":"view",
            "jars" : jars,
            "appId":  "YozoOfficeEditor" + att.id
        };
        layout.openApplication(null, "YozoOfficeEditor", options);
    },
    previewWpsOffice : function (){
        var att = this.att;
        var jars ;
        if(att.data.activity){
            jars = "x_processplatform_assemble_surface";
        }
        if(att.data.categoryId){
            jars = "x_cms_assemble_control";
        }

        var options = {
            "documentId": att.id,
            "mode":"view",
            "jars" : jars,
            "appId":  "WpsOfficeEditor" + att.id
        };
        layout.openApplication(null, "WpsOfficeEditor", options);
    },
    previewLibreOffice : function (){

        if(!layout.serviceAddressList["x_libreoffice_assemble_control"]){
            this.app.form.notice("Please Install LibreOffice");
            return;
        }
        var srv = layout.serviceAddressList["x_libreoffice_assemble_control"];
        var module;
        if(this.att.activity){
            module = "processPlatform";
        }
        if(this.att.categoryId){
            module = "cms";
        }

        var defaultPort = layout.config.app_protocol === 'https' ? "443" : "80";
        var appPort = srv.port || window.location.port;
        var protocol = layout.config.app_protocol || window.location.protocol;
        var hostname = srv.host || window.location.hostname;
        var context = srv.context || '';

        var url = protocol + "//" + hostname + (appPort && appPort.toString() !== defaultPort ? ":" + appPort : "") + context + "/jaxrs/office/doc/to/pdf/" + module + "/" + this.att.id;

        window.open("../o2_lib/pdfjs/web/viewer.html?file=" + encodeURIComponent(url));
    },
    previewOfd : function(){
        window.open("../o2_lib/ofdjs/index.html?file=" + this.fileUrl);
    },
    previewImage : function(){

        var imgNode = new Element("img",{"src":this.fileUrl,"alt":this.att.name}).inject(document.body).hide();
        o2.loadCss("../o2_lib/viewer/viewer.css", document.body,function(){
            o2.load("../o2_lib/viewer/viewer.js", function(){
                this.viewer = new Viewer(imgNode,{
                    navbar : false,
                    toolbar : true,
                    hidden : function(){
                        imgNode.destroy();
                        this.viewer.destroy();
                    }.bind(this)
                });
                this.viewer.show();
            }.bind(this));
        }.bind(this));
    },
    previewAce:function(type){
        this.getAttachmentUrl(this.att,  function (url) {
            o2.require("o2.widget.ace", null, false);
            var fileRequest = new Request({
                url: url,
                method: 'get',
                withCredentials: true,
                onSuccess: function(responseText){
                    var editorNode = new Element("div",{"style":"padding:10px"});
                    editorNode.set("text",responseText);

                    o2.widget.ace.load(function(){
                        o2.load("../o2_lib/ace/src-min-noconflict/ext-static_highlight.js", function(){
                            var highlight = ace.require("ace/ext/static_highlight");
                            highlight(editorNode, {mode: "ace/mode/"+ type , theme: "ace/theme/tomorrow", "fontSize": 30,"showLineNumbers":true});
                        }.bind(this));

                    }.bind(this));
                    var width, height;
                    if( layout.mobile ){
                        var size = $(document.body).getSize();
                        width = size.x+"px";
                        height = size.y+"px";
                    }else{
                        width = "960px";
                        height = "610px";
                    }
                    var dlg = o2.DL.open({
                        "title": this.att.name,
                        "width": width,
                        "height": height,
                        "mask": true,
                        "content": editorNode,
                        "container": null,
                        "positionNode": document.body,
                        "onQueryClose": function () {
                            editorNode.destroy();
                        }.bind(this),
                        "buttonList": [
                            {
                                "text": "关闭",
                                "action": function () {
                                    dlg.close();
                                }.bind(this)
                            }
                        ],
                        "onPostShow": function () {
                            dlg.reCenter();
                        }.bind(this)
                    });
                }.bind(this),
                onFailure: function(){
                    console.log('text', 'Sorry, your request failed :(');
                }
            });
            fileRequest.send();
        }.bind(this));

    },
});
