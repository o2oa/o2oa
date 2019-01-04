MWF.xScript = MWF.xScript || {};
MWF.xScript.Environment = function(ev){
    var jData = ev.data;
    var forms = ev.forms;
    var all = ev.all;
    var work = ev.work;
    var task = this.task;
    var taskList = ev.taskList;
    var control = ev.control;
    var activity = ev.activity;
    var session = ev.session;
    var org = ev.org;
    var layout = layout;
    var target = ev.target;
    var event = ev.event;

    var jsonData = function(d, key, parent){
        var data = d;
        this.key = key;
        this.parent = parent;

        var getter = function(data, mk, _self){
            return function(){
                var t = typeOf(data[mk]);
                if (t!="array" && t!="object") return data[mk];
                return new jsonData(data[mk], mk, _self);
            }
        }
        var setter = function(data, mk, _self){
            return function(v){
                data[mk] = v;
                var fieldKey = mk;
                if (!forms[fieldKey]){
                    fieldKey = _self.key;
                    var p = _self;
                    while (fieldKey && !forms[fieldKey]){
                        p = p.parent;
                        fieldKey = p.key;
                    }
                }
                if (fieldKey) if (forms[fieldKey]) forms[fieldKey].setData();
            }
        }
        var objectFun = function(){
            for (var k in data){
                var o = {};
                o[k] = {
                    "get": getter.apply(this, [data, k, this]),
                    "set": setter.apply(this, [data, k, this]),
                };
                MWF.defineProperties(this, o);
            }
        }

        var type = typeOf(data);


        if (type=="object"){


            //for (var k in data){
            //    var mk = k;
            //    var o = {};
            //    o[mk] = {
            //        "get": (function(){
            //            var data = this.data;
            //            var mk = this.mk;
            //            var _self = this._self;
            //            return function(){
            //                var t = typeOf(data[mk]);
            //                if (t!="array" && t!="object") return data[mk];
            //                return new jsonData(data[mk], mk, _self)
            //            }
            //        }).apply({"data": data, "mk": mk, "_self": this}),
            //
            //        "set": (function(){
            //            var data = this.data;
            //            var mk = this.mk;
            //            var _self = this._self;
            //            return function(v){
            //                debugger;
            //                data[mk] = v;
            //                var fieldKey = mk;
            //                if (!forms[fieldKey]){
            //                    fieldKey = _self.key;
            //                    if (fieldKey){
            //                        var p = _self;
            //                        while (fieldKey && !forms[fieldKey]){
            //                            p = p.parent;
            //                            fieldKey = p.key;
            //                        }
            //                    }
            //                }
            //                if (fieldKey) if (forms[fieldKey]) forms[fieldKey].setData();
            //            }
            //        }).apply({"data": data, "mk": mk, "_self": this}),
            //    };
            //    MWF.defineProperties(this, o);
            //}
        }else if (type=="array"){
            for (var k=0; k<data.length; k++) {
                var mk = k;
                Object.defineProperty(this, mk, {
                    "get": (function(){
                        var data = this.data;
                        var mk = this.mk;
                        var _self = this._self;
                        return function(){
                            var t = typeOf(data[mk]);
                            if (t!="array" && t!="object") return data[mk];
                            return new jsonData(data[mk], _self.key, _self.parent)
                        }
                    }).apply({"data": data, "mk": mk, "_self": this}),

                    "set": (function(){
                        var data = this.data;
                        var mk = this.mk;
                        var _self = this._self;

                        return function(v){
                            data[mk] = v;
                            if (_self.parent){
                                var p = _self.parent;
                                var fieldKey = p.key;
                                while (fieldKey && !forms[fieldKey]){
                                    p = p.parent;
                                    fieldKey = p.key;
                                }
                                if (fieldKey) if (forms[fieldKey]) forms[fieldKey].setData(data[fieldKey]);
                            }
                        }
                    }).apply({"data": data, "mk": mk, "_self": this}),
                });
            }
        }else{}
    }

    this.data = new jsonData(jData);
};