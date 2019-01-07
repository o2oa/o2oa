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



(function (){
    var _Class = {
        create: function(options) {
            var newClass = function() {
                this.initialize.apply(this, arguments);
            };
            _copyPrototype(newClass, options);
            return newClass;
        }
    };
    var _copyPrototype = function (currentNS, props){
        if (!props){return currentNS;}
        if (!currentNS){return currentNS;}
        if ((typeof currentNS).toLowerCase()==="object"){
            for (var prop in props){
                currentNS[prop] = props[prop];
            }
        }
        if ((typeof currentNS).toLowerCase()==="function"){
            for (var prop in props){
                currentNS.prototype[prop] = props[prop];
            }
        }
        return currentNS;
    };

    var _loaded = {};

    var _requireJs = function(url, callback, async, compression){
        var key = encodeURIComponent(url);
        if (_loaded[key]){o2.runCallback(callback, "success"); return "";}

        var jsPath = (compression || !this.o2.session.isDebugger) ? url.replace(/\.js/, ".min.js") : url;
        jsPath = (jsPath.indexOf("?")!==-1) ? jsPath+"&v="+this.o2.version.v : jsPath+"?v="+this.o2.version.v;

        var xhr = new Request({
            url: jsPath, async: async, method: "get",
            onSuccess: function(){
                //try{
                    _loaded[key] = true;
                o2.runCallback(callback, "success");
                //}catch (e){
                //    o2.runCallback(callback, "failure", [e]);
                //}
            },
            onFailure: function(r){
                o2.runCallback(callback, "failure", [r]);
            }
        });
        xhr.send();
    };
    var _requireSingle = function(module, callback, async, compression){
        module = module.replace("MWF", "o2");
        var levels = module.split(".");
        if (levels[levels.length-1]==="*") levels[levels.length-1] = "package";
        levels.shift();

        var jsPath = this.o2.session.path;
        jsPath +="/"+levels.join("/")+".js";

        var loadAsync = (async!==false);

        _requireJs(jsPath, callback, loadAsync, compression);
    };
    var _require = function(module, callback, async, compression){
        var type = typeOf(module);
        if (type==="array"){
            var thisLoaded = [];
            var thisErrorLoaded = [];
            for (var i=0; i<module.length; i++){
                _requireSingle(modules[i], {
                    "onSuccess": function(){
                        thisLoaded.push(modules[i]);
                        if ((thisLoaded.length+thisErrorLoaded.length)===modules.length){
                            if (thisErrorLoaded.length){
                                o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                            }else{
                                o2.runCallback(callback, "success", [thisLoaded, thisErrorLoaded]);
                            }
                        }
                    },
                    "onFailure": function(){
                        thisErrorLoaded.push(modules[i]);
                        o2.runCallback(callback, "failure", [thisLoaded, thisErrorLoaded]);
                    }
                }, async, compression);
            }
        }
        if (type==="string"){
            _requireSingle(module, callback, async, compression);
        }
    };
    var _requireApp = function(module, clazz, callback, async){
        var levels = module.split(".");
        //levels.shift();
        var root = "x_component_"+levels.join("_");
        var clazzName = clazz || "Main";
        var path = "/"+root+"/"+clazzName.replace(/\./g, "/")+".js";
        var loadAsync = (async!==false);
        _requireJs(path, callback, loadAsync);
    };

    var _json = JSON;
    _json.get = function(url, callback, async, nocache){
        var loadAsync = (async !== false);
        var noJsonCache = (nocache === true);

        url = (url.indexOf("?")!==-1) ? url+"&v="+o2.version.v : url+"?v="+o2.version.v;

        var json = null;
        var res = new Request.JSON({
            url: url,
            secure: false,
            method: "get",
            noCache: noJsonCache,
            async: loadAsync,
            withCredentials: true,
            onSuccess: function(responseJSON, responseText){
                json = responseJSON;
                if (typeOf(callback).toLowerCase() === 'function'){
                    callback(responseJSON, responseText);
                }else{
                    o2.runCallback(callback, "success", [responseJSON, responseText]);
                }
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });
        res.send();
        return json;
    };
    _json.getJsonp = function(url, callback, async, callbackKey){
        var loadAsync = (async !== false);

        var callbackKeyWord = callbackKey || "callback";

        url = (url.indexOf("?")!==-1) ? url+"&v="+o2.version.v : url+"?v="+o2.version.v;
        var res = new Request.JSONP({
            url: url,
            secure: false,
            method: "get",
            noCache: true,
            async: loadAsync,
            callbackKey: callbackKeyWord,
            onSuccess: function(responseJSON, responseText){
                o2.runCallback(callback, "success",[responseJSON, responseText]);
            }.bind(this),
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure",[xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error",[text, error]);
            }.bind(this)
        });
        res.send();
    };


    var _loadLP = function(name){
        var jsPath = o2.session.path;
        jsPath = jsPath+"/lp/"+name+".js";
        var r = new Request({
            url: jsPath,
            async: false,
            method: "get",
            onSuccess: function(responseText){
                try{
                    Browser.exec(responseText);
                }catch (e){}
            },
            onFailure: function(xhr){
                throw "loadLP Error: "+xhr.responseText;
            }
        });
        r.send();
    };

    _restful = function(method, address, data, callback, async, withCredentials){
        debugger;
        var loadAsync = (async !== false);
        var credentials = (withCredentials !== false);
        address = (address.indexOf("?")!==-1) ? address+"&v="+o2.version.v : address+"?v="+o2.version.v;

        var res = new Request.JSON({
            url: address,
            secure: false,
            method: method,
            emulation: false,
            noCache: true,
            async: loadAsync,
            withCredentials: credentials,
            onSuccess: function(responseJSON, responseText){
                // var xToken = this.getHeader("authorization");
                // if (!xToken) xToken = this.getHeader("x-token");
                // if (xToken){
                //     if (layout){
                //         if (!layout.session) layout.session = {};
                //         layout.session.token = xToken;
                //     }
                // }
                o2.runCallback(callback, "success", [responseJSON])
            },
            onFailure: function(xhr){
                o2.runCallback(callback, "requestFailure", [xhr]);
            }.bind(this),
            onError: function(text, error){
                o2.runCallback(callback, "error", [text, error]);
            }.bind(this)
        });

        res.setHeader("Content-Type", "application/json; charset=utf-8");
        if (window.layout) {
            if (layout["debugger"]){
                res.setHeader("x-debugger", "true");
            }
            if (layout.session){
                if (layout.session.token) {
                    res.setHeader("x-token", layout.session.token);
                    res.setHeader("authorization", layout.session.token);
                }
            }
        }
        //Content-Type	application/x-www-form-urlencoded; charset=utf-8
        res.send(data);
        return res;
    };

    var _release = function(o){
        var type = typeOf(o);
        switch (type){
            case "object":
                for (var k in o){
                    o[k] = null;
                }
                break;
            case "array":
                for (var i=0; i< o.length; i++){
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
        for (var i = 0; i < keys.length; i++)
            descs.push([keys[i], convertToDescriptor(properties[keys[i]])]);
        for (var i = 0; i < descs.length; i++){
            if (Object.defineProperty && (Browser.name=="ie" && Browser.version!=8)){
                Object.defineProperty(obj, descs[i][0], descs[i][1]);
            }else{
                if (descs[i][1].value) obj[descs[i][0]] = descs[i][1].value;
                if (descs[i][1].get) obj["get"+descs[i][0].capitalize()] = descs[i][1].get;
                if (descs[i][1].set) obj["set"+descs[i][0].capitalize()] = descs[i][1].set;
            }
        }
        return obj;
    };

    this.o2.Class = _Class;
    this.o2.require = _require;
    this.o2.requireApp = _requireApp;
    this.o2.JSON = _json;
    this.o2.loadLP = _loadLP;
    this.o2.restful = _restful;
    this.o2.release = _release;
    this.o2.defineProperties = _defineProperties;

})();
o2.core = true;