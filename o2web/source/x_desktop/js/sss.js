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
    "getTaskCompletedList": function(){return library.JSONDecode(workContext.getTaskCompletedList());},
    "getReadList": function(){return library.JSONDecode(workContext.getReadList());},
    "getReadCompletedList": function(){return library.JSONDecode(workContext.getReadCompletedList());},
    "getReviewList": function(){return library.JSONDecode(workContext.getReviewList());},
    "getWorkLogList": function(){return library.JSONDecode(workContext.getWorkLogList());},
    "getAttachmentList": function(){return library.JSONDecode(workContext.getAttachmentList());},
    "getRouteList": function(){return library.JSONDecode(workContext.getRouteList());},
    "setTitle": function(title){workContext.setTitle(title);},

    "getControl": function(){return null;},
    "getInquiredRouteList": function(){return null;}
};
//applications


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
if ((typeof JSON) == 'undefined'){
    JSON = {};
}
JSON.validate = function(string){
    string = string.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, '@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, ']').replace(/(?:^|:|,)(?:\s*\[)+/g, '');
    return (/^[\],:{}\s]*$/).test(string);
};
JSON.encode = JSON.stringify ? function(obj){
    return JSON.stringify(obj);
} : function(obj){
    if (obj && obj.toJSON) obj = obj.toJSON();
    switch (typeof obj){
        case 'string':
            return '"' + obj.replace(/[\x00-\x1f\\"]/g, escape) + '"';
        case 'array':
            var string = [];
            for (var i=0; i<obj.length; i++){
                var json = JSON.encode(obj[i]);
                if (json) string.push(json);
            }
            return '[' + string + ']';
        case 'object': case 'hash':
        var string = [];
        for (key in obj){
            var json = JSON.encode(obj[key]);
            if (json) string.push(JSON.encode(key) + ':' + json);
        }
        return '{' + string + '}';
        case 'number': case 'boolean': return '' + obj;
        case 'null': return 'null';
    }
    return null;
};
JSON.decode = function(string, secure){
    if (!string || (typeof string) !== 'string') return null;

    if (secure || JSON.secure){
        if (JSON.parse) return JSON.parse(string);
        if (!JSON.validate(string)) throw new Error('JSON could not decode the input; security is enabled and the value is not secure.');
    }
    return eval('(' + string + ')');
};
var body = {
    set: function(data){
        if ((typeof data)=="string"){
            if (jaxrsBody) jaxrsBody.set(data);
        }else{
            if (jaxrsBody) jaxrsBody.set(JSON.encode(data));
        }
    }
};

var getNameFlag = function(name){
    var t = library.typeOf(name);
    if (t==="array"){
        var v = [];
        name.forEach(function(id){
            v.push((library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
        });
        return v;
    }else{
        return [(t==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
    }
};
var org = {
    "oGroup": this.organization.group(),
    "oIdentity": this.organization.identity(),
    "oPerson": this.organization.person(),
    "oPersonAttribute": this.organization.personAttribute(),
    "oRole": this.organization.role(),
    "oGroup": this.organization.group(),
    "oUnit": this.organization.unit(),
    "oUnitAttribute": this.organization.unitAttribute(),
    "oUnitDuty": this.organization.unitDuty(),

    "group": function() { return this.oGroup},
    "identity": function() { return this.oIdentity},
    "person": function() { return this.oPerson},
    "personAttribute": function() { return this.oPersonAttribute},
    "role": function() { return this.oRole},
    "group": function() { return this.oGroup},
    "unit": function() { return this.oUnit},
    "unitAttribute": function() { return this.oUnitAttribute},
    "unitDuty": function() { return this.oUnitDuty},

    "getObject": function(o, v){
        var arr = [];
        if (!v || !v.length){
            return null;
        }else{
            for (var i=0; i<v.length; i++){
                var g = this.o.getObject​(v[i]);
                if (g) arr.push(g);
            }
        }
        return arr;
    },
    //群组***************
    //获取群组--返回群组的对象数组
    getGroup: function(name){
        var v = this.oGroup.listObject​(getNameFlag(name));
        if (!v || !v.length) v = null;
        return (v && v.length===1) ? v[0] : v;
    },

    //查询下级群组--返回群组的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    listSubGroup: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oGroup.listWithGroupSubNested(getNameFlag(name));
        }else{
            var v = this.oGroup.listWithGroupSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oGroup, v);
    },
    //查询上级群组--返回群组的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    listSupGroup:function(name, nested){
        var v = null;
        if (nested){
            var v = this.oGroup.listWithGroupSupNested(getNameFlag(name));
        }else{
            var v = this.oGroup.listWithGroupSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oGroup, v);
    },
    //人员所在群组（嵌套）--返回群组的对象数组
    listGroupWithPerson:function(name){
        var v = this.oGroup.listWithPerson(getNameFlag(name));
        return this.getObject(this.oGroup, v);
    },
    //群组是否拥有角色--返回true, false
    groupHasRole: function(name, role){
        nameFlag = (library.typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oGroup.hasRole(nameFlag, getNameFlag(role));
    },

    //角色***************
    //获取角色--返回角色的对象数组
    getRole: function(name){
        var v = this.oRole.listObject(getNameFlag(name));
        if (!v || !v.length) v = null;
        return (v && v.length===1) ? v[0] : v;
    },
    //人员所有角色（嵌套）--返回角色的对象数组
    listRoleWithPerson:function(name){
        var v = this.oRole.listWithPerson​(getNameFlag(name));
        return this.getObject(this.oRole, v);
    },

    //人员***************
    //人员是否拥有角色--返回true, false
    personHasRole: function(name, role){
        nameFlag = (library.typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oPerson.hasRole(nameFlag, getNameFlag(role));
    },
    //获取人员--返回人员的对象数组
    getPerson: function(name){
        var v = this.oPerson.listObject(getNameFlag(name));
        if (!v || !v.length) v = null;
        return (v && v.length===1) ? v[0] : v;
    },
    //查询下级人员--返回人员的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    listSubPerson: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oPerson.listWithPersonSubNested(getNameFlag(name));
        }else{
            var v = this.oPerson.listWithPersonSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },
    //查询上级人员--返回人员的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    listSupPerson: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oPerson.listWithPersonSupNested(getNameFlag(name));
        }else{
            var v = this.oPerson.listWithPersonSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },
    //获取群组的所有人员--返回人员的对象数组
    listPersonWithGroup: function(name){
        var v = this.oPerson.listWithGroup(getNameFlag(name));
        if (!v || !v.length) v = null;
        return v;
    },
    //获取角色的所有人员--返回人员的对象数组
    listPersonWithRole: function(name){
        var v = this.oPerson.listWithRole​(getNameFlag(name));
        return this.getObject(this.oPerson, v);
    },
    //获取身份的所有人员--返回人员的对象数组
    listPersonWithIdentity: function(name){
        var v = this.oPerson.listWithIdentity(getNameFlag(name));
        return this.getObject(this.oPerson, v);
    },
    //获取身份的所有人员--返回人员的对象数组
    getPersonWithIdentity: function(name){
        var v = this.oPerson.listWithIdentity(getNameFlag(name));
        var arr = this.getObject(this.oPerson, v);
        return (arr && arr.length) ? arr[0] : null;
    },
    //查询组织成员的人员--返回人员的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    listPersonWithUnit: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oPerson.listWithUnitSubNested(getNameFlag(name));
        }else{
            var v = this.oPerson.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },

    //人员属性************
    //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
    appendPersonAttribute: function(person, attr, values){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.appendWithPersonWithName(personFlag, attr, values);
    },
    //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
    setPersonAttribute: function(person, attr, values){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.setWithPersonWithName(personFlag, attr, values);
    },
    //获取人员属性值
    getPersonAttribute: function(person, attr){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.listAttributeWithPersonWithName(personFlag, attr);
    },
    //列出人员所有属性的名称
    listPersonAttributeName: function(name){
        var p = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<p.length; i++){
            var v = this.oPersonAttribute.listNameWithPerson(p[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },
    //列出人员的所有属性
    listPersonAllAttribute: function(name){
        // getOrgActions();
        // var data = {"personList":getNameFlag(name)};
        // var v = null;
        // orgActions.listPersonAllAttribute(data, function(json){v = json.data;}, null, false);
        // return v;
    },

    //身份**********
    //获取身份
    getIdentity: function(name){
        var v = this.oIdentity.listObject​(getNameFlag(name));
        if (!v || !v.length) v = null;
        return (v && v.length===1) ? v[0] : v;
    },
    //列出人员的身份
    listIdentityWithPerson: function(name){
        var v = this.oIdentity.listWithPerson​(getNameFlag(name));
        return this.getObject(this.oIdentity, v);
    },
    //查询组织成员身份--返回身份的对象数组
    //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
    listIdentityWithUnit: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oIdentity.listWithUnitSubNested(getNameFlag(name));
        }else{
            var v = this.oIdentity.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oIdentity, v);
    },

    //组织**********
    //获取组织
    getUnit: function(name){
        var v = this.oUnit.listObject(getNameFlag(name));
        if (!v || !v.length) v = null;
        return (v && v.length===1) ? v[0] : v;
    },
    //查询组织的下级--返回组织的对象数组
    //nested  布尔  true嵌套下级；false直接下级；默认false；
    listSubUnit: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oUnit.listWithUnitSubNested(getNameFlag(name));
        }else{
            var v = this.oUnit.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oUnit, v);
    },
    //查询组织的上级--返回组织的对象数组
    //nested  布尔  true嵌套上级；false直接上级；默认false；
    listSupUnit: function(name, nested){
        var v = null;
        if (nested){
            var v = this.oUnit.listWithUnitSupNested(getNameFlag(name));
        }else{
            var v = this.oUnit.listWithUnitSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oUnit, v);
    },
    //根据个人身份获取组织
    //flag 数字    表示获取第几层的组织
    //     字符串  表示获取指定类型的组织
    //     空     表示获取直接所在的组织
    getUnitByIdentity: function(name, flag){
        getOrgActions();
        var getUnitMethod = "current";
        var v;
        if (flag){
            if (library.typeOf(flag)==="string") getUnitMethod = "type";
            if (library.typeOf(flag)==="number") getUnitMethod = "level";
        }
        var n = getNameFlag(name)[0];
        switch (getUnitMethod){
            case "current":
                v = this.oUnit.getWithIdentity(n);
                break;
            case "type":
                v = this.oUnit.getWithIdentityWithType(n, flag);
                break;
            case "level":
                v = this.oUnit.getWithIdentityWithLevel(n, flag);
                break;
        }
        var o = this.oUnit.getObject(v);
        return o;
    },
    //列出身份所在组织的所有上级组织
    listAllSupUnitWithIdentity: function(name){
        var v = this.oUnit.listWithIdentitySupNested(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },
    //获取人员所在的所有组织
    listUnitWithPerson: function(name){
        var v = this.oUnit.listWithPerson(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },
    //列出人员所在组织的所有上级组织
    listAllSupUnitWithPerson: function(name){
        var v = this.oUnit.listWithPersonSupNested(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },
    //根据组织属性，获取所有符合的组织
    listUnitWithAttribute: function(name, attribute){
        var v = this.oUnit.listWithUnitAttribute(name, attribute);
        return this.getObject(this.oUnit, v);
    },
    //根据组织职务，获取所有符合的组织
    listUnitWithDuty: function(name, id){
        var idflag = (library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        var v = this.oUnit.listWithUnitDuty(name, idflag);
        return this.getObject(this.oUnit, v);
    },

    //组织职务***********
    //获取指定的组织职务的身份
    getDuty: function(duty, id){
        var unit = (library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        var v = this.oUnitDuty.listIdentityWithUnitWithName(unit, duty);
        return this.getObject(this.oIdentity, v);
    },



    //获取身份的所有职务名称
    listDutyNameWithIdentity: function(name){
        var ids = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<ids.length; i++){
            var v = this.oUnitDuty.listNameWithIdentity(ids[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },
    //获取组织的所有职务名称
    listDutyNameWithUnit: function(name){
        var ids = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<ids.length; i++){
            var v = this.oUnitDuty.listNameWithUnit(ids[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },
    //获取组织的所有职务
    listUnitAllDuty: function(name){
        // getOrgActions();
        // var data = {"unitList":getNameFlag(name)};
        // var v = null;
        // orgActions.listUnitAllDuty(data, function(json){v = json.data;}, null, false);
        // return v;
    },

    //组织属性**************
    //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
    appendUnitAttribute: function(unit, attr, values){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.appendWithUnitWithName(unitFlag, attr, values);
    },
    //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
    setUnitAttribute: function(unit, attr, values){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.setWithUnitWithName(unitFlag, attr, values);
    },
    //获取组织属性值
    getUnitAttribute: function(unit, attr){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.listAttributeWithUnitWithName(unitFlag, attr);
    },
    //列出组织所有属性的名称
    listUnitAttributeName: function(name){
        var p = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<p.length; i++){
            var v = this.oUnitAttribute.listNameWithUnit​(p[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },
    //列出组织的所有属性
    listUnitAllAttribute: function(name){
        // getOrgActions();
        // var data = {"unitList":getNameFlag(name)};
        // var v = null;
        // orgActions.listUnitAllAttribute(data, function(json){v = json.data;}, null, false);
        // return v;
    }

};
var restfulAcpplication = this.applications;
var Action = (function(){
    var actions = [];
    return function(root, json){
        if (!actions[root]) actions[root] = {};
        Object.keys(json).forEach(function(key){
            actions[root][key] = json[key];
        });
        //Object.merge(actions[root], json);
        this.root = root;
        this.actions = actions[root];
        this.invoke = function(option){
            // {
            //     "name": "",
            //     "data": "",
            //     "parameter": "",,
            //     "success": function(){}
            //     "failure": function(){}
            // }
            if (this.actions[option.name]){
                var uri = this.actions[option.name].uri;
                var method = this.actions[option.name].method || "get";
                if (option.parameter){
                    Object.keys(option.parameter).forEach(function(key){
                        var v = option.parameter[key];
                        uri = uri.replace("{"+key+"}", v);
                    });
                }
                var res = null;
                try{
                    switch (method.toLowerCase()){
                        case "get":
                            res = restfulAcpplication.getQuery(this.root, uri);
                            break;
                        case "post":
                            res = restfulAcpplication.postQuery(this.root, uri, JSON.stringify(option.data));
                            break;
                        case "put":
                            res = restfulAcpplication.putQuery(this.root, uri, JSON.stringify(option.data));
                            break;
                        case "delete":
                            res = restfulAcpplication.deleteQuery(this.root, uri);
                            break;
                        default:
                            res = restfulAcpplication.getQuery(this.root, uri);
                    }
                    if (res){
                        var json = JSON.parse(res.toString());
                        if (option.success) option.success(json);
                    }else{
                        if (option.failure) option.failure();
                    }
                }catch(e){
                    if (option.failure) option.failure(e);
                }
            }
        }
    }
})();
Action.applications = this.applications;

var Actions = {
    'get': function(root){
        if (loadedActions[root]) return loadedActions[root];
        loadedActions[root] = {
            "root": root,
            "get": function(uri, success, failure){
                return returnRes(estfulAcpplication.getQuery(this.root, uri), success, failure);
            },
            "post": function(uri, data, success, failure){
                return returnRes(estfulAcpplication.postQuery(this.root, uri, JSON.stringify(data)), success, failure);
            },
            "put": function(uri, data, success, failure){
                return returnRes(estfulAcpplication.putQuery(this.root, uri, JSON.stringify(data)), success, failure);
            },
            "delete": function(uri, success, failure){
                return returnRes(estfulAcpplication.deleteQuery(this.root, uri), success, failure);
            }
        };
        return loadedActions[root];
    }
};

bind.library = library;
bind.data = this.data;
bind.workContext = wrapWorkContext;
bind.service = this.webservicesClient;
bind.org = org;
bind.Action = Action;
bind.Actions = Actions;
//bind.organization = this.organization;
bind.include = include;
bind.define = define;
bind.Dict = Dict;
bind.form = null;
bind.body = {
    "set": function(data){
        if ((typeof data)==="string"){
            body.set(data);
        }
        if ((typeof data)==="object"){
            body.set(JSON.encode(data));
        }
    }
};
bind.parameters = this.parameters || null;
bind.response = (function(){
    if (this.jaxrsResponse){
        if (this.jaxrsResponse.get()){
            if (JSON.validate(this.jaxrsResponse.get())){
                return {
                    "status": this.jaxrsResponse.status,
                    "value": JSON.decode(this.jaxrsResponse.get())
                };
            }else{
                return {
                    "status": this.jaxrsResponse.status,
                    "value": this.jaxrsResponse.value
                };
            }
        }else{
            return {"status": this.jaxrsResponse.status};
        }
    }
    return null;
}).apply(this);

bind.assginData = {
    "data": null,
    "get": function(){
        this.data = JSON.decode(assginData.get());
        return this.data;
    },
    "set": function(data){
        assginData.set(JSON.encode(data || this.data));
    }
};
bind.expire = {
    "setHour": function(hour){
        try{expire.setHour(hour);}catch(e){}
    },
    "setWorkHour": function(hour){
        try{expire.setWorkHour(hour);}catch(e){}
    },
    "setDate": function(date){
        try{expire.setDate(date);}catch(e){}
    }
};