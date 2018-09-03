/*
common.js by tommy 
Object:
	COMMON.Browser:
		COMMON.Browser.ie - (boolean) True if the current browser is Internet Explorer.
		COMMON.Browser.firefox - (boolean) True if the current browser is Firefox.
		COMMON.Browser.safari - (boolean) True if the current browser is Safari.
		COMMON.Browser.chrome - (boolean) True if the current browser is Chrome.
		COMMON.Browser.opera - (boolean) True if the current browser is Opera.

		COMMON.Browser.Features.xpath - (boolean) True if the browser supports DOM queries using XPath.
		COMMON.Browser.Features.air - (boolean) True if the browser supports AIR.
		COMMON.Browser.Features.query - (boolean) True if the browser supports querySelectorAll.
		COMMON.Browser.Features.json - (boolean) True if the browser has a native JSON object.
		COMMON.Browser.Features.xhr - (boolean) True if the browser supports native XMLHTTP object.
		
		COMMON.Browser.Platform.mac - (boolean) True if the platform is Mac.
		COMMON.Browser.Platform.win - (boolean) True if the platform is Windows.
		COMMON.Browser.Platform.linux - (boolean) True if the platform is Linux.
		COMMON.Browser.Platform.ios - (boolean) True if the platform is iOS.
		COMMON.Browser.Platform.android - (boolean) True if the platform is Android
		COMMON.Browser.Platform.webos - (boolean) True if the platform is WebOS
		COMMON.Browser.Platform.other - (boolean) True if the platform is neither Mac, Windows, Linux, Android, WebOS nor iOS.
		COMMON.Browser.Platform.name - (string) The name of the platform.
		COMMON.Browser.Platform.isMobile - (boolean) True if the platform is mobile os.
		
		COMMON.Browser.Request - (object) The XMLHTTP object or equivalent.

		COMMON.Browser.exec(jsText) - (string) Executes the passed in string in the browser context.

	COMMON.Class:
		COMMON.Class.create(options) - Create new class.

	COMMON.JSON:
		COMMON.JSON.validate(jsonString) - (boolean) Validate a json String.
		COMMON.JSON.encode(object) - (string) transform a object(or array) to json String.
		COMMON.JSON.decode(jsonString) - (object) transform json String to object(or array).

		COMMON.JSON.get(url, callback, async)
			Arguments:
				url - The URL to json.
				callback - (object or function) If function, Fired when the Request is completed successfully; If object, {onSuccess: function(){}, onRequestFailure: function(){}}, "onSuccess" will fired when the Request is completed successfully, "onRequestFailure" will fired when the request failed;
				async - (boolean: defaults to true) If set to false, the requests will be synchronous and freeze the browser during request.

			Returns:
				object - json object or array
	
	COMMON.XML:
		COMMON.XML.get(url, callback, async)
			Arguments:
				url - The URL to XML.
				callback - (object or function) If function, Fired when the Request is completed successfully; If object, {onSuccess: function(){}, onRequestFailure: function(){}}, "onSuccess" will fired when the Request is completed successfully, "onRequestFailure" will fired when the request failed;
				async - (boolean: defaults to true) If set to false, the requests will be synchronous and freeze the browser during request.

			Returns:
				object - json object or array


	COMMON.AjaxModule
		COMMON.AjaxModule.load(url, callback, async, reload)
			Arguments:
				url - (string or array) The URL to json.
				callback - (object or function) If function, Fired when the Request is completed successfully; If object, {onSuccess: function(){}, onRequestFailure: function(){}}, "onSuccess" will fired when the Request is completed successfully, "onRequestFailure" will fired when the request failed;
				async - (boolean: defaults to true) If set to false, the requests will be synchronous and freeze the browser during request.
				reload - (boolean: defaults to false) If set to true, Even if the javascript had loaded, the javascript will be load.

	COMMON.DOM
		COMMON.DOM.addReady(fn) - add function,  which executes when the DOM is loaded.

Class:
	COMMON.Request:
		var options = {
			url: "",
			async: true,
			method: "get",
			data: "",
			onSuccess: function(responseText, responseXml){
				//.....
			},
			onFailure: function(xhr){
				//.........
			}
		};
		var r = new COMMON.Request(options).send();
		Options:
			url - (string: defaults to null) The URL to request. 
			async - (boolean: defaults to true) If set to false, the requests will be synchronous and freeze the browser during request.
			method - (string: defaults to 'post') The HTTP method for the request, can be either 'post' or 'get'.
			encoding - (string: defaults to 'utf-8') The encoding to be set in the request header.
			data - (mixed: defaults to '') The default data for Request:send, used when no data is given. Can be an Element, Object or String. If an Object is passed the Object:toQueryString method will be used to convert the object to a string. If an Element is passed the Element:toQueryString method will be used to convert the Element to a string.
			onSuccess(responseText, responseXML) - Fired when the Request is completed successfully.
			onFailure(xhr) - Fired when the request failed (error status code).

		Method: 
			myRequest.send(data) - Opens the Request connection and sends the provided data with the specified options.
			myRequest.setHeader(name, value) - Add or modify a header for the request.
			myRequest.getHeader(name) - Returns the given response header or null if not found.

Method:
	COMMON.typeOf(obj)
		Arguments:
			obj - (object) The object to inspect.
 
		Returns:
			■ 'element' - (string) If object is a DOM element node.
			■ 'elements' - (string) If object is an instance of Elements.
			■ 'textnode' - (string) If object is a DOM text node.
			■ 'whitespace' - (string) If object is a DOM whitespace node.
			■ 'arguments' - (string) If object is an arguments object.
			■ 'array' - (string) If object is an array.
			■ 'object' - (string) If object is an object.
			■ 'string' - (string) If object is a string.
			■ 'number' - (string) If object is a number.
			■ 'date' - (string) If object is a date.
			■ 'boolean' - (string) If object is a boolean.
			■ 'function' - (string) If object is a function.
			■ 'regexp' - (string) If object is a regular expression.
			■ 'class' - (string) If object is a Class (created with new Class or the extend of another class).
			■ 'collection' - (string) If object is a native HTML elements collection, such as childNodes or getElementsByTagName.
			■ 'window' - (string) If object is the window object.
			■ 'document' - (string) If object is the document object.
			■ 'domevent' - (string) If object is an event.
			■ 'null' - (string) If object is undefined, null, NaN or none of the above.

	COMMON.copyPrototype(content, options)
		Arguments:
			content - (object or function) If object, options's property will by copy to content; If function, options's property will by copy to content's prototype.
			options - (object)
*/

COMMON = window.COMMON || {};
var href = window.location.href;
if (href.indexOf("debugger")!==-1) COMMON["debugger"] = true;
COMMON.contentPath = "";
COMMON.version = "1.0.6";

COMMON.setContentPath = function(path){
    COMMON.contentPath = path;
    window.CKEDITOR_BASEPATH = path+"/res/framework/htmleditor/ckeditor/";
    COMMON.AjaxModule.init();
};

Function.attempt = function(){
    for (var i = 0, l = arguments.length; i < l; i++){
        try {
            return arguments[i]();
        } catch (e){}
    }
    return null;
};

(function(){

    var document = this.document;
    var window = document.window = this;

    var ua = navigator.userAgent.toLowerCase(),
        platform = navigator.platform.toLowerCase(),
        UA = ua.match(/(opera|ie|firefox|chrome|version)[\s\/:]([\w\d\.]+)?.*?(safari|version[\s\/:]([\w\d\.]+)|$)/) || [null, 'unknown', 0],
        mode = UA[1] == 'ie' && document.documentMode;

    COMMON.Browser = {

        extend: Function.prototype.extend,

        name: (UA[1] == 'version') ? UA[3] : UA[1],

        version: mode || parseFloat((UA[1] == 'opera' && UA[4]) ? UA[4] : UA[2]),

        Platform: {
            name: ua.match(/ip(?:ad|od|hone)/) ? 'ios' : (ua.match(/(?:webos|android)/) || ua.match(/(?:windows\sphone)/) || platform.match(/mac|win|linux/) || ['other'])[0]
        },

        Features: {
            xpath: !!(document.evaluate),
            air: !!(window.runtime),
            query: !!(document.querySelector),
            json: !!(window.JSON)
        },

        Plugins: {}

    };

    COMMON.Browser[COMMON.Browser.name] = true;
    COMMON.Browser[COMMON.Browser.name + parseInt(COMMON.Browser.version, 10)] = true;
    COMMON.Browser.Platform[COMMON.Browser.Platform.name] = true;
    //COMMON.Browser.Platform.isMobile = (COMMON.Browser.Platform.name.match(/(?:ios|webos|android|windows\sphone)/) || COMMON.Browser.chrome || COMMON.Browser.safari || COMMON.Browser.firefox) ? true : false;
    COMMON.Browser.Platform.isMobile = !!(COMMON.Browser.Platform.name.match(/(?:ios|webos|android|windows\sphone)/) );

    // Request

    COMMON.Browser.Request = (function(){

        var XMLHTTP = function(){
            return new XMLHttpRequest();
        };

        var MSXML2 = function(){
            return new ActiveXObject('MSXML2.XMLHTTP');
        };

        var MSXML = function(){
            return new ActiveXObject('Microsoft.XMLHTTP');
        };

        return Function.attempt(function(){
            XMLHTTP();
            return XMLHTTP;
        }, function(){
            MSXML2();
            return MSXML2;
        }, function(){
            MSXML();
            return MSXML;
        });

    })();

    COMMON.Browser.Features.xhr = !!(COMMON.Browser.Request);

    // Flash detection

    var version = (Function.attempt(function(){
        return navigator.plugins['Shockwave Flash'].description;
    }, function(){
        return new ActiveXObject('ShockwaveFlash.ShockwaveFlash').GetVariable('$version');
    }) || '0 r0').match(/\d+/g);

    COMMON.Browser.Plugins.Flash = {
        version: Number(version[0] || '0.' + version[1]) || 0,
        build: Number(version[2]) || 0
    };

    // String scripts

    COMMON.Browser.exec = function(text){
        if (!text) return text;
        if (window.execScript){
            window.execScript(text);
        } else {
            var script = document.createElement('script');
            script.setAttribute('type', 'text/javascript');
            script.text = text;
            document.head.appendChild(script);
            document.head.removeChild(script);
        }
        return text;
    };

    COMMON.typeOf = function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();
        if (item.constructor == window.Array) return "array";

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
            //if ('item' in item) return 'collection';
        }

        return typeof item;
    };
})();

if (COMMON.Browser.name=="ie" && COMMON.Browser.version<10){
    COMMON["debugger"] = true;
}

COMMON.Class = {
    create: function(options) {
        var newClass = function() {
            this.initialize.apply(this, arguments);
        };
        COMMON.copyPrototype(newClass, options);
        return newClass;
    }
};
COMMON.copyPrototype = function (currentNS,props){
    if (!props){return currentNS;}
    if (!currentNS){return currentNS;}
    if ((typeof currentNS).toLowerCase()=="object"){
        for (var prop in props){
            currentNS[prop] = props[prop];
        }
    }
    if ((typeof currentNS).toLowerCase()=="function"){
        for (var prop in props){
            currentNS.prototype[prop] = props[prop];
        }
    }
    return currentNS;
};

if ((typeof JSON) == 'undefined'){
    COMMON.JSON = {};
}else{
    COMMON.JSON = JSON;
}
(function(){
    var special = {'\b': '\\b', '\t': '\\t', '\n': '\\n', '\f': '\\f', '\r': '\\r', '"' : '\\"', '\\': '\\\\'};

    var escape = function(chr){
        return special[chr] || '\\u' + ('0000' + chr.charCodeAt(0).toString(16)).slice(-4);
    };

    COMMON.JSON.validate = function(string){
        string = string.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').replace(/(?:^|:|,)(?:\s*\[)+/g, '');

        return (/^[\],:{}\s]*$/).test(string);
    };

    COMMON.JSON.encode = COMMON.JSON.stringify ? function(obj){
        return COMMON.JSON.stringify(obj);
    } : function(obj){
        if (obj && obj.toJSON) obj = obj.toJSON();
        switch (COMMON.typeOf(obj)){
            case 'string':
                return '"' + obj.replace(/[\x00-\x1f\\"]/g, escape) + '"';
            case 'array':
                var string = [];
                for (var i=0; i<obj.length; i++){
                    var json = COMMON.JSON.encode(obj[i]);
                    if (json) string.push(json);
                }
                return '[' + string + ']';
            case 'object': case 'hash':
            var string = [];
            for (key in obj){
                var json = COMMON.JSON.encode(obj[key]);
                if (json) string.push(COMMON.JSON.encode(key) + ':' + json);
            }
            return '{' + string + '}';
            case 'number': case 'boolean': return '' + obj;
            case 'null': return 'null';
        }

        return null;
    };


    COMMON.JSON.decode = function(string, secure){
        if (!string || COMMON.typeOf(string) != 'string') return null;

        if (secure || COMMON.JSON.secure){
            if (COMMON.JSON.parse) return COMMON.JSON.parse(string);
            if (!COMMON.JSON.validate(string)) throw new Error('JSON could not decode the input; security is enabled and the value is not secure.');
        }
        return eval('(' + string + ')');
    };

    COMMON.JSON.get = function(url, callback, async){
        var jsonObj = null;
        var r = new COMMON.Request({
            url: url,
            async: (async==false) ? false : true,
            method: "get",
            onSuccess: function(responseText){
                jsonObj = COMMON.JSON.decode(responseText);
                if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                    callback(jsonObj);
                }else{
                    COMMON.runCallback(callback, "onSuccess", [jsonObj]);
                }
            },
            onFailure: function(xhr){
                COMMON.runCallback(callback, "onRequestFailure", [xhr]);
            }
        });
        r.send();
        return jsonObj;
    };

})();

COMMON.XML = COMMON.Class.create({
    initialize: function(xml){
        this.xml = xml || null;
        this.request = null;
    },
    get: function(url, callback, async){
        var _self = this;
        this.request = new COMMON.Request({
            url: url,
            async: (async!==false),
            method: "get",
            onSuccess: function(responseText, responseXML){
                _self.xml = responseXML;
                if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                    callback.apply(_self, [_self]);
                }else{
                    COMMON.runCallback(callback, "onSuccess", [_self]);
                }
            },
            onFailure: function(xhr){
                COMMON.runCallback(callback, "onRequestFailure", [_self]);
            }
        });
        this.request.send();
        return this;
    },
    queryNode: function(xpath){
        if (COMMON.Browser.Features.xpath){
            var xpathResult = this.xml.evaluate(xpath,this.xml,null,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,null);
            if (xpathResult.snapshotLength){
                return new COMMON.XML.Element(xpathResult.snapshotItem(0));
            }else{
                return null;
            }
        }else{
            try {
                return new COMMON.XML.Element(this.xml.selectSingleNode(xpath));
            }catch(e){
                try {
                    xpath = xpath.replace(/\//g, ">");
                    xpath = xpath.replace(/\\/g, ">");
                    return new COMMON.XML.Element(this.xml.querySelector(xpath));
                }catch(e){
                    return null;
                }
            }
        }
    },

    queryNodes: function(xpath){
        if (COMMON.Browser.Features.xpath){
            var xpathResult = this.xml.evaluate(xpath,this.xml,null,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,null);
            if (xpathResult.snapshotLength){
                return new COMMON.XML.Elements(xpathResult);
            }else{
                return null;
            }
        }else{
            try {
                return new COMMON.XML.Elements(this.xml.selectNodes(xpath));
            }catch(e){
                var nodes = [];
                xpath = xpath.replace(/\//g, ">");
                xpath = xpath.replace(/\\/g, ">");
                var firstNode = this.xml.querySelector(xpath);
                if (firstNode) nodes.push(firstNode);
                var nextNode = firstNode.nextSibling;
                while (nextNode){
                    if (nextNode.tagName == xpath){
                        nodes.push(nextNode);
                    }
                    nextNode = nextNode.nextSibling;
                }
                return new COMMON.XML.Elements(nodes);
            }
        }
    }
});
COMMON.XML.Element = COMMON.Class.create({
    initialize: function(node){
        this.node = node;
        if (this.node){
            for (p in this.node){
                this[p] = this.node[p];
            }
        }
    },
    queryNode: function(xpath){
        if (COMMON.Browser.Features.xpath){
            var xpathResult = this.node.ownerDocument.evaluate(xpath,this.node,null,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,null);
            if (xpathResult.snapshotLength){
                return new COMMON.XML.Element(xpathResult.snapshotItem(0));
            }else{
                return null;
            }
        }else{
            try {
                return new COMMON.XML.Element(this.node.selectSingleNode(xpath));
            }catch(e){
                try {
                    xpath = xpath.replace(/\//g, ">");
                    xpath = xpath.replace(/\\/g, ">");
                    return new COMMON.XML.Element(this.node.querySelector(xpath));
                }catch(e){
                    return null;
                }
            }
        }
    },

    queryNodes: function(xpath){
        if (COMMON.Browser.Features.xpath){
            var xpathResult = this.node.ownerDocument.evaluate(xpath,this.node,null,XPathResult.ORDERED_NODE_SNAPSHOT_TYPE,null);
            if (xpathResult.snapshotLength){
                return new COMMON.XML.Elements(xpathResult);
            }else{
                return null;
            }
        }else{
            try {
                return new COMMON.XML.Elements(this.node.selectNodes(xpath));
            }catch(e){
                var nodes = [];
                xpath = xpath.replace(/\//g, ">");
                xpath = xpath.replace(/\\/g, ">");
                var firstNode = this.node.querySelector(xpath);

                if (firstNode){
                    nodes.push(firstNode);
                    var nextNode = firstNode.nextSibling;

                    while (nextNode){
                        if (nextNode.tagName == xpath){
                            nodes.push(nextNode);
                        }
                        nextNode = nextNode.nextSibling;
                    }
                }
                return new COMMON.XML.Elements(nodes);
            }
        }
    },
    text: function(){
        try{
            var text = this.node.textContent;
            if (text){
                return text;
            }else{
                try{
                    return this.node.text;
                }catch(e){
                    return text;
                }
            }
        }catch(e){
            try{
                return this.node.text;
            }catch(e){
                return "";
            }
        }
    },
    attr: function(name){
        return this.node.getAttribute(name);
    }

});
COMMON.XML.Elements = COMMON.Class.create({
    initialize: function(nodes){
        this.nodes = nodes;
    },
    length: function(){
        if (this.nodes.snapshotLength){
            return this.nodes.snapshotLength;
        }else{
            return this.nodes.length;
        }
    },
    items: function(i){
        if (this.nodes.snapshotItem){
            return new COMMON.XML.Element(this.nodes.snapshotItem(i));
        }else{
            try {
                return new COMMON.XML.Element(this.nodes.item(i));
            }catch(e){
                try {
                    return new COMMON.XML.Element(this.nodes[i]);
                }catch(e){
                    return null;
                }
            }
        }
    },
    each: function(fun){
        if (this.length()){
            for (var i=0; i<this.length(); i++){
                fun.apply(this, [this.items(i), i]);
            }
        }
    }
});

COMMON.Request = function(o){
    this.url = o.url;
    if (!o.cache){
        if (this.url.indexOf("?")==-1){
            this.url = this.url + "?d=" + new String((new Date()).getTime());
        }else{
            this.url = this.url + "&d=" + new String((new Date()).getTime());
        }
    }

    this.async = (o.async!==false);
    this.method = o.method;
    this.onSuccess = o.onSuccess;
    this.onFailure = o.onFailure;
    this.data = o.data;
    this.xhr = new COMMON.Browser.Request();
    this.headers = {
        'X-Requested-With': 'XMLHttpRequest',
        'Accept': 'text/javascript, text/html, application/xml, text/xml, */*'
    };
    this.encoding = 'utf-8';
};
COMMON.Request.prototype.send = function(data){
    var xhr = this.xhr;

    //if (!this.async) alert(this.url);

    xhr.open(this.method.toUpperCase(), this.url, this.async);
    var req = this;

    if (this.method.toLowerCase() == 'post'){
        var encoding = (this.encoding) ? '; charset=' + this.encoding : '';
        this.headers['Content-type'] = 'application/x-www-form-urlencoded' + encoding;
    }
    for (key in this.headers){
        try {
            xhr.setRequestHeader(key, this.headers[key]);
        } catch (e){}
    }

    var onreadystatechange= function(){
        if (xhr.readyState != 4) return;
        //xhr.onreadystatechange = null;

        var status = xhr.status;
        status = (status == 1223) ? 204 : status;

        req.response = {text: xhr.responseText || '', xml: xhr.responseXML};

        if ((status >= 200 && status < 300))
            req.success(req.response.text, req.response.xml);
        else if ((status >= 300 && status < 400))
            req.redirect(req);
        else
            req.failure(xhr);

        req.async = true;
    };
    xhr.onreadystatechange = onreadystatechange;

    var rdata = data || this.data;

    xhr.send(rdata);
    //if (!this.async) onreadystatechange();
};

COMMON.Request.prototype.processScripts = function(text){
    //if ((/(ecma|java)script/).test(this.getHeader('Content-type'))) return Browser.exec(text);
    return text;
};

COMMON.Request.prototype.success = function(text, xml){
    var t = this.processScripts(text);
    if (this.onSuccess) this.onSuccess(t, xml);
};

COMMON.Request.prototype.failure = function(xhr){
    if (this.onFailure) this.onFailure(xhr);
};
COMMON.Request.prototype.redirect = function(xhr){
    if (this.onRedirect) this.onRedirect(xhr);
};

COMMON.Request.prototype.getHeader = function(name){
    return this.xhr.getResponseHeader(name);
};
COMMON.Request.prototype.setHeader = function(name, value){
    this.headers[name] = value;
    return this;
};
COMMON.runCallback = function(callback, name, par){
    if (COMMON.typeOf(callback).toLowerCase()=='object'){
        if (callback[name]){
            callback[name].apply(callback, ((par) ? par : []));
        }
    }
};
COMMON.Request.prototype.getText = function(url){
    var v = "";
    var r = new COMMON.Request({
        "url": url,
        "async": false,
        "method": "get",
        "onSuccess": function(text){
            v = text;
        }
    }).send();
    return v;
};

COMMON.AjaxModule = {
    "jquery": COMMON.contentPath+"/res/framework/jquery/jquery.min.js",
    "jquery-ui": COMMON.contentPath+"/res/framework/jquery/jquery-ui.custom/js/jquery-ui.custom.min.js",
    "mootools": COMMON.contentPath+"/res/framework/mootools/mootools-1.6.0.js",
    "mootools-more": COMMON.contentPath+"/res/framework/mootools/mootools-1.6.0.js",
    "mwf": COMMON.contentPath+"/res/mwf4/MWF.min.js",
    "ckeditor": COMMON.contentPath+"/res/framework/htmleditor/ckeditor451/ckeditor.js",
    "nicEdit": COMMON.contentPath+"/res/framework/htmleditor/nicEdit/nicEdit.js",
    "kindeditor": COMMON.contentPath+"/res/framework/htmleditor/kindeditor/kindeditor-all-min.js",
    "tinymce": COMMON.contentPath+"/res/framework/htmleditor/tinymce/tinymce.min.js",
    "bmap": "http://api.map.baidu.com/api?v=2.0&ak=fpzGqedB7e8CA8WB8jUgxlOx",
    "amap": "http://webapi.amap.com/maps?v=1.3&key=d7c7ce03a219fa4df4c7a30ba8e68888",
    "raphael": COMMON.contentPath+"/res/framework/raphael/raphael.js",

    "codemirror": COMMON.contentPath+"/res/framework/codemirror/lib/codemirror.js",
    "codemirror_javascript": COMMON.contentPath+"/res/framework/codemirror/mode/javascript/javascript.js",

    "d3": COMMON.contentPath+"/res/framework/d3/d3.min.js",

    "ace": COMMON.contentPath+"/res/framework/ace/src-min-noconflict/ace.js",
    "ace-tools": COMMON.contentPath+"/res/framework/ace/src-min-noconflict/ext-language_tools.js",
    "kity": COMMON.contentPath+"/res/framework/kityminder/kity/kity.min.js",
    "kityminder": COMMON.contentPath+"/res/framework/kityminder/core/dist/kityminder.core.js",
    "JSONTemplate": COMMON.contentPath+"/res/framework/mootools/plugin/Template.js",
    "ie_adapter": COMMON.contentPath+"/res/mwf4/ie_adapter.js",
    //"loaded": [],
    //"loadedCss": [],

    init: function(){
        this["jquery"] = COMMON.contentPath+"/res/framework/jquery/jquery.min.js";
        this["jquery-ui"] = COMMON.contentPath+"/res/framework/jquery/jquery-ui.custom/js/jquery-ui.custom.min.js";
        this["mootools"] = COMMON.contentPath+"/res/framework/mootools/mootools-1.6.0.js";
        this["mootools-more"] = COMMON.contentPath+"/res/framework/mootools/mootools-1.6.0.js";
        this["mwf"] = COMMON.contentPath+"/res/mwf4/MWF.js";

        this["ckeditor"] = COMMON.contentPath+"/res/framework/htmleditor/ckeditor451/ckeditor.js";
        this["nicEdit"] = COMMON.contentPath+"/res/framework/htmleditor/nicEdit/nicEdit.js";
        this["kindeditor"] = COMMON.contentPath+"/res/framework/htmleditor/kindeditor/kindeditor.js";
        this["tinymce"] = COMMON.contentPath+"/res/framework/htmleditor/tinymce/tinymce.full.js";

        this["bmap"] = "http://api.map.baidu.com/api?v=2.0&ak=fpzGqedB7e8CA8WB8jUgxlOx";
        this["amap"] = "http://webapi.amap.com/maps?v=1.3&key=d7c7ce03a219fa4df4c7a30ba8e68888";
        this["raphael"] = COMMON.contentPath+"/res/framework/raphael/raphael.js";

        this["codemirror"] = COMMON.contentPath+"/res/framework/codemirror/lib/codemirror.js";
        this["codemirror_javascript"] = COMMON.contentPath+"/res/framework/codemirror/mode/javascript/javascript.js";

        this["d3"] = COMMON.contentPath+"/res/framework/d3/d3.min.js";

        this["ace"] = COMMON.contentPath+"/res/framework/ace/src-min-noconflict/ace.js";
        this["ace-tools"] = COMMON.contentPath+"/res/framework/ace/src-min-noconflict/ext-language_tools.js";

        this["kity"] =  COMMON.contentPath+"/res/framework/kityminder/kity/kity.min.js";
        this["kityminder"] =  COMMON.contentPath+"/res/framework/kityminder/core/dist/kityminder.core.js";
        this["JSONTemplate"] = COMMON.contentPath+"/res/framework/mootools/plugin/Template.js";
        this["ie_adapter"] = COMMON.contentPath+"/res/mwf4/ie_adapter.js";

    },
    "loaded": {},
    "loadedCss": {},

    loadDom: function(urls, callback, async, reload){
        if (COMMON.typeOf(urls)==="array"){
            var thisLoaded = [];
            urls.each(function(url){
                this.loadDomSingle(url, function(){
                    thisLoaded.push(url);
                    if (thisLoaded.length===urls.length){
                        if (COMMON.typeOf(callback).toLowerCase() === 'function'){
                            callback();
                        }else{
                            COMMON.runCallback(callback, "onSuccess");
                        }
                    }
                }, async, reload);
            }.bind(this));
        }
        if (COMMON.typeOf(urls)==="string"){
            this.loadDomSingle(urls, callback, async, reload);
        }
    },

    loadDomSingle: function(url, callback, async, reload){
        //var jsurl = this[url] || url;
        var jsurl = this[url];
        if (!jsurl){
            jsurl = url;
            if (!COMMON["debugger"]){
                jsurl = jsurl.replace(/\.js/, ".min.js");
            }
        }
        jsurl = (jsurl.indexOf("?")!==-1) ? jsurl+"&v="+COMMON.version : jsurl+"?v="+COMMON.version;

        if (!reload){
            if (this.loaded[key]){
                if (COMMON.typeOf(callback).toLowerCase() === 'function'){
                    callback();
                }else{
                    COMMON.runCallback(callback, "onSuccess");
                }
                return;
            }
        }
        var key = encodeURIComponent(url);

        var head = (document.head || document.getElementsByTagName("head")[0] || document.documentElement);
        var s = document.createElement('script');

        s.src = jsurl;
        head.appendChild(s);
        s.onload = s.onreadystatechange = function(_, isAbort) {
            if (isAbort || !s.readyState || s.readyState === "loaded" || s.readyState === "complete") {
                COMMON.AjaxModule.loaded[key] = true;
                s = s.onload = s.onreadystatechange = null;
                if (!isAbort)
                    callback();
            }
        };
    },

    load: function(urls, callback, async, reload){
        if (COMMON.typeOf(urls)==="array"){
            var thisLoaded = [];
            urls.each(function(url){
                this.loadSingle(url, function(){
                    thisLoaded.push(url);
                    if (thisLoaded.length===urls.length){
                        if (COMMON.typeOf(callback).toLowerCase() === 'function'){
                            callback();
                        }else{
                            COMMON.runCallback(callback, "onSuccess");
                        }
                    }
                }, async, reload);
            }.bind(this));
        }
        if (COMMON.typeOf(urls)==="string"){
            this.loadSingle(urls, callback, async, reload);
        }
    },

    loadSingle: function(url, callback, async, reload){
        var jsurl = this[url];
        if (!jsurl){
            jsurl = url;
            if (!COMMON["debugger"]){
                jsurl = jsurl.replace(/\.js/, ".min.js");
            }
        }
        jsurl = (jsurl.indexOf("?")!=-1) ? jsurl+"&v="+COMMON.version : jsurl+"?v="+COMMON.version;
        var key = encodeURIComponent(url);
        if (!reload){
            if (this.loaded[key]){
                if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                    callback();
                }else{
                    COMMON.runCallback(callback, "onSuccess");
                }
                return;
            }
        }

        var loadAsync = true;
        if (async===false){
            loadAsync = false;
        }

        var r = new COMMON.Request({
            url: jsurl,
            async: loadAsync,
            method: "get",
            cache: true,
            onSuccess: function(responseText, responseXML){
                var jsText = responseText;
                try{
                    COMMON.Browser.exec(jsText);
                    //	COMMON.AjaxModule.loaded.push(url);
                    COMMON.AjaxModule.loaded[key] = true;
                }catch (e){
                    COMMON.runCallback(callback, "onFailure", e);
                    return;
                }
                if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                    callback();
                }else{
                    COMMON.runCallback(callback, "onSuccess");
                }

            },
            onFailure: function(xhr){
                COMMON.runCallback(callback, "onRequestFailure", xhr);
            }
        });
        r.send();
    },
    loadCss: function(urls, callback, async, reload, sourceDoc){
        if (COMMON.typeOf(urls)=="array"){
            var thisLoaded = [];
            urls.each(function(url){
                this.loadSingleCss(url, function(){
                    thisLoaded.push(url);
                    if (thisLoaded.length==urls.length){
                        if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                            callback();
                        }else{
                            COMMON.runCallback(callback, "onSuccess");
                        }
                    }
                }, async, reload);
            }.bind(this));
        }
        if (COMMON.typeOf(urls)=="string"){
            this.loadSingleCss(urls, callback, async, reload);
        }
    },

    loadSingleCss: function(url, callback, async, reload, sourceDoc){
        var key = encodeURIComponent(url);
        if (!reload){
            if (this.loadedCss[key]){
                if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                    callback();
                }else{
                    COMMON.runCallback(callback, "onSuccess");
                }
                return;
            };
        }

        var async = true;
        if (async===false){
            async = false;
        }

        var cssurl = this[url] || url;
        var r = new COMMON.Request({
            url: cssurl,
            async: async,
            method: "get",
            cache: true,
            onSuccess: function(responseText, responseXML){
                var cssText = responseText;
                try{
                    if (cssText){
                        var doc = sourceDoc || document;
                        var style = doc.createElement("style");
                        style.setAttribute("type", "text/css");
                        var head = doc.getElementsByTagName("head")[0];
                        head.appendChild(style);

                        if(style.styleSheet){
                            var setFunc = function(){
                                style.styleSheet.cssText = cssText;
                            };
                            if(style.styleSheet.disabled){
                                setTimeout(setFunc, 10);
                            }else{
                                setFunc();
                            }
                        }else{
                            var cssTextNode = doc.createTextNode(cssText);
                            style.appendChild(cssTextNode);
                        }
                    }
                    COMMON.AjaxModule.loadedCss[key] = true;
                    if (COMMON.typeOf(callback).toLowerCase() == 'function'){
                        callback();
                    }else{
                        COMMON.runCallback(callback, "onSuccess");
                    }
                }catch (e){
                    COMMON.runCallback(callback, "onFailure", e);
                    return;
                }
            },
            onFailure: function(xhr){
                COMMON.runCallback(callback, "onRequestFailure", xhr);
            }
        });
        r.send();
    }

};
COMMON.onReady = function(){
    return true;
};
COMMON.DOM = {
    ready: false,
    loaded: false,
    checks: [],
    shouldPoll: false,
    timer: null,
    testElement: document.createElement('div'),
    readys: [],

    domready: function(){
        clearTimeout(this.timer);
        if (COMMON.DOM.ready) return;
        COMMON.DOM.loaded = COMMON.DOM.ready = true;
        COMMON.DOM.removeListener(document, 'DOMContentLoaded', COMMON.DOM.domready);
        COMMON.DOM.removeListener(document, 'readystatechange', COMMON.DOM.check);
        COMMON.DOM.onReady();
    },
    check: function(){
        for (var i = COMMON.DOM.checks.length; i--;) if (COMMON.DOM.checks[i]()){
            COMMON.DOM.domready();
            return true;
        }
        return false;
    },
    poll: function(){
        clearTimeout(COMMON.DOM.timer);
        if (!COMMON.DOM.check()) COMMON.DOM.timer = setTimeout(COMMON.DOM.poll, 10);
    },

    /*<ltIE8>*/
    // doScroll technique by Diego Perini http://javascript.nwbox.com/IEContentLoaded/
    // testElement.doScroll() throws when the DOM is not ready, only in the top window
    doScrollWorks: function(){
        try {
            this.testElement.doScroll();
            return true;
        } catch (e){}
        return false;
    },
    /*</ltIE8>*/
    addListener: function(dom, type, fn){
        if (type == 'unload'){
            var old = fn, self = this;
            fn = function(){
                self.removeListener(dom, 'unload', fn);
                old();
            };
        }
        if (dom.addEventListener) dom.addEventListener(type, fn, !!arguments[2]);
        else dom.attachEvent('on' + type, fn);
    },
    removeListener: function(dom, type, fn){
        if (dom.removeEventListener) dom.removeEventListener(type, fn, !!arguments[2]);
        else dom.detachEvent('on' + type, fn);
        return this;
    },
    onReady: function(){
        for (var i=0; i<COMMON.DOM.readys.length; i++){
            COMMON.DOM.readys[i].apply(COMMON);
        }
    },
    addReady: function(fn){
        if (COMMON.DOM.loaded){
            if (fn) fn.apply(COMMON);
        }else{
            if (fn) COMMON.DOM.readys.push(fn);
        }
        return COMMON.DOM;
    }
};

(function(window, document){
    COMMON.DOM.addListener(document, 'DOMContentLoaded', COMMON.DOM.domready);

    /*<ltIE8>*/
    // If doScroll works already, it can't be used to determine domready
    //   e.g. in an iframe
    if (COMMON.DOM.testElement.doScroll && !COMMON.DOM.doScrollWorks()){
        COMMON.DOM.checks.push(COMMON.DOM.doScrollWorks);
        COMMON.DOM.shouldPoll = true;
    }
    /*</ltIE8>*/

    if (document.readyState) COMMON.DOM.checks.push(function(){
        var state = document.readyState;
        return (state == 'loaded' || state == 'complete');
    });

    if ('onreadystatechange' in document) COMMON.DOM.addListener(document, 'readystatechange', COMMON.DOM.check);
    else COMMON.DOM.shouldPoll = true;

    if (COMMON.DOM.shouldPoll) COMMON.DOM.poll();

})(window, document);

(function(){
    Array.prototype.arrayIndexOf = function (o){
        for (var i=0; i<this.length; i++){
            if (this[i]==o){
                return i
            }
        }
        return null;
    };
    Array.prototype.arrayLastIndexOf = function (o){
        for (var i=this.length-1; i>=0; i--){
            if (this[i]==o){
                return i
            }
        }
        return null;
    };
    Array.prototype.arrayInStr = function (str){
        var returnArray = [];
        if (str.toString()!=""){
            for (var i=0; i<this.length; i++){
                if (this[i].toString().indexOf(str.toString())!=-1){
                    returnArray.push(this[i])
                }
            }
        }
        return returnArray;
    };
    Array.prototype.isIntersection = function (arr){
        if (arr){
            for (var i=0; i<arr.length; i++){
                if (this.arrayIndexOf(arr[i])!=null){
                    return true;
                }
            }
        }
        return false;
    };
})();
COMMON.getUserCN = function(name){
    var userName = name;
    userName = userName.substr(3, userName.length);
    userName = userName.substr(0,userName.indexOf("/"));
    return userName;
};
COMMON.getNowDateString = function (){
    var now = new Date();
    m = now.getMonth()+1;
    return now.getYear()+"年"+m+"月"+now.getDate()+"日 "+now.getHours()+":"+now.getMinutes()+":"+now.getSeconds();
};