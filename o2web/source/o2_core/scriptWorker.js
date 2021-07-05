window = self;
window.addEvent = function(){};
self.window = window;
window.execScript = function(text){
    return eval(text);
};

if (!this.document) document = {
    "window": self,
    "head": "head",
    "location": "url",
    "documentElement": {
        style:{
            "color": ""
        }
    },
    "html": {
        style:{
            "color": ""
        }
    },
    "readyState": "loaded",
    "addEventListener": function(){},
    "removeEventListener": function(){},
    "createElement": function(){
        return {
            contains: null,
            getAttribute:function(){return null},
            cloneNode: function(){return {
                firstChild: {
                    childNodes: {length: 1},
                    value: "s"
                }
            }},
            firstChild: {
                childNodes: {length: 1},
                value: "s"
            },
            getAttributeNode: function(){return null},
            childNodes: [""],
            style: {},
            appendChild: function(){}
        }
    }
};

self.importScripts("../o2_core/o2.min.js");

layout = window.layout || {};
layout.desktop = layout;
layout.desktop.type = "app";
var locate = window.location;
layout.protocol = locate.protocol;
layout.inBrowser = true;
layout.session = layout.session || {};
layout.debugger = (locate.href.toString().indexOf("debugger") !== -1);
layout.anonymous = (locate.href.toString().indexOf("anonymous") !== -1);
o2.xApplication = o2.xApplication || {};

o2.xDesktop = o2.xDesktop || {};
o2.xDesktop.requireApp = function (module, clazz, callback, async) {
    o2.requireApp(module, clazz, callback, async);
};

layout.openApplication = function(){};
layout.refreshApp = function(){};
layout.load = function(){};

layout.readys = [];
layout.addReady = function () {
    for (var i = 0; i < arguments.length; i++) {
        if (o2.typeOf(arguments[i]) === "function") layout.readys.push(arguments[i]);
    }
};

o2.addReady(function () {
    var _setLayoutService = function(service, center){
        layout.serviceAddressList = service;
        layout.centerServer = center;
        layout.desktop.serviceAddressList = service;
        layout.desktop.centerServer = center;
    };
    var _getDistribute = function (callback) {
        if (layout.config.app_protocol === "auto") {
            layout.config.app_protocol = window.location.protocol;
        }
        if (layout.config.configMapping && (layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname])){
            var mapping = layout.config.configMapping[window.location.host] || layout.config.configMapping[window.location.hostname];
            if (mapping.servers){
                layout.serviceAddressList = mapping.servers;
                layout.desktop.serviceAddressList = mapping.servers;
                if (mapping.center) center = (o2.typeOf(mapping.center)==="array") ? mapping.center[0] : mapping.center;
                layout.centerServer = center;
                layout.desktop.centerServer = center;
                if (callback) callback();
            }else{
                if (mapping.center) layout.config.center = (o2.typeOf(mapping.center)==="array") ? mapping.center : [mapping.center];
                o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                    _setLayoutService(service, center);
                    if (callback) callback();
                }.bind(this));
            }
        }else{
            o2.xDesktop.getServiceAddress(layout.config, function (service, center) {
                _setLayoutService(service, center);
                if (callback) callback();
            }.bind(this));
        }
    };

    var _load = function () {
        var _loadApp = function (data) {
            //用户已经登录
            if (data){
                layout.user = data;
                layout.session = layout.session || {};
                layout.session.user = data;
                layout.session.token = data.token;
                layout.desktop.session = layout.session;
            }
            while (layout.readys && layout.readys.length) {
                layout.readys.shift().apply(window);
            }
        };

        var data = { name: "anonymous", roleList: [] };
        o2.Actions.get("x_organization_assemble_authentication").getAuthentication(function (json) {
            data = json.data;
        }.bind(this), null, false);
        _loadApp(data);
    };

    //异步载入必要模块
    layout.config = null;
    var configLoaded = false;
    var lpLoaded = false;
    var commonLoaded = false;
    var lp = o2.session.path + "/lp/" + o2.language + ".js";

    var loadModuls = function () {
        lpLoaded = true;

        var modules = ["o2.xDesktop.$all"];
        o2.require(modules, {
            "onSuccess": function () {
                commonLoaded = true;
                if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
            }
        });
    }

    if (!o2.LP){
        o2.load(lp, loadModuls);
    }else{
        loadModuls();
    }

    o2.getJSON("../x_desktop/res/config/config.json", function (config) {
        layout.config = config;
        o2.tokenName = config.tokenName || "x-token";
        configLoaded = true;
        if (configLoaded && commonLoaded && lpLoaded) _getDistribute(function () { _load(); });
    });

});

var _worker = self;
layout.addReady(function(){
    window.isCompletionEnvironment = true;

    _worker.runtimeEnvironment = {};

    _worker.createEnvironment = function(runtime, url){
        return new Promise(function(s, f){
            o2.xhr_get(url, function (xhr) {
                if (xhr.responseText){
                    _worker.createRuntime(runtime, xhr.responseText);
                    if (s) s();
                }
            }, function (err) {
                f(err);
            });
        });
    };
    _worker.createRuntime = function(runtime, script){
        var code = "o2.Macro.swapSpace.tmpMacroCompletionFunction = function (){\n" + script + "\nreturn bind;" + "\n};";
        Browser.exec(code);
        var ev = o2.Macro.swapSpace.tmpMacroCompletionFunction() ;
        _worker.runtimeEnvironment[runtime] = {
            "environment": ev,
            exec: function(code){
                try{
                    return o2.Macro.exec(code, this.environment);
                }catch(e){
                    return null;
                }
            }
        }
    };

    _worker.getCompletionEnvironment = function(runtime) {
        if (!_worker.runtimeEnvironment[runtime]) {
            return new Promise(function(s){
                o2.require("o2.xScript.Macro", function() {
                    switch (runtime) {
                        case "service":
                            s(_worker.getServiceCompletionEnvironment());
                            break;
                        case "server":
                            s(_worker.getServerCompletionEnvironment());
                            break;
                        case "all":
                            s(_worker.getAllCompletionEnvironment());
                            break;
                        default:
                            s(_worker.getDefaultCompletionEnvironment());
                    }
                });
            });
        } else {
            return Promise.resolve();
        }
    };
    _worker.getServiceCompletionEnvironment = function() {
        var runtime = "service";
        return _worker.createEnvironment(runtime, "../x_desktop/js/initalServiceScriptSubstitute.js");
    };
    _worker.getServerCompletionEnvironment = function() {
        var runtime = "server";
        return _worker.createEnvironment(runtime, "../x_desktop/js/initalScriptSubstitute.js");
    };
    _worker.getDefaultCompletionEnvironment = function(){
        var runtime = "web";
        return new Promise(function(s){
            o2.getJSON("../o2_core/o2/widget/$JavascriptEditor/environment.json", function (data) {
                json = data;
                _worker.runtimeEnvironment[runtime] = new o2.Macro.FormContext(json);
                if (s) s();
            });
        });
    }
    _worker.getAllCompletionEnvironment = function(){
        var runtime = "all";
        var arr = [
            _worker.getServiceCompletionEnvironment(),
            _worker.getServerCompletionEnvironment(),
            _worker.getDefaultCompletionEnvironment()
        ];

        return Promise.all(arr).then(function(){
            if (_worker.runtimeEnvironment["service"] && _worker.runtimeEnvironment["server"] && _worker.runtimeEnvironment["web"] ){
                var ev = Object.merge(_worker.runtimeEnvironment["service"].environment,
                    _worker.runtimeEnvironment["server"].environment,
                    _worker.runtimeEnvironment["web"].environment)


                _worker.runtimeEnvironment[runtime] = {
                    "environment": ev,
                    exec: function(code){
                        try{
                            return o2.Macro.exec(code, this.environment);
                        }catch(e){
                            return null;
                        }
                    }
                }
            }
        });
    };

    _worker.getMonacoCompletions = function(o, range, code){
        var arr = [];
        o = (o2.typeOf(o)=="array") ? o[0] : o;
        Object.keys(o).each(function (key) {
            if (key!="__type__"){
                var keyType = typeOf(o[key]);
                var oRange = Object.clone(range);
                switch (keyType){
                    case "function":
                        var count = o[key].length;
                        var v = key + "(";
                        for (var i = 1; i <= count; i++) v += (i == count) ? "par" + i : "par" + i + ", ";
                        v += ")";
                        key = key+"()";
                        arr.push({ label: key, kind: 1, insertText: v, detail: keyType, range: oRange });
                        break;
                    default:
                        var insertText = key;
                        var filterText = key;
                        var kind = 3;
                        if (o[key]){
                            var text = (keyType=="array" && o[key][0] && o[key][0]["__type__"]) ?  o[key][0]["__type__"] : o[key].toString();
                            var flagCount = text.indexOf(":");
                            if (flagCount!=-1){
                                keyType = text.substr(0,flagCount);
                                text = text.substr(flagCount+1);
                            }
                            var oType = (o["__type__"]) ? o["__type__"].substr(0, o["__type__"].indexOf(":")) : o2.typeOf(o);
                            if (oType=="object array"){
                                if (code.substr(-1)!=="]"){
                                    insertText =  "[0]."+key;
                                    oRange.startColumn = oRange.startColumn-1;
                                }
                                filterText="."+filterText;
                            }
                        }
                        switch (keyType){
                            case "array":
                                kind = 15;
                                key = key+"[]";
                                break;
                            case "object array":
                                kind = 15;
                                key = key+"[]";
                                break;
                        }
                        arr.push({ label: key, filterText: filterText, key, kind: kind, insertText: insertText, detail: keyType, documentation: text, range: oRange});
                }
            }
        });
        return arr;
    };
    _worker.getAceCompletions = function(o, range, code){
        var arr = [];
        o = (o2.typeOf(o)=="array") ? o[0] : o;
        Object.keys(o).each(function(key){
            if (key!="__type__"){
                var keyType = typeOf(o[key]);
                var oRange = (range) ? Object.clone(range): null;
                var offset = 0;
                switch (keyType){
                    case "function":
                        var count = o[key].length;
                        var v = key + "(";
                        for (var i = 1; i <= count; i++) v += (i == count) ? "par" + i : "par" + i + ", ";
                        v += ")";
                        key = key+"()";
                        arr.push({ caption: key, value: v, score: 3, meta: keyType, type: keyType, docText: key });
                        break;
                    default:
                        var insertText = key;
                        var filterText = key;
                        var kind = 3;
                        if (o[key]){
                            var text = (keyType=="array" &&  o[key][0]["__type__"]) ?  o[key][0]["__type__"] : o[key].toString();
                            var flagCount = text.indexOf(":");
                            if (flagCount!=-1){
                                keyType = text.substr(0,flagCount);
                                text = text.substr(flagCount+1);
                            }
                            var oType = (o["__type__"]) ? o["__type__"].substr(0, o["__type__"].indexOf(":")) : o2.typeOf(o);
                            if (oType=="object array"){
                                if (code.substr(-1)!=="]"){
                                    insertText =  "[0]."+key;
                                    offset = -1;
                                }
                                filterText="."+filterText;
                            }
                        }
                        if (keyType=="array" || keyType=="object array") key = key+"[]";
                        arr.push({ caption: key, value: insertText, score: 3, meta: keyType, type: keyType, docText: text, offset: offset });
                }
            }
        });
        return arr;
    };

    _worker.overwriteRequest = function(){
        if (!_worker.isOverwriteRequest){
            if (_worker.XMLHttpRequest){
                var send = XMLHttpRequest.prototype.send;
                var open = XMLHttpRequest.prototype.open;
                XMLHttpRequest.prototype.open = function(method, url){
                    this.continueRequest = false;
                    if (url){
                        var tmpUrl = url;
                        if (tmpUrl.indexOf(":")==-1) tmpUrl = "http://"+tmpUrl;
                        var u = new URI(tmpUrl);
                        var f = u.get("file");
                        var ext = f.substr(f.indexOf(".")+1).toLowerCase();
                        if (ext=="json" || ext=="js" || ext=="html" ){
                            this.continueRequest = true;
                            return open.apply(this, arguments);
                        }
                    }
                };
                XMLHttpRequest.prototype.send = function(){
                    if (this.continueRequest){
                        return send.apply(this, arguments);
                    }
                };
            }
            if (_worker.fetch){
                var nativeFetch = _worker.fetch;
                _worker.fetch = function(o){
                    var url = o;
                    if (o2.typeOf(o)=="string"){
                        url = o;
                    }else if (Object.getPrototypeOf(o)==NativeRequest.prototype){
                        url = o.url;
                    }
                    if (url.indexOf(":")==-1) url = "http://"+url;
                    var u = new URI(url);
                    var f = u.get("file");
                    var ext = f.substr(f.indexOf(".")+1).toLowerCase();
                    if (ext=="json" || ext=="js" || ext=="html" ){
                        return nativeFetch.apply(_worker, arguments);
                    }
                }
            }

            _worker.isOverwriteRequest = true;
        }
    };

    _worker.exec = function(id, uuid, code, preCode, range, runtime, type){
        var codeText = code;
        var promise = _worker.getCompletionEnvironment(runtime);
        promise.then(function(){
            _worker.overwriteRequest();
            if (_worker.runtimeEnvironment[runtime]){

                code = "return "+code+";";
                if (preCode){
                    code = "try{\n"+preCode+"\n}catch(e){}\n"+"try{\n"+code+"\n}catch(e){return null;}";
                }
                //code = "try {\n"+code+"\n}catch(e){return null;}";
                var o = _worker.runtimeEnvironment[runtime].exec(code);
                if (o) {
                    var completions = (type=="ace") ? _worker.getAceCompletions(o, range, codeText) : _worker.getMonacoCompletions(o ,range, codeText);
                    _worker.postMessage({"o": completions, "id": id, "uuid": uuid});
                }
            }
        }, function(){});
    };
    _worker.postMessage({"type": "ready"});
});
onmessage = function(e) {
    if (e.data){
        var id = e.data.id;
        var uuid = e.data.uuid;
        var code = e.data.code;
        var runtime = e.data.runtime;
        var type = e.data.type;
        var preCode = e.data.preCode;
        var range = e.data.range;
        if (id && code && runtime && uuid && type){
            _worker.exec(id, uuid, code, preCode, range, runtime, type)
        }
    }
}
