o2.widget = o2.widget || {};
o2.widget.ImageViewer = o2.ImageViewer = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "path": o2.session.path + "/widget/$ImageViewer/",
        "imageUrl": ""
    },
    initialize: function (container, nodeList, options) {
        this.container = container;
        if(nodeList){
            this.nodeList = typeOf(nodeList) === "array" ? nodeList : [nodeList];
        }else{
            this.nodeList = [container];
        }
        this.setOptions(options);

        // this.path = this.options.path || (o2.session.path + "/widget/$ImageViewer/");
        // this.cssPath = this.path + this.options.style + "/css.wcss";
        //
        // this._loadCss();
        this.fireEvent("init");
    },
    load: function () {
        debugger;
        var images = [];
        this.nodeList.each(function(node){
            if(node)images = images.concat( node.getElements("img") )
        }.bind(this));
        var previewImageList = images.filter(function (img) {
            var enablePreview = img.get("data-prv");
            if( enablePreview !== "false" && enablePreview !== false ){
                img.setStyle("cursor", "pointer");
                img.set("preview", "true");
                return true;
            }
            return false;
        });
        if( previewImageList.length > 0 ){
            this.loadResource(function () {
                new Viewer( this.container, {
                    url: function (image) {
                        var id = image.get("data-orgid") || image.get("data-id");
                        return id ? o2.xDesktop.getImageSrc(id) : ( image.get("data-src") || image.get("src") )
                    },
                    filter: function (image) {
                        return image.get("preview") === "true";
                    }
                });
            }.bind(this))
        }
    },
    loadResource : function( callback ){
        if( window.Viewer ){
            if( callback )callback();
            return;
        }
        COMMON.AjaxModule.loadCss("../o2_lib/viewer/viewer.css", function () {
            o2.load( "../o2_lib/viewer/viewer.js", function () {
                if(callback)callback();
            }.bind(this))
        }.bind(this))
    }
});