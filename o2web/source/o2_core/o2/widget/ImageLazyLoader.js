o2.widget = o2.widget || {};
o2.widget.ImageLazyLoader = o2.ImageLazyLoader = new Class({
    Implements: [Options, Events],
    Extends: o2.widget.Common,
    options: {
        "style": "default",
        "path": o2.session.path + "/widget/$ImageLazyLoader/"
    },
    initialize: function (node, html, options) {
        this.node = node;
        this.html = html;
        this.setOptions(options);

        this.isIE11 = !!window.MSInputMethodContext && !!document.documentMode;

        this.nodeWidth = this.node.getSize().x;

        this.path = this.options.path || (o2.session.path + "/widget/$ImageLazyLoader/");
        // this.cssPath = this.path + this.options.style + "/css.wcss";
        //
        // this._loadCss();
        this.fireEvent("init");
    },
    load: function(callback){
        debugger;
        if( Browser.name === 'ie' && !this.isIE11 ){
            this.parseOnerror();
            this.node.set("html", this.html_new);
            if(callback)callback();
        }else{
            this.loadResource(function () {
                if(window.lozad){
                    this.parseHtml();
                    this.node.set("html", this.html_new);
                    var observer = lozad( this.node.querySelectorAll('img.lozad'), {
                        rootMargin: '1000px 0px', // syntax similar to that of CSS Margin
                        threshold: 0, // ratio of element convergence
                        enableAutoReload: true // it will reload the new image when validating attributes changes
                    });
                    observer.observe();
                }else{
                    this.parseOnerror();
                    this.node.set("html", this.html_new);
                }
                if(callback)callback();
            }.bind(this));
        }
    },
    parseOnerror: function(){
        var html = this.replaceOnAttribute(this.html);
        var regexp_all = /(i?)(<img)([^>]+>)/gmi;
        var images = this.html.match(regexp_all);
        if(images){
            if (images.length){
                for (var i=0; i<images.length; i++){
                    var image = images[i];

                    var image1 = this.removeAttribute(image, "onerror");
                    image1 = this.addAttribute(image1, "onerror", "MWF.xDesktop.setImageSrc()");

                    html = html.replace(image, image1);
                }
            }
        }
        html = this.replaceHrefJavascriptStr( html );
        this.html_new = html;
    },
    parseHtml: function(){
        var html = this.replaceOnAttribute(this.html);
        var regexp_all = /(i?)(<img)([^>]+>)/gmi;
        var images = this.html.match(regexp_all);
        if(images){
            if (images.length){
                for (var i=0; i<images.length; i++){
                    var image = images[i];

                    var image1 = this.removeAttribute(image, "onerror");
                    image1 = this.addAttribute(image1, "onerror", "MWF.xDesktop.setImageSrc()");

                    var src =  this.getAttributeValue(image, "src");
                    if( src.substr(0, 5) !== "data:" ){ //不是base64位
                        var size = this.getSize(image);
                        if( size ){
                            image1 = this.removeAttribute(image1, "src");

                            var id = this.getAttributeValue(image1, "data-id");
                            var src2 = id ? MWF.xDesktop.getImageSrc(id) : src;
                            image1 = this.addAttribute(image1, "data-src", src2);

                            image1 = this.replaceStyles(image1, {
                                "height": size.y+"px",
                                "width": size.x+"px"
                            });

                            image1 = this.addAttribute(image1, "class", "lozad");
                        }
                    }

                    html = html.replace(image, image1);
                }
            }
        }

        html = this.replaceHrefJavascriptStr( html );

        this.html_new = html;
    },
    replaceOnAttribute: function (htmlString){

        var tempDiv = document.createElement('div');

        tempDiv.innerHTML = htmlString;

        var elements = tempDiv.getElementsByTagName('*');

        for (var i = 0; i < elements.length; i++) {
            var element = elements[i];

            var attributeNames = element.getAttributeNames();

            for (var j = 0; j < attributeNames.length; j++) {
                var attributeName = attributeNames[j];
                if (attributeName.substr(0,2).toLowerCase() === 'on') {
                    element.removeAttribute(attributeName);
                }
            }
        }
        return tempDiv.innerHTML;
    },
    replaceHrefJavascriptStr: function( html ){
        var regexp_a_all = /(i?)(<a)([^>]+>)/gmi;
        var as = html.match(regexp_a_all);
        if(as){
            if (as.length){
                for (var i=0; i<as.length; i++){
                    var a = as[i];
                    var href =  this.getAttributeValue(a, "href");
                    if( href.indexOf('javascript:') > -1 ){
                        var a1 = this.removeAttribute(a, "href");
                        html = html.replace(a, a1);
                    }
                }
            }
        }
        return html;
    },
    getAttrRegExp: function( attr ){
        return "\\s+" + attr + "\\s*=\\s*[\"|\'](.*?)[\"|\']";
    },
    getAttributeValue: function(str, attribute){
        var regexp = new RegExp( this.getAttrRegExp(attribute) , "i");
        var array = str.match( regexp );
        return (o2.typeOf(array) === "array" && array.length === 2) ? array[1] : "";
    },
    addAttribute: function(str, attribute, value){
        var regexp = new RegExp( "\\/*\\s*>" , "i");
        return str.replace( regexp, ' ' + attribute + '="' + value + '"' + " />");
    },
    removeAttribute: function(str, attribute){
        var regexp = new RegExp( this.getAttrRegExp(attribute) , "ig");
        return str.replace( regexp, "" );
    },
    replaceStyles: function(str, object){
        /*object 参数 {
           "width" : "100px", //添加或替换
           "height": "" //删除
        }*/
        var regexp = new RegExp( this.getAttrRegExp("style") , "i");
        var array = str.match( regexp );
        var newArray = [];
        Object.each(object, function (value, key) {
            if(value)newArray.push( key + ":" + value )
        });
        if( o2.typeOf(array) === "array" && array.length>1){
            var styles = array[1].split(/\s*;\s*/gi);
            for(var j=0; j<styles.length; j++){
                var ar = styles[j].split(/\s*:\s*/gi);
                var key = ar[0].toLowerCase();
                if( !object.hasOwnProperty( key ) ){
                    newArray.push( styles[j] );
                }
            }
        }
        if(o2.typeOf(array) === "array" && array[0]){ //原先有style
            if( newArray.length === 0 ){
                return str.replace(array[0], "")
            }else{
                return str.replace(array[0], " style=\""+ newArray.join("; ") + "\"")
            }
        }else{
            if( newArray.length === 0 ){
                return str;
            }else{
                return this.addAttribute(str, "style", newArray.join("; "));
            }
        }
    },
    getSize: function(imgStr){
        //获取占位图片高宽，先从style获取高宽，没有从data-width获取，并判断maxWidth
        var width="", height="", maxWidth="";
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

        if( !width )width = this.getAttributeValue(imgStr, "width");
        if( !height )height = this.getAttributeValue(imgStr, "height");

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
    loadResource: function (callback) {
        var lozadPath = "../o2_lib/lozad/lozad.min.js";
        var observerPath = "../o2_lib/IntersectionObserver/intersection-observer.min.js";
        var observerPath_ie11 = "../o2_lib/IntersectionObserver/polyfill_ie11.min.js";
        if( this.isIE11 ){
            o2.load(observerPath_ie11, function () {
                o2.load(lozadPath, function () { if(callback)callback(); }.bind(this));
            }.bind(this));
        }else if( window.IntersectionObserver && window.MutationObserver){
            o2.load(lozadPath, function () { if(callback)callback(); }.bind(this));
        }else{
            o2.load([observerPath, observerPath_ie11], function () {
                o2.load(lozadPath, function () { if(callback)callback(); }.bind(this));
            }.bind(this));
        }

    }
});