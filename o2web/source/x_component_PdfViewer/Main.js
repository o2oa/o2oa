
MWF.xApplication.PdfViewer = MWF.xApplication.PdfViewer || {};


MWF.xApplication.PdfViewer.Main = new Class({
    Extends: MWF.xApplication.Common.Main,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "name": "PdfViewer",
        "mvcStyle": "style.css",
        "mode" : "view",
        "title": ""
    },
    onQueryLoad: function () {

        this.lp = MWF.xApplication.PdfViewer.LP;
        this.fileUrl = this.options.fileUrl;
        this.fileName = this.options.fileName;

        if(this.status){
            this.fileUrl = this.status.fileUrl;
        }

    },
    onQueryClose : function (){

    },
    loadApplication: function (callback) {
        this.setTitle(this.fileName);
        this.loadEditor();

    },
    loadEditor: function(){

        var iframe = new Element("iframe").inject(this.content);
        iframe.set("src",this.fileUrl);
        iframe.set("scrolling","no");
        iframe.set("frameborder",0);
        iframe.setStyles({
            "height" : "100%",
            "width" : "100%"
        });
    },

    recordStatus: function(){
        var status ={
            "fileUrl": this.fileUrl
        };
        return status;
    }
});
