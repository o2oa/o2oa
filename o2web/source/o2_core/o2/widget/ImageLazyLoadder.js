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
        debugger;
        var html = this.html;
        var regexp_all = /(i?)(<img)([^>]+>)/gmi;
        var images = this.html.match(regexp_all);
        if(images){
            if (images.length){
                for (var i=0; i<images.length; i++){
                    var image = images[i];
                    var size = this.getSize(image);
                    if( size ){

                        var id = this.getAttributeValue(image, "data-id");
                        var src = id ? MWF.xDesktop.getImageSrc(id) : this.getAttributeValue(image, "src");
                        image1 = this.addAttribute(image1, "data-src", src);

                        var image1 = this.removeAttribute(image, "src");

                        image1 = this.removeStyle(image1, "width");
                        image1 = this.removeStyle(image1, "height");

                        image1 = this.addStyle(image1, "height", size.y+"px");
                        image1 = this.addStyle(image1, "width", size.x+"px");

                        html = html.replace(image, image1);
                    }
                }
            }
        }
        this.html = html;
    },
    getAttributeValue: function(str, attribute){
        var regexp = new RegExp( attribute + "\\s*=\\s*[\"|\'](.*?)[\"|\']" , "i");
        var array = str.match( regexp );
        return (o2.typeOf(array) === "array" && array.length === 2) ? array[1] : "";
    },
    addAttribute: function(str, attribute, value){
        var last = str.lastIndexOf(">");
        return str.substring(0, last) + ' ' + attribute + '"' + value + '"' + str.substring(last, str.length-1);
    },
    removeAttribute: function(str, attribute){
        var regexp = new RegExp( attribute + "\\s*=\\s*[\"|\'](.*?)[\"|\']" , "ig");
        return str.replace( regexp, "" );
    },
    removeStyle: function(str, key){
        var regexp = new RegExp( key + "\\s*:\\s*.*?;" , "i");
        return str.replace( regexp, "" );
    },
    getSize: function(imgStr){
        //获取占位图片高宽，先从style获取高宽，没有从data-width获取，并判断maxWidth
        var width, height, maxWidth;
        var style = this.getAttributeValue(imgStr, "style");
        var styles = style.split(";");
        for(var j=0; j<styles.length; j++){
            var array = styles[j].split(":");
            if(array.length > 1){
                array[1] = array[1].trim();
                switch (array[0].trim() ) {
                    case "max-width": maxWidth = array[1]; break;
                    case "width":  width = array[1]; break;
                    case "height": height = array[1]; break;
                }
            }
        }
        if( !width )width = this.getAttributeValue(imgStr, "data-width");
        if( !height )height = this.getAttributeValue(imgStr, "data-height");

        if( width && height ){
            width = parseFloat( width );
            height = parseFloat( height );
            var mWidth, x, y;
            if( maxWidth.indexOf("%") === maxWidth.length-1 ){
                mWidth = this.nodeWidth * parseFloat(maxWidth) / 100;
            }else{
                mWidth = parseFloat(maxWidth);
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