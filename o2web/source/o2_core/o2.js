/** ***** BEGIN LICENSE BLOCK *****
 * |------------------------------------------------------------------------------|
 * | O2OA 活力办公 创意无限    o2.js                                                 |
 * |------------------------------------------------------------------------------|
 * | Distributed under the AGPL license:                                          |
 * |------------------------------------------------------------------------------|
 * | Copyright © 2018, o2oa.net, o2server.io O2 Team                              |
 * | All rights reserved.                                                         |
 * |------------------------------------------------------------------------------|
 *
 *  This file is part of O2OA.
 *
 *  O2OA is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  O2OA is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ******/


/* load o2 Core
 * |------------------------------------------------------------------------------|
 * |addReady:     o2.addReady(fn),                                                |
 * |------------------------------------------------------------------------------|
 * |load:         o2.load(urls, callback, reload)                                 |
 * |loadCss:      o2.loadCss(urls, dom, callback, reload, doc)                    |
 * |------------------------------------------------------------------------------|
 * |typeOf:       o2.typeOf(o)                                                    |
 * |------------------------------------------------------------------------------|
 * |uuid:         o2.uuid()                                                       |
 * |------------------------------------------------------------------------------|
 */
(function(){
    var _href = window.location.href;
    var _debug = (_href.indexOf("debugger")!==-1);
    var _par = _href.substr(_href.lastIndexOf("?")+1, _href.length);
    var _lp = "zh-cn";
    if (_par){
        var _parList = _par.split("&");
        for (var i=0; i<_parList.length; i++){
            var _v = _parList[i];
            var _kv = _v.split("=");
            if (_kv[0].toLowerCase()==="lg") _lp = _kv[1];
        }
    }
    this.o2 = {
        "version": {
            "v": '2.0.0',
            "build": "2018.11.22",
            "info": "O2OA 活力办公 创意无限. Copyright © 2018, o2oa.net O2 Team All rights reserved."
        },
        "session": {
            "isDebugger": _debug,
            "path": "/o2_core/o2"
        },
        "language": _lp,
        "splitStr": /(,\s*){1}|(;\s*){1}/g
    };
    
    var _attempt = function(){
        for (var i = 0, l = arguments.length; i < l; i++){
            try {
                arguments[i]();
                return arguments[i];
            } catch (e){}
        }
        return null;
    };
    var _typeOf = function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();
        if (item.constructor == window.Array) return "array";

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
        }
        return typeof item;
    };
    this.o2.typeOf = _typeOf;

    var _addListener = function(dom, type, fn){
        if (type == 'unload'){
            var old = fn, self = this;
            fn = function(){
                _removeListener(dom, 'unload', fn);
                old();
            };
        }
        if (dom.addEventListener) dom.addEventListener(type, fn, !!arguments[2]);
        else dom.attachEvent('on' + type, fn);
    };
    var _removeListener = function(dom, type, fn){
        if (dom.removeEventListener) dom.removeEventListener(type, fn, !!arguments[2]);
        else dom.detachEvent('on' + type, fn);
    };

    //http request class
    var _request = (function(){
        var XMLHTTP = function(){ return new XMLHttpRequest(); };
        var MSXML2 = function(){ return new ActiveXObject('MSXML2.XMLHTTP'); };
        var MSXML = function(){ return new ActiveXObject('Microsoft.XMLHTTP'); };
        return _attempt(XMLHTTP, MSXML2, MSXML);
    })();

    var _returnBase = function(number, base) {
        return (number).toString(base).toUpperCase();
    };
    var _getIntegerBits = function(val, start, end){
        var base16 = _returnBase(val, 16);
        var quadArray = new Array();
        var quadString = '';
        var i = 0;
        for (i = 0; i < base16.length; i++) {
            quadArray.push(base16.substring(i, i + 1));
        }
        for (i = Math.floor(start / 4); i <= Math.floor(end / 4); i++) {
            if (!quadArray[i] || quadArray[i] == '')
                quadString += '0';
            else
                quadString += quadArray[i];
        }
        return quadString;
    };
    var _rand = function(max) {
        return Math.floor(Math.random() * (max + 1));
    };
    var _uuid = function(){
        var dg = new Date(1582, 10, 15, 0, 0, 0, 0);
        var dc = new Date();
        var t = dc.getTime() - dg.getTime();
        var tl = _getIntegerBits(t, 0, 31);
        var tm = _getIntegerBits(t, 32, 47);
        var thv = _getIntegerBits(t, 48, 59) + '1';
        var csar = _getIntegerBits(_rand(4095), 0, 7);
        var csl = _getIntegerBits(_rand(4095), 0, 7);

        var n = _getIntegerBits(_rand(8191), 0, 7)
            + _getIntegerBits(_rand(8191), 8, 15)
            + _getIntegerBits(_rand(8191), 0, 7)
            + _getIntegerBits(_rand(8191), 8, 15)
            + _getIntegerBits(_rand(8191), 0, 15);
        return tl + tm + thv + csar + csl + n;
    };
    this.o2.uuid = _uuid;


    var _runCallback = function(callback, key, par){
        if (typeOf(callback).toLowerCase() === 'function'){
            if (key.toLowerCase()==="success") callback.apply(callback, par);
        }else{
            if (typeOf(callback).toLowerCase()==='object'){
                var name = ("on-"+key).camelCase();
                if (callback[name]) callback[name].apply(callback, par);
            }
        }
    };
    this.o2.runCallback = _runCallback;

    //use framework url
    var _frameworks = {
        "o2.core": ["/o2_core/o2/o2.core.js"],
        "o2.more": ["/o2_core/o2/o2.more.js"],
        "ie_adapter": ["/o2_lib/o2/ie_adapter.js"],
        "jquery": ["/o2_lib/jquery/jquery.min.js"],
        "mootools": ["/o2_lib/mootools/mootools-1.6.0.js"],
        "ckeditor": ["/o2_lib/htmleditor/ckeditor/ckeditor.js"],
        "raphael": ["/o2_lib/raphael/raphael.js"],
        "d3": ["/o2_lib/d3/d3.min.js"],
        "ace": ["/o2_lib/ace/src-noconflict/ace.js","/o2_lib/ace/src-noconflict/ext-language_tools.js"],
        "JSBeautifier": ["/o2_lib/JSBeautifier/beautify.js"],
        "JSBeautifier_css": ["/o2_lib/JSBeautifier/beautify-css.js"],
        "JSBeautifier_html": ["/o2_lib/JSBeautifier/beautify-html.js"]
    };
    var _loaded = {};
    var _loadedCss = {};

    var _loadSingle = function(url, callback, reload){
        var addr_uri = _frameworks[url] || url;
        if (!_debug) if (addr_uri.indexOf("o2_lib")===-1) addr_uri = addr_uri.replace(/\.js/, ".min.js");
        addr_uri = (addr_uri.indexOf("?")!==-1) ? addr_uri+"&v="+this.o2.version.v : addr_uri+"?v="+this.o2.version.v;

        var key = encodeURIComponent(url);

        if (!reload) if (_loaded[key]){ if (callback)callback(); return; }

        var head = (document.head || document.getElementsByTagName("head")[0] || document.documentElement);
        var s = document.createElement('script');
        s.src = addr_uri;
        head.appendChild(s);

        var _checkScriptLoaded = function(_, isAbort){
            if (isAbort || !s.readyState || s.readyState === "loaded" || s.readyState === "complete") {
                _loaded[key] = true;
                _removeListener(s, 'DOMContentLoaded', _checkScriptLoaded);
                _removeListener(s, 'readystatechange', _checkScriptLoaded);
                if (!isAbort) if (callback)callback();
            }
        };

        if ('onreadystatechange' in s) _addListener(s, 'readystatechange', _checkScriptLoaded);
        _addListener(s, 'load', _checkScriptLoaded);
    };
    var _load = function(urls, callback, reload, doc){
        var urltype = _typeOf(urls);
        var modules;
        if (urltype==="array"){
            modules = [];
            for (var i=0; i<urls.length; i++){
                var url = urls[i];
                var module = _frameworks[url] || url;
                if (_typeOf(module)==="array"){
                    modules.concat(module)
                }else{
                    modules.push(url)
                }
            }
        }else{
            modules = _frameworks[urls] || urls;
        }

        var type = _typeOf(modules);
        if (type==="array"){
            var thisLoaded = [];
            for (var i=0; i<modules.length; i++){
                _loadSingle(modules[i], function(){
                    thisLoaded.push(modules[i]);
                    if (thisLoaded.length===modules.length){
                        if (callback) callback();
                    }
                }, reload, doc);
            }
        }
        if (type==="string"){
            _loadSingle(modules, callback, reload);
        }
    };
    this.o2.load = _load;

    var _loadSingleCss = function(url, callback, uuid, reload, sourceDoc){
        var key = encodeURIComponent(url);
        if (!reload) if (_loadedCss[key]){ if (callback)callback(_loadedCss[key]); return; }

        var cssurl = _frameworks[url] || url;

        var xhr = new _request();
        xhr.open("GET", cssurl, true);

        var success = function(xhr){
            var cssText = xhr.responseText;
            try{
                if (cssText){
                    if (uuid){
                        var rex = new RegExp("(.+)(?=\\{)", "g");
                        var match;
                        while ((match = rex.exec(cssText)) !== null) {
                            var prefix = "." + uuid + " ";
                            var rule = prefix + match[0];
                            cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length)
                            rex.lastIndex = rex.lastIndex + prefix.length;
                        }
                    }

                    var doc = sourceDoc || document;
                    var style = doc.createElement("style");
                    style.setAttribute("type", "text/css");
                    var head = (document.head || document.getElementsByTagName("head")[0] || document.documentElement);
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
                    style.sheet.cssRules[0].cssText = "#layout_xxx {\n" +
                        "    width: 500px;\n" +
                        "    height: 300px;\n" +
                        "    background: #FFCCBA;\n" +
                        "}"
                }
                _loadedCss[key] = style;
                if (callback) callback(style);
            }catch (e){
                if (callback) callback();
                return;
            }
        };
        var failure = function(xhr){
            if (callback) callback();
        };
        var onreadystatechange= function(){
            if (xhr.readyState != 4) return;

            var status = xhr.status;
            status = (status == 1223) ? 204 : status;
            //var response = {text: xhr.responseText || '', xml: xhr.responseXML};
            if ((status >= 200 && status < 300))
                success(xhr);
            else if ((status >= 300 && status < 400))
                failure(xhr);
            else
                failure(xhr);
        };
        _addListener(xhr, "readystatechange", onreadystatechange);
        //xhr.onreadystatechange = onreadystatechange;
        xhr.send();
    };

    var _parseDomString = function(dom, fn, sourceDoc){
        var doc = sourceDoc || document;
        var list = doc.querySelectorAll(dom);
        if (list.length) for (var i=0; i<list.length; i++) _parseDomElement(list[i], fn);
    };
    var _parseDomElement = function(dom, fn){
        if (fn) fn(dom);
    };
    var _parseDom = function(dom, fn, sourceDoc){
        var domType = _typeOf(dom);
        if (domType==="string") _parseDomString(dom, fn, sourceDoc);
        if (domType==="element") _parseDomElement(dom, fn);
        if (domType==="array") for (var i=0; i<dom.length; i++) _parseDom(dom[i], fn, sourceDoc);
    };
    var _loadCss = function(urls, dom, callback, reload, sourceDoc){
        var uuid = "";
        if (dom){
            uuid = "css"+_uuid();
            _parseDom(dom, function(node){ node.className += ((node.className) ? " "+uuid : uuid)}, sourceDoc);
        }
        modules = _frameworks[urls] || urls;
        var type = _typeOf(modules);
        if (type==="array"){
            var thisLoaded = [];
            var styleList = [];
            for (var i=0; i<modules.length; i++){
                _loadSingleCss(modules[i], function(style){
                    thisLoaded.push(modules[i]);
                    if (style) styleList.push(styleList);
                    if (thisLoaded.length===modules.length){
                        if (callback) callback(styleList);
                    }
                }, uuid, reload, sourceDoc);
            }
        }
        if (type==="string"){
            _loadSingleCss(modules, callback, uuid, reload, sourceDoc);
        }
    };
    this.o2.loadCss = _loadCss;

    var _dom = {
        ready: false,
        loaded: false,
        checks: [],
        shouldPoll: false,
        timer: null,
        testElement: document.createElement('div'),
        readys: [],

        domready: function(){
            clearTimeout(_dom.timer);
            if (_dom.ready) return;
            _dom.loaded = _dom.ready = true;
            _removeListener(document, 'DOMContentLoaded', _dom.checkReady);
            _removeListener(document, 'readystatechange', _dom.check);
            _dom.onReady();
        },
        check: function(){
            for (var i = _dom.checks.length; i--;) if (_dom.checks[i]() && window.MooTools && o2.core && o2.more){
                _dom.domready();
                return true;
            }
            return false;
        },
        poll: function(){
            clearTimeout(_dom.timer);
            if (!_dom.check()) _dom.timer = setTimeout(_dom.poll, 10);
        },

        /*<ltIE8>*/
        // doScroll technique by Diego Perini http://javascript.nwbox.com/IEContentLoaded/
        // testElement.doScroll() throws when the DOM is not ready, only in the top window
        doScrollWorks: function(){
            try {
                _dom.testElement.doScroll();
                return true;
            } catch (e){}
            return false;
        },
        /*</ltIE8>*/

        onReady: function(){
            for (var i=0; i<_dom.readys.length; i++){
                this.readys[i].apply(window);
            }
        },
        addReady: function(fn){
            if (_dom.loaded){
                if (fn) fn.apply(window);
            }else{
                if (fn) _dom.readys.push(fn);
            }
            return _dom;
        },
        checkReady: function(){
            _dom.checks.push(function(){return true});
            _dom.check();
        }
    };
    var _loadO2 = function(){
        this.o2.load("o2.core", _dom.check);
        this.o2.load("o2.more", _dom.check);
    };

    _addListener(document, 'DOMContentLoaded', _dom.checkReady);

    /*<ltIE8>*/
    // If doScroll works already, it can't be used to determine domready
    //   e.g. in an iframe
    if (_dom.testElement.doScroll && !_dom.doScrollWorks()){
        _dom.checks.push(_dom.doScrollWorks);
        _dom.shouldPoll = true;
    }
    /*</ltIE8>*/

    if (document.readyState) _dom.checks.push(function(){
        var state = document.readyState;
        return (state == 'loaded' || state == 'complete');
    });

    if ('onreadystatechange' in document) _addListener(document, 'readystatechange', _dom.check);
    else _dom.shouldPoll = true;

    if (_dom.shouldPoll) _dom.poll();

    if (!window.MooTools) this.o2.load("mootools", function(){ _loadO2(); _dom.check(); });
    this.o2.addReady = function(fn){ _dom.addReady.call(_dom, fn); };
})();
