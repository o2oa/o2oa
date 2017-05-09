MWF.xScript = MWF.xScript || {};
MWF.xScript.CMSEnvironment = function(ev){

    var _data = ev.data;
    var _form = ev.form;
    var _forms = ev.forms;

    this.library = COMMON;
    //this.library.version = "4.0";

    //data
    var getJSONData = function(jData){
        return new MWF.xScript.CMSJSONData(jData, function(data, key, _self){
            var p = {"getKey": function(){return key;}, "getParent": function(){return _self;}};
            while (p && !_forms[p.getKey()]) p = p.getParent();
            if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
        });
    };
    this.setData = function(data){
        this.data = getJSONData(data);
        this.data.save = function(callback){
            form.documentAction.saveData(function(json){if (callback) callback();}.bind(this), null, document.id, jData);
        }
    };
    this.setData(_data);
    //task
    //this.task = ev.task;
    //this.task.process = function(routeName, opinion, callback){
    //    _form.submitWork(routeName, opinion, callback);
    //};
    //inquiredRouteList
    //this.inquiredRouteList = null;

    //workContext
    this.documentContext = {
        "getDocument": function(){return ev.document },
        "getControl": function(){return ev.control;},
        "getAttachmentList": function(){return ev.attachmentList;}
        //"setTitle": function(title){
        //    if (!this.workAction){
        //        MWF.require("MWF.xScript.Actions.WorkActions", null, false);
        //        this.workAction = new MWF.xScript.Actions.WorkActions();
        //    }
        //    this.workAction.setTitle(ev.work.id, {"title": title});
        //}
    };

    //dict
    this.Dict = MWF.xScript.createCMSDict(_form.json.application);
    //org
    var orgActions = null;
    var getOrgActions = function(){
        if (!orgActions){
            MWF.require("MWF.xScript.Actions.OrgActions", null, false);
            orgActions = new MWF.xScript.Actions.OrgActions();
        }
    };
    this.org = {
        //options = {
        //    "name": "职务名称",
        //    "departmentName": "部门名称",
        //    "identityName": "身份名称",
        //    "level": "1",
        //
        //}
        //获取部门指定职务
        "getDepartmentDuty": function(options){
            getOrgActions();
            var v;
            orgActions.getDepartmentDutyBydepartmentName(options.name, options.departmentName, function(json){
                if (json.data) v = json.data.identityList;
            }, null, false);
            return v;
        },
        //"getPersonInfo": function(options){
        //    getOrgActions();
        //    var v,p;
        //    orgActions.getPersonInfo(options.name, function(json){
        //        if (json.data){
        //            v = json.data;
        //            p = v[options.attribute];
        //        }
        //    }, null, false);
        //    return p;
        //},
        //获取个人基本信息
        "getPersonInfo": function(options){
            getOrgActions();
            var v,p;
            orgActions.getPersonInfo(options.name, function(json){
                if (json.data){
                    v = json.data;
                    p = v[options.attribute];
                }
            }, null, false);
            return p;
        },
        //获取个人属性
        "getPersonAttribute": function(options){
            getOrgActions();
            var v;
            orgActions.getPersonAttribute(options.name, options.personName, function(json){
                if (json.data) v = json.data.attributeList;
            }, null, false);
            return v;
        },
        //根据个人身份获取部门
        "getDepartmentByIdentity": function(options){
            getOrgActions();
            var v;
            orgActions.getDepartmentByIdentity(options.personIdentity, function(json){
                v = json.data.name;
            }, null, false);
            return v;
        },
        //修改当前人的属性
        "updatePersonAttribute": function(options){
            getOrgActions();
            var attributeList = {"attributeList":options.attributeList};
            orgActions.updatePersonAttribute(options.name, attributeList, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
        },
        //修改他人属性
        "setPersonAttribute": function(options){
            getOrgActions();
            var attributeList = {"attributeList":options.attributeList};
            orgActions.setPersonAttribute(options.name, options.personName, attributeList, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
        },
        //获取指定部门的所有上级部门
        "listSupDepartmentNested": function(options){
            getOrgActions();
            var v;
            orgActions.listSupDepartmentNested(options.departmentName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取个人所有属性（返回json对象数组）
        "listPersonAttribute": function(options){
            getOrgActions();
            var v;
            orgActions.listPersonAttribute(options.personName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取公司（多个）根据人名，返回列表
        "listCompanyByPerson": function(options){
            getOrgActions();
            var v;
            orgActions.listCompanyByPerson(options.personName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取公司根据身份
        "getCompanyByIdentity": function(options){
            getOrgActions();
            var v;
            orgActions.getCompanyByIdentity(options.personIdentity, function(json){
                v = json.data.name;
            }, null, false);
            return v;
        },
        //列出公司所有属性
        "listCompanyAttribute": function(options){
            getOrgActions();
            var v;
            orgActions.listCompanyAttribute(options.compName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取公司指定属性
        "getCompanyAttribute": function(options){
            getOrgActions();
            var v;
            orgActions.getCompanyAttribute(options.name, options.compName, function(json){
                if (json.data) v = json.data.attributeList;
            }, null, false);
            return v;
        },
        //列出公司所有职务
        "listCompanyDuty": function(options){
            getOrgActions();
            var v;
            orgActions.listCompanyDuty(options.compName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取公司指定职务
        "getCompanyDuty": function(options){
            getOrgActions();
            var v;
            orgActions.getCompanyDuty(options.name, options.compName, function(json){
                if (json.data) v = json.data.identityList;
            }, null, false);
            return v;
        },
        //列出部门所有属性
        "listDepartmentAttribute": function(options){
            getOrgActions();
            var v;
            orgActions.listDepartmentAttribute(options.deptName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取部门指定属性
        "getDepartmentAttribute": function(options){
            getOrgActions();
            var v;
            orgActions.getDepartmentAttribute(options.name, options.deptName, function(json){
                if (json.data) v = json.data.attributeList;
            }, null, false);
            return v;
        },
        //列出部门所有职务
        "listDepartmentDuty": function(options){
            getOrgActions();
            var v;
            orgActions.listDepartmentDuty(options.deptName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取部门指定职务
        //"getDepartmentDuty": function(options){
        //    getOrgActions();
        //    var v;
        //    orgActions.getDepartmentDuty(options.name, options.deptName, function(json){
        //        if (json.data) v = json.data.identityList;
        //    }, null, false);
        //    return v;
        //},
        //列出指定部门下的所有身份（直接成员）
        "listIdentity": function(options){
            getOrgActions();
            var v;
            orgActions.listIdentity(options.deptName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //列出指定部门下的所有身份（包括所有下级部门成员）
        "listIdentityNested": function(options){
            getOrgActions();
            var v;
            orgActions.listIdentityNested(options.deptName, function(json){
                v = json.data;
            }, null, false);
            return v;
        },
        //获取指定部门的上级部门
        "getSupDepartmentDirect": function(options){
            getOrgActions();
            var v;
            orgActions.getSupDepartmentDirect(options.deptName, function(json){
                if (json.data) v = json.data.name;
            }, null, false);
            return v;
        },
        //获取指定人员所在的部门
        "listDepartmentByPerson": function(options){
            getOrgActions();
            var v;
            orgActions.listDepartmentByPerson(options.personName, function(json){
                v = json.data;
            }, null, false);
            return v;
        }
    };

    this.service = {
        "jaxwsClient": {},
        "jaxrsClient":{}
    };

    var lookupAction = null;
    var getLookupAction = function(callback){
        if (!lookupAction){
            MWF.require("MWF.xDesktop.Actions.RestActions", function(){
                lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
                lookupAction.getActions = function(actionCallback){
                    this.actions = {
                        "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
                        "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                    };
                    if (actionCallback) actionCallback();
                }
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    };

    this.view = {
        "lookup": function(view, callback){
            getLookupAction(function(){
                lookupAction.invoke({"name": "lookup","async": true, "parameter": {"view": view.view, "application": view.application},"success": function(json){
                    var data = {
                        "grid": json.data.grid,
                        "groupGrid": json.data.groupGrid
                    };
                    if (callback) callback(data);
                }.bind(this)});
            }.bind(this));
        },
        "select": function(view, callback, options){
            if (view.view){
                var viewJson = {
                    "application": view.application || "",
                    "viewName": view.view || "",
                    "isTitle": view.isTitle || "yes",
                    "select": view.select || "multi"
                };
                if (!options) options = {};
                var width = options.width || "700";
                var height = options.height || "400";

                if (layout.mobile){
                    var size = document.body.getSize();
                    width = size.x;
                    height = size.y;
                    options.style = "viewmobile";
                }
                width = width.toInt();
                height = height.toInt();

                var size = _form.app.content.getSize();
                var x = (size.x-width)/2;
                var y = (size.y-height)/2;
                if (x<0) x = 0;
                if (y<0) y = 0;
                if (layout.mobile){
                    x = 20;
                    y = 0;
                }

                var _self = this;
                MWF.require("MWF.xDesktop.Dialog", function(){
                    var dlg = new MWF.xDesktop.Dialog({
                        "title": options.title || "select View",
                        "style": options.style || "view",
                        "top": y,
                        "left": x-20,
                        "fromTop":y,
                        "fromLeft": x-20,
                        "width": width,
                        "height": height,
                        "html": "<div></div>",
                        "maskNode": _form.app.content,
                        "container": _form.app.content,
                        "buttonList": [
                            {
                                "text": MWF.LP.process.button.ok,
                                "action": function(){
                                    //if (callback) callback(_self.view.selectedItems);
                                    if (callback) callback(_self.view.getData());
                                    this.close();
                                }
                            },
                            {
                                "text": MWF.LP.process.button.cancel,
                                "action": function(){this.close();}
                            }
                        ]
                    });
                    dlg.show();
                    if (layout.mobile){
                        var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                        var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                        if (backAction) backAction.addEvent("click", function(e){
                            dlg.close();
                        }.bind(this));
                        if (okAction) okAction.addEvent("click", function(e){
                            //if (callback) callback(this.view.selectedItems);
                            if (callback) callback(this.view.getData());
                            dlg.close();
                        }.bind(this));
                    }

                    MWF.xDesktop.requireApp("cms.Xform", "widget.View", function(){
                        this.view = new MWF.xApplication.cms.Xform.widget.View(dlg.content.getFirst(), viewJson, {"style": "select"});
                    }.bind(this));
                }.bind(this));
            }
        }
    };

    //include 引用脚本
    var includedScripts = [];
    this.include = function(name, callback){
        if (includedScripts.indexOf(name)==-1){
            if (!this.scriptAction){
                MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
                this.scriptAction = new MWF.xScript.Actions.CMSScriptActions();
            }
            this.scriptAction.getScriptByName(_form.json.application, name, includedScripts, function(json){
                includedScripts = includedScripts.concat(json.data.importedList);
                MWF.Macro.exec(json.data.text, this);
                if (callback) callback.apply(this);
            }.bind(this), null, false);
        }else{
            if (callback) callback.apply(this);
        }
    }.bind(this);

    this.define = function(name, fun, overwrite){
        var over = true;
        if (overwrite===false) over = false;
        var o = {};
        o[name] = {"value": fun, "configurable": over};
        MWF.defineProperties(this, o);
    }.bind(this);

    //仅前台对象-----------------------------------------
    //form
    this.form = {
        "get": function(name){return _form.all[name];},
        "getField": function(name){return _forms[name];},
        "getData": function(){return new MWF.xScript.CMSJSONData(_form.getData());},
        "save": function(callback){
            _form.saveDocument(callback);
        },
        "close": function(){_form.closeDocument();},
        "publish": function(option){
            _form.publishDocument()
        },
        //"archive": function(option){
        //    _form.archiveDocument()
        //},
        //"redraft": function(option){
        //    _form.redraftDocument()
        //},
        "confirm": function(type, e, title, text, width, height, ok, cancel, callback){
            _form.confirm(type, e, title, text, width, height, ok, cancel, callback);
        },
        "notice": function(content, type, target, where){
            _form.notice(content, type, target, where);
        },
        "addEvent": function(e, f){_form.addEvent(e, f);}
    };

    this.target = ev.target;
    this.event = ev.event;
    this.status = ev.status;
    this.session = layout.desktop.session;
};

MWF.xScript.CMSJSONData = function(data, callback, key, parent){
    var getter = function(data, callback, k, _self){
        return function(){return (["array","object"].indexOf(typeOf(data[k]))==-1) ? data[k] : new MWF.xScript.CMSJSONData(data[k], callback, k, _self);};
    }
    var setter = function(data, callback, k, _self){
        return function(v){
            data[k] = v;
            if (callback) callback(data, k, _self);
        }
    }
    var define = function(){
        var o = {};
        for (var k in data) o[k] = {"configurable": true, "get": getter.apply(this, [data, callback, k, this]),"set": setter.apply(this, [data, callback, k, this])};
        o["length"] = {"get": function(){return Object.keys(data).length;}};
        MWF.defineProperties(this, o);
        this.getKey = function(){ return key; };
        this.getParent = function(){ return parent; };
        this.toString = function() { return data.toString();};
        this.add = function(newKey, newValue){
            var type = typeOf(data);
            if (!this.hasOwnProperty(newKey)){
                if (type=="array"){
                    data.push(newValue || {});
                    newKey = data.length-1;
                }else{
                    data[newKey] = newValue || {};
                }
                var o = {};
                o[newKey] = {"configurable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                MWF.defineProperties(this, o);
            }
            this[newKey] = newValue;
            return this;
        };
        this.del = function(delKey){
            if (!this.hasOwnProperty(delKey)) return null;
            delete data[newKey];
            delete this[newKey];
            return this;
        };
    }
    var type = typeOf(data);
    if (type=="object" || type=="array") define.apply(this);
};

MWF.xScript.createCMSDict = function(application){
    return function(name){
        var applicationId = application;
        this.name = name;
        MWF.require("MWF.xScript.Actions.CMSDictActions", null, false);
        var action = new MWF.xScript.Actions.CMSDictActions();

        this.get = function(path, success, failure){
            var p = path.replace(/\./g, "/");
            var value = null;
            action.getDict(applicationId, encodeURIComponent(this.name), p, function(json){
                value = json.data;
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
            return value;
        };

        this.set = function(path, value, success, failure){
            var p = path.replace(/\./g, "/");
            action.setDict(applicationId, encodeURIComponent(this.name), p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            });
        };
        this.add = function(path, value, success, failure){
            var p = path.replace(/\./g, "/");
            action.addDict(applicationId, encodeURIComponent(this.name), p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            });
        };
        this.delete = function(path, success, failure){
            var p = path.replace(/\./g, "/");
            action.deleteDict(applicationId, encodeURIComponent(this.name), p, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            });
        };
    }
};

