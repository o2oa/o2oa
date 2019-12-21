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
            _form.documentAction.saveData(function(json){if (callback) callback();}.bind(this), null, ev.document.id, data);
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
        // if (!orgActions){
        //     MWF.xDesktop.requireApp("Org", "Actions.RestActions", null, false);
        //     orgActions = new MWF.xApplication.Org.Actions.RestActions ();
        // }
        if (!orgActions){
            MWF.require("MWF.xScript.Actions.UnitActions", null, false);
            orgActions = new MWF.xScript.Actions.UnitActions();
        }
    };
    var getNameFlag = function(name){
        var t = typeOf(name);
        if (t==="array"){
            var v = [];
            name.each(function(id){
                v.push((typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
            });
            return v;
        }else{
            return [(t==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
        }
    };
    this.org = {
        //群组***************
        //获取群组--返回群组的对象数组
        getGroup: function(name){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};
            var v = null;
            orgActions.listGroup(data, function(json){v = json.data;}, null, false);
            return (v && v.length===1) ? v[0] : v;
        },
        //查询下级群组--返回群组的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubGroup: function(name, nested){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listSubGroupNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listSubGroupDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },
        //查询上级群组--返回群组的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupGroup:function(name, nested){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listSupGroupNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listSupGroupDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },
        //人员所在群组（嵌套）--返回群组的对象数组
        listGroupWithPerson:function(name){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            orgActions.listGroupWithPerson(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //群组是否拥有角色--返回true, false
        groupHasRole: function(name, role){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"group":nameFlag,"roleList":getNameFlag(role)};
            var v = false;
            orgActions.groupHasRole(data, function(json){v = json.data.value;}, null, false);
            return v;
        },

        //角色***************
        //获取角色--返回角色的对象数组
        getRole: function(name){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};
            var v = null;
            orgActions.listRole(data, function(json){v = json.data;}, null, false);
            return (v && v.length===1) ? v[0] : v;
        },
        //人员所有角色（嵌套）--返回角色的对象数组
        listRoleWithPerson:function(name){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            orgActions.listRoleWithPerson(data, function(json){v = json.data;}, null, false);
            return v;
        },

        //人员***************
        //人员是否拥有角色--返回true, false
        personHasRole: function(name, role){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"person":nameFlag,"roleList":getNameFlag(role)};
            var v = false;
            orgActions.personHasRole(data, function(json){v = json.data.value;}, null, false);
            return v;
        },
        //获取人员--返回人员的对象数组
        getPerson: function(name){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            orgActions.listPerson(data, function(json){v = json.data;}, null, false);
            return (v && v.length===1) ? v[0] : v;;
        },
        //查询下级人员--返回人员的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubPerson: function(name, nested){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listPersonSubNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listPersonSubDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },
        //查询上级人员--返回人员的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupPerson: function(name, nested){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listPersonSupNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listPersonSupDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },
        //获取群组的所有人员--返回人员的对象数组
        listPersonWithGroup: function(name){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};
            var v = null;
            orgActions.listPersonWithGroup(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //获取角色的所有人员--返回人员的对象数组
        listPersonWithRole: function(name){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};
            var v = null;
            orgActions.listPersonWithRole(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //获取身份的所有人员--返回人员的对象数组
        listPersonWithIdentity: function(name){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            orgActions.listPersonWithIdentity(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //获取身份的所有人员--返回人员的对象数组或人员对象
        getPersonWithIdentity: function(name){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            orgActions.listPersonWithIdentity(data, function(json){v = json.data;}, null, false);
            return (v && v.length===1) ? v[0] : v;
        },
        //查询组织成员的人员--返回人员的对象数组
        //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
        listPersonWithUnit: function(name, nested){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listPersonWithUnitNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listPersonWithUnitDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },
        //根据属性查询人员--返回人员的对象数组
        //name  string 属性名
        //value  string 属性值
        listPersonWithAttribute: function(name, value){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            orgActions.listPersonWithAttribute(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //根据属性查询人员--返回人员的全称数组
        //name  string 属性名
        //value  string 属性值
        listPersonNameWithAttribute: function(name, value){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            orgActions.listPersonWithAttributeValue(data, function(json){v = json.data.personList;}, null, false);
            return v;
        },

        //人员属性************
        //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
        appendPersonAttribute: function(person, attr, values, success, failure){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};
            orgActions.appendPersonAttribute(data, function(json){
                if (json.data.value){
                    if (success) success();
                }else{
                    if (failure) failure(null, "", "append values failed");
                }
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
        },
        //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
        setPersonAttribute: function(person, attr, values, success, failure){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};
            orgActions.setPersonAttribute(data, function(json){
                if (json.data.value){
                    if (success) success();
                }else{
                    if (failure) failure(null, "", "append values failed");
                }
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
        },
        //获取人员属性值
        getPersonAttribute: function(person, attr){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"name":attr,"person":personFlag};
            var v = null;
            orgActions.getPersonAttribute(data, function(json){v = json.data.attributeList;}, null, false);
            return v;
        },
        //列出人员所有属性的名称
        listPersonAttributeName: function(name){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            orgActions.listPersonAttributeName(data, function(json){v = json.data.nameList;}, null, false);
            return v;
        },
        //列出人员的所有属性
        listPersonAllAttribute: function(name){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            orgActions.listPersonAllAttribute(data, function(json){v = json.data;}, null, false);
            return v;
        },

        //身份**********
        //获取身份
        getIdentity: function(name){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            orgActions.listIdentity(data, function(json){v = json.data;}, null, false);
            return (v && v.length===1) ? v[0] : v;
        },
        //列出人员的身份
        listIdentityWithPerson: function(name){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            orgActions.listIdentityWithPerson(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //查询组织成员身份--返回身份的对象数组
        //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
        listIdentityWithUnit: function(name, nested){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listIdentityWithUnitNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listIdentityWithUnitDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },

        //组织**********
        //获取组织
        getUnit: function(name){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            orgActions.listUnit(data, function(json){v = json.data;}, null, false);
            return (v && v.length===1) ? v[0] : v;
        },
        //查询组织的下级--返回组织的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubUnit: function(name, nested){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listUnitSubNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listUnitSubDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
        },
        //查询组织的上级--返回组织的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupUnit: function(name, nested){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            if (nested){
                orgActions.listUnitSupNested(data, function(json){v = json.data;}, null, false);
            }else{
                orgActions.listUnitSupDirect(data, function(json){v = json.data;}, null, false);
            }
            return v;
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
                if (typeOf(flag)==="string") getUnitMethod = "type";
                if (typeOf(flag)==="number") getUnitMethod = "level";
            }
            switch (getUnitMethod){
                case "current":
                    var data = {"identityList":getNameFlag(name)};
                    orgActions.listUnitWithIdentity(data, function(json){ v = json.data; v=(v&&v.length===1) ? v[0] : v }, null, false);
                    break;
                case "type":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"type":flag};
                    orgActions.getUnitWithIdentityAndType(data, function(json){ v = json.data; }, null, false);
                    break;
                case "level":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"level":flag};
                    orgActions.getUnitWithIdentityAndLevel(data, function(json){ v = json.data; }, null, false);
                    break;
            }
            return v;
        },
        //列出身份所在组织的所有上级组织
        listAllSupUnitWithIdentity: function(name){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            orgActions.listUnitSupNestedWithIdentity(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //获取人员所在的所有组织
        listUnitWithPerson: function(name){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            orgActions.listUnitWithPerson(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //列出身份所在组织的所有上级组织
        listAllSupUnitWithPerson: function(name){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            orgActions.listUnitSupNestedWithPerson(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //根据组织属性，获取所有符合的组织
        listUnitWithAttribute: function(name, attribute){
            getOrgActions();
            var data = {"name":name,"attribute":attribute};
            var v = null;
            orgActions.listUnitWithAttribute(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //根据组织职务，获取所有符合的组织
        listUnitWithDuty: function(name, id){
            getOrgActions();
            var data = {"name":"","identity":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;
            orgActions.listUnitWithDuty(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //列出顶层组织
        listTopUnit: function(){
            var action = MWF.Actions.get("x_organization_assemble_control");
            var v = null;
            action.listTopUnit(function(json){
                v = json.data;
            }, null, false);
            return v;
        },

        //组织职务***********
        //获取指定的组织职务的身份
        getDuty: function(duty, id){
            getOrgActions();
            var data = {"name":duty,"unit":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;
            orgActions.getDuty(data, function(json){v = json.data;}, null, false);
            return v;
        },
        //获取身份的所有职务名称
        listDutyNameWithIdentity: function(name){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            orgActions.listDutyNameWithIdentity(data, function(json){v = json.data.nameList;}, null, false);
            return v;
        },
        //获取组织的所有职务名称
        listDutyNameWithUnit: function(name){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            orgActions.listDutyNameWithUnit(data, function(json){v = json.data.nameList;}, null, false);
            return v;
        },
        //获取组织的所有职务
        listUnitAllDuty: function(name){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            orgActions.listUnitAllDuty(data, function(json){v = json.data;}, null, false);
            return v;
        },

        //组织属性**************
        //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
        appendUnitAttribute: function(unit, attr, values, success, failure){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};
            orgActions.appendUnitAttribute(data, function(json){
                if (json.data.value){
                    if (success) success();
                }else{
                    if (failure) failure(null, "", "append values failed");
                }
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
        },
        //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
        setUnitAttribute: function(unit, attr, values, success, failure){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};
            orgActions.setUnitAttribute(data, function(json){
                if (json.data.value){
                    if (success) success();
                }else{
                    if (failure) failure(null, "", "append values failed");
                }
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false);
        },
        //获取组织属性值
        getUnitAttribute: function(unit, attr){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"name":attr,"unit":unitFlag};
            var v = null;
            orgActions.getUnitAttribute(data, function(json){v = json.data.attributeList;}, null, false);
            return v;
        },
        //列出组织所有属性的名称
        listUnitAttributeName: function(name){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            orgActions.listUnitAttributeName(data, function(json){v = json.data.nameList;}, null, false);
            return v;
        },
        //列出组织的所有属性
        listUnitAllAttribute: function(name){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            orgActions.listUnitAllAttribute(data, function(json){v = json.data;}, null, false);
            return v;
        }
    };

    this.Action = (function(){
        var actions = [];
        return function(root, json){
            var action = actions[root] || (actions[root] = new MWF.xDesktop.Actions.RestActions("", root, ""));
            action.getActions = function(callback){
                if (!this.actions) this.actions = {};
                Object.merge(this.actions, json);
                if (callback) callback();
            };
            this.invoke = function(option){
                action.invoke(option)
            }
        }
    })();

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
        "lookup": function(view, callback, async){
            var filterList = {"filterList": (view.filter || null)};
            MWF.Actions.get("x_query_assemble_surface").loadView(view.view, view.application, filterList, function(json){
                var data = {
                    "grid": json.data.grid || json.data.groupGrid,
                    "groupGrid": json.data.groupGrid
                };
                if (callback) callback(data);
            }, null, async);
        },

        "lookupV1": function(view, callback){
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
                    "application": view.application || _form.json.application,
                    "viewName": view.view || "",
                    "isTitle": (view.isTitle===false) ? "no" : "yes",
                    "select": (view.isMulti===false) ? "single" : "multi",
                    "filter": view.filter
                };
                if (!options) options = {};
                options.width = view.width;
                options.height = view.height;
                options.title = view.caption;
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
                        "title": options.title || "select view",
                        "style": options.style || "view",
                        "top": y,
                        "left": x-20,
                        "fromTop":y,
                        "fromLeft": x-20,
                        "width": width,
                        "height": height,
                        "html": "<div style='height: 100%;'></div>",
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

                    MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                        this.view = new MWF.xApplication.query.Query.Viewer(dlg.content.getFirst(), viewJson, {"style": "select"});
                    }.bind(this));
                }.bind(this));
            }
        }
    };


    //var lookupAction = null;
    //var getLookupAction = function(callback){
    //    if (!lookupAction){
    //        MWF.require("MWF.xDesktop.Actions.RestActions", function(){
    //            lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_cms_assemble_control", "");
    //            lookupAction.getActions = function(actionCallback){
    //                this.actions = {
    //                    "lookup": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute", "method":"PUT"},
    //                    "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
    //                };
    //                if (actionCallback) actionCallback();
    //            }
    //            if (callback) callback();
    //        }.bind(this));
    //    }else{
    //        if (callback) callback();
    //    }
    //};
    //
    //this.view = {
    //    "lookup": function(view, callback){
    //        getLookupAction(function(){
    //            lookupAction.invoke({"name": "lookup","async": true, "parameter": {"view": view.view, "application": view.application},"success": function(json){
    //                var data = {
    //                    "grid": json.data.grid,
    //                    "groupGrid": json.data.groupGrid
    //                };
    //                if (callback) callback(data);
    //            }.bind(this)});
    //        }.bind(this));
    //    },
    //    "select": function(view, callback, options){
    //        if (view.view){
    //            var viewJson = {
    //                "application": view.application || "",
    //                "viewName": view.view || "",
    //                "isTitle": view.isTitle || "yes",
    //                "select": view.select || "multi",
    //                "title": view.title || "Select View"
    //            };
    //            if (!options) options = {};
    //            var width = options.width || "700";
    //            var height = options.height || "400";
    //
    //            if (layout.mobile){
    //                var size = document.body.getSize();
    //                width = size.x;
    //                height = size.y;
    //                options.style = "viewmobile";
    //            }
    //            width = width.toInt();
    //            height = height.toInt();
    //
    //            var size = _form.app.content.getSize();
    //            var x = (size.x-width)/2;
    //            var y = (size.y-height)/2;
    //            if (x<0) x = 0;
    //            if (y<0) y = 0;
    //            if (layout.mobile){
    //                x = 20;
    //                y = 0;
    //            }
    //
    //            var _self = this;
    //            MWF.require("MWF.xDesktop.Dialog", function(){
    //                var dlg = new MWF.xDesktop.Dialog({
    //                    "title": options.title || "select View",
    //                    "style": options.style || "view",
    //                    "top": y,
    //                    "left": x-20,
    //                    "fromTop":y,
    //                    "fromLeft": x-20,
    //                    "width": width,
    //                    "height": height,
    //                    "html": "<div style='height: 100%;'></div>",
    //                    "maskNode": _form.app.content,
    //                    "container": _form.app.content,
    //                    "buttonList": [
    //                        {
    //                            "text": MWF.LP.process.button.ok,
    //                            "action": function(){
    //                                //if (callback) callback(_self.view.selectedItems);
    //                                if (callback) callback(_self.view.getData());
    //                                this.close();
    //                            }
    //                        },
    //                        {
    //                            "text": MWF.LP.process.button.cancel,
    //                            "action": function(){this.close();}
    //                        }
    //                    ]
    //                });
    //                dlg.show();
    //                if (layout.mobile){
    //                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
    //                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
    //                    if (backAction) backAction.addEvent("click", function(e){
    //                        dlg.close();
    //                    }.bind(this));
    //                    if (okAction) okAction.addEvent("click", function(e){
    //                        //if (callback) callback(this.view.selectedItems);
    //                        if (callback) callback(this.view.getData());
    //                        dlg.close();
    //                    }.bind(this));
    //                }
    //
    //                // MWF.xDesktop.requireApp("cms.Xform", "widget.View", function(){
    //                //     this.view = new MWF.xApplication.cms.Xform.widget.View(dlg.content.getFirst(), viewJson, {"style": "select"});
    //                // }.bind(this));
    //                MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
    //                    this.view = new MWF.xApplication.query.Query.Viewer(dlg.content.getFirst(), viewJson, {"style": "select"});
    //                }.bind(this));
    //            }.bind(this));
    //        }
    //    }
    //};


    //include 引用脚本
    //optionsOrName : {
    //  type : "", 默认为cms, 可以为 portal  process  cms
    //  application : "", 门户/流程/CMS的名称/别名/id, 默认为当前应用
    //  name : "" // 脚本名称/别名/id
    //}
    //或者name: "" // 脚本名称/别名/id
    // if( !window.includedScripts ){
    //     var includedScripts = window.includedScripts = [];
    // }else{
    //     var includedScripts = window.includedScripts;
    // }
    var includedScripts = [];
    this.include = function( optionsOrName , callback ){
        var options = optionsOrName;
        if( typeOf( options ) == "string" ){
            options = { name : options };
        }
        var name = options.name;
        var type = ( options.type && options.application ) ?  options.type : "cms";
        var application = options.application || _form.json.application;
        var key = type +"-" + application + "-"  + name;
        if (includedScripts.indexOf( key )> -1){
            if (callback) callback.apply(this);
            return;
        }
        //if (includedScripts.indexOf( name )> -1){
        //    if (callback) callback.apply(this);
        //    return;
        //}
        var scriptAction;
        switch ( type ){
            case "portal" :
                if( this.scriptActionPortal ){
                    scriptAction = this.scriptActionPortal;
                }else{
                    MWF.require("MWF.xScript.Actions.PortalScriptActions", null, false);
                    scriptAction = this.scriptActionPortal = new MWF.xScript.Actions.PortalScriptActions();
                }
                break;
            case "process" :
                if( this.scriptActionProcess ){
                    scriptAction = this.scriptActionProcess;
                }else{
                    MWF.require("MWF.xScript.Actions.ScriptActions", null, false);
                    scriptAction = this.scriptActionProcess = new MWF.xScript.Actions.ScriptActions();
                }
                break;
            case "cms" :
                if( this.scriptActionCMS ){
                    scriptAction = this.scriptActionCMS;
                }else{
                    MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
                    scriptAction = this.scriptActionCMS = new MWF.xScript.Actions.CMSScriptActions();
                }
                break;
        }
        scriptAction.getScriptByName( application, name, includedScripts, function(json){
            if (json.data){
                includedScripts.push( key );
                includedScripts = includedScripts.concat(json.data.importedList);
                MWF.Macro.exec(json.data.text, this);
                if (callback) callback.apply(this);
            }else{
                if (callback) callback.apply(this);
            }
        }.bind(this), null, false);
    };
    //var includedScripts = [];
    //this.include = function(name, callback){
    //    if (includedScripts.indexOf(name)==-1){
    //        if (!this.scriptAction){
    //            MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
    //            this.scriptAction = new MWF.xScript.Actions.CMSScriptActions();
    //        }
    //        this.scriptAction.getScriptByName(_form.json.application, name, includedScripts, function(json){
    //            includedScripts = includedScripts.concat(json.data.importedList);
    //            MWF.Macro.exec(json.data.text, this);
    //            if (callback) callback.apply(this);
    //        }.bind(this), null, false);
    //    }else{
    //        if (callback) callback.apply(this);
    //    }
    //}.bind(this);

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
        "getInfor": function(){return ev.formInfor;},
        "infor": ev.formInfor,
        "getApp": function(){return _form.app;},
        "app": _form.app,
        "node": function(){return _form.node;},
        "readonly": _form.options.readonly,
        "get": function(name){return (_form.all) ? _form.all[name] : null;},
        "getField": function(name){return _forms[name];},
        "getAction": function(){return _form.documentAction},
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
        "confirm": function(type, title, text, width, height, ok, cancel, callback, mask, style){
            // var p = MWF.getCenter({"x": width, "y": height});
            // e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
            // _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            if ((arguments.length<=1) || o2.typeOf(arguments[1])==="string"){
                var p = MWF.getCenter({"x": width, "y": height});
                e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }else{
                e = (arguments.length>1) ? arguments[1] : null;
                title = (arguments.length>2) ? arguments[2] : null;
                text = (arguments.length>3) ? arguments[3] : null;
                width = (arguments.length>4) ? arguments[4] : null;
                height = (arguments.length>5) ? arguments[5] : null;
                ok = (arguments.length>6) ? arguments[6] : null;
                cancel = (arguments.length>7) ? arguments[7] : null;
                callback = (arguments.length>8) ? arguments[8] : null;
                mask = (arguments.length>9) ? arguments[9] : null;
                style = (arguments.length>10) ? arguments[10] : null;
                _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
            }
        },
        //"confirm": function(type, e, title, text, width, height, ok, cancel, callback){
        //    _form.confirm(type, e, title, text, width, height, ok, cancel, callback);
        //},
        "notice": function(content, type, target, where, offset, option){
            _form.notice(content, type, target, where, offset, option);
        },
        "addEvent": function(e, f){_form.addEvent(e, f);},
        "openWork": function(id, completedId, title, options){
            var op = options || {};
            op.workId = id;
            op.workCompletedId = completedId;
            op.docTitle = title;
            op.appId = "process.Work"+(op.workId || op.workCompletedId);
            return layout.desktop.openApplication(this.event, "process.Work", op);
        },
        "openJob": function(id, choice, options){
            var workData = null;
            o2.Actions.get("x_processplatform_assemble_surface").listWorkByJob(id, function(json){
                if (json.data) workData = json.data;
            }.bind(this), null, false);

            if (workData){
                var len = workData.workList.length + workData.workCompletedList.length;
                if (len){
                    if (len>1 && choice){
                        var node = new Element("div", {"styles": {"padding": "20px", "width": "500px"}}).inject(_form.node);
                        workData.workList.each(function(work){
                            var workNode = new Element("div", {
                                "styles": {
                                    "background": "#ffffff",
                                    "border-radius": "10px",
                                    "clear": "both",
                                    "margin-bottom": "10px",
                                    "height": "40px",
                                    "padding": "10px 10px"
                                }
                            }).inject(node);
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(/x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+work.activityName+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+work.activityArrivedTime+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+(work.manualTaskIdentityText || "")+"</div></div>";
                            workNode.set("html", html);
                            var action = workNode.getElement(".MWFAction");
                            action.store("work", work);
                            action.addEvent("click", function(e){
                                var work = e.target.retrieve("work");
                                if (work) this.openWork(work.id, null, work.title, options);
                                dlg.close();
                            }.bind(this));

                        }.bind(this));
                        workData.workCompletedList.each(function(work){
                            var workNode = new Element("div", {
                                "styles": {
                                    "background": "#ffffff",
                                    "border-radius": "10px",
                                    "clear": "both",
                                    "margin-bottom": "10px",
                                    "height": "40px",
                                    "padding": "10px 10px"
                                }
                            }).inject(node);
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(/x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+o2.LP.widget.workcompleted+"</div>" +
                                "<div style='color:#999999; float: left; margin-right: 10px'>"+work.completedTime+"</div>";
                            workNode.set("html", html);
                            var action = workNode.getElement(".MWFAction");
                            action.store("work", work);
                            action.addEvent("click", function(e){
                                var work = e.target.retrieve("work");
                                if (work) this.openWork(null, work.id, work.title, options);
                                dlg.close();
                            }.bind(this));

                        }.bind(this));
                        var height = node.getSize().y+20;
                        if (height>600) height = 600;

                        var dlg = o2.DL.open({
                            "title": o2.LP.widget.choiceWork,
                            "style" : "user",
                            "isResize": false,
                            "content": node,
                            "buttonList": [
                                {
                                    "type" : "cancel",
                                    "text": o2.LP.widget.close,
                                    "action": function(){dlg.close();}
                                }
                            ]
                        });
                    }else{
                        if (workData.workList.length){
                            var work =  workData.workList[0];
                            return this.openWork(work.id, null, work.title, options);
                        }else{
                            var work =  workData.workCompletedList[0];
                            return this.openWork(null, work.id, work.title, options);
                        }
                    }
                }
            }
        },
        "openDocument": function(id, title, options){
            var op = options || {};
            op.documentId = id;
            op.docTitle = title;
            layout.desktop.openApplication(this.event, "cms.Document", op);
        },
        "openPortal": function(name, page, par){
            var action = MWF.Actions.get("x_portal_assemble_surface");
            action.getApplication(name, function(json){
                if (json.data){
                    if (page){
                        action.getPageByName(page, json.data.id, function(pageJson){
                            layout.desktop.openApplication(null, "portal.Portal", {
                                "portalId": json.data.id,
                                "pageId": (pageJson.data) ? pageJson.data.id : "",
                                "parameters": par,
                                "appId": "portal.Portal"+json.data.id
                            })
                        });
                    }else{
                        layout.desktop.openApplication(null, "portal.Portal", {
                            "portalId": json.data.id,
                            "parameters": par,
                            "appId": "portal.Portal"+json.data.id
                        })
                    }
                }

            });
        },
        "openCMS": function(name){
            var action = MWF.Actions.get("x_cms_assemble_control");
            action.getColumn(name, function(json){
                if (json.data){
                    layout.desktop.openApplication(null, "cms.Module", {
                        "columnId": json.data.id,
                        "appId": "cms.Module"+json.data.id
                    });
                }
            });
        },
        "openProcess": function(name){
            var action = MWF.Actions.get("x_processplatform_assemble_surface");
            action.getApplication(name, function(json){
                if (json.data){
                    layout.desktop.openApplication(null, "process.Application", {
                        "id": json.data.id,
                        "appId": "process.Application"+json.data.id
                    });
                }
            });
        },
        "openApplication":function(name, options){
            layout.desktop.openApplication(null, name, options);
        },
        "createDocument" : function(columnOrOptions, category, data, identity, callback, target, latest, selectColumnEnable, ignoreTitle){
            var column = columnOrOptions;
            if( typeOf( columnOrOptions ) == "object" ){
                column = columnOrOptions.column;
                category = columnOrOptions.category;
                data = columnOrOptions.data;
                identity = columnOrOptions.identity;
                callback = columnOrOptions.callback;
                target = columnOrOptions.target;
                latest = columnOrOptions.latest;
                selectColumnEnable = columnOrOptions.selectColumnEnable;
                ignoreTitle = columnOrOptions.ignoreTitle;
            }
            if (target){
                if (layout.app && layout.app.inBrowser){
                    layout.app.content.empty();
                    layout.app = null;
                }
            }

            MWF.xDesktop.requireApp("cms.Index", "Newer", function(){
                var starter = new MWF.xApplication.cms.Index.Newer(null, null, _form.app, null, {
                    "documentData": data,
                    "identity": identity,

                    "ignoreTitle" : ignoreTitle === true,
                    "ignoreDrafted" : latest === false,
                    "selectColumnEnable" : !category || selectColumnEnable === true,
                    "restrictToColumn" : !!category && selectColumnEnable !== true,

                    "categoryFlag" : category, //category id or name
                    "columnFlag" : column, //column id or name,
                    "onStarted" : function( documentId, data ){
                        if(callback)callback();
                    },
                    "onPostLoad" : function(){
                    },
                    "onPostPublish" : function(){
                    }
                });
                starter.load();
            })
        },
        "startProcess": function(app, process, data, identity, callback, target, latest){
            if (arguments.length>2){
                for (var i=2; i<arguments.length; i++){
                    if (typeOf(arguments[i])=="boolean"){
                        target = arguments[i];
                        break;
                    }
                }
            }
            if (target){
                if (layout.app && layout.app.inBrowser){
                    //layout.app.content.empty();
                    layout.app.$openWithSelf = true;
                }
            }
            var action = MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(process, app, function(json){
                if (json.data){
                    MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", function(){
                        var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, _form.app, {
                            "workData": data,
                            "identity": identity,
                            "latest": latest,
                            "onStarted": function(data, title, processName){
                                var currentTask = [];
                                data.each(function(work){
                                    if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                }.bind(this));

                                if (currentTask.length==1){
                                    var options = {"workId": currentTask[0], "appId": currentTask[0]};
                                    layout.desktop.openApplication(null, "process.Work", options);
                                }else{}

                                if (callback) callback(data);
                            }.bind(this)
                        });
                        starter.load();
                    }.bind(this));
                }
            });
        }
    };

    this.target = ev.target;
    this.event = ev.event;
    this.status = ev.status;
    this.session = layout.desktop.session;
    this.Actions = o2.Actions;

    this.query = function(option){
        // options = {
        //      "name": "statementName",
        //      "data": "json data",
        //      "firstResult": 1,
        //      "maxResults": 100,
        //      "success": function(){},
        //      "error": function(){},
        //      "async": true or false, default is true
        // }
        if (option){
            var json = (option.data) || {};
            if (option.firstResult) json.firstResult = option.firstResult.toInt();
            if (option.maxResults) json.maxResults = option.maxResults.toInt();
            o2.Actions.get("x_query_assemble_surface").executeStatement(option.name, json, success, error, options.async);
        }
    };
    this.Table = MWF.xScript.createTable();
};
MWF.xScript.createTable = function(){
    return function(name){
        this.name = name;
        this.action = o2.Actions.get("x_query_assemble_surface");

        this.listRowNext = function(id, count, success, error, async){
            this.action.listRowNext(this.name, id, count, success, error, async);
        };
        this.listRowPrev = function(id, count, success, error, async){
            this.action.listRowPrev(this.name, id, count, success, error, async);
        };
        this.listRowPrev = function(id, count, success, error, async){
            this.action.listRowPrev(this.name, id, count, success, error, async);
        };
        this.listRowSelectWhere = function(where, success, error, async){
            this.action.listRowSelectWhere(this.name, where, success, error, async);
        };
        this.listRowCountWhere = function(where, success, error, async){
            this.action.listRowCountWhere(this.name, where, success, error, async);
        };
        this.deleteRow = function(id, success, error, async){
            this.action.deleteRow(this.name, id, success, error, async);
        };
        this.deleteAllRow = function(success, error, async){
            this.action.deleteAllRow(this.name, success, error, async);
        };
        this.getRow = function(id, success, error, async){
            this.action.getRow(this.name, id, success, error, async);
        };
        this.insertRow = function(data, success, error, async){
            this.action.insertRow(this.name, data, success, error, async);
        };
        this.updateRow = function(id, data, success, error, async){
            this.action.updateRow(this.name, id, data, success, error, async);
        };
    }
};
MWF.xScript.CMSJSONData = function(data, callback, key, parent){
    var getter = function(data, callback, k, _self){
        return function(){return (["array","object"].indexOf(typeOf(data[k]))===-1) ? data[k] : new MWF.xScript.CMSJSONData(data[k], callback, k, _self);};
    };
    var setter = function(data, callback, k, _self){
        return function(v){
            data[k] = v;
            //debugger;
            //this.add(k, v, true);
            if (callback) callback(data, k, _self);
        }
    };
    var define = function(){
        var o = {};
        for (var k in data) o[k] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, k, this]),"set": setter.apply(this, [data, callback, k, this])};
        o["length"] = {"get": function(){return Object.keys(data).length;}};
        MWF.defineProperties(this, o);

        var methods = {
            "getKey": {"value": function(){ return key; }},
            "getParent": {"value": function(){ return parent; }},
            "toString": {"value": function() { return data.toString();}},
            "add": {"value": function(newKey, newValue, overwrite){
                var flag = true;
                var type = typeOf(data);
                if (type==="array"){
                    if (arguments.length<2){
                        data.push(newKey);
                        newValue = newKey;
                        newKey = data.length-1;
                    }else{
                        if (!newKey && newKey!==0){
                            data.push(newValue);
                            newKey = data.length-1;
                        }else{
                            flag = false;
                        }
                    }
                    if (flag){
                        var o = {};
                        o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                        MWF.defineProperties(this, o);
                    }
                    this[newKey] = newValue;
                }else if (type==="object"){
                    if (!this.hasOwnProperty(newKey)){
                        data[newKey] = newValue;

                        if (flag){
                            var o = {};
                            o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                            MWF.defineProperties(this, o);
                        }
                        this[newKey] = newValue;
                    }else{
                        if (overwrite) this[newKey] = newValue;
                    }
                }

                return this[newKey];
            }},
            "del": {"value": function(delKey){
                if (!this.hasOwnProperty(delKey)) return null;
                delete data[delKey];
                delete this[delKey];
                return this;
            }}
        };
        MWF.defineProperties(this, methods);




        //this.getKey = function(){ return key; };
        //this.getParent = function(){ return parent; };
        //this.toString = function() { return data.toString();};
        //this.add = function(newKey, newValue, overwrite){
        //    var flag = true;
        //    var type = typeOf(data);
        //    if (!this.hasOwnProperty(newKey)){
        //        if (type=="array"){
        //            if (arguments.length<2){
        //                data.push(newKey);
        //                newValue = newKey;
        //                newKey = data.length-1;
        //            }else{
        //                debugger;
        //                if (!newKey && newKey!=0){
        //                    data.push(newValue);
        //                    newKey = data.length-1;
        //                }else{
        //                    flag == false;
        //                }
        //            }
        //        }else{
        //            data[newKey] = newValue;
        //        }
        //        //var valueType = typeOf(newValue);
        //        //var newValueData = newValue;
        //        //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
        //        //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
        //        if (flag){
        //            var o = {};
        //            o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
        //            MWF.defineProperties(this, o);
        //        }
        //        this[newKey] = newValue;
        //    }else{
        //        if (overwrite) this[newKey] = newValue;
        //    }
        //
        //    //var valueType = typeOf(newValue);
        //    //var newValueData = newValue;
        //    //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
        //    //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
        //    //
        //    //this[newKey] = newValueData;
        //
        //    return this[newKey];
        //};
        //this.del = function(delKey){
        //    if (!this.hasOwnProperty(delKey)) return null;
        //    delete data[newKey];
        //    delete this[newKey];
        //    return this;
        //};
    };
    var type = typeOf(data);
    if (type==="object" || type==="array") define.apply(this);
};

//MWF.xScript.CMSJSONData = function(data, callback, key, parent){
//    var getter = function(data, callback, k, _self){
//        return function(){return (["array","object"].indexOf(typeOf(data[k]))==-1) ? data[k] : new MWF.xScript.CMSJSONData(data[k], callback, k, _self);};
//    }
//    var setter = function(data, callback, k, _self){
//        return function(v){
//            data[k] = v;
//            if (callback) callback(data, k, _self);
//        }
//    }
//    var define = function(){
//        var o = {};
//        for (var k in data) o[k] = {
//            "configurable": true,
//            "get": getter.apply(this, [data, callback, k, this]),
//            "set": setter.apply(this, [data, callback, k, this])
//        };
//        o["length"] = {"get": function(){return Object.keys(data).length;}};
//        MWF.defineProperties(this, o);
//        this.getKey = function(){ return key; };
//        this.getParent = function(){ return parent; };
//        this.toString = function() { return data.toString();};
//        this.add = function(newKey, newValue){
//            var type = typeOf(data);
//            if (!this.hasOwnProperty(newKey)){
//                if (type=="array"){
//                    data.push(newValue || {});
//                    newKey = data.length-1;
//                }else{
//                    data[newKey] = newValue || {};
//                }
//                var o = {};
//                o[newKey] = {"configurable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
//                MWF.defineProperties(this, o);
//            }
//            this[newKey] = newValue;
//            return this;
//        };
//        this.del = function(delKey){
//            if (!this.hasOwnProperty(delKey)) return null;
//            delete data[newKey];
//            delete this[newKey];
//            return this;
//        };
//    }
//    var type = typeOf(data);
//    if (type=="object" || type=="array") define.apply(this);
//};

//MWF.xScript.createCMSDict = function(application){
//    return function(name){
//        var applicationId = application;
//        this.name = name;
//        MWF.require("MWF.xScript.Actions.CMSDictActions", null, false);
//        var action = new MWF.xScript.Actions.CMSDictActions();
//
//        this.get = function(path, success, failure){
//            var value = null;
//            if( path ){
//                var arr = path.split(/\./g);
//                var ar = arr.map(function(v){
//                    return encodeURIComponent(v);
//                });
//                //var p = path.replace(/\./g, "/");
//                var p = ar.join("/");
//                action.getDict(applicationId, encodeURIComponent(this.name), p, function(json){
//                    value = json.data;
//                    if (success) success(json.data);
//                }, function(xhr, text, error){
//                    if (failure) failure(xhr, text, error);
//                }, false);
//            }else{
//                action.getDictWhole(applicationId, encodeURIComponent(this.name), function(json){
//                    value = json.data;
//                    if (success) success(json.data);
//                }, function(xhr, text, error){
//                    if (failure) failure(xhr, text, error);
//                }, false);
//            }
//            return value;
//        };
//
//        this.set = function(path, value, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.setDict(applicationId, encodeURIComponent(this.name), p, value, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this.add = function(path, value, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.addDict(applicationId, encodeURIComponent(this.name), p, value, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this["delete"] = function(path, success, failure){
//            var p = path.replace(/\./g, "/");
//            action.deleteDict(applicationId, encodeURIComponent(this.name), p, function(json){
//                if (success) success(json.data);
//            }, function(xhr, text, error){
//                if (failure) failure(xhr, text, error);
//            });
//        };
//        this.destory = this["delete"];
//    }
//};

MWF.xScript.createCMSDict = function(application){
    //optionsOrName : {
    //  type : "", //默认为cms, 可以为  process  cms
    //  application : "", //流程/CMS的名称/别名/id, 默认为当前应用
    //  name : "", // 数据字典名称/别名/id
    //  enableAnonymous : false //允许在未登录的情况下读取CMS的数据字典
    //}
    //或者name: "" // 数据字典名称/别名/id
    return function(optionsOrName){
        var options = optionsOrName;
        if( typeOf( options ) == "string" ){
            options = { name : options };
        }
        var name = this.name = options.name;
        var type = ( options.type && options.application ) ?  options.type : "cms";
        var applicationId = options.application || application;
        var enableAnonymous = options.enableAnonymous || false;

        //MWF.require("MWF.xScript.Actions.DictActions", null, false);
        if( type == "cms" ){
            var action = MWF.Actions.get("x_cms_assemble_control");
        }else{
            var action = MWF.Actions.get("x_processplatform_assemble_surface");
        }

        var encodePath = function( path ){
            var arr = path.split(/\./g);
            var ar = arr.map(function(v){
                return encodeURIComponent(v);
            });
            return ar.join("/");
        };

        this.get = function(path, success, failure){
            var value = null;
            if (path){
                var p = encodePath( path );
                //var p = path.replace(/\./g, "/");
                action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                }, false, false);
            }else{
                action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](encodeURIComponent(this.name), applicationId, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                }, false);
            }

            return value;
        };

        this.set = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        };
        this.add = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        };
        this["delete"] = function(path, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        };
        this.destory = this["delete"];
    }
};

