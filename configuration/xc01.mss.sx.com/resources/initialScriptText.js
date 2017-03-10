bind = {};
var library = {
    'version': '4.0',
    "defineProperties": Object.defineProperties || function (obj, properties) {
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
                d.enumerable = !!obj.enumerable;
            if (hasProperty(desc, "configurable"))
                d.configurable = !!obj.configurable;
            if (hasProperty(desc, "value"))
                d.value = obj.value;
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

        for (var i = 0; i < descs.length; i++)
            Object.defineProperty(obj, descs[i][0], descs[i][1]);

        return obj;
    },
    'typeOf': function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();
        if (item.constructor == Array) return 'array';

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
            //if ('item' in item) return 'collection';
        }

        return typeof item;
    },

    'JSONDecode': function(string, secure){
        if (!string || library.typeOf(string) != 'string') return null;
        return eval('(' + string + ')');
    },

    'JSONEncode': function(obj){
        if (obj && obj.toJSON) obj = obj.toJSON();
        switch (library.typeOf(obj)){
            case 'string':
                return '"' + obj.replace(/[\x00-\x1f\\"]/g, escape) + '"';
            case 'array':
                var string = [];
                for (var i=0; i<obj.length; i++){
                    var json = library.JSONEncode(obj[i]);
                    if (json) string.push(json);
                }
                return '[' + string + ']';
            case 'object': case 'hash':
            var string = [];
            for (key in obj){
                var json = library.JSONEncode(obj[key]);
                if (json) string.push(library.JSONEncode(key) + ':' + json);
            }
            return '{' + string + '}';
            case 'number': case 'boolean': return '' + obj;
            case 'null': return 'null';
        }
        return null;
    }
};
(function(){
    var o={"indexOf": {
        "value": function(item, from){
            var length = this.length >>> 0;
            for (var i = (from < 0) ? Math.max(0, length + from) : from || 0; i < length; i++){
                if (this[i] === item) return i;
            }
            return -1;
        }
    }};
    library.defineProperties(Array.prototype, o);
})();

var wrapWorkContext = {
    "getTask": function(){return library.JSONDecode(workContext.getCurrentTaskCompleted());},
    "getWork": function(){return library.JSONDecode(workContext.getWork());},
	"getActivity": function(){return library.JSONDecode(workContext.getActivity());},
    "getTaskList": function(){return library.JSONDecode(workContext.getTaskList());},
    "getWorkLogList": function(){return library.JSONDecode(workContext.getWorkLogList());},
    "getAttachmentList": function(){return library.JSONDecode(workContext.getAttachmentList());},
    "getRouteList": function(){return library.JSONDecode(workContext.getRouteList());},
    "getInquiredRouteList": function(){return library.JSONDecode(workContext.getInquiredRouteList());},
    "setTitle": function(title){workContext.setTitle(title);},
    "getControl": function(){return null;}
};

var includedScripts = [];
var _self = this;
var include = function(name, callback){
    if (includedScripts.indexOf(name)==-1){
        var json = library.JSONDecode(_self.workContext.getScript(name, includedScripts));
        includedScripts = includedScripts.concat(json.importedList);
        if (json.text){
            MWF.Macro.exec(json.data.text, bind);
            if (callback) callback.apply(bind);
        }
    }
};

var define = function(name, fun, overwrite){
    var over = true;
    if (overwrite===false) over = false;
    var o = {};
    o[name] = {"value": fun, "configurable": over};
    library.defineProperties(bind, o);
};

var Dict =  function(name){
    var dictionary = _self.dictionary;
    this.name = name;
    this.get = function(path){
        return library.JSONDecode(dictionary.select(this.name, path));
    };
    this.set = function(path, value){
        try {
            dictionary.update(this.name, library.JSONEncode(value), path);
            return true;
        }catch(e){
            return false;
        }
    };
    this.add = function(path, value){
        try {
            dictionary.insert(this.name, library.JSONEncode(value), path);
            return true;
        }catch(e){
            return false;
        }
    };
};

bind.library = library;
bind.data = this.data;
bind.workContext = wrapWorkContext;
bind.service = this.webserviceClient;
bind.org = this.organization;
bind.include = include;
bind.define = define;
bind.Dict = Dict;
bind.form = null;
bind.body = this.body || null;
bind.parameters = this.parameters || null;