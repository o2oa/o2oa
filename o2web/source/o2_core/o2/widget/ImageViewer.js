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

        this.isIE11 = !!window.MSInputMethodContext && !!document.documentMode;

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
    load: function (callback) {
        if( Browser.name === 'ie' && !this.isIE11 ){
            if(callback)callback();
        }else{
            var flag = false;
            this.nodeList.each(function(node){
                if(node)node.getElements("img").each(function(img){
                    var preview = img.get("data-prv");
                    if( preview !== "false" && preview !== false ){
                        flag = true;
                        img.setStyle("cursor", "pointer");
                        img.set("preview", "true");
                        var orgId = img.get("data-orgid");
                        if(orgId){
                            img.set("data-originalUrl", o2.xDesktop.getImageSrc(orgId));
                        }
                    }
                }.bind(this))
            }.bind(this));
            if( flag ){
                this.loadResource(function () {
                    if(window.Viewer){
                        new Viewer( this.container, {
                            url: function (image) {
                                // var id = image.get("data-orgid") || image.get("data-id");
                                var id = image.get("data-id");
                                return id ? o2.xDesktop.getImageSrc(id) : ( image.get("data-src") || image.get("src") )
                            },
                            filter: function (image) {
                                return image.get("preview") === "true";
                            }
                        });
                    }
                    if(callback)callback();
                }.bind(this))
            }else{
                if(callback)callback();
            }
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