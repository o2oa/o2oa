o2.widget = o2.widget || {};
o2.widget.ImageLazyLoadder = o2.ImageLazyLoadder = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "path": o2.session.path + "/widget/$ImageLazyLoadder/"
    },
    initialize: function (node, html, options) {
        this.node = node;
        this.html = html;
        this.setOptions(options);

        this.nodeWidth = this.node.getSize().x;

        this.path = this.options.path || (o2.session.path + "/widget/$ImageLazyLoadder/");
        // this.cssPath = this.path + this.options.style + "/css.wcss";
        //
        // this._loadCss();
        this.fireEvent("init");
    },
    load: function(){
        this.loadRerource(function () {
            this.parseHtml()
        }.bind(this))
    },
    parseHtml: function(){
        var html = this.html;
        var regexp_all = /(i?)(<img)([^>]+>)/gmi;
        var images = this.html.match(regexp_all);
        if(images){
            if (images.length){
                for (var i=0; i<images.length; i++){
                    var image = images[i];
                    var size = this.getSize(image);
                    if( size ){
                        var id = this.getValue(image, "data-id");
                        var src = id ? MWF.xDesktop.getImageSrc(id) : this.getValue(image, "src");
                        var image1 = this.removeAttribute(image, "src");
                        
                        html = html.replace(image, image1);
                    }
                }
            }
        }
    },
    removeAttribute: function(str, attribute){
        var regexp = new RegExp( attribute + "\\s*=\\s*[\"|\'](.*?)[\"|\']" , "ig");
        return str.replace( regexp, "" );
    },
    getValue: function(str, attribute){
        var regexp = new RegExp( attribute + "\\s*=\\s*[\"|\'](.*?)[\"|\']" , "i");
        var array = str.match( regexp );
        return (o2.typeOf(array) === "array" && array.length === 2) ? array[1] : "";
    },
    getSize: function(imgStr){
        //获取占位图片高宽，先从style获取高宽，没有从data-width获取，并判断maxWidth
        var width, height, maxWidth;
        var style = this.getValue(imgStr, "style");
        for(var i=0; i<style.length; i++){
            var styles = style[i].split(";");
            for(var j=0; j<styles.length; j++){
                var array = styles[j].split(":");
                switch (array[0]) {
                    case "max-width": maxWidth = array[1]; break;
                    case "width":  width = array[1]; break;
                    case "height": height = array[1]; break;
                }
            }
        }
        if( !width )width = this.getValue(imgStr, "data-width");
        if( !height )height = this.getValue(imgStr, "data-height");

        if( width && height ){
            var mWidth, x, y;
            if( maxWidth.indexOf("%") === maxWidth.length-1 ){
                mWidth = this.nodeWidth * parseFloat(maxWidth) / 100;
            }else{
                mWidth = parseFloat(mWidth);
            }
            if( mWidth && parseFloat(width) > mWidth ){
                y = parseInt( height * ( mWidth / width ) );
                x = parseInt( mWidth )
            }else{
                y = parseInt( height );
                x = parseInt( width )
            }
            return {"x": x, "y": y};
        }
        return null;
    },
    loadRerource: function (callback) {
        var lozadPath = "../o2_lib/lozad/lozad.min.js";
        var observerPath = "../o2_lib/IntersectionObserver/intersection-observer.min.js";
        if( window.IntersectionObserver || window.MutationObserver ){
            o2.load(lozadPath, function () { if(callback)callback(); }.bind(this));
        }else{
            o2.load(observerPath, function () {
                o2.load(lozadPath, function () { if(callback)callback(); }.bind(this));
            }.bind(this));
        }

    }
});