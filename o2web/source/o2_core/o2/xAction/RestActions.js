MWF.xAction = MWF.xAction || {};
//MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xAction.RestActions = MWF.Actions = {
    "actions": {},
    "loadedActions": {},
    "get": function(root){
        if (this.actions[root]) return this.actions[root];

        var actions = null;
        var url = o2.session.path+"/xAction/services/"+root+".json";
        MWF.getJSON(url, function(json){actions = json;}.bind(this), false, false, false);

        if (!MWF.xAction.RestActions.Action[root] && actions.clazz) MWF.require("MWF.xAction.services."+actions.clazz, null, false);
        if (!MWF.xAction.RestActions.Action[root]) MWF.xAction.RestActions.Action[root] = new Class({Extends: MWF.xAction.RestActions.Action});

        this.actions[root] = new MWF.xAction.RestActions.Action[root](root, actions);
        return this.actions[root];
    },
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        var jaxrs = null;
        //var url = this.getHost(root)+"/"+root+"/describe/describe.json";
        var url = this.getHost(root)+"/"+root+"/describe/api.json";
        //var url = "../o2_core/o2/xAction/temp.json";
        MWF.getJSON(url, function(json){jaxrs = json.jaxrs;}.bind(this), false, false, false);
        if (jaxrs){
            var actionObj = {};
            jaxrs.each(function(o){
                if (o.methods && o.methods.length){
                    var actions = {};
                    o.methods.each(function(m){
                        var o = {"uri": "/"+m.uri};
                        if (m.method) o.method = m.method;
                        if (m.enctype) o.enctype = m.enctype;
                        actions[m.name] = o;
                    }.bind(this));
                    actionObj[o.name] = new MWF.xAction.RestActions.Action(root, actions);
                    //actionObj[o.name] = new MWF.xAction.RestActions.Action(root, o.methods);
                }
            }.bind(this));
            this.loadedActions[root] = actionObj;
            return actionObj;
        }
        return null;
    },
    //actions: [{"action": "", "subAction": "TaskAction", "name": "list", "par": [], "body": "",  "urlEncode"： false, "cache": false}]
    async: function(actions, callback){
        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;
        var res = [];
        var len = actions.length;
        var jsons = new Array(len-1);

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var actionArgs = action.par || [];
            actionArgs.push(function(json){
                jsons[i] = json;
                res.push(true);
                cb();
            });
            actionArgs.push(function(xhr, text, error){
                res.push(false);
                if (!cbf){
                    _doError(xhr, text, error);
                }else{
                    cbf();
                }
                cb();
            });
            actionArgs.push(true);
            actionArgs.push(action.urlEncode);
            actionArgs.push(action.cache);
            action.action[action.subAction][action.name].apply(action.action[action.subAction], actionArgs);
        });
    },

    //actions: [{"action": "", "name": "list", "par": [], "body": "",  "urlEncode"： false, "cache": false}]
    invokeAsync2: function(actions, callback){

        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;
        var res = [];
        var len = actions.length;
        var jsons = new Array(len-1);

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var actionArgs = action.par || [];
            actionArgs.push(function(json){
                jsons[i] = json;
                res.push(true);
                cb();
            });
            actionArgs.push(function(xhr, text, error){
                res.push(false);
                if (!cbf){
                    _doError(xhr, text, error);
                }else{
                    cbf();
                }
                cb();
            });
            actionArgs.push(true);
            actionArgs.push(action.urlEncode);
            actionArgs.push(action.cache);
            action.action[action.name].apply(action.action, actionArgs);
        });
    },

    "getHost": function(root){
        var addressObj = layout.serviceAddressList[root];
        var address = "";
        if (addressObj){
            //var mapping = layout.getAppUrlMapping();
            address = layout.config.app_protocol+"//"+(addressObj.host || window.location.hostname)+(addressObj.port==80 ? "" : ":"+addressObj.port);
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port;
            //var mapping = layout.getCenterUrlMapping();
            address = layout.config.app_protocol+"//"+host+(port=="80" ? "" : ":"+port);
        }
        return address;
    },
    "invokeAsync": function(actions, callback){
        var len = actions.length;
        var parlen = arguments.length-2;
        var res = [];
        var jsons = new Array(len-1);
        var args = arguments;

        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var par = (i<parlen) ? args[i+2] : args[parlen+1];
            if (par){
                var actionArgs = (o2.typeOf(par)==="array") ? par : [par];
                actionArgs.unshift(function(xhr, text, error){
                    res.push(false);
                    if (!cbf){
                        _doError(xhr, text, error);
                    }else{
                        cbf();
                    }
                    cb();
                });
                //actionArgs.unshift(null);
                actionArgs.unshift(function(json){
                    jsons[i] = json;
                    res.push(true);
                    cb();
                });

                var p = action.action[action.name].apply(action.action, actionArgs);
                // p.catch(function(xhr, text, error){
                //     res.push(false);
                //     if (!cbf){
                //         _doError(xhr, text, error);
                //     }else{
                //         cbf();
                //     }
                //     cb();
                // })

            }else{
                action.action[action.name](function(){
                    jsons[i] = json;
                    res.push(true);
                    cb();
                }, function(xhr, text, error){
                    res.push(false);
                    if (!cbf){
                        _doError(xhr, text, error);
                    }else{
                        cbf();
                    }
                    cb();
                });
            }
        });
    }
};
MWF.xAction.RestActions.Action = new Class({
    initialize: function(root, actions){
        this.action = new MWF.xDesktop.Actions.RestActions("/xAction/services/"+root+".json", root, "");
        this.action.actions = actions;

        Object.each(this.action.actions, function(service, key){
            if (service.uri) if (!this[key]) this.createMethod(service, key);
        }.bind(this));
    },
    createMethod: function(service, key){
        var jaxrsUri = service.uri;
        var re = new RegExp("\{.+?\}", "g");
        var replaceWords = jaxrsUri.match(re);
        var parameters = [];
        if (replaceWords) parameters = replaceWords.map(function(s){
            return s.substring(1,s.length-1);
        });

        this[key] = this.invokeFunction(service, parameters, key);
    },
    invokeFunction: function(service, parameters, key){
        //uri的参数, data(post, put), file(formData), success, failure, async
        return function(){
            var i = parameters.length-1;
            var n = arguments.length;
            var functionArguments = arguments;
            var parameter = {};
            var success, failure, async, data, file;
            if (typeOf(functionArguments[0])==="function"){
                i=-1;
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
                parameters.each(function(p, x){
                    parameter[p] = (n>++i) ? functionArguments[i] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    if ((!service.enctype) || service.enctype.toLowerCase()!=="formdata"){
                        data = (n>++i) ? functionArguments[i] : null;
                    }else{
                        data = (n>++i) ? functionArguments[i] : null;
                        file = (n>++i) ? functionArguments[i] : null;
                    }
                }
                async = (n>++i) ? functionArguments[i] : null;
                urlEncode = (n>++i) ? functionArguments[i] : true;
                cache = (n>++i) ? functionArguments[i] : (Browser.name != "ie");
            }else{
                parameters.each(function(p, x){
                    parameter[p] = (n>x) ? functionArguments[x] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    if ((!service.enctype) || service.enctype.toLowerCase()!=="formdata"){
                        data = (n>++i) ? functionArguments[i] : null;
                    }else{
                        data = (n>++i) ? functionArguments[i] : null;
                        file = (n>++i) ? functionArguments[i] : null;
                    }
                }
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
                async = (n>++i) ? functionArguments[i] : null;
                urlEncode = (n>++i) ? functionArguments[i] : true;
                cache = (n>++i) ? functionArguments[i] : (Browser.name != "ie");
            }
            return this.invoke(service,{"name": key, "async": async, "data": data, "file": file, "parameter": parameter, "success": success, "failure": failure, "urlEncode": urlEncode, "cache": cache});
            //if (!cache) debugger;
            //return this.action.invoke({"name": key, "async": async, "data": data, "file": file, "parameter": parameter, "success": success, "failure": failure, "urlEncode": urlEncode, "cache": cache});
        }.bind(this);
    },
    invoke: function(service, options){
        return this.action.invoke(options);
    }
});

