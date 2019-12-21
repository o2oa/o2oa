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
            "v": '2.1.0',
            "build": "2018.11.22",
            "info": "O2OA 活力办公 创意无限. Copyright © 2018, o2oa.net O2 Team All rights reserved."
        },
        "session": {
            "isDebugger": _debug,
            "path": "/o2_core/o2"
        },
        "language": _lp,
        "splitStr": /\s*(?:,|;)\s*/
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
    this.o2.addListener = _addListener;
    this.o2.removeListener = _removeListener;

    //uuid
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


    //load js, css, html adn all.
    var _getAllOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": doc,
            "dom": (options && options.dom) || document.body,
            "bind": (options && options.bind) || null,
            "position": (options && options.position) || "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'
        }
    };
    var _getCssOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": doc,
            "dom": (options && options.dom) || null
        }
    };
    var _getJsOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": (!(options && options.sequence == false)),
            "doc": doc
        }
    };
    var _getHtmlOptions = function(options){
        var doc = (options && options.doc) || document;
        if (!doc.unid) doc.unid = _uuid();
        return {
            "noCache": !!(options && options.nocache),
            "reload": !!(options && options.reload),
            "sequence": !!(options && options.sequence),
            "doc": doc,
            "dom": (options && options.dom) || null,
            "bind": (options && options.bind) || null,
            "position": (options && options.position) || "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'
        }
    };
    var _xhr_get = function(url, success, failure, completed){
        var xhr = new _request();
        xhr.open("GET", url, true);

        var _checkCssLoaded= function(_, err){
            if (!(xhr.readyState == 4)) return;
            if (err){
                if (completed) completed(xhr);
                return;
            }

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
            if (completed) completed(xhr);
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

    //load js
    //use framework url
    var _frameworks = {
        "o2.core": ["/o2_core/o2/o2.core.js"],
        "o2.more": ["/o2_core/o2/o2.more.js"],
        "ie_adapter": ["/o2_lib/o2/ie_adapter.js"],
        "jquery": ["/o2_lib/jquery/jquery.min.js"],
        "mootools": ["/o2_lib/mootools/mootools-1.6.0_all.js"],
        "ckeditor": ["/o2_lib/htmleditor/ckeditor4114/ckeditor.js"],
        "ckeditor5": ["/o2_lib/htmleditor/ckeditor5-12-1-0/ckeditor.js"],
        "raphael": ["/o2_lib/raphael/raphael.js"],
        "d3": ["/o2_lib/d3/d3.min.js"],
        "ace": ["/o2_lib/ace/src-noconflict/ace.js","/o2_lib/ace/src-noconflict/ext-language_tools.js"],
        "JSBeautifier": ["/o2_lib/JSBeautifier/beautify.js"],
        "JSBeautifier_css": ["/o2_lib/JSBeautifier/beautify-css.js"],
        "JSBeautifier_html": ["/o2_lib/JSBeautifier/beautify-html.js"],
        "JSONTemplate": ["/o2_lib/mootools/plugin/Template.js"],
        "kity": ["/o2_lib/kityminder/kity/kity.min.js"],
        "kityminder": ["/o2_lib/kityminder/core/dist/kityminder.core.js"]
    };
    var _loaded = {};
    var _loadedCss = {};
    var _loadedHtml = {};
    var _loadCssRunning = {};
    var _loadCssQueue = [];

    var _loadSingle = function(module, callback, op){
        var url = module;
        var uuid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uuid : addr_uri+"?v="+uuid;
        var key = encodeURIComponent(url+op.doc.unid);
        if (!op.reload) if (_loaded[key]){
            if (callback)callback(); return;
        }

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
                        //head.removeChild(s);
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

    //load css
    var _loadSingleCss = function(module, callback, op, uuid){
        var url = module;
        var uid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uid : url+"?v="+uid;

        var key = encodeURIComponent(url+op.doc.unid);
        if (_loadCssRunning[key]){
            _loadCssQueue.push(function(){
                _loadSingleCss(module, callback, op, uuid);
            });
            return;
        }

        if (_loadedCss[key]) uuid = _loadedCss[key]["class"];
        if (op.dom) _parseDom(op.dom, function(node){ if (node.className.indexOf(uuid) == -1) node.className += ((node.className) ? " "+uuid : uuid);}, op.doc);

        var completed = function(){
            if (_loadCssRunning[key]){
                _loadCssRunning[key] = false;
                delete _loadCssRunning[key];
            }
            if (_loadCssQueue && _loadCssQueue.length){
                (_loadCssQueue.shift())();
            }
        };

        if (_loadedCss[key])if (!op.reload){
            if (callback)callback(_loadedCss[key]);
            completed();
            return;
        }

        var success = function(xhr){
            var cssText = xhr.responseText;
            try{
                if (cssText){
                    if (op.bind) cssText = cssText.bindJson(op.bind);
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
                var styleObj = {"module": module, "id": uid, "style": style, "doc": op.doc, "class": uuid};
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

        _loadCssRunning[key] = true;

        _xhr_get(url, success, failure, completed);
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
        var thisLoaded = [];
        if (op.sequence){
            _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleCss, uuid);
        }else{
            _loadDisarray(ms, cb, op, thisLoaded, _loadSingleCss, uuid);
        }
    };
    var _removeCss = function(modules, doc){
        var thisDoc = doc || document;
        var ms = (_typeOf(modules)==="array") ? modules : [modules];
        for (var i=0; i<ms.length; i++){
            var module = modules[i];

            var k = encodeURIComponent(module+(thisDoc.unid||""));
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

    //load html
    _loadSingleHtml = function(module, callback, op){
        var url = module;
        var uid = _uuid();
        if (op.noCache) url = (url.indexOf("?")!==-1) ? url+"&v="+uid : url+"?v="+uid;
        var key = encodeURIComponent(url+op.doc.unid);
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
        if (op.bind) data = data.bindJson(op.bind);
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

    //load all
    _loadAll = function(modules, options, callback){
        //var ms = (_typeOf(modules)==="array") ? modules : [modules];
        var op =  (_typeOf(options)==="object") ? _getAllOptions(options) : _getAllOptions(null);
        var cb = (_typeOf(options)==="function") ? options : callback;

        var ms, htmls, styles, sctipts;
        var _htmlLoaded=(!modules.html), _cssLoaded=(!modules.css), _jsLoaded=(!modules.js);
        var _checkloaded = function(){
            if (_htmlLoaded && _cssLoaded && _jsLoaded) if (cb) cb(htmls, styles, sctipts);
        };
        if (modules.html){
            _loadHtml(modules.html, op, function(h){
                htmls = h;
                _htmlLoaded = true;
                _checkloaded();
            });
        }
        if (modules.css){
            _loadCss(modules.css, op, function(s){
                styles = s;
                _cssLoaded = true;
                _checkloaded();
            });
        }
        if (modules.js){
            _load(modules.js, op, function(s){
                sctipts = s;
                _jsLoaded = true;
                _checkloaded();
            });
        }
    };
    this.o2.loadAll = _loadAll;
    Element.prototype.loadAll = function(modules, options, callback){
        var op =  (_typeOf(options)==="object") ? options : {};
        var cb = (_typeOf(options)==="function") ? options : callback;
        op.dom = this;
        _loadAll(modules, op, cb);
    };

    var _getIfBlockEnd = function(v){
        var rex = /(\{\{if\s+)|(\{\{\s*end if\s*\}\})/gmi;
        var rexEnd = /\{\{\s*end if\s*\}\}/gmi;
        var subs = 1;
        while ((match = rex.exec(v)) !== null) {
            var fullMatch = match[0];
            if (fullMatch.search(rexEnd)!==-1){
                subs--;
                if (subs==0) break;
            }else{
                subs++
            }
        }
        if (match) return {"codeIndex": match.index, "lastIndex": rex.lastIndex};
        return {"codeIndex": v.length-1, "lastIndex": v.length-1};
    }
    var _getEachBlockEnd = function(v){
        var rex = /(\{\{each\s+)|(\{\{\s*end each\s*\}\})/gmi;
        var rexEnd = /\{\{\s*end each\s*\}\}/gmi;
        var subs = 1;
        while ((match = rex.exec(v)) !== null) {
            var fullMatch = match[0];
            if (fullMatch.search(rexEnd)!==-1){
                subs--;
                if (subs==0) break;
            }else{
                subs++;
            }
        }
        if (match) return {"codeIndex": match.index, "lastIndex": rex.lastIndex};
        return {"codeIndex": v.length-1, "lastIndex": v.length-1};
    }

    var _parseHtml = function(str, json){
        var v = str;
        var rex = /(\{\{\s*)[\s\S]*?(\s*\}\})/gmi;

        var match;
        while ((match = rex.exec(v)) !== null) {
            var fullMatch = match[0];
            var offset = 0;

            //if statement begin
            if (fullMatch.search(/\{\{if\s+/i)!==-1){
                //找到对应的end if
                var condition = fullMatch.replace(/^\{\{if\s*/i, "");
                condition = condition.replace(/\s*\}\}$/i, "");
                var flag = _jsonText(json, condition, "boolean");

                var tmpStr = v.substring(rex.lastIndex, v.length);
                var endIfIndex = _getIfBlockEnd(tmpStr);
                if (flag){ //if 为 true
                    var parseStr = _parseHtml(tmpStr.substring(0, endIfIndex.codeIndex), json);
                    var vLeft = v.substring(0, match.index);
                    var vRight = v.substring(rex.lastIndex+endIfIndex.lastIndex, v.length);
                    v = vLeft + parseStr + vRight;
                    offset = parseStr.length - fullMatch.length;
                }else{
                    v = v.substring(0, match.index) + v.substring(rex.lastIndex+endIfIndex.lastIndex, v.length);
                    offset = 0-fullMatch.length;
                }
            }else  if (fullMatch.search(/\{\{each\s+/)!==-1) { //each statement
                var itemString = fullMatch.replace(/^\{\{each\s*/, "");
                itemString = itemString.replace(/\s*\}\}$/, "");
                var eachValue = _jsonText(json, itemString, "object");

                var tmpEachStr = v.substring(rex.lastIndex, v.length);
                var endEachIndex = _getEachBlockEnd(tmpEachStr);

                var parseEachStr = tmpEachStr.substring(0, endEachIndex.codeIndex);
                var eachResult = "";
                if (eachValue && _typeOf(eachValue)==="array"){
                    for (var i=0; i<eachValue.length; i++){
                        eachValue[i]._ = json;
                        eachResult += _parseHtml(parseEachStr, eachValue[i]);
                    }
                    var eLeft = v.substring(0, match.index);
                    var eRight = v.substring(rex.lastIndex+endEachIndex.lastIndex, v.length);
                    v = eLeft + eachResult + eRight;
                    offset = eachResult.length - fullMatch.length;
                }else{
                    v = v.substring(0, match.index) + v.substring(rex.lastIndex+endEachIndex.lastIndex, v.length);
                    offset = 0-fullMatch.length;
                }

            }else{ //text statement
                var text = fullMatch.replace(/^\{\{\s*/, "");
                text = text.replace(/\}\}\s*$/, "");
                var value = _jsonText(json, text);
                offset = value.length-fullMatch.length;
                v = v.substring(0, match.index) + value + v.substring(rex.lastIndex, v.length);
            }
            rex.lastIndex = rex.lastIndex + offset;
        }
        return v;
    };
    var _jsonText = function(json, text, type){
        try {
            var $ = json;
            var f = eval("(function($){\n return "+text+";\n})");
            returnValue = f.apply(json, [$]);
            if (returnValue===undefined) returnValue="";
            if (type==="boolean") return (!!returnValue);
            if (type==="object") return returnValue;
            returnValue = returnValue.toString();
            return returnValue || "";
        }catch(e){
            if (type==="boolean") return false;
            if (type==="object") return null;
            return "";
        }
    };

    o2.bindJson = function(str, json){
        return _parseHtml(str, json);
    };
    String.prototype.bindJson = function(json){
        return _parseHtml(this, json);
    };

    //dom ready
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

    if (!window.MooTools){
        this.o2.load("mootools", function(){ _loadO2(); _dom.check(); });
    }else{
        _loadO2();
    }
    this.o2.addReady = function(fn){ _dom.addReady.call(_dom, fn); };
})();

layout = window.layout || {};
layout.desktop = layout;
var locate = window.location;
layout.protocol = locate.protocol;
layout.session = layout.session || {};
layout.debugger = (locate.href.toString().indexOf("debugger")!==-1);
o2.xApplication = o2.xApplication || {};

o2.xDesktop = o2.xDesktop || {};
o2.xDesktop.requireApp = function(module, clazz, callback, async){
    o2.requireApp(module, clazz, callback, async);
};
o2.addReady(function(){
    //兼容方法
    Element.implement({
        "makeLnk": function(options){}
    });

    //异步载入必要模块
    layout.config = null;

    var lp = o2.session.path+"/lp/"+o2.language+".js";
    var modules = [ "MWF.xDesktop.Common", "MWF.xAction.RestActions",lp];
    MWF.require(modules, function(){
        if (layout.config) _getDistribute(function(){ _load(); });
    });
    o2.getJSON("/x_desktop/res/config/config.json", function(config){
        layout.config = config;
        if (MWF.xDesktop.getServiceAddress) _getDistribute(function(){ _load(); });
    });


    var _getDistribute = function(callback){
        if (layout.config.app_protocol==="auto"){
            layout.config.app_protocol = window.location.protocol;
        }
        MWF.xDesktop.getServiceAddress(layout.config, function(service, center){
            layout.serviceAddressList = service;
            layout.centerServer = center;
            if (callback) callback();
        }.bind(this));
    };

    var _load = function(){
        //先判断用户是否登录
        MWF.Actions.get("x_organization_assemble_authentication").getAuthentication(function(json){
            //用户已经登录
            layout.user = json.data;
            layout.session = {};
            layout.session.user = json.data;

            (function(layout){
                var _loadResource = function(callback){
                    var isLoadedA = false;
                    var isLoadedB = false;
                    //var isLoadedC = false;


                    var modules = [
                        "o2.xDesktop.Dialog",
                        "MWF.xDesktop.UserData",
                        "MWF.xDesktop.Access",
                        "MWF.widget.UUID",
                        "MWF.xDesktop.Menu",
                        "MWF.xDesktop.shortcut",
                        "MWF.widget.PinYin",
                        "MWF.xDesktop.Access",
                        "MWF.xDesktop.MessageMobile",
                        "MWF.xScript.Macro"
                    ];
                    //MWF.xDesktop.requireApp("Common", "", null, false);
                    var _check = function(){ if (isLoadedA && isLoadedB) if (callback) callback(); };

                    o2.load(["../o2_lib/mootools/plugin/mBox.min.js"], function(){isLoadedA = true; _check();});
                    o2.require("MWF.widget.Common", function(){
                        o2.require(modules, function(){
                            o2.requireApp("Common", "", function(){isLoadedB = true; _check();})
                        });
                    });
                };

                var _loadContent =function(){
                    _loadResource(function(){
                        //this.Macro = new MWF.Macro["PageContext"](this);
                        for (var i=0; i<layout.readys.length; i++){
                            layout.readys[i].apply(window);
                        }
                    });
                };

                _loadContent();
            })(layout);
        }, function(){
            //用户未经登录
            //打开登录页面
            var _loadResource = function(callback){
                var isLoadedA = false;
                var isLoadedB = false;
                //var isLoadedC = false;

               // var lp = o2.session.path+"/lp/"+o2.language+".js";
                var modules = [
                    "o2.xDesktop.Dialog",
                    "MWF.xDesktop.UserData",
                    "MWF.xDesktop.Access",
                    "MWF.widget.UUID",
                    "MWF.xDesktop.Menu",
                    "MWF.xDesktop.shortcut",
                    "MWF.widget.PinYin",
                    "MWF.xDesktop.Access",
                    "MWF.xDesktop.MessageMobile"
                ];
                //MWF.xDesktop.requireApp("Common", "", null, false);
                var _check = function(){ if (isLoadedA && isLoadedB) if (callback) callback(); };

                o2.load(["../o2_lib/mootools/plugin/mBox.min.js"], function(){isLoadedA = true; _check();});
                o2.require("MWF.widget.Common", function(){
                    o2.require(modules, function(){
                        o2.requireApp("Common", "", function(){isLoadedB = true; _check();})
                    });
                });
            };
            _loadResource(function(){
                layout.openLogin();
            });

        });

        layout.openLogin = function(){
            MWF.require("MWF.widget.Common", null, false);
            MWF.require("MWF.xDesktop.Authentication", function(){
                var authentication = new MWF.xDesktop.Authentication({
                    "onLogin": _load.bind(layout)
                });
                authentication.loadLogin(document.body);
            });
        };
    };
});

(function(layout){
    layout.readys = [];
    layout.addReady = function(){
        for (var i = 0; i<arguments.length; i++){
            if (o2.typeOf(arguments[i])==="function") layout.readys.push(arguments[i]);
        }
    };
    var _requireApp = function(appNames, callback, clazzName){
        var appPath = appNames.split(".");
        var baseObject = o2.xApplication;
        appPath.each(function(path, i){
            if (i<(appPath.length-1)){
                baseObject[path] = baseObject[path] || {};
            }else {
                baseObject[path] = baseObject[path] || {"options": Object.clone(MWF.xApplication.Common.options)};
            }
            baseObject = baseObject[path];
        }.bind(this));
        if (!baseObject.options) baseObject.options = Object.clone(MWF.xApplication.Common.options);

        var _lpLoaded = false;
        MWF.xDesktop.requireApp(appNames, "lp."+o2.language, {
            "failure": function(){
                MWF.xDesktop.requireApp(appNames, "lp.zh-cn", null, false);
            }.bind(this)
        }, false);
        MWF.xDesktop.requireApp(appNames, clazzName, function(){
            if (callback) callback(baseObject);
        });
    };
    var _createNewApplication = function(e, appNamespace, appName, options, statusObj){
        var app = new appNamespace["Main"](this, options);
        app.desktop = layout;
        app.inBrowser = true;
        app.status = statusObj;
        app.load(true);

        var appId = appName;
        if (options.appId){
            appId = options.appId;
        }else{
            if (appNamespace.options.multitask) appId = appId+"-"+(new MWF.widget.UUID());
        }
        app.appId = appId;
        layout.app = app;
        layout.desktop.currentApp = app;
    };
    var _openWorkAndroid = function(options){
        if (window.o2android && window.o2android.openO2Work) {
            if (options.workId) {
                window.o2android.openO2Work(options.workId, "", title);
            } else if (options.workCompletedId) {
                window.o2android.openO2Work("", options.workCompletedId, title);
            }
            return true;
        }
        return false;
    };
    var _openWorkIOS = function(options){
        if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Work) {
            if (options.workId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": options.workId,
                    "workCompleted": "",
                    "title": title
                });
            } else if (options.workCompletedId) {
                window.webkit.messageHandlers.openO2Work.postMessage({
                    "work": "",
                    "workCompleted": options.workCompletedId,
                    "title": title
                });
            }
            return true;
        }
        return false;
    };
    var _openWorkHTML = function(options){
        var uri = new URI(window.location.href);
        var redirectlink = uri.getData("redirectlink");
        if (!redirectlink) {
            redirectlink = encodeURIComponent(locate.pathname + locate.search);
        } else {
            redirectlink = encodeURIComponent(redirectlink);
        }
        if (options.workId) {
            window.location = "workmobilewithaction.html?workid=" + options.workId + "&redirectlink=" + redirectlink;
        } else if (options.workCompletedId) {
            window.location = "workmobilewithaction.html?workcompletedid=" + options.workCompletedId + "&redirectlink=" + redirectlink;
        }
    };
    var _openWork = function(options){
        if (!_openWorkAndroid(options)) if (!_openWorkIOS(options)) _openWorkHTML(options);
    };
    var _openDocument = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsDocument){
            window.o2android.openO2CmsDocument(options.documentId, title);
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsDocument){
            window.webkit.messageHandlers.openO2CmsDocument.postMessage({"docId":options.documentId,"docTitle":title});
        }else{
            window.location = "appMobile.html?"+par;
        }
    };
    var _openCms = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2CmsApplication){
            window.o2android.openO2CmsApplication(options.columnId, title);
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2CmsApplication){
            window.webkit.messageHandlers.openO2CmsApplication.postMessage(options.columnId);
        }else{
            window.location = "appMobile.html?"+par;
        }
    };
    var _openMeeting = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Meeting){
            window.o2android.openO2Meeting("");
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Meeting){
            window.webkit.messageHandlers.openO2Meeting.postMessage("");
        }else{
            window.location = "appMobile.html?"+par;
        }
    };

    var _openCalendar = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        if (window.o2android && window.o2android.openO2Calendar){
            window.o2android.openO2Calendar("");
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2Calendar){
            window.webkit.messageHandlers.openO2Calendar.postMessage("");
        }else{
            window.location = "appMobile.html?"+par;
        }
    };
    var _openTaskCenter = function(appNames, options, statusObj){
        var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
        var tab = ((options && options.navi) ? options.navi : "task").toLowerCase();
        if (tab==="done") tab = "taskCompleted";
        if (tab==="readed") tab = "readCompleted";

        if (window.o2android && window.o2android.openO2WorkSpace){
            window.o2android.openO2WorkSpace(tab);
        }else if (window.webkit && window.webkit.messageHandlers && window.webkit.messageHandlers.openO2WorkSpace){
            window.webkit.messageHandlers.openO2WorkSpace.postMessage(tab);
        }else{
            window.location = "appMobile.html?"+par;
        }
    };

    var _openApplicationMobile = function(appNames, options, statusObj){
        switch (appNames) {
            case "process.Work":
                _openWork(options);
                break;
            case "cms.Document":
                _openDocument(appNames, options, statusObj);
                break;
            case "cms.Module":
                _openCms(appNames, options, statusObj);
                break;
            case "Meeting":
                _openMeeting(appNames, options, statusObj);
                break;
            case "Calendar":
                _openCalendar(appNames, options, statusObj);
                break;
            case "process.TaskCenter":
                _openTaskCenter(appNames, options, statusObj);
                break;
            default:
                window.location = "appMobile.html?app="+appNames+"&option="+(optionsStr || "")+"&status="+(statusStr || "");
        }
    };

    layout.openApplication = function(e, appNames, options, statusObj){
        if (layout.app){
            if (layout.mobile){
                _openApplicationMobile(appNames, options, statusObj);
            }else{
                var par = "app="+encodeURIComponent(appNames)+"&status="+encodeURIComponent((statusObj)? JSON.encode(statusObj) : "")+"&option="+encodeURIComponent((options)? JSON.encode(options) : "");
                return window.open("app.html?"+par, "_blank");
            }
        }else{
            var appPath = appNames.split(".");
            var appName = appPath[appPath.length-1];

            _requireApp(appNames, function(appNamespace){
                _createNewApplication(e, appNamespace, appName, options, statusObj);
            }.bind(this));
        }
    };

    layout.refreshApp = function(app){
        var status = app.recordStatus();

        var uri = new URI(window.location.href);
        var appNames = uri.getData("app");
        var optionsStr = uri.getData("option");
        var statusStr = uri.getData("status");
        if (status) statusStr = JSON.encode(status);

        var port = uri.get("port");
        window.location = uri.get("scheme") + "://" + uri.get("host") + ((port) ? ":" + port + "/" : "") + uri.get("directory ") + "?app=" + encodeURIComponent(appNames) + "&status=" + encodeURIComponent(statusStr) + "&option=" + encodeURIComponent((options) ? JSON.encode(options) : "");
    };

    layout.load =function(appNames, options, statusObj){
        layout.message = new MWF.xDesktop.MessageMobile();
        layout.message.load();

        layout.apps = [];
        layout.node = $("layout");
        var appName=appNames, m_status=statusObj, option=options;

        var topWindow = window.opener;
        if (topWindow){
            try{
                if (!appName) appName = topWindow.layout.desktop.openBrowserApp;
                if (!m_status) m_status = topWindow.layout.desktop.openBrowserStatus;
                if (!option)  option = topWindow.layout.desktop.openBrowserOption;
            }catch(e){}
        }
        layout.openApplication(null, appName, option||{}, m_status);
    }

})(layout);