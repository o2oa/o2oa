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
 *  along with O2OA.  If not, see <https://www.gnu.org/licenses/>.
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
//Element.firstElementChild Polyfill
// (function(constructor) {
//     if (constructor &&
//         constructor.prototype &&
//         constructor.prototype.firstElementChild == null) {
//         Object.defineProperty(constructor.prototype, 'firstElementChild', {
//             get: function() {
//                 var node, nodes = this.childNodes, i = 0;
//                 while (node = nodes[i++]) {
//                     if (node.nodeType === 1) {
//                         return node;
//                     }
//                 }
//                 return null;
//             }
//         });
//     }
// })(window.Node || window.Element);

if (window.Promise && !Promise.any){
    Promise.any = function(promises){
        if (!promises || !promises.length) return new Promise();

        return new Promise(function(resolve){
            var value = null;
            var resolved = false;
            var checkValue = function(){
                if (resolved) resolve(value);
            };
            for (var i=0; i<promises.length; i++){
                promises[i],then(function(v){
                    value == v;
                    resolved = true;
                    checkValue();
                });
            }
        });
    }
}

if (!window.o2) {
    (function () {
        var _language = ""
        try {
            _language = localStorage.getItem("o2.language");
        } catch (e) {};

        var _href = window.location.href;
        var _debug = (_href.indexOf("debugger") !== -1);
        var _par = _href.substr(_href.lastIndexOf("?") + 1, _href.length);

        // var supportedLanguages = ["zh-cn", "en", "es", "ko", "zh-tw", "zh-hk", "ja"];

        var _lp = _language || navigator.language || "zh-cn";
        //if (!_lp) _lp = "zh-cn";

        if (_par) {
            var _parList = _par.split("&");
            for (var i = 0; i < _parList.length; i++) {
                var _v = _parList[i];
                var _kv = _v.split("=");
                if (_kv[0].toLowerCase() === "lg") _lp = _kv[1];
                if (_kv[0].toLowerCase() === "lp") _lp = _kv[1];
            }
        }

        /**
         * @summary 平台全局对象，在前端(浏览器/移动端H5页面）可用。<br/>
         * @namespace o2
         * @o2cn 平台全局对象
         * @o2category web
         */
        this.o2 = window.o2 || {};
        /**
         * @summary 平台版本信息。
         * @member {Object} version
         * @memberOf o2
         * @o2syntax
         * //获取版本号
         * var v = o2.version.v;
         */
        this.o2.version = {
            "v": "o2oa",
            "build": "2022.09.13",
            "info": "O2OA 活力办公 创意无限. Copyright © 2022, o2oa.net O2 Team All rights reserved."
        };

        /**
         * @summary 平台运行环境。
         * @member {Object} session
         * @memberOf o2
         * @property {Boolean}  isDebugger   是否是调试模式
         * @property {Boolean}  isMobile   是否是移动端环境
         * @o2syntax
         * var debuggerMode = o2.session.isDebugger;
         * var ismobile = o2.session.isMobile;
         */
        if (!this.o2.session) this.o2.session = {
            "isDebugger": _debug,
            "path": "../o2_core/o2"
        };

        /**
         * @summary 语言环境名称。
         * @member {String} language
         * @memberOf o2
         * @o2syntax
         * var lp = o2.language;
         */
        this.o2.languageName = _lp;
        _lp = _lp.toLocaleLowerCase();

        // if (supportedLanguages.indexOf(_lp) == -1){
        //     _lp = _lp.substring(0, _lp.indexOf('-'));
        // }
        // if (supportedLanguages.indexOf(_lp) == -1) _lp = "zh-cn";
        this.o2.language = _lp;
        this.o2.splitStr = /\s*(?:,|;)\s*/;

        this.wrdp = this.o2;

        var debug = function (reload) {
            if (!o2.session.isMobile) {
                window.location.assign(_href + ((_href.indexOf("?") == -1) ? "?" : "&") + "debugger");
            } else {
                if (!o2.session.isDebugger) {
                    o2.session.isDebugger = true;
                    if (o2.session.isMobile || layout.mobile) o2.load("../o2_lib/eruda/eruda.js");
                }
            }
        };
        /**
         * @summary 使平台进入调试模式。
         * @function debug
         * @memberOf o2
         * @o2syntax
         * o2.debug();
         */
        this.o2.debug = debug;


        this.o2.runningRequestsList = [];
        var o2 = this.o2;
        var requestSend = XMLHttpRequest.prototype.send;
        var requestOpen = XMLHttpRequest.prototype.open;
        XMLHttpRequest.prototype.send = function(){
            var request = this;
            o2.runningRequestsList.push(request);
            request.addEventListener("loadend", function(){
                o2.runningRequestsList.splice(o2.runningRequestsList.indexOf(request, 1));
            });
            requestSend.apply(this, arguments);
        }
        XMLHttpRequest.prototype.open = function(){
            var request = this;
            request.requestOptions = Array.from(arguments);
            requestOpen.apply(this, arguments);
        }

        var _attempt = function () {
            for (var i = 0, l = arguments.length; i < l; i++) {
                try {
                    arguments[i]();
                    return arguments[i];
                } catch (e) {
                }
            }
            return null;
        };
        var _typeOf = function (item) {
            if (item == null) return 'null';
            if (item.$family != null) return item.$family();
            if (item.constructor == window.Array) return "array";

            if (item.nodeName) {
                if (item.nodeType == 1) return 'element';
                if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
            } else if (typeof item.length == 'number') {
                if (item.callee) return 'arguments';
            }
            return typeof item;
        };
        /**
         * @summary 判断一个任意参数的类型。
         * @function typeOf
         * @memberOf o2
         * @param {Object} [obj] 要检查的对象
         * @return {String} 对象的类型，返回值：
         * <pre><code class="language-js">'element' - 如果obj是一个DOM Element对象.
         * 'elements' - 如果obj是一个Elements实例.
         * 'textnode' - 如果obj是一个DOM text节点.
         * 'whitespace' - 如果obj是一个DOM whitespace 节点.
         * 'arguments' - 如果obj是一个arguments对象.
         * 'array' - 如果obj是一个array数组.
         * 'object' - 如果obj是一个object对象.
         * 'string' - 如果obj是一个string.
         * 'number' - 如果obj是一个数字number.
         * 'date' - 如果obj是一个日期date.
         * 'boolean' - 如果obj是一个布尔值boolean.
         * 'function' - 如果obj是一个function.
         * 'regexp' - 如果obj是一个正则表达式.
         * 'collection' - 如果obj是一个原生HTML elements collection, 如childNodes or getElementsByTagName获取的对象.
         * 'window' - 如果obj是window对象.
         * 'document' - 如果obj是document对象.
         * 'domevent' - 如果obj是一个event.
         * 'null' - 如果obj是undefined, null, NaN 或者 none.
         * </code></pre>
         * @o2syntax
         * o2.typeOf(obj);
         * @example
         * var myString = 'hello';
         * o2.typeOf(myString); // returns "string"
         */
        this.o2.typeOf = _typeOf;

        var _addListener = function (dom, type, fn) {
            if (type == 'unload') {
                var old = fn, self = this;
                fn = function () {
                    _removeListener(dom, 'unload', fn);
                    old();
                };
            }
            if (dom.addEventListener) dom.addEventListener(type, fn, !!arguments[2]);
            else dom.attachEvent('on' + type, fn);
        };
        var _removeListener = function (dom, type, fn) {
            if (dom.removeEventListener) dom.removeEventListener(type, fn, !!arguments[2]);
            else dom.detachEvent('on' + type, fn);
        };

        //http request class
        var _request = (function () {
            var XMLHTTP = function () {
                return new XMLHttpRequest();
            };
            var MSXML2 = function () {
                return new ActiveXObject('MSXML2.XMLHTTP');
            };
            var MSXML = function () {
                return new ActiveXObject('Microsoft.XMLHTTP');
            };
            return _attempt(XMLHTTP, MSXML2, MSXML);
        })();
        this.o2.request = _request;

        var _returnBase = function (number, base) {
            return (number).toString(base).toUpperCase();
        };
        var _getIntegerBits = function (val, start, end) {
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
        var _rand = function (max) {
            return Math.floor(Math.random() * (max + 1));
        };
        this.o2.addListener = _addListener;
        this.o2.removeListener = _removeListener;

        //uuid
        var _uuid = function () {
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
        /**
         * @summary 生成一个唯一的uuid。
         * @function uuid
         * @memberOf o2
         * @o2syntax
         * var id = o2.uuid();
         */
        this.o2.uuid = _uuid;


        var _runCallback = function (callback, key, par, bind, promise_cb) {
            var b = bind || callback;
            if (!key) key = "success";

            var cb;
            if (callback) {
                var type = o2.typeOf(callback).toLowerCase();
                if (key.toLowerCase() === "success" && type === "function") {
                    cb = callback;
                } else {
                    var name = ("on-" + key).camelCase();
                    cb = (callback[name]) ? callback[name] : ((callback[key]) ? callback[key] : null);
                }
            }
            if (cb) return cb.apply(b, par);
        };
        this.o2.runCallback = _runCallback;

        if (window.CustomEvent) this.o2.customEventLoad = new CustomEvent("o2load");

        //load js, css, html adn all.
        var _getAllOptions = function (options) {
            var doc = (options && options.doc) || document;
            if (!doc.unid) doc.unid = _uuid();
            var type = (options && options.type) || "text/javascript";
            return {
                "noCache": !!(options && options.nocache),
                "reload": !!(options && options.reload),
                "sequence": !!(options && options.sequence),
                "type": type,
                "doc": doc,
                "dom": (options && options.dom) || document.body,
                "module": (options && options.module) || null,
                "noConflict": (options && options.noConflict) || false,
                "url": !!(options && options.url),
                "bind": (options && options.bind) || null,
                "evalScripts": (options && options.evalScripts) || false,
                "baseUrl": (options && options.baseUrl) ? options.baseUrl : "",
                "position": (options && options.position) || "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'debugger
            }
        };
        var _getCssOptions = function (options) {
            var doc = (options && options.doc) || document;
            if (!doc.unid) doc.unid = _uuid();
            return {
                "url": !!(options && options.url),
                "noCache": !!(options && options.nocache),
                "reload": !!(options && options.reload),
                "sequence": !!(options && options.sequence),
                "doc": doc,
                "dom": (options && options.dom) || null
            }
        };
        var _getJsOptions = function (options) {
            var doc = (options && options.doc) || document;
            if (!doc.unid) doc.unid = _uuid();
            var type = (options && options.type) || "text/javascript";
            return {
                "noCache": !!(options && options.nocache),
                "reload": !!(options && options.reload),
                "sequence": (!(options && options.sequence == false)),
                "type": type,
                "baseUrl": (options && options.baseUrl) ? options.baseUrl : "",
                "doc": doc
            }
        };
        var _getHtmlOptions = function (options) {
            var doc = (options && options.doc) || document;
            if (!doc.unid) doc.unid = _uuid();
            return {
                "noCache": !!(options && options.nocache),
                "reload": !!(options && options.reload),
                "sequence": !!(options && options.sequence),
                "doc": doc,
                "dom": (options && options.dom) || null,
                "module": (options && options.module) || null,
                "noConflict": (options && options.noConflict) || false,
                "bind": (options && options.bind) || null,
                "evalScripts": (options && options.evalScripts) || false,
                "baseUrl": (options && options.baseUrl) ? options.baseUrl : "",
                "position": (options && options.position) || "beforeend" //'beforebegin' 'afterbegin' 'beforeend' 'afterend'
            }
        };
        _filterUrl = function (url) {
            if (o2.base) {
                if (url.indexOf(":") === -1) {
                    var s = url.substring(0, url.indexOf("/") + 1);
                    var r = url.substring(url.indexOf("/") + 1, url.length);
                    if ("../" === s || "./" === s || "/" === s) {
                        return s + o2.base + r;
                    } else {
                        return o2.base + url
                    }
                }
            }

            if (window.layout && layout.config && layout.config.urlMapping) {
                for (var k in layout.config.urlMapping) {
                    var regex = new RegExp(k);
                    if (regex.test(url)) {
                        return url.replace(regex, layout.config.urlMapping[k]);
                    }
                }
            }

            return url;
        };
        /**
         * @summary 解析平台内的url，如果配置了反向代理的路径转发，平台内的url需要通过filterUrl解析后，才能得到正确的url。
         * @see {@link https://www.o2oa.net/search.html?q=urlmapping|基于nginx快速集群部署-上下文分发}
         * @function filterUrl
         * @memberOf o2
         * @param {String} [url] 要解析的url
         * @return {String} 解析后的url
         * @o2syntax
         * var url = o2.filterUrl(url);
         * @example
         * <caption>
         *    当我们配置了按路径转发后，在portal.json中配置了urlMapping如：
         *    <pre><code class="language-js">"urlMapping": {
         *      "qmx.o2oa.net:20020": "qmx.o2oa.net/dev/app",
         *      "qmx.o2oa.net:20030": "qmx.o2oa.net/dev/center"
         * },</code></pre>
         *    在获取平台内部的url时，如附件的下载地址，需要通过filterUrl解析。
         * </caption>
         * var attachmentUrl = "http://qmx.o2oa.net:20020/x_processplatform_assemble_surface/jaxrs/attachment/{attid}/work/{workid}";
         * var url = o2.filterUrl(attachmentUrl);
         * //return "http://qmx.o2oa.net/dev/app/x_processplatform_assemble_surface/jaxrs/attachment/{attid}/work/{workid}"
         */
        this.o2.filterUrl = _filterUrl;
        var _xhr_get = function (url, success, failure, completed, sync) {
            var xhr = new _request();
            url = _filterUrl(url);
            xhr.open("GET", url, !sync);

            var _checkCssLoaded = function (_, err) {
                if (!(xhr.readyState == 4)) return;
                if (err) {
                    if (completed) completed(xhr);
                    return;
                }

                _removeListener(xhr, 'readystatechange', _checkCssLoaded);
                _removeListener(xhr, 'load', _checkCssLoaded);
                _removeListener(xhr, 'error', _checkCssErrorLoaded);

                if (err) {
                    if (failure) failure(xhr);
                    return
                }
                var status = xhr.status;
                status = (status == 1223) ? 204 : status;
                if ((status >= 200 && status < 300))
                    if (success) success(xhr);
                    else if ((status >= 300 && status < 400))
                        if (failure) failure(xhr);
                        else
                            failure(xhr);
                if (completed) completed(xhr);
            };
            var _checkCssErrorLoaded = function (err) {
                _checkCssLoaded(err)
            };

            if ("load" in xhr) _addListener(xhr, "load", _checkCssLoaded);
            if ("error" in xhr) _addListener(xhr, "load", _checkCssErrorLoaded);
            _addListener(xhr, "readystatechange", _checkCssLoaded);
            xhr.send();
        };
        this.o2.xhr_get = _xhr_get;
        var _loadSequence = function (ms, cb, op, n, thisLoaded, loadSingle, uuid, fun) {
            loadSingle(ms[n], function (module) {
                if (module) thisLoaded.push(module);
                n++;
                if (fun) fun(module);
                if (n === ms.length) {
                    if (cb) cb(thisLoaded);
                } else {
                    _loadSequence(ms, cb, op, n, thisLoaded, loadSingle, uuid, fun);
                }
            }, op, uuid);
        };
        var _loadDisarray = function (ms, cb, op, thisLoaded, loadSingle, uuid, fun) {
            var count = 0;
            for (var i = 0; i < ms.length; i++) {
                loadSingle(ms[i], function (module) {
                    if (module) thisLoaded.push(module);
                    count++;
                    if (fun) fun(module);
                    if (count === ms.length) if (cb) cb(thisLoaded);
                }, op, uuid);
            }
        };

        //load js
        //use framework url
        var _frameworks = {
            "o2.core": ["../o2_core/o2/o2.core.js"],
            "o2.more": ["../o2_core/o2/o2.more.js"],
            "ie_adapter": ["../o2_core/o2/ie_adapter.js"],
            "jquery": ["../o2_lib/jquery/jquery.min.js"],
            "mootools": ["../o2_lib/mootools/mootools-1.6.0_all.js"],
            "ckeditor": ["../o2_lib/htmleditor/ckeditor4161/ckeditor.js"],
            "ckeditor5": ["../o2_lib/htmleditor/ckeditor5-12-1-0/ckeditor.js"],
            "raphael": ["../o2_lib/raphael/raphael.js"],
            "d3": ["../o2_lib/d3/d3.min.js"],
            "ace": ["../o2_lib/ace/src-min-noconflict/ace.js", "../o2_lib/ace/src-min-noconflict/ext-language_tools.js"],
            //"ace": ["../o2_lib/ace/src-noconflict/ace.js","../o2_lib/ace/src-noconflict/ext-language_tools.js"],
            "monaco": ["../o2_lib/vs/loader.js"],
            "JSBeautifier": ["../o2_lib/JSBeautifier/beautify.js"],
            "JSBeautifier_css": ["../o2_lib/JSBeautifier/beautify-css.js"],
            "JSBeautifier_html": ["../o2_lib/JSBeautifier/beautify-html.js"],
            "JSONTemplate": ["../o2_lib/mootools/plugin/Template.js"],
            "kity": ["../o2_lib/kityminder/kity/kity.js"],
            "kityminder": ["../o2_lib/kityminder/core/dist/kityminder.core.js"],
            "vue": ["../o2_lib/vue/vue.pro.js"],
            "vue_develop": ["../o2_lib/vue/vue.js"],
            "elementui": ["../o2_lib/vue/element/index.js"]
        };
        var _loaded = {};
        var _loadedCss = {};
        var _loadedHtml = {};
        var _loadCssRunning = {};
        var _loadCssQueue = [];
        var _loadingModules = {};

        var _checkUrl = function (url, base) {
            var urlStr = new URI(url, {base: base}).toString();
            return urlStr;
        }
        var _loadSingle = function (module, callback, op) {
            var url = module;

            if (op.baseUrl) url = _checkUrl(url, op.baseUrl);

            var uuid = _uuid();
            if (op.noCache) url = (url.indexOf("?") !== -1) ? url + "&v=" + uuid : addr_uri + "?v=" + uuid;
            var key = encodeURIComponent(url + op.doc.unid);
            if (!op.reload) if (_loaded[key]) {
                Promise.resolve(_loaded[key]).then(function(o){
                    if (callback) callback(o);
                });
                //if (callback) callback();
                return;
            }

            if (_loadingModules[key] && !op.reload) {
                if (!_loadingModules[key].callbacks) _loadingModules[key].callbacks = [];
                _loadingModules[key].callbacks.push(callback);
            } else {
                _loadingModules[key] = {callbacks: [callback]};

                var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
                var s = op.doc.createElement('script');
                s.type = op.type || "text/javascript";
                head.appendChild(s);
                s.id = uuid;
                s.src = this.o2.filterUrl(url);

                var _checkScriptLoaded = function (_, isAbort, err) {
                    if (isAbort || !s.readyState || s.readyState === "loaded" || s.readyState === "complete") {
                        var scriptObj = {"module": module, "id": uuid, "script": s, "doc": op.doc};
                        if (!err) _loaded[key] = scriptObj;
                        _removeListener(s, 'readystatechange', _checkScriptLoaded);
                        _removeListener(s, 'load', _checkScriptLoaded);
                        _removeListener(s, 'error', _checkScriptErrorLoaded);
                        if (!isAbort || err) {
                            if (err) {
                                if (s) head.removeChild(s);
                                while (_loadingModules[key].callbacks.length) {
                                    (_loadingModules[key].callbacks.shift())();
                                }
                                delete _loadingModules[key];
                                //if (callback)callback();
                            } else {
                                //head.removeChild(s);
                                while (_loadingModules[key].callbacks.length) {
                                    (_loadingModules[key].callbacks.shift())(scriptObj);
                                }
                                delete _loadingModules[key];
                                //if (callback)callback(scriptObj);
                            }
                        }
                    }
                };
                var _checkScriptErrorLoaded = function (e, err) {
                    console.log("Error: load javascript module: " + module);
                    _checkScriptLoaded(e, true, "error");
                };

                if ('onreadystatechange' in s) _addListener(s, 'readystatechange', _checkScriptLoaded);
                _addListener(s, 'load', _checkScriptLoaded);
                _addListener(s, 'error', _checkScriptErrorLoaded);
            }
        };

        var _load = function (urls, options, callback) {
            if (window.document && !window.importScripts) {
                var ms = (_typeOf(urls) === "array") ? urls : [urls];
                var op = (_typeOf(options) === "object") ? _getJsOptions(options) : _getJsOptions(null);
                var cbk = (_typeOf(options) === "function") ? options : callback;

                var cb = cbk;
                if (typeof define === 'function' && define.amd) {
                    define.amd = false;
                    cb = (cbk) ? function () {
                        define.amd = true;
                        cbk();
                    } : function () {
                        define.amd = true;
                    }
                }

                var modules = [];
                for (var i = 0; i < ms.length; i++) {
                    var url = ms[i];
                    var module = _frameworks[url] || url;
                    if (_typeOf(module) === "array") {
                        modules = modules.concat(module)
                    } else {
                        modules.push(module)
                    }
                }
                var thisLoaded = [];
                if (op.sequence) {
                    _loadSequence(modules, cb, op, 0, thisLoaded, _loadSingle);
                } else {
                    _loadDisarray(modules, cb, op, thisLoaded, _loadSingle);
                }
            } else {
                if (window.importScripts) {
                    var ms = (_typeOf(urls) === "array") ? urls : [urls];
                    ms.each(function (url) {
                        window.importScripts(o2.filterUrl(url));
                    });
                    var cbk = (_typeOf(options) === "function") ? options : callback;
                    if (cbk) cbk();
                }
            }

        };
        /**
         * @summary 引入外部javascript文件。
         * @function load
         * @memberOf o2
         * @param {String|Array} [urls] 要载入的js文件url，或要载入多个js问价的urls数组。
         * @param {Object|Function} [options|callback] 载入js文件的配置参数，或者载入成功后的回调。
         * <pre><code class="language-js">options参数格式如下：
         * {
         *      "noCache":  是否使用缓存，默认true,
         *      "reload":   如果相同路径的js文件已经加载了，是否重新载入,默认为：false
         *      "sequence": 当urls参数为数组时，多个脚本文件是否按数组顺序依次载入,默认为false
         *      "type":     载入脚本的类型,默认为"text/javascript"
         *      "baseUrl":  要载入脚本的url的base路径,默认""
         *      "doc":      要在哪个document对象中载入脚本文件，默认为当前document
         * }
         * </code></pre>
         * @param {Function} [callback] 可选参数，载入成功后的回调方法。
         * @o2syntax
         * o2.load(urls, options, callback);
         * @example
         * //载入jsfile1.js和js/jsfile2.js两个文件，它们是按顺序载入的
         * o2.load(["js/jsfile1.js", "js/jsfile2.js"], function(){
         *     //js文件已经载入
         * });
         *
         * //载入jsfile1.js和js/jsfile2.js两个文件，它们是同时载入的
         * //并且无论是否已经加载过，都需要重新加载，并且要按顺序加载
         * o2.load(["js/jsfile1.js", "js/jsfile2.js"], {"reload": true, "sequence": true}, function(){
         *     //js文件已经载入
         * });
         */
        this.o2.load = _load;

        //load css
        var _loadSingleCss = function (module, callback, op, uuid) {
            var url = module;
            var uid = _uuid();
            if (op.noCache) url = (url.indexOf("?") !== -1) ? url + "&v=" + uid : url + "?v=" + uid;

            var key = encodeURIComponent(url + op.doc.unid);
            if (_loadCssRunning[key]) {
                _loadCssQueue.push(function () {
                    _loadSingleCss(module, callback, op, uuid);
                });
                return;
            }

            var completed = function () {
                if (_loadCssRunning[key]) {
                    _loadCssRunning[key] = false;
                    delete _loadCssRunning[key];
                }
                if (_loadCssQueue && _loadCssQueue.length) {
                    (_loadCssQueue.shift())();
                }
            };
            if (_loadedCss[key]) uuid = _loadedCss[key]["class"];
            if (op.dom) _parseDom(op.dom, function (node) {
                if (node.className.indexOf(uuid) == -1) node.className += ((node.className) ? " " + uuid : uuid);
            }, op.doc);

            if (_loadedCss[key]) if (!op.reload) {
                Promise.resolve(_loadedCss[key]).then(function(o){
                    if (callback) callback(o);
                });
                //if (callback) callback(_loadedCss[key]);
                completed();
                return;
            }

            if (op.url){
                var style = op.doc.createElement("link");
                style.setAttribute("rel", "stylesheet");
                style.setAttribute("type", "text/css");
                style.setAttribute("id", uuid);
                style.setAttribute("href", url);
                if (!op.notInject) {
                    var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
                    head.appendChild(style);
                }
                var styleObj = {"module": module, "id": uid, "style": style, "doc": op.doc, "class": uuid};
                if (callback) callback(styleObj);
                completed();
            }else{
                var success = function (xhr) {
                    var cssText = xhr.responseText;
                    try {
                        if (cssText) {
                            op.uuid = uuid;
                            var style = _loadCssText(cssText, op);
                        }
                        style.id = uid;
                        var styleObj = {"module": module, "id": uid, "style": style, "doc": op.doc, "class": uuid};
                        _loadedCss[key] = styleObj;
                        if (callback) callback(styleObj);
                    } catch (e) {
                        if (callback) callback();
                        return;
                    }
                };
                var failure = function (xhr) {
                    console.log("Error: load css module: " + module);
                    if (callback) callback();
                };

                _loadCssRunning[key] = true;

                _xhr_get(url, success, failure, completed);
            }
        };

        var _parseDomString = function (dom, fn, sourceDoc) {
            var doc = sourceDoc || document;
            var list = doc.querySelectorAll(dom);
            if (list.length) for (var i = 0; i < list.length; i++) _parseDomElement(list[i], fn);
        };
        var _parseDomElement = function (dom, fn) {
            if (fn) fn(dom);
        };
        var _parseDom = function (dom, fn, sourceDoc) {
            var domType = _typeOf(dom);
            if (domType === "string") _parseDomString(dom, fn, sourceDoc);
            if (domType === "element") _parseDomElement(dom, fn);
            if (domType === "array") for (var i = 0; i < dom.length; i++) _parseDom(dom[i], fn, sourceDoc);
        };
        var _loadCss = function (modules, options, callback) {
            var ms = (_typeOf(modules) === "array") ? modules : [modules];
            var op = (_typeOf(options) === "object") ? _getCssOptions(options) : _getCssOptions(null);
            var cb = (_typeOf(options) === "function") ? options : callback;

            var uuid = "css" + _uuid();
            var thisLoaded = [];
            if (op.sequence) {
                _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleCss, uuid);
            } else {
                _loadDisarray(ms, cb, op, thisLoaded, _loadSingleCss, uuid);
            }
        };
        var _removeCss = function (modules, doc) {
            var thisDoc = doc || document;
            var ms = (_typeOf(modules) === "array") ? modules : [modules];
            for (var i = 0; i < ms.length; i++) {
                var module = ms[i];

                var k = encodeURIComponent(module + (thisDoc.unid || ""));
                var removeCss = _loadedCss[k];
                if (!removeCss) for (key in _loadedCss) {
                    if (_loadedCss[key].id == module) {
                        removeCss = _loadedCss[key];
                        k = key;
                        break;
                    }
                }
                if (removeCss) {
                    delete _loadedCss[k];
                    var styleNode = removeCss.doc.getElementById(removeCss.id);
                    if (styleNode) styleNode.parentNode.removeChild(styleNode);
                    removeCss = null;
                }
            }
        };

        var _loadCssText = function (cssText, options, callback) {
            var op = (_typeOf(options) === "object") ? _getCssOptions(options) : _getCssOptions(null);
            var cb = (_typeOf(options) === "function") ? options : callback;
            var uuid = options.uuid || "css" + _uuid();

            if (cssText) {
                if (op.dom) _parseDom(op.dom, function (node) {
                    if (node.className.indexOf(uuid) == -1) node.className += ((node.className) ? " " + uuid : uuid);
                }, op.doc);
                cssText = cssText.replace(/\/\*(\s|\S)*?\*\//g, "");
                if (op.bind) cssText = cssText.bindJson(op.bind);
                if (op.dom) {

                    var rex = new RegExp("(.+)(?=[\\r\\n]*\\{)", "g");
                    var match;
                    var prefix = "." + uuid + " ";
                    while ((match = rex.exec(cssText)) !== null) {
                        // var rule = prefix + match[0];
                        // cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                        // rex.lastIndex = rex.lastIndex + prefix.length;

                        var rulesStr = match[0];
                        var startWith = rulesStr.substring(0, 1);
                        if (startWith === "@" || startWith === ":" || rulesStr.indexOf("%") !== -1) {
                            // var begin = 0;
                            // var end = 0;
                        }else if (rulesStr.trim()==='from' || rulesStr.trim()==='to'){
                            //nothing
                        } else {
                            if (rulesStr.indexOf(",") != -1) {
                                //var rules = rulesStr.split(/\s*,\s*/g);
                                var rules = rulesStr.split(/,/g);
                                rules = rules.map(function (r) {
                                    return prefix + r;
                                });
                                var rule = rules.join(",");
                                cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                                rex.lastIndex = rex.lastIndex + (prefix.length * rules.length);

                            } else {
                                var rule = prefix + match[0];
                                cssText = cssText.substring(0, match.index) + rule + cssText.substring(rex.lastIndex, cssText.length);
                                rex.lastIndex = rex.lastIndex + prefix.length;
                            }
                        }
                    }
                }
                var style = op.doc.createElement("style");
                style.setAttribute("type", "text/css");
                style.setAttribute("id", uuid);
                if (!op.notInject) {
                    var head = (op.doc.head || op.doc.getElementsByTagName("head")[0] || op.doc.documentElement);
                    head.appendChild(style);
                }

                if (style.styleSheet) {
                    var setFunc = function () {
                        style.styleSheet.cssText = cssText;
                    };
                    if (style.styleSheet.disabled) {
                        setTimeout(setFunc, 10);
                    } else {
                        setFunc();
                    }
                } else {
                    var cssTextNode = op.doc.createTextNode(cssText);
                    style.appendChild(cssTextNode);
                }
            }
            if (callback) callback(style, uuid);
            return style;
        };

        /**
         * @summary 引入外部css资源。
         * @function loadCss
         * @memberOf o2
         * @param {String|Array} [urls] 要载入的css文件url，或要载入多个css文件的urls数组。
         * @param {Object|Function} [options|callback] 载入css文件的配置参数，或者载入成功后的回调。
         * <pre><code class="language-js">options参数格式如下：
         * {
         *      "noCache":  是否使用缓存，默认true,
         *      "reload":   如果相同路径的css文件已经引入了，是否重新载入,默认为：false
         *      "sequence": 当urls参数为数组时，多个css文件是否按数组顺序依次载入,默认为false
         *      "dom":      dom element对象，表示css在这个element生效，默认是null，表示对整个document生效
         * }
         * </code></pre>
         * @param {Function} [callback] 可选参数，载入成功后的回调方法。
         * @o2syntax
         * o2.loadCss(urls, options, callback);
         * @o2syntax
         * Element.loadCss(urls, options, callback);
         * @example
         * //载入style1.css和style2.css两个文件，作用于document
         * o2.loadCss(["../css/style1.css", "../css/style2.css"], function(){
         *     //css文件已经载入
         * });
         *
         * //载入style1.css和style2.css两个文件，作用于id为content的dom对象
         * o2.loadCss(["../css/style1.css", "../css/style2.css"], {"dom": document.getELementById("content")}, function(){
         *     //css文件已经载入
         * });
         * //在Dom对象上载入style1.css和style2.css两个css
         * var node = document.getElementById("mydiv");
         * node.loadCss(["../css/style1.css", "../css/style2.css"], function(){
         *     //css文件已经载入
         * });
         */
        this.o2.loadCss = _loadCss;

        /**
         * @summary 引入文本css资源。
         * @function loadCssText
         * @memberOf o2
         * @param {String} [cssText] 要载入的css文本内容。
         * @param {Object|Function} [options|callback] 载入css文件的配置参数，或者载入成功后的回调。
         * <pre><code class="language-js">options参数格式如下：
         * {
         *      "dom":      dom element对象，表示css在这个element生效，默认是null，表示对整个document生效
         * }
         * </code></pre>
         * @param {Function} [callback] 可选参数，载入成功后的回调方法。
         * @o2syntax
         * o2.loadCssText(cssText, options, callback);
         * @o2syntax
         * Element.loadCssText(cssText, options, callback);
         * @see o2.loadCss
         * @example
         * //引入css文本，作用于id为content的dom对象
         * var csstext = ".myclass{color:#ff0000}"
         * o2.loadCssText(csstext, {"dom": document.getELementById("content")}, function(){
         *     //css已经载入
         * });
         * //引入css文本，作用于id为content的dom对象
         * var csstext = ".myclass{color:#ff0000}"
         * var node = document.getELementById("content");
         * node.loadCssText(csstext, function(){
         *     //css已经载入
         * });
         */
        this.o2.loadCssText = _loadCssText;
        if (window.Element) Element.prototype.loadCss = function (modules, options, callback) {
            var op = (_typeOf(options) === "object") ? options : {};
            var cb = (_typeOf(options) === "function") ? options : callback;
            op.dom = this;
            _loadCss(modules, op, cb);
        };
        if (window.Element) Element.prototype.loadCssText = function (cssText, options, callback) {
            var op = (_typeOf(options) === "object") ? options : {};
            var cb = (_typeOf(options) === "function") ? options : callback;
            op.dom = this;
            return _loadCssText(cssText, op, cb);
        };
        /**
         * @summary 移除通过o2.loadCss方法引入css资源。
         * @function removeCss
         * @memberOf o2
         * @param {String|Array} [urls] 要移除的的css文本url，必须与引入时所使用的url相同。
         * @o2syntax
         * o2.removeCss(urls);
         * @example
         * //载入style1.css和style2.css两个文件，作用于id为content的dom对象
         * o2.load(["../css/style1.css", "../css/style2.css"], {"dom": document.getELementById("content")}, function(){
         *     //css文件已经载入
         * });
         *
         * //移除style1.css和style2.css两个文件
         * //引入时使用了"../css/style1.css"字符串作为路径，移除时也要使用相同的字符串
         * o2.removeCss(["../css/style1.css", "../css/style2.css"])
         */
        this.o2.removeCss = _removeCss;

        //load html
        _loadSingleHtml = function (module, callback, op) {
            var url = module;
            var uid = _uuid();
            if (op.noCache) url = (url.indexOf("?") !== -1) ? url + "&v=" + uid : url + "?v=" + uid;
            var key = encodeURIComponent(url + op.doc.unid);
            if (!op.reload) if (_loadedHtml[key]) {
                Promise.resolve(_loadedHtml[key]).then(function(html){
                    if (callback) callback(html);
                });
                //if (callback) callback(_loadedHtml[key]);
                return;
            }

            var success = function (xhr) {
                var htmlObj = {"module": module, "id": uid, "data": xhr.responseText, "doc": op.doc};
                _loadedHtml[key] = htmlObj;
                if (callback) callback(htmlObj);
            };
            var failure = function () {
                console.log("Error: load html module: " + module);
                if (callback) callback();
            };
            _xhr_get(url, success, failure);
        };

        var _injectHtml = function (op, data, baseUrl) {
            if (op.bind) data = data.bindJson(op.bind);
            if (op.dom) _parseDom(op.dom, function (node) {
                var scriptText;
                var scriptSrc;
                var text = data.stripScriptSrcs(function (script) {
                    scriptSrc = script;
                });
                text = text.stripScripts(function (script) {
                    scriptText = script;
                });

                if (op.baseUrl){
                    var reg = /(?:href|src)\s*=\s*"([^"]*)"/gi;
                    var m = reg.exec(text);
                    while (m) {
                        var l = m[0].length;
                        var u = new URI(m[1], {base: baseUrl}).toString();
                        var r = m[0].replace(m[1], u);
                        var i = r.length - l;
                        var left = text.substring(0, m.index);
                        var right = text.substring(m.index + l, text.length);
                        text = left + r + right;
                        reg.lastIndex = reg.lastIndex + i;

                        m = reg.exec(text);
                    }
                }

                if (op.module) {
                    _parseModule(node, text, op);
                    //node.insertAdjacentHTML(op.position, data);
                } else {
                    node.insertAdjacentHTML(op.position, text);
                }
                if (op.evalScripts) {
                    if (scriptSrc) {
                        var scriptSrcs = scriptSrc.split(/\n/g).trim();
                        if (scriptSrcs && scriptSrcs.length) {
                            o2.load(scriptSrcs, {baseUrl: baseUrl, reload:true}, function () {
                            });
                            if (op.evalScripts && scriptText) Browser.exec(scriptText);
                        } else {
                            if (op.evalScripts && scriptText) Browser.exec(scriptText);
                        }
                    }
                }
            }, op.doc);
        };
        var _parseModule = function (node, data, op) {
            var dom = op.noConflict ? document.createElement("div") : node;
            if (op.noConflict) {
                dom.insertAdjacentHTML("afterbegin", data);
            } else {
                dom.insertAdjacentHTML(op.position, data);
            }

            var bindDataId = "";
            var bindDataNode = dom.querySelector("[data-o2-binddata]");
            if (bindDataNode){
                bindDataId = bindDataNode.dataset["o2Binddata"];
                bindDataNode.destroy();
            }

            var els = dom.querySelectorAll("[data-o2-element],[data-o2-events]");
            for (var i = 0; i < els.length; i++) {
                var el = els.item(i);
                var name = el.getAttribute("data-o2-element");
                if (name) _bindToModule(op.module, el, name.toString());
                if (el.hasAttribute("data-o2-events")) {

                    var events = el.getAttribute("data-o2-events").toString();
                    if (events) _bindToEvents(op.module, el, events, bindDataId);
                    el.removeAttribute("data-o2-events");
                }
            }

            if (op.noConflict) {
                var n = dom.firstElementChild;
                var newNode = node.insertAdjacentElement(op.position, n);
                nextNode = dom.firstElementChild;
                while (nextNode) {
                    newNode = newNode.insertAdjacentElement("afterend", nextNode);
                    nextNode = dom.firstElementChild;
                }
                dom.destroy();
            }
        };

        var _bindToEvents = function (m, node, events, bindDataId) {
            var p = node.getParent("div[data-o2-binddataid]");
            var data = null;
            if (p){
                data = _parseDataCache[p.dataset["o2Binddataid"]];
                //_parseDataCache[p.dataset["o2Binddataid"]] = null;
                //delete _parseDataCache[p.dataset["o2Binddataid"]];
            }else{
                if (bindDataId) data = (_parseDataCache[bindDataId] || null);
                //_parseDataCache[bindDataId] = null;
                //delete _parseDataCache[bindDataId];
            }
            // var data = (p) ? _parseDataCache[p.dataset["o2Binddataid"]] : (_parseDataCache["bind"] || null);

            var eventList = events.split(/\s*;\s*/);
            eventList.forEach(function (ev) {
                var evs = ev.split(/\s*:\s*/);
                if (evs.length > 1) {
                    var event = evs.shift();
                    var method = evs.shift();
                    // if (event==="o2load"){
                    //
                    //     if (m[method]) m[method].apply(m, evs.concat([new PointerEvent("o2load"), data]));
                    // }else{
                    node.addEventListener(event, function (e) {
                        if (m[method]) m[method].apply(m, evs.concat([e, data]));
                    }, false);
                    // }
                }
            });
            // try {
                node.dispatchEvent(o2.customEventLoad);
            // }catch(e){
            //     debugger;
            //     console.error(e)
            // }

        }
        var _bindToModule = function (m, node, name) {
            // if (m[name]){
            //     if (o2.typeOf(m[name])!=="array"){
            //         var tmp = m[name];
            //         m[name] = [];
            //         m[name].push(tmp);
            //     }
            //     m[name].push(node);
            // }else{
            m[name] = node;
            // }
        };
        var _loadHtml = function (modules, options, callback) {
            var ms = (_typeOf(modules) === "array") ? modules : [modules];
            var op = (_typeOf(options) === "object") ? _getHtmlOptions(options) : _getHtmlOptions(null);
            var cb = (_typeOf(options) === "function") ? options : callback;

            var thisLoaded = [];
            if (op.sequence) {
                _loadSequence(ms, cb, op, 0, thisLoaded, _loadSingleHtml, null, function (html) {
                    if (html) _injectHtml(op, html.data, html.module);
                });
            } else {
                _loadDisarray(ms, cb, op, thisLoaded, _loadSingleHtml, null, function (html) {
                    if (html) _injectHtml(op, html.data, html.module);
                });
            }
        };

        /**
         * @summary 引入外部html模板资源，并将html内容渲染到指定dom对象的某个位置。
         * @function loadHtml
         * @memberOf o2
         * @param {String|Array} [urls] 要载入的html文件url，或要载入多个html文件的urls数组。
         * @param {Object|Function} [options|callback] 载入html文件的配置参数，或者载入成功后的回调。
         * <pre><code class="language-js">options参数格式如下：
         * {
         *       "noCache":  是否使用缓存，默认true,
         *       "reload":   如果相同路径的html文件已经引入了，是否重新载入,默认为：false
         *       "sequence": 当urls参数为数组时，多个html文件是否按数组顺序依次载入,默认为false
         *       "dom": 引入html后，要将html内容渲染到的目标dom对象（具体位置由position参数确定）,
         *       "position": 渲染到的目标dom对象的位置，可以是以下值：'beforebegin' 'afterbegin' 'beforeend'（默认） 'afterend'
         *       "module": Object，与此html模板关联的对象。（在下面的例子中详细介绍）
         *       "bind": Json，与此html模板关联的Json对象。（在下面的例子中详细介绍）
         *       "evalScripts": html模板中通过&lt;script&gt;引入或内嵌的javascript，是否要执行。默认为false
         *       "baseUrl": html模板中引用连接的baseUrl，默认为空
         * }
         * </code></pre>
         * @param {Function} [callback] 可选参数，载入成功后的回调方法。
         * @o2syntax
         * o2.loadHtml(urls, options, callback);
         * @o2syntax
         * Element.loadHtml(urls, options, callback);
         * @example
         * <caption><b>样例1: </b>引入一个html模板文件，并插入到id为content的dom对象的最后</caption>
         * //引入一个html模板文件，并插入到id为content的dom对象的最后
         * var node = document.getELementById("content");
         * o2.loadHtml("../html/template.html", {"dom": node}, function(){
         *     //html文件已经载入
         * });
         *
         * //或者使用Element.loadHtml方法
         * var node = document.getELementById("content");
         * node.loadHtml("../html/template.html", function(){
         *     //html文件已经载入
         * });
         *
         * @example
         * <caption>
         *    <b>样例2: </b>本例中我们使用一个html模板来渲染展现已有的数据。我们将一个json对象绑定到要载入的模板，通过{{$.xxx}}来展现json数据。<br>
         *    html模板内容如下：
         *    <pre><code class="language-js">&lt;div&gt;{{$.title}}&lt;/div&gt;
         * &lt;div&gt;{{$.description}}&lt;/div&gt;</code></pre>
         *    然后通过以下代码来载入html模板：
         * </caption>
         * var json = {
         *     "title": "这是标题",
         *     "description": "描述内容"
         * };
         * var node = document.getELementById("content");
         * o2.loadHtml("../html/template.html", {"dom": node, "bind": json}, function(){
         *     //html文件已经载入
         *     //载入后，node对象的html如下：
         *     //<div>
         *     //   ......
         *     //   <div>这是标题</div>;
         *     //   <div>描述内容</div>
         *     //</div>
         * });
         * @example
         * <caption>
         *    <b>样例3: </b>本例中我们除了使用一个html模板来渲染展现已有的json数据，还给已有模块绑定上html模板中的一个dom对象，并监听html模板中的指定元素的事件。<br>
         *    html模板内容如下：
         *    <pre><code class="language-js">&lt;div&gt;{{$.title}}&lt;/div&gt;
         * &lt;div&gt;{{$.description}}&lt;/div&gt;
         * &lt;div data-o2-element="myElement"&gt;&lt;/div&gt;
         * &lt;button data-o2-events="click:clickMe:{{$.info}};mouseover:overMe;mouseout:outMe"&gt;绑定了事件的按钮&lt;/button&gt;
         * </code></pre>
         *    然后通过以下代码来载入html模板：
         * </caption>
         * //json数据
         * var json = {
         *     "title": "这是标题",
         *     "description": "描述内容",
         *     "info": "按钮点击后的信息"
         * };
         * //业务模块
         * var module = {
         *     //当button按钮被点击时，会调用此方法
         *     clickMe: function(info, e){
         *         this.myElement.insertAdjacentText("afterbegin", "button clicked " + info);
         *     },
         *     //当鼠标移动到button按钮时，会调用此方法
         *     overMe: function(e){
         *         console.log("button over");
         *         console.log(e);  //MouseEvent对象
         *     },
         *     //当鼠标移出button按钮时，会调用此方法
         *     outMe: function(e){
         *         console.log("button out");
         *         console.log(e);  //MouseEvent对象
         *     }
         * };
         *
         * var node = document.getELementById("content");
         * node.loadHtml("../html/template.html", {"bind": json, "module": module}, function(){
         *     //html文件已经载入
         *     //载入后，node对象的html如下：
         *     //<div>
         *     //   ......
         *     //   <div>这是标题</div>;
         *     //   <div>描述内容</div>
         *     //   <div data-o2-element="myElement"></div>
         *     //   <button>绑定了事件的按钮</button>
         *     //</div>
         *     console.log(module.myElement);   //Dom对象：<div data-o2-element="myElement"></div>
         *                                      //html模板中的div对象，被绑定到了module对象的myElement属性上
         *
         * });
         * @example
         * <caption>
         *    <b>样例4: </b>本例中演示了html模板中each和if的用法。<br>
         *    html模板内容如下：
         *    <pre><code class="language-js">&lt;div&gt;
         *    {{each $.items}}
         *        &lt;div&gt;{{$.title}}&lt;/div&gt;
         *        &lt;div&gt;{{$.description}}&lt;/div&gt;
         *        {{if $.title=="这是标题2"}}
         *             &lt;div&gt;这是标题2的个性化内容&lt;/div&gt;
         *        {{end if}}
         *        &lt;button data-o2-events="click:clickMe:{{$.info}}"&gt;绑定了事件的按钮&lt;/button&gt;
         *        &lt;hr&gt;
         *    {{end each}}
         * &lt;/div&gt;</code></pre>
         *    然后通过以下代码来载入html模板：
         * </caption>
         * //json数据
         * var json = {
         *       "items": [{
         *           "title": "这是标题1",
         *           "description": "描述内容1",
         *           "info": "按钮点击后的信息1"
         *       },{
         *           "title": "这是标题2",
         *           "description": "描述内容2",
         *           "info": "按钮点击后的信息2"
         *       },{
         *           "title": "这是标题3",
         *           "description": "描述内容3",
         *           "info": "按钮点击后的信息3"
         *       }]
         * };
         * //业务模块
         * var module = {
         *     //当button按钮被点击时，会调用此方法
         *     clickMe: function(info, e){
         *         alert(info);
         *     }
         * };
         * var node = document.getELementById("content");
         * node.loadHtml("../html/template.html", {"bind": json, "module": module}, function(){
         *     //html文件已经载入
         *     //载入后，node对象的html如下：
         *     //<div>
         *     //   <div>这是标题1</div>;
         *     //   <div>描述内容1</div>
         *     //   <button>绑定了事件的按钮</button>
         *     //   <hr>
         *     //</div>
         *     //<div>
         *     //   <div>这是标题2</div>;
         *     //   <div>描述内容2</div>
         *     //   <div>描这是标题2的个性化内容</div>
         *     //   <button>绑定了事件的按钮</button>
         *     //   <hr>
         *     //</div>
         *     //<div>
         *     //   <div>这是标题3</div>;
         *     //   <div>描述内容3</div>
         *     //   <button>绑定了事件的按钮</button>
         *     //   <hr>
         *     //</div>
         *     //当点击按钮时，会alert对应的items的info数据
         * });
         */
        this.o2.loadHtml = _loadHtml;
        if (window.Element) Element.prototype.loadHtml = function (modules, options, callback) {
            var op = (_typeOf(options) === "object") ? options : {};
            var cb = (_typeOf(options) === "function") ? options : callback;
            op.dom = this;
            _loadHtml(modules, op, cb);
        };

        /**
         * @summary 解析html文本内容，并将html内容渲染到指定dom对象的某个位置，与loadHtml相同，只是传入html内容，而不是获取html的url。
         * @function loadHtmlText
         * @memberOf o2
         * @param {String} [html] 要解析的html文本内容。
         * @param {Object} [options] 载入html文件的配置参数。
         * <pre><code class="language-js">options参数格式如下：
         * {
         *       "noCache":  是否使用缓存，默认true,
         *       "reload":   如果相同路径的html文件已经引入了，是否重新载入,默认为：false
         *       "sequence": 当urls参数为数组时，多个html文件是否按数组顺序依次载入,默认为false
         *       "dom": 引入html后，要将html内容渲染到的目标dom对象（具体位置由position参数确定）,
         *       "position": 渲染到的目标dom对象的位置，可以是以下值：'beforebegin' 'afterbegin' 'beforeend'（默认） 'afterend'
         *       "module": Object，与此html模板关联的对象。（在下面的例子中详细介绍）
         *       "bind": Json，与此html模板关联的Json对象。（在下面的例子中详细介绍）
         *       "evalScripts": html模板中通过&lt;script&gt;引入或内嵌的javascript，是否要执行。默认为false
         *       "baseUrl": html模板中引用连接的baseUrl，默认为空
         * }
         * </code></pre>
         * @o2syntax
         * o2.loadHtmlText(html, options);
         * @o2syntax
         * Element.loadHtmlText(html, options);
         * @see o2.loadHtml
         * @example
         * var html = "<div>{{$.title}}</div>"
         * var json = {"title": "标题"};
         * var node = document.getELementById("content");
         * o2.loadHtmlText(html, {"dom": node, "bind": json});
         *
         * //获
         * node.loadHtmlText(html, {"bind": json});
         */
        this.o2.loadHtmlText = this.o2.injectHtml = function (html, options) {
            var op = (_typeOf(options) === "object") ? _getHtmlOptions(options) : _getHtmlOptions(null);
            _injectHtml(op, html);
        };
        if (window.Element) Element.prototype.loadHtmlText = Element.prototype.injectHtml = function (html, options) {
            var op = (_typeOf(options) === "object") ? options : {};
            op.dom = this;
            op.position = (options && options.position) || "beforeend"
            _injectHtml(op, html);
        };

        //load all
        _loadAll = function (modules, options, callback) {
            //var ms = (_typeOf(modules)==="array") ? modules : [modules];
            var op = (_typeOf(options) === "object") ? _getAllOptions(options) : _getAllOptions(null);
            var cb = (_typeOf(options) === "function") ? options : callback;

            var ms, htmls, styles, sctipts;
            var _htmlLoaded = (!modules.html), _cssLoaded = (!modules.css), _jsLoaded = (!modules.js);
            var _checkloaded = function () {
                if (_htmlLoaded && _cssLoaded && _jsLoaded) if (cb) cb(htmls, styles, sctipts);
            };
            if (modules.html) {
                _loadHtml(modules.html, op, function (h) {
                    htmls = h;
                    _htmlLoaded = true;
                    _checkloaded();
                });
            }
            if (modules.css) {
                _loadCss(modules.css, op, function (s) {
                    styles = s;
                    _cssLoaded = true;
                    _checkloaded();
                });
            }
            if (modules.js) {
                _load(modules.js, op, function (s) {
                    sctipts = s;
                    _jsLoaded = true;
                    _checkloaded();
                });
            }
        };
        /**
         * @summary 同时载入js文件、css文件和html模板文件，相当于同时调用了o2.load, o2.loadCss 和 o2.loadHtml。
         * @function loadAll
         * @memberOf o2
         * @param {Object} [modules] 要解析的html文本内容。
         * <pre><code class="language-js">modules参数格式如下：
         * {
         *       "js": {String|Array} 要载入的js文件url，或要载入多个js文件的urls数组。
         *       "css": {String|Array} 要载入的css文件url，或要载入多个css文件的urls数组。
         *       "html": {String|Array} 要载入的html文件url，或要载入多个html文件的urls数组。
         * }
         * </code></pre>
         * @param {Object} [options] 载入html文件的配置参数。
         * <pre><code class="language-js">options参数格式如下：
         * {
         *       "noCache":  是否使用缓存，默认true,
         *       "reload":   如果相同路径的html文件已经引入了，是否重新载入,默认为：false
         *       "sequence": 当urls参数为数组时，多个html文件是否按数组顺序依次载入,默认为false
         *       "dom": 引入html后，要将html内容渲染到的目标dom对象（具体位置由position参数确定）,
         *       "position": 渲染到的目标dom对象的位置，可以是以下值：'beforebegin' 'afterbegin' 'beforeend'（默认） 'afterend'
         *       "module": Object，与此html模板关联的对象。（在下面的例子中详细介绍）
         *       "bind": Json，与此html模板关联的Json对象。（在下面的例子中详细介绍）
         *       "evalScripts": html模板中通过&lt;script&gt;引入或内嵌的javascript，是否要执行。默认为false
         *       "baseUrl": html模板中引用连接的baseUrl，默认为空
         * }
         * </code></pre>
         * @param {Function} [callback] 可选参数，载入成功后的回调方法。
         * @o2syntax
         * o2.loadAll(modules, options, callback);
         * @o2syntax
         * Element.loadAll(modules, options, callback);
         * @see o2.load
         * @see o2.loadCss
         * @see o2.loadHtml
         * @example
         * var html = "<div>{{$.title}}</div>"
         * var json = {"title": "标题"};
         * var node = document.getELementById("content");
         * o2.loadAll({
         *     "js": ["file1.js", "file2.js"],
         *     "css": ["style.css"],
         *     "html": "template.html",
         * }, {"dom": node, "bind": json}, function(){
         *     //载入完成后的回调
         * });
         */
        this.o2.loadAll = _loadAll;
        if (window.Element) Element.prototype.loadAll = function (modules, options, callback) {
            var op = (_typeOf(options) === "object") ? options : {};
            var cb = (_typeOf(options) === "function") ? options : callback;
            op.dom = this;
            _loadAll(modules, op, cb);
        };

        var _getIfBlockEnd = function (v) {
            var rex = /(\{\{if\s+)|(\{\{\s*end if\s*\}\})/gmi;
            var rexEnd = /\{\{\s*end if\s*\}\}/gmi;
            var subs = 1;
            while ((match = rex.exec(v)) !== null) {
                var fullMatch = match[0];
                if (fullMatch.search(rexEnd) !== -1) {
                    subs--;
                    if (subs == 0) break;
                } else {
                    subs++
                }
            }
            if (match) return {"codeIndex": match.index, "lastIndex": rex.lastIndex};
            return {"codeIndex": v.length - 1, "lastIndex": v.length - 1};
        }
        var _getEachBlockEnd = function (v) {
            var rex = /(\{\{each\s+)|(\{\{\s*end each\s*\}\})/gmi;
            var rexEnd = /\{\{\s*end each\s*\}\}/gmi;
            var subs = 1;
            while ((match = rex.exec(v)) !== null) {
                var fullMatch = match[0];
                if (fullMatch.search(rexEnd) !== -1) {
                    subs--;
                    if (subs == 0) break;
                } else {
                    subs++;
                }
            }
            if (match) return {"codeIndex": match.index, "lastIndex": rex.lastIndex};
            return {"codeIndex": v.length - 1, "lastIndex": v.length - 1};
        }

        var _parseDataCache = {};
        var _parseHtml = function (str, json, i) {
            var v = str;
            var r = (Math.random() * 1000000).toInt().toString();
            while (_parseDataCache[r]) r = (Math.random() * 1000000).toInt().toString();
            if (i || i===0) {
                _parseDataCache[r] = json;
                v = "<div data-o2-binddataid='" + r + "'>" + str + "</div>";
            }else{
                var regex = new RegExp("\\<[\\s\\S]+\\>");
                if (regex.test(str)){
                    if (json.data){
                        _parseDataCache[r] = json.data;
                        v = str+"<div style='display: none' data-o2-binddata='" + r + "'></div>"
                    }
                }
            }
            var rex = /(\{\{\s*)[\s\S]*?(\s*\}\})/gmi;

            var match;
            while ((match = rex.exec(v)) !== null) {
                var fullMatch = match[0];
                var offset = 0;

                //if statement begin
                if (fullMatch.search(/\{\{if\s+/i) !== -1) {
                    //找到对应的end if
                    var condition = fullMatch.replace(/^\{\{if\s*/i, "");
                    condition = condition.replace(/\s*\}\}$/i, "");
                    var flag = _jsonText(json, condition, "boolean");

                    var tmpStr = v.substring(rex.lastIndex, v.length);
                    var endIfIndex = _getIfBlockEnd(tmpStr);
                    if (flag) { //if 为 true
                        var parseStr = _parseHtml(tmpStr.substring(0, endIfIndex.codeIndex), json);
                        var vLeft = v.substring(0, match.index);
                        var vRight = v.substring(rex.lastIndex + endIfIndex.lastIndex, v.length);
                        v = vLeft + parseStr + vRight;
                        offset = parseStr.length - fullMatch.length;
                    } else {
                        v = v.substring(0, match.index) + v.substring(rex.lastIndex + endIfIndex.lastIndex, v.length);
                        offset = 0 - fullMatch.length;
                    }
                } else if (fullMatch.search(/\{\{each\s+/) !== -1) { //each statement
                    var itemString = fullMatch.replace(/^\{\{each\s*/, "");
                    itemString = itemString.replace(/\s*\}\}$/, "");
                    var eachValue = _jsonText(json, itemString, "object");

                    var tmpEachStr = v.substring(rex.lastIndex, v.length);
                    var endEachIndex = _getEachBlockEnd(tmpEachStr);

                    var parseEachStr = tmpEachStr.substring(0, endEachIndex.codeIndex);
                    var eachResult = "";
                    if (eachValue && _typeOf(eachValue) === "array") {
                        for (var i = 0; i < eachValue.length; i++) {
                            eachValue[i]._ = json;
                            eachResult += _parseHtml(parseEachStr, eachValue[i], i);
                        }
                        var eLeft = v.substring(0, match.index);
                        var eRight = v.substring(rex.lastIndex + endEachIndex.lastIndex, v.length);
                        v = eLeft + eachResult + eRight;
                        offset = eachResult.length - fullMatch.length;
                    } else {
                        v = v.substring(0, match.index) + v.substring(rex.lastIndex + endEachIndex.lastIndex, v.length);
                        offset = 0 - fullMatch.length;
                    }

                } else { //text statement
                    var text = fullMatch.replace(/^\{\{\s*/, "");
                    text = text.replace(/\}\}\s*$/, "");
                    var value = _jsonText(json, text);
                    offset = value.length - fullMatch.length;
                    v = v.substring(0, match.index) + value + v.substring(rex.lastIndex, v.length);
                }
                rex.lastIndex = rex.lastIndex + offset;
            }
            return v;
        };
        var _jsonText = function (json, text, type) {
            try {
                var $ = json;
                var f = eval("(function($){\n return " + text + ";\n})");
                returnValue = f.apply(json, [$]);
                if (returnValue === undefined) returnValue = "";
                if (type === "boolean") return (!!returnValue);
                if (type === "object") return returnValue;
                returnValue = returnValue.toString();
                returnValue = returnValue.replace(/\&/g, "&amp;");
                returnValue = returnValue.replace(/>/g, "&gt;");
                returnValue = returnValue.replace(/</g, "&lt;");
                returnValue = returnValue.replace(/\"/g, "&quot;");
                return returnValue || "";
            } catch (e) {
                if (type === "boolean") return false;
                if (type === "object") return null;
                return "";
            }
        };

        o2.bindJson = function (str, json) {
            return _parseHtml(str, json);
        };
        String.prototype.bindJson = function (json) {
            return _parseHtml(this, json);
        };

        var _createComponent = function(o, res){
            o.multitask = false;
        };
        var _loadComponent = function(o, res){
            o.loading = new Promise(function(resolve){
                o2.loadAll(res, {evalScripts:true, url: true}, function(){ resolve(); });
            });
        };
        o2.component = function(name, res){
            o2.xApplication = o2.xApplication || {};
            var names = name.split(".");
            var o = o2.xApplication;
            names.forEach(function(n){
                o = o[n] = o[n] || {};
            });
            if (o2.typeOf(res)==="object"){
                _loadComponent(o, res)
            }else{
                _createComponent(o, res);
            }
        }

    })();

    /** ***** BEGIN LICENSE BLOCK *****
     * |------------------------------------------------------------------------------|
     * | O2OA 活力办公 创意无限    o2.core.js                                            |
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
    (function () {
        var _Class = {
            create: function (options) {
                // var newClass = function() {
                //     this.initialize.apply(this, arguments);
                // };
                return _copyPrototype(function () {
                    return this.initialize.apply(this, arguments) || this;
                }, options);
                //return newClass;
            }
        };
        var _copyPrototype = function (currentNS, props) {
            if (!props) {
                return currentNS;
            }
            if (!currentNS) {
                return currentNS;
            }
            if ((typeof currentNS).toLowerCase() === "object") {
                for (var prop in props) {
                    currentNS[prop] = props[prop];
                }
            }
            if ((typeof currentNS).toLowerCase() === "function") {
                for (var propfun in props) {
                    currentNS.prototype[propfun] = props[propfun];
                }
            }
            return currentNS;
        };

        var _loaded = {};

        var _requireJs = function (url, callback, async, compression, module) {
            var key = encodeURIComponent(url);
            if (_loaded[key]) {
                o2.runCallback(callback, "success", [module]);
                return "";
            }

            var jsPath = (compression || !this.o2.session.isDebugger) ? url.replace(/\.js/, ".min.js") : url;
            jsPath = (jsPath.indexOf("?") !== -1) ? jsPath + "&v=" + this.o2.version.v : jsPath + "?v=" + this.o2.version.v;

            if (window.importScripts) {
                window.importScripts(o2.filterUrl(jsPath));
                o2.runCallback(callback, "success", [module]);
            } else {
                var xhr = new Request({
                    url: o2.filterUrl(jsPath), async: async, method: "get",
                    onSuccess: function () {
                        //try{
                        _loaded[key] = true;
                        o2.runCallback(callback, "success", [module]);
                        //}catch (e){
                        //    o2.runCallback(callback, "failure", [e]);
                        //}
                    },
                    onFailure: function (r) {
                        var rex = /lp\/.+\.js/;
                        if (rex.test(url)) {
                            var zhcnUrl = url.replace(rex, "lp/zh-cn.js");
                            if (zhcnUrl !== url) {
                                _requireJs(zhcnUrl, callback, async, compression, module)
                            } else {
                                o2.runCallback(callback, "failure", [r]);
                            }
                        } else {
                            o2.runCallback(callback, "failure", [r]);
                        }
                    }
                });
                xhr.send();
            }
        };
        var _requireSingle = function (module, callback, async, compression) {
            if (o2.typeOf(module) === "array") {
                _requireAppSingle(module, callback, async, compression);
            } else {
                module = module.replace("MWF.", "o2.");
                var levels = module.split(".");
                if (levels[levels.length - 1] === "*") levels[levels.length - 1] = "package";
                levels.shift();
                var o = o2;
                var i = 0;
                while (o && i < levels.length) {
                    o = o[levels[i]];
                    i++
                }
                if (!o) {
                    var jsPath = this.o2.session.path;
                    jsPath += "/" + levels.join("/") + ".js";
                    var loadAsync = (async !== false);
                    _requireJs(jsPath, callback, loadAsync, compression, module);
                } else {
                    o2.runCallback(callback, "success", [module]);
                }
            }
        };
        var _requireSequence = function (fun, module, thisLoaded, thisErrorLoaded, callback, async, compression) {
            var m = module.shift();
            fun(m, {
                "onSuccess": function (m) {
                    thisLoaded.push(m);
                    o2.runCallback(callback, "every", [m]);
                    if (module.length) {
                        _requireSequence(fun, module, thisLoaded, thisErrorLoaded, callback, async, compression);
                    } else {
                        if (thisErrorLoaded.length) {
                            o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                        } else {
                            o2.runCallback(callback, "success", [thisLoaded, thisErrorLoaded]);
                        }
                    }
                },
                "onFailure": function () {
                    thisErrorLoaded.push(module[i]);
                    o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                }
            }, async, compression);
        };
        var _requireDisarray = function (fun, module, thisLoaded, thisErrorLoaded, callback, async, compression) {
            for (var i = 0; i < module.length; i++) {
                fun(module[i], {
                    "onSuccess": function (m) {
                        thisLoaded.push(m);
                        o2.runCallback(callback, "every", [m]);
                        if ((thisLoaded.length + thisErrorLoaded.length) === module.length) {
                            if (thisErrorLoaded.length) {
                                o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                            } else {
                                o2.runCallback(callback, "success", [thisLoaded, thisErrorLoaded]);
                            }
                        }
                    },
                    "onFailure": function () {
                        thisErrorLoaded.push(module[i]);
                        o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                    }
                }, async, compression);
            }
        };
        var _require = function (module, callback, async, sequence, compression) {
            var type = typeOf(module);
            if (type === "array") {
                var sql = !!sequence;
                var thisLoaded = [];
                var thisErrorLoaded = [];
                if (sql) {
                    _requireSequence(_requireSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);

                } else {
                    _requireDisarray(_requireSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);
                }
            }
            if (type === "string") {
                _requireSingle(module, callback, async, compression);
            }
        };

        var _requireAppSingle = function (modules, callback, async, compression) {
            var module = modules[0];
            var clazz = modules[1];
            var levels = module.split(".");

            var o = o2.xApplication;
            var i = 0;
            while (o && i < levels.length) {
                o = o[levels[i]];
                i++
            }
            if (o) o = o[clazz || "Main"];

            if (!o) {
                //levels.shift();
                var root = "x_component_" + levels.join("_");
                var clazzName = clazz || "Main";
                var path = "../" + root + "/" + clazzName.replace(/\./g, "/") + ".js";
                var loadAsync = (async !== false);
                _requireJs(path, callback, loadAsync, compression);
            } else {
                o2.runCallback(callback, "success");
            }
        };
        var _requireApp = function (module, clazz, callback, async, sequence, compression) {
            var type = typeOf(module);
            if (type === "array") {
                var sql = !!sequence;
                var thisLoaded = [];
                var thisErrorLoaded = [];
                if (sql) {
                    _requireSequence(_requireAppSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);

                } else {
                    _requireDisarray(_requireAppSingle, module, thisLoaded, thisErrorLoaded, callback, async, compression);
                }
            }
            if (type === "string") {
                var modules = [module, clazz];
                _requireAppSingle(modules, callback, async, compression);
            }
        };

        JSON = window.JSON || {};
        var _json = JSON;
        _json.get = function (url, callback, async, nocache) {
            var loadAsync = (async !== false);
            var noJsonCache = (nocache === true);
            if (url.indexOf("config.json") > -1){
                noJsonCache = true;
            }

            url = (url.indexOf("?") !== -1) ? url + "&v=" + o2.version.v : url + "?v=" + o2.version.v;

            var json = null;
            var res = new Request.JSON({
                url: o2.filterUrl(url),
                secure: false,
                method: "get",
                noCache: noJsonCache,
                async: loadAsync,
                withCredentials: true,
                onSuccess: function (responseJSON, responseText) {
                    json = responseJSON;
                    if (typeOf(callback).toLowerCase() === 'function') {
                        callback(responseJSON, responseText);
                    } else {
                        o2.runCallback(callback, "success", [responseJSON, responseText]);
                    }
                }.bind(this),
                onFailure: function (xhr) {
                    o2.runCallback(callback, "requestFailure", [xhr]);
                }.bind(this),
                onError: function (text, error) {
                    o2.runCallback(callback, "error", [text, error]);
                }.bind(this)
            });
            res.send();
            return json;
        };
        _json.getJsonp = function (url, callback, async, callbackKey) {
            var loadAsync = (async !== false);

            var callbackKeyWord = callbackKey || "callback";

            url = (url.indexOf("?") !== -1) ? url + "&v=" + o2.version.v : url + "?v=" + o2.version.v;
            var res = new Request.JSONP({
                url: o2.filterUrl(url),
                secure: false,
                method: "get",
                noCache: true,
                async: loadAsync,
                callbackKey: callbackKeyWord,
                onSuccess: function (responseJSON, responseText) {
                    o2.runCallback(callback, "success", [responseJSON, responseText]);
                }.bind(this),
                onFailure: function (xhr) {
                    o2.runCallback(callback, "requestFailure", [xhr]);
                }.bind(this),
                onError: function (text, error) {
                    o2.runCallback(callback, "error", [text, error]);
                }.bind(this)
            });
            res.send();
        };


        var _loadLP = function (name) {
            var jsPath = o2.session.path;
            jsPath = jsPath + "/lp/" + name + ".js";
            var r = new Request({
                url: o2.filterUrl(jsPath),
                async: false,
                method: "get",
                onSuccess: function (responseText) {
                    try {
                        Browser.exec(responseText);
                    } catch (e) {
                    }
                },
                onFailure: function (xhr) {
                    if (name != "zh-cn") {
                        _loadLP("zh-cn");
                    } else {
                        throw "loadLP Error: " + xhr.responseText;
                    }
                }
            });
            r.send();
        };

        var _cacheUrls = (Browser.name == "ie") ? [] : [
            /jaxrs\/form\/workorworkcompleted\/.+/ig,
            /jaxrs\/form\/.+/ig,
            /jaxrs\/script\/.+\/app\/.+\/imported/ig,
            /jaxrs\/script\/portal\/.+\/name\/.+\/imported/ig,
            /jaxrs\/script\/.+\/application\/.+\/imported/ig,
            /jaxrs\/page\/.+\/portal\/.+/ig,
            /jaxrs\/document\/.+/ig,
            /jaxrs\/applicationdict\/.+/ig,
            /jaxrs\/custom\/.+/ig,
            /jaxrs\/definition\/idea.+/ig,
            /jaxrs\/distribute\/assemble\/source\/.+/ig,
        ];
        // _restful_bak = function(method, address, data, callback, async, withCredentials, cache){
        //     var loadAsync = (async !== false);
        //     var credentials = (withCredentials !== false);
        //     address = (address.indexOf("?")!==-1) ? address+"&v="+o2.version.v : address+"?v="+o2.version.v;
        //     //var noCache = cache===false;
        //     var noCache = !cache;
        //
        //
        //     //if (Browser.name == "ie")
        //     if (_cacheUrls.length){
        //         for (var i=0; i<_cacheUrls.length; i++){
        //             _cacheUrls[i].lastIndex = 0;
        //             if (_cacheUrls[i].test(address)){
        //                 noCache = false;
        //                 break;
        //             }
        //         }
        //     }
        //     //var noCache = false;
        //     var res = new Request.JSON({
        //         url: o2.filterUrl(address),
        //         secure: false,
        //         method: method,
        //         emulation: false,
        //         noCache: noCache,
        //         async: loadAsync,
        //         withCredentials: credentials,
        //         onSuccess: function(responseJSON, responseText){
        //             // var xToken = this.getHeader("authorization");
        //             // if (!xToken) xToken = this.getHeader("x-token");
        //             var xToken = this.getHeader("x-token");
        //             if (xToken){
        //                 if (window.layout){
        //                     if (!layout.session) layout.session = {};
        //                     layout.session.token = xToken;
        //                 }
        //             }
        //             o2.runCallback(callback, "success", [responseJSON]);
        //         },
        //         onFailure: function(xhr){
        //             o2.runCallback(callback, "requestFailure", [xhr]);
        //         }.bind(this),
        //         onError: function(text, error){
        //             o2.runCallback(callback, "error", [text, error]);
        //         }.bind(this)
        //     });
        //
        //     res.setHeader("Content-Type", "application/json; charset=utf-8");
        //     res.setHeader("Accept", "text/html,application/json,*/*");
        //     if (window.layout) {
        //         if (layout["debugger"]){
        //             res.setHeader("x-debugger", "true");
        //         }
        //         if (layout.session && layout.session.user){
        //             if (layout.session.user.token) {
        //                 res.setHeader("x-token", layout.session.user.token);
        //                 res.setHeader("authorization", layout.session.user.token);
        //             }
        //         }
        //     }
        //     //Content-Type	application/x-www-form-urlencoded; charset=utf-8
        //     res.send(data);
        //     return res;
        // };
        var _resGetQueue = {};
        var _checkRestful = function(address, callback){
            if (_resGetQueue[address]){
                var resPromise = _resGetQueue[address];
                var p = new Promise(function(resolve, reject){
                    resPromise.then(function(){
                        resolve(resPromise.json);
                    }, function(){
                        reject(resPromise.err);
                    });
                });

                if (_resGetQueue[address].res) p.res = _resGetQueue[address].res;
                if (_resGetQueue[address].actionWorker) p.actionWorker = _resGetQueue[address].actionWorker;

                return p;
            }
            return false
        }

        _restful = function (method, address, data, callback, async, withCredentials, cache) {
            var p = null;
            if (method.toLowerCase()==="get" && async!==false){
                p = _checkRestful(address, callback);
                //if (p) return p;
            }

            var _addr = address;

            var loadAsync = (async !== false);
            var credentials = (withCredentials !== false);
            address = (address.indexOf("?") !== -1) ? address + "&v=" + o2.version.v : address + "?v=" + o2.version.v;
            //var noCache = cache===false;
            var noCache = !cache;

            //if (Browser.name == "ie")
            if (_cacheUrls.length) {
                for (var i = 0; i < _cacheUrls.length; i++) {
                    _cacheUrls[i].lastIndex = 0;
                    if (_cacheUrls[i].test(address)) {
                        noCache = false;
                        break;
                    }
                }
            }

            var useWebWorker = (window.layout && layout.config && layout.config.useWebWorker);
            //var noCache = false;
            if (!loadAsync || !useWebWorker) {
                var res;
                if (!p) p = new Promise(function (resolve, reject) {
                    res = new Request.JSON({
                        url: o2.filterUrl(address),
                        secure: false,
                        method: method,
                        emulation: false,
                        noCache: noCache,
                        async: loadAsync,
                        withCredentials: credentials,
                        onSuccess: function (responseJSON, responseText) {
                            // var xToken = this.getHeader("authorization");
                            // if (!xToken) xToken = this.getHeader("x-token");
                            _resGetQueue[_addr] = null;
                            delete _resGetQueue[_addr];

                            var xToken = this.getHeader(o2.tokenName);
                            if (xToken) {
                                if (window.layout) {
                                    if (!layout.session) layout.session = {};
                                    layout.session.token = xToken;
                                }
                                if (layout.config && layout.config.sessionStorageEnable && window.sessionStorage) window.sessionStorage.setItem("o2LayoutSessionToken", xToken);
                            }
                            if (!loadAsync){
                                var r = o2.runCallback(callback, "success", [responseJSON], null);
                                resolve(r || responseJSON);
                            }else{
                                resolve(responseJSON);
                            }

                            //resolve(responseJSON);
                            //return o2.runCallback(callback, "success", [responseJSON],null, resolve);
                        },
                        onFailure: function (xhr) {
                            _resGetQueue[_addr] = null;
                            delete _resGetQueue[_addr];
                            if (!loadAsync){
                                var r = o2.runCallback(callback, "failure", [xhr, "", ""], null);
                                reject((r) ? r : {"xhr": xhr, "text": "", "error": "error"});
                            }else{
                                reject({"xhr": xhr, "text": "", "error": "error"});
                            }
                        }.bind(this),
                        onError: function (text, error) {
                            _resGetQueue[_addr] = null;
                            delete _resGetQueue[_addr];
                            if (!loadAsync){
                                var r = o2.runCallback(callback, "failure", [text, error], null);
                                reject((r) ? r : {"xhr": xhr, "text": text, "error": "error"});
                            }else{
                                reject({"xhr": xhr, "text": text, "error": "error"});
                            }
                        }.bind(this),
                        onComplete: function(){
                            _resGetQueue[_addr] = null;
                            delete _resGetQueue[_addr];
                        },
                        onCancel: function(){
                            _resGetQueue[_addr] = null;
                            delete _resGetQueue[_addr];
                        }
                    });

                    res.setHeader("Content-Type", "application/json; charset=utf-8");
                    res.setHeader("Accept", "text/html,application/json,*/*");
                    res.setHeader("Accept-Language", o2.languageName);

                    if (window.layout) {
                        if (layout["debugger"]) {
                            res.setHeader("x-debugger", "true");
                        }
                        var token = (layout.config && layout.config.sessionStorageEnable && window.sessionStorage) ? window.sessionStorage.getItem("o2LayoutSessionToken") : "";
                        if (!token) {
                            if (layout.session && (layout.session.user || layout.session.token)) {
                                token = layout.session.token;
                                if (!token && layout.session.user && layout.session.user.token) token = layout.session.user.token;
                            }
                        }
                        if (token) {
                            //res.setHeader(o2.tokenName, token);
                            res.setHeader("Authorization", token);
                        }
                    }
                    res.send(data);
                }.bind(this)).catch(function (err) {
                    throw err;
                });
                //     .then(function (responseJSON) {
                //
                //     _resGetQueue[address].events.each(function(e){
                //         var r = o2.runCallback(e.callback, "success", [responseJSON], null);
                //         if (e.promise){
                //             e.promise
                //         }
                //     });
                //
                //     return responseJSON;
                // }, function(err){
                //     var r = o2.runCallback(callback, "failure", [xhr, "", ""], null);
                //     return r || err;
                // }).catch(function (err) {
                //     throw err;
                //     //return Promise.reject(err);
                // });
                var oReturn = p;
                //oReturn.res = res;
                var resPromise = Promise.resolve(oReturn).then(function(json){
                    if (!loadAsync) return json;
                    resPromise.json = json;
                    var r = o2.runCallback(callback, "success", [json], null);
                    if (r) return r;
                }, function(err){
                    if (!loadAsync) return err;
                    resPromise.err = err;
                    var r = o2.runCallback(callback, "failure", [err.xhr, err.text, err.error], null);
                    if (r) return Promise.reject(r);
                    return Promise.reject(err);
                }).catch(function (err) { throw err;});
                resPromise.res = res;

                if (loadAsync) _resGetQueue[_addr] = resPromise;

                return resPromise;
            } else {
                var workerMessage = {
                    method: method,
                    noCache: noCache,
                    loadAsync: loadAsync,
                    credentials: credentials,
                    address: o2.filterUrl(address),
                    body: data,
                    debug: (window.layout && layout["debugger"]),
                    token: (window.layout && layout.session && layout.session.user) ? layout.session.user.token : "",
                    tokenName: o2.tokenName
                }
                var actionWorker = new Worker("../o2_core/o2/actionWorker.js");
                if (!p) p = new Promise(function (s, f) {
                    actionWorker.onmessage = function (e) {

                        _resGetQueue[_addr] = null;
                        delete _resGetQueue[_addr];

                        result = e.data;
                        if (result.type === "done") {
                            var xToken = result.data.xToken;
                            if (xToken) {
                                if (window.layout) {
                                    if (!layout.session) layout.session = {};
                                    layout.session.token = xToken;
                                }
                            }
                            s(result.data);
                            //o2.runCallback(callback, "success", [result.data], null, s);
                        } else {
                            f(result.data);
                            //o2.runCallback(callback, "failure", [result.data], null, f);
                        }
                        actionWorker.terminate();
                    }
                    actionWorker.postMessage(workerMessage);
                }.bind(this));

                // p = p.then(function (data) {
                //     return o2.runCallback(callback, "success", [data], null);
                // }, function (data) {
                //     return o2.runCallback(callback, "failure", [data], null);
                // });

                //var oReturn = (callback.success && callback.success.addResolve) ? callback.success : callback;
                var oReturn = p;
                //oReturn.actionWorker = actionWorker;

                var resPromise = Promise.resolve(oReturn).then(function(json){
                    resPromise.json = json;
                    var r = o2.runCallback(callback, "success", [json], null);
                    if (r) return r;
                }, function(err){
                    resPromise.err = err;
                    var r = o2.runCallback(callback, "failure", [err], null);
                    if (r) return r;
                }).catch(function (err) { throw err;});
                resPromise.actionWorker = actionWorker;

                _resGetQueue[_addr] = resPromise;
                return resPromise;
                //return oReturn;
                //return callback;
            }
            //return res;
        };

        var _release = function (o) {
            var type = typeOf(o);
            switch (type) {
                case "object":
                    for (var k in o) {
                        //if (o[k] && o[k].destroy) o[k].destroy();
                        o[k] = null;
                    }
                    break;
                case "array":
                    for (var i = 0; i < o.length; i++) {
                        _release(o[i]);
                        if (o[i]) o[i] = null;
                    }
                    break;
            }
        };

        var _defineProperties = Object.defineProperties || function (obj, properties) {
            function convertToDescriptor(desc) {
                function hasProperty(obj, prop) {
                    return Object.prototype.hasOwnProperty.call(obj, prop);
                }

                function isCallable(v) {
                    // NB: modify as necessary if other values than functions are callable.
                    return typeof v === "function";
                }

                if (typeof desc !== "object" || desc === null)
                    throw new TypeError("bad desc");
                var d = {};
                if (hasProperty(desc, "enumerable"))
                    d.enumerable = !!desc.enumerable;
                if (hasProperty(desc, "configurable"))
                    d.configurable = !!desc.configurable;
                if (hasProperty(desc, "value"))
                    d.value = desc.value;
                if (hasProperty(desc, "writable"))
                    d.writable = !!desc.writable;
                if (hasProperty(desc, "get")) {
                    var g = desc.get;
                    if (!isCallable(g) && typeof g !== "undefined")
                        throw new TypeError("bad get");
                    d.get = g;
                }
                if (hasProperty(desc, "set")) {
                    var s = desc.set;
                    if (!isCallable(s) && typeof s !== "undefined")
                        throw new TypeError("bad set");
                    d.set = s;
                }
                if (("get" in d || "set" in d) && ("value" in d || "writable" in d))
                    throw new TypeError("identity-confused descriptor");
                return d;
            }

            if (typeof obj !== "object" || obj === null)
                throw new TypeError("bad obj");
            properties = Object(properties);
            var keys = Object.keys(properties);
            var descs = [];
            for (var j = 0; j < keys.length; j++)
                descs.push([keys[j], convertToDescriptor(properties[keys[j]])]);
            for (var i = 0; i < descs.length; i++) {
                if (Object.defineProperty && (Browser.name == "ie" && Browser.version != 8)) {
                    Object.defineProperty(obj, descs[i][0], descs[i][1]);
                } else {
                    if (descs[i][1].value) obj[descs[i][0]] = descs[i][1].value;
                    if (descs[i][1].get) obj["get" + descs[i][0].capitalize()] = descs[i][1].get;
                    if (descs[i][1].set) obj["set" + descs[i][0].capitalize()] = descs[i][1].set;
                }
            }
            return obj;
        };
        if (!Array.prototype.findIndex) {
            Object.defineProperty(Array.prototype, 'findIndex', {
                value: function (predicate) {
                    if (this == null) {
                        throw new TypeError('"this" is null or not defined');
                    }
                    var o = Object(this);
                    var len = o.length >>> 0;
                    if (typeof predicate !== 'function') {
                        throw new TypeError('predicate must be a function');
                    }
                    var thisArg = arguments[1];
                    var k = 0;
                    while (k < len) {
                        var kValue = o[k];
                        if (predicate.call(thisArg, kValue, k, o)) {
                            return k;
                        }
                        k++;
                    }
                    return -1;
                }
            });
        }
        if (!Array.prototype.find) {
            Object.defineProperty(Array.prototype, 'find', {
                value: function (predicate) {
                    if (this == null) {
                        throw new TypeError('"this" is null or not defined');
                    }
                    var o = Object(this);
                    var len = o.length >>> 0;
                    if (typeof predicate !== 'function') {
                        throw new TypeError('predicate must be a function');
                    }
                    var thisArg = arguments[1];
                    var k = 0;
                    while (k < len) {
                        var kValue = o[k];
                        if (predicate.call(thisArg, kValue, k, o)) {
                            return kValue;
                        }
                        k++;
                    }
                    return undefined;
                }
            });
        }

        var _txt = function (v) {
            if (typeof v !== "string") return v;
            var t = v.replace(/\</g, "&lt;");
            t = t.replace(/\>/g, "&gt;");
            return t;
        };
        var _dtxt = function (v) {
            if (typeof v !== "string") return v;
            var t = v.replace(/&lt;/g, "<");
            t = t.replace(/&gt;/g, ">");
            return t;
        };

        this.o2.Class = _Class;
        this.o2.require = _require;
        this.o2.requireApp = _requireApp;
        this.o2.JSON = _json;
        this.o2.loadLP = _loadLP;
        this.o2.restful = _restful;
        this.o2.release = _release;
        this.o2.defineProperties = _defineProperties;
        this.o2.txt = _txt;
        this.o2.dtxt = _dtxt;

        Object.repeatArray = function (o, count) {
            var arr = [];
            for (var i = 0; i < count; i++) {
                arr.push(o)
            }
            return arr;
        }
        /**
         * @summary 从服务器获取当前时间。
         * @function getDateFromServer
         * @memberOf o2
         * @param {Boolean|Function} [async|callback] 可选，如果传入true或一个Function：表示异步调用此方法，传入的function为回调方法。如果省略此参数或传入false，则为同步方法
         * @return {Date|Promise} 同步调用时，返回获取到的时间Date；异步调用时，返回Promise。
         * @o2syntax
         * o2.getDateFromServer(async);
         * @o2syntax
         * Date.getFromServer(async);
         * @example
         * //同步获取服务器时间
         * var d = o2.getDateFromServer();
         * //或者
         * var d = Date.getFromServer();
         *
         * //通过回调方法异步获取服务器时间
         * o2.getDateFromServer(function(d){
         *     console.log(d);  //从服务器获取的当前时间
         * });
         * //或者
         * Date.getFromServer(function(d){
         *     console.log(d);  //从服务器获取的当前时间
         * });
         *
         * //通过Promise异步获取服务器时间
         * o2.getDateFromServer(true).then((d)=>{
         *     console.log(d);  //从服务器获取的当前时间
         * });
         * //或者
         * Date.getFromServer(true).then((d)=>{
         *     console.log(d);  //从服务器获取的当前时间
         * });
         */
        this.o2.getDateFromServer = Date.getFromServer = function (async) {
            var d;
            // var cb = ((async && o2.typeOf(async) == "function") ? async : null) || function (json) {
            //     //var cb = function(json){
            //     d = Date.parse(json.data.serverTime);
            //     return d;
            // };

            var cb = function(json){
                d = Date.parse(json.data.serverTime);
                return d;
            }

            var promise = o2.Actions.get("x_program_center").echo(cb, null, !!async);
            if (async && o2.typeOf(async) == "function"){
                return promise.then(async);
            }
            return (!!async) ? promise : d;
        };


        var _promiseAll = function (p) {
            if (o2.typeOf(p) == "array") {
                if (p.some(function (e) {
                    return (e && o2.typeOf(e.then) == "function")
                })) {
                    return Promise.all(p);
                } else {
                    return {
                        "then": function (s) {
                            if (s) {
                                var r = s(p);
                                return (r && r.then && o2.typeOf(r.then) == "function") ? r : this;
                            }
                            return this;
                        }
                    };
                }
            } else {
                if (p && o2.typeOf(p.then) == "function") {
                    return Promise.resolve(p);
                } else {
                    return {
                        "then": function (s) {
                            if (s) {
                                var r = s(p);
                                return (r && r.then && o2.typeOf(r.then) == "function") ? r : this;
                            }
                            return this;
                        }
                    };
                    //return new Promise(function(s){s(p); return this;});
                }
            }
            // var method = (o2.typeOf(p)=="array") ? "all" : "resolve";
            // return Promise[method](p);
        }
        o2.promiseAll = _promiseAll;

    })();
    o2.defer = function(fn, timer, bind, pars){
        if (fn.timerId) clearTimeout(fn.timerId);
        fn.timerId = setTimeout(function(){
            fn.timerId = null;
            fn.apply((bind || this), pars);
        }, timer);
    }
    o2.core = true;


    /** ***** BEGIN LICENSE BLOCK *****
     * |------------------------------------------------------------------------------|
     * | O2OA 活力办公 创意无限    o2.more.js                                          |
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
    (function () {
        o2.getCenterPosition = function (el, width, height) {
            var elPositon = $(el).getPosition();
            var elSize = $(el).getSize();
            var node = $("layout");
            var size = (node) ? $(node).getSize() : $(document.body).getSize();

            var top = (elPositon.y + elSize.y) / 2 - (height / 2);
            var left = (elPositon.x + elSize.x) / 2 - (width / 2);
            if ((left + width) > size.x) {
                left = size.x - width - 10;
            }
            if ((top + height) > size.y) {
                top = size.y - height - 10;
            }

            return {"x": left, "y": top};
        };
        o2.getMarkSize = function (node) {
            var size;
            if (!node) {
                size = $(document.body).getSize();
                var winSize = $(window).getSize();

                var height = size.y;
                var width = size.x;

                if (height < winSize.y) height = winSize.y;
                if (width < winSize.x) width = winSize.x;

                return {x: size.x, y: height};
            } else {
                size = $(node).getSize();
                return {x: size.x, y: size.y};
            }
        };
        o2.json = function (jsonString, fun) {
            var obj = JSON.decode(jsonString);
            var p = fun.split(".");
            var tmp = obj;
            p.each(function (item) {
                if (item.indexOf("[") !== -1) {
                    var x = item.split("[");
                    var i = parseInt(x[1].substr(0, x[1].indexOf("]")));
                    tmp = tmp[x[0]][i];
                } else {
                    tmp = tmp[item];
                }
            });
            return tmp;
        };
        o2.getHTMLTemplate = function (url, callback, async) {
            var loadAsync = (async !== false);
            var res = new Request.HTML({
                url: url,
                async: loadAsync,
                method: "get",
                onSuccess: function (responseTree, responseElements, responseHTML, responseJavaScript) {
                    o2.runCallback(callback, "success", [responseTree, responseElements, responseHTML, responseJavaScript]);
                }.bind(this),
                onFailure: function (xhr) {
                    o2.runCallback(callback, "requestFailure", [xhr]);
                }
            });
            res.send();
        };

        o2.getRequestText = function (url, callback, async) {
            var loadAsync = (async !== false);

            url = (url.indexOf("?") !== -1) ? url + "&v=" + o2.version.v : url + "?v=" + o2.version.v;
            var res = new Request({
                url: url,
                async: loadAsync,
                method: "get",
                onSuccess: function (responseText, responseXML) {
                    o2.runCallback(callback, "success", [responseText, responseXML]);
                }.bind(this),
                onFailure: function (xhr) {
                    o2.runCallback(callback, "requestFailure", [xhr]);
                }
            });
            res.send();
        };

        o2.encodeJsonString = function (str) {
            var tmp = [str];
            var dataStr = (JSON.encode(tmp));
            return dataStr.substr(2, dataStr.length - 4);
        };
        o2.decodeJsonString = function (str) {
            var tmp = "[\"" + str + "\"]";
            var dataObj = (JSON.decode(tmp));
            return dataObj[0];
        };

        o2.getTextSize = function (text, styles) {
            var tmpSpan = new Element("span", {
                "text": text,
                "styles": styles
            }).inject($(document.body));
            var size = tmpSpan.getSize();
            tmpSpan.destroy();
            return size;
        };
        o2.getCenter = function (size, target, offset) {
            if (!target) target = document.body;
            var targetSize = target.getSize();
            var targetPosition = target.getPosition(offset);
            var targetScroll = target.getScroll();

            var x = targetSize.x / 2;
            var y = targetSize.y / 2;

            x = x - (size.x / 2);
            y = y - (size.y / 2);
            x = x + targetPosition.x;
            y = y + targetPosition.y;
            x = x + targetScroll.x;
            y = y + targetScroll.y;

            return {"x": x, "y": y};
        };
        o2.getEPointer = function (e) {
            var x = 0;
            var y = 0;
            if (typeOf(e) == "element") {
                var position = e.getPosition(this.content);
                x = position.x;
                y = position.y;
            } else {
                if (Browser.name == "firefox") {
                    x = parseFloat(e.event.clientX || e.event.x);
                    y = parseFloat(e.event.clientY || e.event.y);
                } else {
                    x = parseFloat(e.event.x);
                    y = parseFloat(e.event.y);
                }

                if (e.target) {
                    var position = e.target.getPosition(this.content);
                    x = position.x;
                    y = position.y;
                }
                //    }
            }
            return {"x": x, "y": y};
        };
        o2.getParent = function (node, tag) {
            var pNode = node.parentElement;
            while (pNode && pNode.tagName.toString().toLowerCase() !== tag.toString().toLowerCase()) {
                pNode = pNode.parentElement;
            }
            return pNode;
        };
        o2.getOffset = function (evt) {
            if (Browser.name === "firefox") {
                return {
                    "offsetX": evt.layerX,
                    "offsetY": evt.layerY
                };
            } else {
                return {
                    "offsetX": evt.offsetX,
                    "offsetY": evt.offsetY
                }
            }
        };

        /**
         * @summary 对页面进行缩放。
         * @function zoom
         * @memberOf o2
         * @param {Number} [scale] 缩放的比例。1表示原始大小
         * @o2syntax
         * o2.zoom(scale);
         * @example
         * //将页面放大到150%大小
         * o2.zoom(1.5);
         */
        o2.zoom = function (scale) {
            if (!layout) layout = {};
            if (layout && !layout.userLayout) layout.userLayout = {};
            layout.userLayout.scale = scale;
            // var s = (1 / layout.userLayout.scale) * 100;
            // var p = s + "%";
            // document.id(document.documentElement).setStyles({
            //     "transform": "scale(" + layout.userLayout.scale + ")",
            //     "transform-origin": "0 0",
            //     "width": p,
            //     "height": p
            // });
            document.body.style.zoom = scale;
            if (layout.desktop){
                if (layout.desktop.resizeHeight) layout.desktop.resizeHeight();
            }


        };

        if (String.implement) String.implement({
            "getAllIndexOf": function (str) {
                var idxs = [];
                var idx = this.indexOf(str);
                while (idx !== -1) {
                    idxs.push(idx);
                    idx = this.indexOf(str, idx + 1);
                }
                return idxs;
            }
        });
        if (Array.implement) Array.implement({
            "trim": function () {
                var arr = [];
                this.each(function (v) {
                    if (v) arr.push(v);
                });
                return arr;
            },
            "isIntersect": function (arr) {
                return this.some(function (item) {
                    return (arr.indexOf(item) !== -1);
                })
            },
            "add": function(newKey, newValue, overwrite){
                if (arguments.length<2){
                    this[this.length] = newKey;
                    //this.push(newKey);
                }else{
                    if (o2.typeOf(newKey)=="number"){
                        if (newKey<this.length){
                            if (overwrite) this[newKey] = newValue;
                        }else if (newKey==this.length){
                            //this.push(newValue);
                            this[this.length] = newValue;
                        }
                    }
                }
            }
        });
        if (!Array.prototype.find) {
            if (Array.implement) Array.implement({
                "find": this.find || function (callback, thisArg) {
                    for (var i = 0; i < this.length; i++) {
                        if (callback.apply((thisArg || this), this[i], i, this)) {
                            return this[i];
                        }
                    }
                    return undefined;
                }
            });
        }


        var styleString = Element.getComputedStyle;

        function styleNumber(element, style) {
            return styleString(element, style).toInt() || 0;
        }

        function topBorder(element) {
            return styleNumber(element, 'border-top-width');
        }

        function leftBorder(element) {
            return styleNumber(element, 'border-left-width');
        }

        function isBody(element) {
            return (/^(?:body|html)$/i).test(element.tagName);
        }

        var heightComponents = ['height', 'paddingTop', 'paddingBottom', 'borderTopWidth', 'borderBottomWidth'],
            widthComponents = ['width', 'paddingLeft', 'paddingRight', 'borderLeftWidth', 'borderRightWidth'];
        var svgCalculateSize = function (el) {

            var gCS = window.getComputedStyle(el),
                bounds = {x: 0, y: 0};

            heightComponents.each(function (css) {
                bounds.y += parseFloat(gCS[css]);
            });
            widthComponents.each(function (css) {
                bounds.x += parseFloat(gCS[css]);
            });
            return bounds;
        };

        [Document, Window].invoke('implement', {
            getSize: function () {
                var doc = this.getDocument();
                doc = ((!doc.compatMode || doc.compatMode == 'CSS1Compat') && (!layout || !layout.userLayout || !layout.userLayout.scale || layout.userLayout.scale == 1)) ? doc.html : doc.body;
                return {x: doc.clientWidth, y: doc.clientHeight};
            },
        });
        if (window.Element && Element.implement) Element.implement({
            "isIntoView": function () {
                // var pNode = this.getParent();
                // while (pNode && ((pNode.getScrollSize().y-(pNode.getComputedSize().height+1)<=0) || pNode.getStyle("overflow")==="visible")) pNode = pNode.getParent();
                //
                var pNode = this.getParentSrcollNode();

                if (!pNode) pNode = document.body;
                var size = pNode.getSize();
                var srcoll = pNode.getScroll();
                var p = (pNode == window) ? {"x": 0, "y": 0} : this.getPosition(pNode);
                var nodeSize = this.getSize();
                //return (p.x-srcoll.x>=0 && p.y-srcoll.y>=0) && (p.x+nodeSize.x<size.x+srcoll.x && p.y+nodeSize.y<size.y+srcoll.y);
                return (p.x - srcoll.x >= 0 && p.y >= 0) && (p.x + nodeSize.x < size.x + srcoll.x && p.y + nodeSize.y < size.y)
            },
            "appendHTML": function (html, where) {
                if (this.insertAdjacentHTML) {
                    var whereText = "beforeEnd";
                    if (where === "before") whereText = "beforeBegin";
                    if (where === "after") whereText = "afterEnd";
                    if (where === "bottom") whereText = "beforeEnd";
                    if (where === "top") whereText = "afterBegin";
                    this.insertAdjacentHTML(whereText, html);
                } else {
                    if (where === "bottom") this.innerHTML = this.innerHTML + html;
                    if (where === "top") this.innerHTML = html + this.innerHTML;
                }
            },
            "positionTo": function (x, y) {
                var left = x.toFloat();
                var top = y.toFloat();
                var offsetNode = this.getOffsetParent();
                if (offsetNode) {
                    var offsetPosition = offsetNode.getPosition();
                    left = left - offsetPosition.x;
                    top = top - offsetPosition.y;
                }
                this.setStyles({"top": top, "left": left});
                return this;
            },
            "getBorder": function () {
                var positions = ["top", "left", "right", "bottom"];
                var styles = ["color", "style", "width"];

                var obj = {};
                positions.each(function (position) {
                    styles.each(function (style) {
                        var key = "border-" + position + "-" + style;
                        obj[key] = this.getStyle(key);
                    }.bind(this));
                }.bind(this));

                return obj;
            },
            "isOutside": function (e) {
                var elementCoords = this.getCoordinates();
                var targetCoords = this.getCoordinates();
                if (((e.page.x < elementCoords.left || e.page.x > (elementCoords.left + elementCoords.width)) ||
                        (e.page.y < elementCoords.top || e.page.y > (elementCoords.top + elementCoords.height))) &&
                    ((e.page.x < targetCoords.left || e.page.x > (targetCoords.left + targetCoords.width)) ||
                        (e.page.y < targetCoords.top || e.page.y > (targetCoords.top + targetCoords.height)))) return true;

                return false;
            },
            "getAbsolutePosition": function () {
                var styleLeft = 0;
                var styleTop = 0;
                var node = this;

                styleLeft = node.offsetLeft;
                styleTop = node.offsetTop;

                node = node.parentElement;

                while (node && node.tagName.toString().toLowerCase() !== "body") {
                    styleLeft += node.offsetLeft;
                    styleTop += node.offsetTop;
                    node = node.offsetParent;
                }
                return {x: styleLeft, y: styleTop};
            },
            "tweenScroll": function (to, time) {
                if (!this.tweenScrollQueue) {
                    this.tweenScrollQueue = [];
                }
                if (this.tweenScrollQueue.length) {
                    this.tweenScrollQueue.push(to);
                } else {
                    this.tweenScrollQueue.push(to);
                    this.doTweenScrollQueue(time);
                }
            },
            "doTweenScrollQueue": function (time) {
                if (this.tweenScrollQueue.length) {
                    var i = this.tweenScrollQueue.length;
                    var to = this.tweenScrollQueue[this.tweenScrollQueue.length - 1];

                    var scroll = this.getScroll();
                    var dy = to - scroll.y;
                    var step = dy / time;
                    var count = 0;
                    var move = 0;

                    var id = window.setInterval(function () {

                        this.scrollTo(0, scroll.y + count * step);
                        count++;
                        if (count > time) {
                            window.clearInterval(id);
                            for (var x = 1; x <= i; x++) this.tweenScrollQueue.shift();
                            if (this.tweenScrollQueue.length) this.doTweenScrollQueue(time);
                        }
                    }.bind(this), 1);
                }
            },
            "isPointIn": function (px, py, offX, offY, el) {
                if (!offX) offX = 0;
                if (!offY) offY = 0;
                var position = this.getPosition(el);
                var size = this.getSize();
                return (position.x - offX <= px && position.x + size.x + offX >= px && position.y - offY <= py && position.y + size.y + offY >= py);
            },
            "isInPointInRect": function (sx, sy, ex, ey) {
                var position = this.getPosition();
                var size = this.getSize();

                var p1 = {"x": position.x, "y": position.y};
                var p2 = {"x": position.x + size.x, "y": position.y};
                var p3 = {"x": position.x + size.x, "y": position.y + size.y};
                var p4 = {"x": position.x, "y": position.y + size.y};

                var sp = {"x": Math.min(sx, ex), "y": Math.min(sy, ey)};
                var ep = {"x": Math.max(sx, ex), "y": Math.max(sy, ey)};

                if (p1.x >= sp.x && p1.y >= sp.y && p1.x <= ep.x && p1.y <= ep.y) return true;
                if (p2.x >= sp.x && p2.y >= sp.y && p2.x <= ep.x && p2.y <= ep.y) return true;
                if (p3.x >= sp.x && p3.y >= sp.y && p3.x <= ep.x && p3.y <= ep.y) return true;
                if (p4.x >= sp.x && p4.y >= sp.y && p4.x <= ep.x && p4.y <= ep.y) return true;
                if (p3.x >= sp.x && p3.y >= sp.y && p1.x <= sp.x && p1.y <= sp.y) return true;
                if (p3.x >= ep.x && p3.y >= ep.y && p1.x <= ep.x && p1.y <= ep.y) return true;
                if (p1.x <= sp.x && p2.x >= sp.x && p1.y >= sp.y && p4.y <= ep.y) return true;
                if (p1.y <= sp.y && p4.y >= sp.y && p1.x >= sp.x && p2.x <= ep.x) return true;

                return false;
            },
            "isOverlap": function (node) {
                var p = node.getPosition();
                var s = node.getSize();
                return this.isInPointInRect(p.x, p.y, p.x + s.x, p.y + s.y);
            },

            "getUsefulSize": function () {
                var size = this.getSize();
                var borderLeft = this.getStyle("border-left").toInt();
                var borderBottom = this.getStyle("border-bottom").toInt();
                var borderTop = this.getStyle("border-top").toInt();
                var borderRight = this.getStyle("border-right").toInt();

                var paddingLeft = this.getStyle("padding-left").toInt();
                var paddingBottom = this.getStyle("padding-bottom").toInt();
                var paddingTop = this.getStyle("padding-top").toInt();
                var paddingRight = this.getStyle("padding-right").toInt();

                var x = size.x - paddingLeft - paddingRight;
                var y = size.y - paddingTop - paddingBottom;

                return {"x": x, "y": y};
            },
            "clearStyles": function (isChild) {
                this.removeProperty("style");
                if (isChild) {
                    var subNode = this.getFirst();
                    while (subNode) {
                        subNode.clearStyles(isChild);
                        subNode = subNode.getNext();
                    }
                }
            },
            "maskIf": function (styles, click) {
                var style = {
                    "background-color": "#666666",
                    "opacity": 0.4,
                    "z-index": 100
                };
                if (styles) {
                    style = Object.merge(style, styles);
                }
                var position = this.getPosition(this.getOffsetParent());
                this.mask({
                    "destroyOnHide": true,
                    "style": style,
                    "useIframeShim": true,
                    "iframeShimOptions": {"browsers": true},
                    "onShow": function () {
                        this.shim.shim.setStyles({
                            "opacity": 0,
                            "top": "" + position.y + "px",
                            "left": "" + position.x + "px"
                        });
                    },
                    "onClick": click
                });
            },
            "scrollIn": function (where) {
                var wh = (where) ? where.toString().toLowerCase() : "center";

                if (Browser.name == "ie" || Browser.name == "safari") {
                    var scrollNode = this.getParentSrcollNode();
                    var scrollFx = new Fx.Scroll(scrollNode);
                    var scroll = scrollNode.getScroll();
                    var size = scrollNode.getSize();
                    var thisSize = this.getComputedSize();
                    var p = this.getPosition(scrollNode);

                    if (wh == "start") {
                        var top = 0;
                        scrollFx.start(scroll.x, p.y - top + scroll.y);
                    } else if (wh == "end") {
                        var bottom = size.y - thisSize.totalHeight;
                        scrollFx.start(scroll.x, p.y - bottom + scroll.y);
                    } else {
                        var center = size.y / 2 - thisSize.totalHeight / 2;
                        scrollFx.start(scroll.x, p.y - center + scroll.y);
                    }
                } else {
                    if (wh !== "start" && wh !== "end") wh = "center"
                    this.scrollIntoView({"behavior": "smooth", "block": wh, "inline": "nearest"});
                }
            },
            scrollToNode: function (el, where) {
                var scrollSize = this.getScrollSize();
                if (!scrollSize.y) return true;
                var wh = (where) ? where.toString().toLowerCase() : "bottom";
                var node = $(el);
                var size = node.getComputedSize();
                var p = node.getPosition(this);
                var thisSize = this.getComputedSize();
                var scroll = this.getScroll();
                if (wh === "top") {
                    var n = (p.y - thisSize.computedTop);
                    if (n < 0) this.scrollTo(scroll.x, scroll.y + n);
                    n = (size.totalHeight + p.y - thisSize.computedTop) - thisSize.height;
                    if (n > 0) this.scrollTo(scroll.x, scroll.y + n);

                } else {
                    var n = (size.totalHeight + p.y - thisSize.computedTop) - thisSize.height;
                    if (n > 0) this.scrollTo(scroll.x, scroll.y + n);
                    n = p.y - thisSize.computedTop;
                    if (n < 0) this.scrollTo(scroll.x, scroll.y + n);
                }
            },
            "getInnerStyles": function () {
                var styles = {};
                style = this.get("style");
                if (style) {
                    var styleArr = style.split(/\s*\;\s*/g);
                    styleArr.each(function (s) {
                        if (s) {
                            var sarr = s.split(/\s*\:\s*/g);
                            styles[sarr[0]] = (sarr.length > 1) ? sarr[1] : ""
                        }
                    }.bind(this));
                }
                return styles;
            },
            "getInnerProperties": function () {
                var properties = {};
                if (this.attributes.length) {
                    for (var i = 0; i < this.attributes.length; i++) {
                        properties[this.attributes[i].nodeName] = this.attributes[i].nodeValue;
                    }
                }
                return properties;
            },
            "getZIndex": function () {
                var n = this;
                var i = 0;
                while (n) {
                    if (n.getStyle("position") === "absolute") {
                        var idx = n.getStyle("z-index");
                        i = (idx && idx.toFloat() > i) ? idx.toFloat() + 1 : 0;
                        break;
                    }
                    n = n.getParent();
                }
                return i;
            },
            "getParentSrcollNode": function () {
                var node = this.getParent();
                while (node && (node.getScrollSize().y - 2 <= node.getSize().y || (node.getStyle("overflow") !== "auto" && node.getStyle("overflow-y") !== "auto"))) {
                    node = node.getParent();
                }
                return node || null;
            },
            "getEdgeHeight": function (notMargin) {
                var h = 0;
                h += (this.getStyle("border-top-width").toFloat() || 0) + (this.getStyle("border-bottom-width").toFloat() || 0);
                h += (this.getStyle("padding-top").toFloat() || 0) + (this.getStyle("padding-bottom").toFloat() || 0);
                if (!notMargin) h += (this.getStyle("margin-top").toFloat() || 0) + (this.getStyle("margin-bottom").toFloat() || 0);
                return h;
            },
            "getEdgeWidth": function (notMargin) {
                var h = 0;
                h += (this.getStyle("border-left-width").toFloat() || 0) + (this.getStyle("border-right-width").toFloat() || 0);
                h += (this.getStyle("padding-left").toFloat() || 0) + (this.getStyle("padding-right").toFloat() || 0);
                if (!notMargin) h += (this.getStyle("margin-left").toFloat() || 0) + (this.getStyle("margin-right").toFloat() || 0);
                return h;
            },
            "getSize": function () {
                if ((/^(?:body|html)$/i).test(this.tagName)) return this.getWindow().getSize();
                if (!window.getComputedStyle) return {x: this.offsetWidth, y: this.offsetHeight};
                if (this.get('tag') == 'svg') return svgCalculateSize(this);
                try {
                    if (!layout || !layout.userLayout || !layout.userLayout.scale || layout.userLayout.scale == 1) {
                        var bounds = this.getBoundingClientRect();
                        return {x: bounds.width, y: bounds.height};
                    } else {
                        return {"x": this.offsetWidth.toFloat(), "y": this.offsetHeight.toFloat()};
                    }
                } catch (e) {
                    return {x: 0, y: 0};
                }
            },
            "getScaleOffsets": function () {
                var hasGetBoundingClientRect = this.getBoundingClientRect;
//<1.4compat>
                //hasGetBoundingClientRect = hasGetBoundingClientRect && !Browser.Platform.ios;
//</1.4compat>
                if (hasGetBoundingClientRect) {
                    var bound = this.getBoundingClientRect();

                    var boundLeft = bound.left;
                    var boundTop = bound.top;
                    if (!layout || !layout.userLayout || !layout.userLayout.scale || layout.userLayout.scale == 1) {

                    } else {
                        // boundLeft = boundLeft / layout.userLayout.scale;
                        // boundTop = boundTop / layout.userLayout.scale;
                    }


                    var html = document.id(this.getDocument().documentElement);
                    var htmlScroll = html.getScroll();
                    var elemScrolls = this.getScrolls();
                    var isFixed = (Element.getComputedStyle(this, 'position') == 'fixed');

                    return {
                        x: boundLeft.toFloat() + elemScrolls.x + ((isFixed) ? 0 : htmlScroll.x) - html.clientLeft,
                        y: boundTop.toFloat() + elemScrolls.y + ((isFixed) ? 0 : htmlScroll.y) - html.clientTop
                    };
                }

                var element = this, position = {x: 0, y: 0};
                if (isBody(this)) return position;

                while (element && !isBody(element)) {
                    position.x += element.offsetLeft;
                    position.y += element.offsetTop;
//<1.4compat>
                    if (Browser.firefox) {
                        if (!borderBox(element)) {
                            position.x += leftBorder(element);
                            position.y += topBorder(element);
                        }
                        var parent = element.parentNode;
                        if (parent && styleString(parent, 'overflow') != 'visible') {
                            position.x += leftBorder(parent);
                            position.y += topBorder(parent);
                        }
                    } else if (element != this && Browser.safari) {
                        position.x += leftBorder(element);
                        position.y += topBorder(element);
                    }
//</1.4compat>
                    element = element.offsetParent;
                }
//<1.4compat>
                if (Browser.firefox && !borderBox(this)) {
                    position.x -= leftBorder(this);
                    position.y -= topBorder(this);
                }
//</1.4compat>
                return position;
            },
            getPosition: function (relative) {
                var offset = this.getScaleOffsets(),
                    scroll = this.getScrolls();
                var position = {
                    x: offset.x - scroll.x,
                    y: offset.y - scroll.y
                };

                if (relative && (relative = document.id(relative))) {
                    var relativePosition = relative.getPosition();
                    return {
                        x: position.x - relativePosition.x - leftBorder(relative),
                        y: position.y - relativePosition.y - topBorder(relative)
                    };
                }
                return position;
            }
        });

        Object.copy = function (from, to) {
            Object.each(from, function (value, key) {
                switch (typeOf(value)) {
                    case "object":
                        if (!to[key]) to[key] = {};
                        Object.copy(value, to[key]);
                        break;
                    default:
                        to[key] = value;
                }
            });
        };

        if (window.JSON) JSON.format = JSON.encode;

        if (window.Slick) {
            Slick.definePseudo('src', function (value) {
                return Element.get(this, "src").indexOf(value) !== -1;
            });
            Slick.definePseudo('srcarr', function (value) {
                var vList = value.split(",");
                var src = Element.get(this, "src");
                var flag = false;
                for (var i = 0; i < vList.length; i++) {
                    if (src.indexOf(vList[i]) !== -1) {
                        flag = true;
                        break;
                    }
                }
                return flag;
            });
            Slick.definePseudo('ahref', function (value) {
                var href = Element.get(this, "href");
                if (!href) href = "";
                href = href.toString().toLowerCase();
                return (href.indexOf(value) !== -1);
            });

            Slick.definePseudo('rowspanBefore', function (line) {
                var tr = MWF.getParent(this, "tr");
                var rowspan = this.get("rowspan").toInt() || 1;
                var currentRowIndex = tr.rowIndex.toInt();

                return rowspan > 1 && currentRowIndex < line.toInt() && currentRowIndex + rowspan - 1 >= line;
            });
            Slick.definePseudo('rowspan', function () {
                var rowspan = this.get("rowspan").toInt() || 1;
                return rowspan > 1;
            });

            Slick.definePseudo('colspanBefore', function (col) {
                var tr = MWF.getParent(this, "tr");
                var colspan = this.get("colspan").toInt() || 1;
                var currentColIndex = this.cellIndex.toInt();

                return colspan > 1 && currentColIndex < col.toInt() && currentColIndex + colspan - 1 >= col.toInt();
            });

            Slick.definePseudo('colspan', function () {
                var colspan = this.get("colspan").toInt() || 1;
                return colspan > 1;
            });
        }

        // 第三方扩展用的函数
        o2.thirdparty =  o2.thirdparty || {};
        
        // 是否手机端
        o2.thirdparty.isMobile = function(){
            return /(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i.test(navigator.userAgent.toLowerCase()); 
        }
        function alwaysFalse() {
            return false;
        }
        
        if (o2.thirdparty.isMobile()) {
            // 手机端钉钉
            o2.thirdparty.isDingdingMobile = function() {
                var isDingding = /dingtalk/i.test(navigator.userAgent.toLowerCase())
                return isDingding && o2.thirdparty.isMobile();
            }
            //手机端企业微信
            o2.thirdparty.isQywxMobile = function() {
                var isComWx = /wxwork/i.test(navigator.userAgent); // 是否企业微信
                return (isComWx && o2.thirdparty.isMobile());
            }
             //手机端微信
            o2.thirdparty.isWeiXinMobile = function() {
                var isWx = /micromessenger/i.test(navigator.userAgent); // 是否微信
                return (isWx && o2.thirdparty.isMobile());
            }
            o2.thirdparty.isDingdingPC = alwaysFalse;
            o2.thirdparty.isQywxPC = alwaysFalse;
            o2.thirdparty.isWeiXinPC = alwaysFalse;
        } else {
            o2.thirdparty.isDingdingMobile = alwaysFalse;
            o2.thirdparty.isQywxMobile = alwaysFalse;
            o2.thirdparty.isWeiXinMobile = alwaysFalse;
            // pc端钉钉
            o2.thirdparty.isDingdingPC = function() {
                var isDingding = /dingtalk/i.test(navigator.userAgent.toLowerCase())
                return isDingding && !o2.thirdparty.isMobile();
            }
            //PC端企业微信
            o2.thirdparty.isQywxPC = function() {
                var isComWx = /wxwork/i.test(navigator.userAgent); // 是否企业微信
                return (isComWx && !o2.thirdparty.isMobile());
            }
            //PC端微信
            o2.thirdparty.isWeiXinPC = function() {
                var isWx = /micromessenger/i.test(navigator.userAgent); // 是否微信
                return (isWx && !o2.thirdparty.isMobile());
            }
        }

        
    
        o2.common = o2.common || {};

        o2.common.encodeHtml = function (str) {
            str = str.toString();
            str = str.replace(/\&/g, "&amp;");
            str = str.replace(/>/g, "&gt;");
            str = str.replace(/</g, "&lt;");
            return str.replace(/\"/g, "&quot;");
        };

        o2.common.getResponseTextPost = function (path, body, contentType) {
            var returnText = "";
            var options = {
                url: path,
                async: false,
                data: body,
                method: "post",
                onSuccess: function (esponseTree, responseElements, responseHTML, responseJavaScript) {
                    returnText = responseHTML;
                }
            };
            var r = new Request.HTML(options);
            r.send();
            return returnText;
        };
        o2.common.getResponseText = function (path) {
            var returnText = "";
            var options = {
                url: path,
                async: false,
                method: "get",
                onSuccess: function (esponseTree, responseElements, responseHTML, responseJavaScript) {
                    returnText = responseHTML;
                }
            };
            var r = new Request.HTML(options);
            r.send();
            return returnText;
        };
        o2.common.toDate = function (str) {
            var tmpArr = str.split(" ");
            if (!tmpArr[1]) tmpArr.push("0:0:0");
            var dateArr = tmpArr[0].split("-");
            var timeArr = tmpArr[1].split(":");
            return new Date(dateArr[0], parseInt(dateArr[1]) - 1, dateArr[2], timeArr[0], timeArr[1], timeArr[2]);
        };

        o2.common.toDate = function (str) {
            var tmpArr = str.split(" ");
            if (!tmpArr[1]) tmpArr.push("0:0:0");
            var dateArr = tmpArr[0].split("-");
            var timeArr = tmpArr[1].split(":");
            return new Date(dateArr[0], parseInt(dateArr[1]) - 1, dateArr[2], timeArr[0], timeArr[1], timeArr[2]);
        };

        o2.grayscale = function (src, width, height, callback) {
            try {
                var canvas = document.createElement('canvas');
                var ctx = canvas.getContext('2d');
                var imgObj = new Image();
                imgObj.src = src;
                canvas.width = width || imgObj.width;
                canvas.height = height || imgObj.height;
                ctx.drawImage(imgObj, 0, 0);

                var imgPixels = ctx.getImageData(0, 0, canvas.width, canvas.height);
                for (var y = 0; y < imgPixels.height; y++) {
                    for (var x = 0; x < imgPixels.width; x++) {
                        var i = (y * 4) * imgPixels.width + x * 4;
                        var avg = (imgPixels.data[i] + imgPixels.data[i + 1] + imgPixels.data[i + 2]) / 3;
                        imgPixels.data[i] = avg;
                        imgPixels.data[i + 1] = avg;
                        imgPixels.data[i + 2] = avg;
                    }
                }
                ctx.putImageData(imgPixels, 0, 0, 0, 0, imgPixels.width, imgPixels.height);
                var src1 = canvas.toDataURL();
                //var blob = canvas.toBlob();
                canvas.destroy();
                return {"status": "success", "src": src1};
            } catch (e) {
                return {"status": "error", "src": src}
            }
        };
        o2.eventPosition = function (e) {
            var x = 0;
            var y = 0;
            if (Browser.name == "firefox") {
                x = parseFloat(e.event.clientX || e.event.x);
                y = parseFloat(e.event.clientY || e.event.y);
            } else {
                x = parseFloat(e.event.x);
                y = parseFloat(e.event.y);
            }
            return {"x": x, "y": y};
        };
        o2.dlgPosition = function (e, content, width, height) {
            var size = content.getSize();
            var x = 0;
            var y = 0;
            var fromx = 0;
            var fromy = 0;
            if (typeOf(e) == "element") {
                var position = e.getPosition(content);
                fromx = position.x;
                fromy = position.y;
            } else {
                if (Browser.name == "firefox") {
                    fromx = parseFloat(e.clientX || e.x);
                    fromy = parseFloat(e.clientY || e.y);
                } else {
                    fromx = parseFloat(e.x);
                    fromy = parseFloat(e.y);
                }

                if (e.target) {
                    var position = e.target.getPosition(content);
                    fromx = position.x;
                    fromy = position.y;
                }
            }
            if (fromx + parseFloat(width) > size.x) {
                //fromx = fromx + 20;
                x = fromx - parseFloat(width);
            }else{
                fromx = x;
                //if (x < 0) x = 20;
            }

            if (fromy + parseFloat(height) > size.y) {
                y = fromy - parseFloat(height);
                //y = y - 20;
            }else{
                y = fromy;
                // if (y < 0) y = 0;
                // y = y + 20;
            }
            return {
                "x": x,
                "y": y,
                "fromx": fromx,
                "fromy": fromy
            }
        };

        if (window.Browser) {
            if (Browser.name === "ie" && Browser.version < 9) {
                Browser.ieuns = true;
            } else if (Browser.name === "ie" && Browser.version < 10) {
                Browser.iecomp = true;
            }
            if (Browser.iecomp) {
                o2.load("ie_adapter", null, false);
                o2.session.isDebugger = true;
            }
            o2.session.isMobile = (["mac", "win", "linux"].indexOf(Browser.Platform.name) === -1);
        }
    })();
    o2.more = true;

//o2.addReady
    (function () {
        //dom ready
        var _dom;
        if (window.document) {
            _dom = {
                ready: false,
                loaded: false,
                checks: [],
                shouldPoll: false,
                timer: null,
                testElement: document.createElement('div'),
                readys: [],

                domready: function () {
                    clearTimeout(_dom.timer);
                    if (_dom.ready) return;
                    _dom.loaded = _dom.ready = true;
                    o2.removeListener(document, 'DOMContentLoaded', _dom.checkReady);
                    o2.removeListener(document, 'readystatechange', _dom.check);
                    _dom.onReady();
                },
                check: function () {
                    for (var i = _dom.checks.length; i--;) if (_dom.checks[i]() && window.MooTools && o2.core && o2.more) {
                        _dom.domready();
                        return true;
                    }
                    return false;
                },
                poll: function () {
                    clearTimeout(_dom.timer);
                    if (!_dom.check()) _dom.timer = setTimeout(_dom.poll, 10);
                },

                /*<ltIE8>*/
                // doScroll technique by Diego Perini http://javascript.nwbox.com/IEContentLoaded/
                // testElement.doScroll() throws when the DOM is not ready, only in the top window
                doScrollWorks: function () {
                    try {
                        _dom.testElement.doScroll();
                        return true;
                    } catch (e) {
                    }
                    return false;
                },
                /*</ltIE8>*/

                onReady: function () {
                    for (var i = 0; i < _dom.readys.length; i++) {
                        this.readys[i].apply(window);
                    }
                },
                addReady: function (fn) {
                    if (_dom.loaded) {
                        if (fn) fn.apply(window);
                    } else {
                        if (fn) _dom.readys.push(fn);
                    }
                    return _dom;
                },
                checkReady: function () {
                    _dom.checks.push(function () {
                        return true
                    });
                    _dom.check();
                }
            };


            o2.addListener(document, 'DOMContentLoaded', _dom.checkReady);

            /*<ltIE8>*/
            // If doScroll works already, it can't be used to determine domready
            //   e.g. in an iframe
            if (_dom.testElement.doScroll && !_dom.doScrollWorks()) {
                _dom.checks.push(_dom.doScrollWorks);
                _dom.shouldPoll = true;
            }
            /*</ltIE8>*/

            if (document.readyState) _dom.checks.push(function () {
                var state = document.readyState;
                return (state == 'loaded' || state == 'complete');
            });

            if ('onreadystatechange' in document) o2.addListener(document, 'readystatechange', _dom.check);
            else _dom.shouldPoll = true;

            if (_dom.shouldPoll) _dom.poll();
        } else {
            _dom = {
                ready: false,
                loaded: false,
                checks: [],
                shouldPoll: false,
                timer: null,
                readys: [],

                domready: function () {
                    clearTimeout(_dom.timer);
                    if (_dom.ready) return;
                    _dom.loaded = _dom.ready = true;
                    _dom.onReady();
                },
                check: function () {
                    if (window.MooTools && o2.core && o2.more) {
                        _dom.domready();
                        return true;
                    }
                    return false;
                },
                onReady: function () {
                    for (var i = 0; i < _dom.readys.length; i++) {
                        this.readys[i].apply(window);
                    }
                },
                addReady: function (fn) {
                    if (_dom.loaded) {
                        if (fn) fn.apply(window);
                    } else {
                        if (fn) _dom.readys.push(fn);
                    }
                    return _dom;
                },
                checkReady: function () {
                    _dom.checks.push(function () {
                        return true
                    });
                    _dom.check();
                }
            };
        }
        var _loadO2 = function () {
            (!o2.core) ? this.o2.load("o2.core", _dom.check) : _dom.check();
            (!o2.more) ? this.o2.load("o2.more", _dom.check) : _dom.check();
        };
        if (!window.MooTools) {
            this.o2.load("mootools", function () {
                _loadO2();
                _dom.check();
            });
        } else {
            _loadO2();
        }
        this.o2.addReady = function (fn) {
            _dom.addReady.call(_dom, fn);
        };


    })();

//compatible
    COMMON = {
        "DOM": {},
        "setContentPath": function (path) {
            COMMON.contentPath = path;
        },
        "JSON": o2.JSON,
        "Browser": window.Browser,
        "Class": o2.Class,
        "XML": o2.xml,
        "AjaxModule": {
            "load": function (urls, callback, async, reload) {
                o2.load(urls, callback, reload, document);
            },
            "loadDom": function (urls, callback, async, reload) {
                o2.load(urls, callback, reload, document);
            },
            "loadCss": function (urls, callback, async, reload, sourceDoc) {
                o2.loadCss(urls, document.body, callback, reload, sourceDoc);
            }
        },
        "Request": Request,
        "typeOf": o2.typeOf
    };
    if (COMMON.Browser) COMMON.Browser.Platform.isMobile = o2.session.isMobile;
    COMMON.DOM.addReady = o2.addReady;
    MWF = o2;
    MWF.getJSON = o2.JSON.get;
    MWF.getJSONP = o2.JSON.getJsonp;
    MWF.defaultPath = o2.session.path;
}
