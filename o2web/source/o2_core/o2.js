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

    var _getAllOptions = function(options){
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": (options && options.doc) || document,
            "dom": (options && options.dom) || document.body,
            "position": "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'
        }
    };
    var _getCssOptions = function(options){
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": (options && options.doc) || document,
            "dom": (options && options.dom) || null
        }
    };
    var _getJsOptions = function(options){
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": (options && options.doc) || document
        }
    };
    var _getHtmlOptions = function(options){
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": (options && options.doc) || document,
            "dom": (options && options.dom) || null,
            "position": "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'
        }
    };
    var _xhr_get = function(url, success, failure){
        var xhr = new _request();
        xhr.open("GET", url, true);

        var _checkCssLoaded= function(_, err){
            if (!(xhr.readyState == 4 || err)) return;

            _removeListener(xhr, 'readystatechange', _checkCssLoaded);
            _removeListener(xhr, 'load', _checkCssLoaded);
            _removeListener(xhr, 'error', _checkCssErrorLoaded);

            if (err) {failure(xhr); return}
            var status = xhr.status;
            status = (status == 1223) ? 204 : status;
            if ((status >= 200 && status < 300))
                success(xhr);
            else if ((status >= 300 && status < 400))
                failure(xhr);
            else
                failure(xhr);
        };
        var _checkCssErrorLoaded= function(err){ _checkCssLoaded(err) };

        if ("load" in xhr) _addListener(xhr, "load", _checkCssLoaded);
        if ("error" in xhr) _addListener(xhr, "load", _checkCssErrorLoaded);
        _addListener(xhr, "readystatechange", _checkCssLoaded);
        xhr.send();
    };

    var _loadSequence = function(ms, cb, op, n, thisLoaded, loadSingle, uuid, fun){
        loadSingle(ms[n], function(module){
            if (module) thisLoaded.push(module);
            n++;
            if (fun) fun(module);
            if (n===ms.length){
                if (cb) cb(thisLoaded);
            }else{
                _loadSequence(ms, cb, op, n, thisLoaded, loadSingle, uuid, fun);
            }
        }, op, uuid);
    };
    var _loadDisarray = function(ms, cb, op, thisLoaded, loadSingle, uuid, fun){
        var count=0;
        for (var i=0; i<ms.length; i++){
            loadSingle(ms[i], function(module){
                if (module) thisLoaded.push(module);
                count++;
                if (fun) fun(module);
                if (count===ms.length) if (cb) cb(thisLoaded);
            }, op, uuid);
        }
    };

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
    var _loadedHtml = {};

    var _loadSingle = function(module, callback, op){
        var url = module;
        var uuid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uuid : addr_uri+"?v="+uuid;
        var key = encodeURIComponent(url);
        if (!op.reload) if (_loaded[key]){ if (callback)callback(); return; }

        var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
        var s = op.doc.createElement('script');
        head.appendChild(s);
        s.id = uuid;
        s.src = url;

        var _checkScriptLoaded = function(_, isAbort, err){
            if (isAbort || !s.readyState || s.readyState === "loaded" || s.readyState === "complete") {
                var scriptObj = {"module": module, "id": uuid, "script": s, "doc": op.doc};
                if (!err) _loaded[key] = scriptObj;
                _removeListener(s, 'readystatechange', _checkScriptLoaded);
                _removeListener(s, 'load', _checkScriptLoaded);
                _removeListener(s, 'error', _checkScriptErrorLoaded);
                if (!isAbort || err){
                    if (err){
                        if (s) head.removeChild(s);
                        if (callback)callback();
                    }else{
                        head.removeChild(s);
                        if (callback)callback(scriptObj);
                    }
                }
            }
        };
        var _checkScriptErrorLoaded = function(e, err){
            console.log("Error: load javascript module: "+module);
            _checkScriptLoaded(e, true, "error");
        };

        if ('onreadystatechange' in s) _addListener(s, 'readystatechange', _checkScriptLoaded);
        _addListener(s, 'load', _checkScriptLoaded);
        _addListener(s, 'error', _checkScriptErrorLoaded);
    };

    var _load = function(urls, options, callback){
        var ms = (_typeOf(urls)==="array") ? urls : [urls];
        var op =  (_typeOf(options)==="object") ? _getJsOptions(options) : _getJsOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var modules = [];
        for (var i=0; i<ms.length; i++){
            var url = ms[i];
            var module = _frameworks[url] || url;
            if (_typeOf(module)==="array"){
                modules = modules.concat(module)
            }else{
                modules.push(module)
            }
        }
        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(modules, cb, op, 0, thisLoaded, _loadSingle);
        }else{
            _loadDisarray(modules, cb, op, thisLoaded, _loadSingle);
        }
    };
    this.o2.load = _load;

    var _loadSingleCss = function(module, callback, op, uuid){
        var url = module;
        var uid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uid : url+"?v="+uid;

        var key = encodeURIComponent(url);
        if (!op.reload) if (_loadedCss[key]){ if (callback)callback(_loadedCss[key]); return; }

        var success = function(xhr){
            var cssText = xhr.responseText;
            try{
                if (cssText){
                    if (op.dom){
                        var rex = new RegExp("(.+)(?=\\{)", "g");
                        var match;
                        while ((match = rex.exec(cssText)) !== null) {
                            var prefix = "." + uuid + " ";
                            var rule = prefix + match[0];
                            cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                            rex.lastIndex = rex.lastIndex + prefix.length;
                        }
                    }
                    var style = op.doc.createElement("style");
                    style.setAttribute("type", "text/css");
                    var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
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
                        var cssTextNode = op.doc.createTextNode(cssText);
                        style.appendChild(cssTextNode);
                    }
                }
                style.id = uid;
                var styleObj = {"module": module, "id": uid, "style": style, "doc": op.doc};
                _loadedCss[key] = styleObj;
                if (callback) callback(styleObj);
            }catch (e){
                if (callback) callback();
                return;
            }
        };
        var failure = function(xhr){
            console.log("Error: load css module: "+module);
            if (callback) callback();
        };
        _xhr_get(url, success, failure);
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
    var _loadCss = function(modules, options, callback){
        var ms = (_typeOf(modules)==="array") ? modules : [modules];
        var op =  (_typeOf(options)==="object") ? _getCssOptions(options) : _getCssOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var uuid = "css"+_uuid();
        if (op.dom) _parseDom(op.dom, function(node){ node.className += ((node.className) ? " "+uuid : uuid)}, op.doc);

        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleCss, uuid);
        }else{
            _loadDisarray(ms, cb, op, thisLoaded, _loadSingleCss, uuid);
        }
    };
    var _removeCss = function(module){
        var k = encodeURIComponent(module);
        var removeCss = _loadedCss[k];
        if (!removeCss) for (key in _loadedCss){
            if (_loadedCss[key].id==module){
                removeCss = _loadedCss[key];
                k = key;
                break;
            }
        }
        if (removeCss){
            delete _loadedCss[k];
            var styleNode = removeCss.doc.getElementById(removeCss.id);
            if (styleNode) styleNode.parentNode.removeChild(styleNode);
            removeCss = null;
        }
    };
    this.o2.loadCss = _loadCss;
    this.o2.removeCss = _removeCss;
    Element.prototype.loadCss = function(modules, options, callback){
        var op =  (_typeOf(options)==="object") ? options : {};
        var cb = (_typeOf(options)==="function") ? options : callback;
        op.dom = this;
        _loadCss(modules, op, cb);
    };

    _loadSingleHtml = function(module, callback, op){
        var url = module;
        var uid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uid : url+"?v="+uid;
        var key = encodeURIComponent(url);
        if (!op.reload) if (_loadedHtml[key]){ if (callback)callback(_loadedHtml[key]); return; }

        var success = function(xhr){
            var htmlObj = {"module": module, "id": uid, "data": xhr.responseText, "doc": op.doc};
            _loadedHtml[key] = htmlObj;
            if (callback) callback(htmlObj);
        };
        var failure = function(){
            console.log("Error: load html module: "+module);
            if (callback) callback();
        };
        _xhr_get(url, success, failure);
    };

    var _injectHtml = function(op, data){
        if (op.dom) _parseDom(op.dom, function(node){ node.insertAdjacentHTML(op.position, data) }, op.doc);
    };
    var _loadHtml = function(modules, options, callback){
        var ms = (_typeOf(modules)==="array") ? modules : [modules];
        var op =  (_typeOf(options)==="object") ? _getHtmlOptions(options) : _getHtmlOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleHtml, null, function(html){ if (html) _injectHtml(op, html.data ); });
        }else{
            _loadDisarray(ms, cb, op, thisLoaded, _loadSingleHtml, null, function(html){ if (html) _injectHtml(op, html.data ); });
        }
    };
    this.o2.loadHtml = _loadHtml;
    Element.prototype.loadHtml = function(modules, options, callback){
        var op =  (_typeOf(options)==="object") ? options : {};
        var cb = (_typeOf(options)==="function") ? options : callback;
        op.dom = this;
        _loadHtml(modules, op, cb);
    };

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
