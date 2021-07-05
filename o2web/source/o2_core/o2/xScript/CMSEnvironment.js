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
    /**
     * 您可以通过documentContext获取内容管理实例相关的对象数据。
     * @module documentContext
     * @o2range {CMS}
     * @o2ordernumber 30
     * @example
     * //您可以在内容管理表单中，通过this来获取当前实例的documentContext对象，如下：
     * var context = this.documentContext;
     */
    this.documentContext = {
        /**
         * 获取当前内容管理实例的文档对象：document对象。
         * @method getDocument
         * @static
         * @return {Document} 文档对象.
         * @o2ActionOut x_cms_assemble_control.WoDocument|ignoreNoDescr=true|example=Document
         * @o2syntax
         * var doc = this.documentContext.getDocument();
         */
        "getDocument": function(){return ev.document },
        /**
         * 获取当前人对文档的权限。
         * @method getControl
         * @static
         * @return {DocumentControl} 当前人对文档所拥有的权限.
         * <pre><code class='language-js'>{
         *    "allowRead": true,              //是否允许阅读文档
         *    "allowPublishDocument": true,   //是否允许发布文档
         *    "allowSave": true,              //是否允许保存文档
         *    "allowPopularDocument": true,   //是否允许设置为热点
         *    "allowEditDocument": true,      //是否允许编辑文档
         *    "allowDeleteDocument": true     //是否允许删除文档
         * }</code></pre>
         * @o2syntax
         * var control = this.documentContext.getControl();
         */
        "getControl": function(){return ev.control;},
        /**
         * 获取当前文档的附件对象列表。
         * @method getAttachmentList
         * @static
         * @return {DocumentFileInfo[]} 当前文档的附件对象列表.
         * @o2ActionOut x_cms_assemble_control.FileInfoAction.get|example=Attachment
         * @o2syntax
         * var attachmentList = this.documentContext.getAttachmentList();
         */
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
        getGroup: function(name, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;

            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroup(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = null;
            // orgActions.listGroup(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //查询下级群组--返回群组的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubGroup: function(name, nested, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
            //     v = json.data;
            //     return v;
            // }.ag().catch(function(json){ return json; });
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listSubGroupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listSubGroupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;

            // var v = null;
            // if (nested){
            //     orgActions.listSubGroupNested(data, function(json){v = json.data;}, null, false);
            // }else{
            //     orgActions.listSubGroupDirect(data, function(json){v = json.data;}, null, false);
            // }
            // return v;
        },
        //查询上级群组--返回群组的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupGroup:function(name, nested, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise
            if (nested){
                var promise = orgActions.listSupGroupNested(data, cb, null, !!async);
            }else{
                var promise = orgActions.listSupGroupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
            // var v = null;
            // if (nested){
            //     orgActions.listSupGroupNested(data, function(json){v = json.data;}, null, false);
            // }else{
            //     orgActions.listSupGroupDirect(data, function(json){v = json.data;}, null, false);
            // }
            // return v;
        },
        //人员所在群组（嵌套）--返回群组的对象数组
        listGroupWithPerson:function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listGroupWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listGroupWithPerson(data, function(json){v = json.data;}, null, false);
            // return v;
        },
        //群组是否拥有角色--返回true, false
        groupHasRole: function(name, role, async){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"group":nameFlag,"roleList":getNameFlag(role)};

            var v = false;
            var cb = function(json){
                v = json.data.value;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.groupHasRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = false;
            // orgActions.groupHasRole(data, function(json){v = json.data.value;}, null, false);
            // return v;
        },

        //角色***************
        //获取角色--返回角色的对象数组
        getRole: function(name, async){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRole(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = null;
            // orgActions.listRole(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //人员所有角色（嵌套）--返回角色的对象数组
        listRoleWithPerson:function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRoleWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listRoleWithPerson(data, function(json){v = json.data;}, null, false);
            // return v;
        },

        //人员***************
        //人员是否拥有角色--返回true, false
        personHasRole: function(name, role, async){
            getOrgActions();
            nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
            var data = {"person":nameFlag,"roleList":getNameFlag(role)};

            var v = false;
            var cb = function(json){
                v = json.data.value;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listRoleWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var v = false;
            // orgActions.personHasRole(data, function(json){v = json.data.value;}, null, false);
            // return v;
        },
        //获取人员--返回人员的对象数组
        getPerson: function(name, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
            // var v = null;
            // orgActions.listPerson(data, function(json){v = json.data;}, null, false);
            // return (v && v.length===1) ? v[0] : v;
        },
        //查询下级人员--返回人员的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubPerson: function(name, nested, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonSubNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonSubDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //查询上级人员--返回人员的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        listSupPerson: function(name, nested, async){
            getOrgActions();
            var data = {"personList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonSupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonSupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //获取群组的所有人员--返回人员的对象数组
        listPersonWithGroup: function(name, async){
            getOrgActions();
            var data = {"groupList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithGroup(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取角色的所有人员--返回人员的对象数组
        listPersonWithRole: function(name, async){
            getOrgActions();
            var data = {"roleList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise
            promise = orgActions.listPersonWithRole(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取身份的所有人员--返回人员的对象数组
        listPersonWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取身份的所有人员--返回人员的对象数组或人员对象
        getPersonWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v =  (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //查询组织成员的人员--返回人员的对象数组
        //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
        listPersonWithUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listPersonWithUnitNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listPersonWithUnitDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //根据属性查询人员--返回人员的对象数组
        //name  string 属性名
        //value  string 属性值
        listPersonWithAttribute: function(name, value, async){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //根据属性查询人员--返回人员的全称数组
        //name  string 属性名
        //value  string 属性值
        listPersonNameWithAttribute: function(name, value, async){
            getOrgActions();
            var data = {"name": name, "attribute": value};
            var v = null;
            var cb = function(json){
                v = json.data.personList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonWithAttributeValue(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //人员属性************
        //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
        appendPersonAttribute: function(person, attr, values, success, failure, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.appendPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.appendPersonAttribute(data, cb, null, !!async);
        },
        //设置人员属性值(将属性值修改为values，如果没有此属性，则创建一个)
        setPersonAttribute: function(person, attr, values, success, failure, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"attributeList":values,"name":attr,"person":personFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.setPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.setPersonAttribute(data, cb, null, !!async);
        },
        //获取人员属性值
        getPersonAttribute: function(person, attr, async){
            getOrgActions();
            var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
            var data = {"name":attr,"person":personFlag};
            var v = null;
            var cb = function(json){
                v = json.data.attributeList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getPersonAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员所有属性的名称
        listPersonAttributeName: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonAttributeName(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员的所有属性
        listPersonAllAttribute: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listPersonAllAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //身份**********
        //获取身份
        getIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员的身份
        listIdentityWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listIdentityWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //查询组织成员身份--返回身份的对象数组
        //nested  布尔  true嵌套的所有成员；false直接成员；默认false；
        listIdentityWithUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;

            // var cb = function(json){
            //     v = json.data;
            //     if (async && o2.typeOf(async)=="function") return async(v);
            //     return v;
            // }.ag().catch(function(json){ return json; });

            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var method = (nested) ? "listIdentityWithUnitNested" : "listIdentityWithUnitDirect";
            var promise = orgActions[method](data, cb, null, !!async);
            promise.name = "org";

            //
            // if (nested){
            //     orgActions.listIdentityWithUnitNested(data, cb, null, !!async);
            // }else{
            //     orgActions.listIdentityWithUnitDirect(data, cb, null, !!async);
            // }
            return (!!async) ? promise : v;
        },

        //组织**********
        //获取组织
        getUnit: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                v = (v && v.length===1) ? v[0] : v;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnit(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //查询组织的下级--返回组织的对象数组
        //nested  布尔  true嵌套下级；false直接下级；默认false；
        listSubUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listUnitSubNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listUnitSubDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;
        },
        //查询组织的上级--返回组织的对象数组
        //nested  布尔  true嵌套上级；false直接上级；默认false；
        //async 布尔 true异步请求
        listSupUnit: function(name, nested, async){
            getOrgActions();
            var data = {"unitList": getNameFlag(name)};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise;
            if (nested){
                promise = orgActions.listUnitSupNested(data, cb, null, !!async);
            }else{
                promise = orgActions.listUnitSupDirect(data, cb, null, !!async);
            }
            return (!!async) ? promise : v;

            // if (callback){
            //     if (nested){
            //         orgActions.listUnitSupNested(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
            //     }else{
            //         orgActions.listUnitSupDirect(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
            //     }
            // }else{
            //     var v = null;
            //     if (nested){
            //         orgActions.listUnitSupNested(data, function(json){v = json.data;}, null, false);
            //     }else{
            //         orgActions.listUnitSupDirect(data, function(json){v = json.data;}, null, false);
            //     }
            //     return v;
            // }
        },
        //根据个人身份获取组织
        //flag 数字    表示获取第几层的组织
        //     字符串  表示获取指定类型的组织
        //     空     表示获取直接所在的组织
        getUnitByIdentity: function(name, flag, async){
            getOrgActions();
            var getUnitMethod = "current";
            var v;
            if (flag){
                if (typeOf(flag)==="string") getUnitMethod = "type";
                if (typeOf(flag)==="number") getUnitMethod = "level";
            }

            var cb;
            var promise;
            switch (getUnitMethod){
                case "current":
                    var data = {"identityList":getNameFlag(name)};

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  v=(v&&v.length===1) ? v[0] : v; return v;
                    // }.ag().catch(function(json){ return json; });


                    cb = function(json){
                        v = json.data;  v=(v&&v.length===1) ? v[0] : v;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };


                    promise = orgActions.listUnitWithIdentity(data, cb, null, !!async);
                    break;
                case "type":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"type":flag};

                    cb = function(json){
                        v = json.data;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  return v;
                    // }.ag().catch(function(json){ return json; });

                    promise = orgActions.getUnitWithIdentityAndType(data, cb, null, !!async);
                    break;
                case "level":
                    var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"level":flag};

                    cb = function(json){
                        v = json.data;  v=(v&&v.length===1) ? v[0] : v;
                        if (async && o2.typeOf(async)=="function") return async(v);
                        return v;
                    };

                    // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                    //     v = json.data;  return v;
                    // }.ag().catch(function(json){ return json; });

                    promise = orgActions.getUnitWithIdentityAndLevel(data, cb, null, !!async);
                    break;
            }
            return (!!async) ? promise : v;
        },
        //列出身份所在组织的所有上级组织
        listAllSupUnitWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitSupNestedWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取人员所在的所有组织
        listUnitWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出人员所在组织的所有上级组织
        listAllSupUnitWithPerson: function(name, async){
            getOrgActions();
            var data = {"personList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitSupNestedWithPerson(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //根据组织属性，获取所有符合的组织
        listUnitWithAttribute: function(name, attribute, async){
            getOrgActions();
            var data = {"name":name,"attribute":attribute};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            promise = orgActions.listUnitWithAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //根据组织职务，获取所有符合的组织
        listUnitWithDuty: function(name, id, async){
            getOrgActions();
            var data = {"name":name,"identity":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitWithDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //组织职务***********
        //获取指定的组织职务的身份
        getDuty: function(duty, id, async){
            getOrgActions();
            var data = {"name":duty,"unit":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
            var v = null;

            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取身份的所有职务名称
        listDutyNameWithIdentity: function(name, async){
            getOrgActions();
            var data = {"identityList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listDutyNameWithIdentity(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取组织的所有职务名称
        listDutyNameWithUnit: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listDutyNameWithUnit(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //获取组织的所有职务
        listUnitAllDuty: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAllDuty(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出顶层组织
        listTopUnit: function(async){
            var action = MWF.Actions.get("x_organization_assemble_control");
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = action.listTopUnit(cb, null, !!async);
            return (!!async) ? promise : v;
        },

        //组织属性**************
        //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
        appendUnitAttribute: function(unit, attr, values, success, failure, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.appendUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            //
            // orgActions.appendPersonAttribute(data, cb, null, !!async);

            // orgActions.appendUnitAttribute(data, function(json){
            //     if (json.data.value){
            //         if (success) success();
            //     }else{
            //         if (failure) failure(null, "", "append values failed");
            //     }
            // }, function(xhr, text, error){
            //     if (failure) failure(xhr, text, error);
            // }, false);
        },
        //设置组织属性值(将属性值修改为values，如果没有此属性，则创建一个)
        setUnitAttribute: function(unit, attr, values, success, failure, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"attributeList":values,"name":attr,"unit":unitFlag};

            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };
            var promise = orgActions.setUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;

            // var cb = function(json){
            //     if (success) return success(json);
            // }.ag().catch(function(xhr, text, error){
            //     if (failure) return failure(xhr, text, error);
            // });
            // orgActions.setUnitAttribute(data, cb, null, !!async);

            // orgActions.setUnitAttribute(data, function(json){
            //     if (json.data.value){
            //         if (success) success();
            //     }else{
            //         if (failure) failure(null, "", "append values failed");
            //     }
            // }, function(xhr, text, error){
            //     if (failure) failure(xhr, text, error);
            // }, false);
        },
        //获取组织属性值
        getUnitAttribute: function(unit, attr, async){
            getOrgActions();
            var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
            var data = {"name":attr,"unit":unitFlag};
            var v = null;
            var cb = function(json){
                v = json.data.attributeList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.getUnitAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出组织所有属性的名称
        listUnitAttributeName: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data.nameList;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAttributeName(data, cb, null, !!async);
            return (!!async) ? promise : v;
        },
        //列出组织的所有属性
        listUnitAllAttribute: function(name, async){
            getOrgActions();
            var data = {"unitList":getNameFlag(name)};
            var v = null;
            var cb = function(json){
                v = json.data;
                if (async && o2.typeOf(async)=="function") return async(v);
                return v;
            };

            var promise = orgActions.listUnitAllAttribute(data, cb, null, !!async);
            return (!!async) ? promise : v;
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
            MWF.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList, function(json){
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
                        this.view = new MWF.xApplication.query.Query.Viewer(dlg.content.getFirst(), viewJson, {"style": "select"}, _form.app, _form.Macro);
                    }.bind(this));
                }.bind(this));
            }
        }
    };

    this.statement = {
        "execute": function (statement, callback, async) {
            var parameter = this.parseParameter(statement.parameter);
            var filterList = this.parseFilter(statement.filter, parameter);
            var obj = {
                "filterList": filterList,
                "parameter" : parameter
            };
            MWF.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
                statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
                function (json) {
                    if (callback) callback(json);
                }, null, async);
        },
        parseFilter : function( filter, parameter ){
            if( typeOf(filter) !== "array" )return [];
            var filterList = [];
            ( filter || [] ).each( function (d) {
                var parameterName = d.path.replace(/\./g, "_");
                var value = d.value;
                if( d.comparison === "like" || d.comparison === "notLike" ){
                    if( value.substr(0, 1) !== "%" )value = "%"+value;
                    if( value.substr(value.length-1,1) !== "%" )value = value+"%";
                    parameter[ parameterName ] = value; //"%"+value+"%";
                }else{
                    if( d.formatType === "dateTimeValue" || d.formatType === "datetimeValue"){
                        value = "{ts '"+value+"'}"
                    }else if( d.formatType === "dateValue" ){
                        value = "{d '"+value+"'}"
                    }else if( d.formatType === "timeValue" ){
                        value = "{t '"+value+"'}"
                    }
                    parameter[ parameterName ] = value;
                }
                d.value = parameterName;

                filterList.push( d );
            }.bind(this));
            return filterList;
        },
        parseParameter : function( obj ){
            if( typeOf(obj) !== "object" )return {};
            var parameter = {};
            //传入的参数
            for( var p in obj ){
                var value = obj[p];
                if( typeOf( value ) === "date" ){
                    value = "{ts '"+value.format("db")+"'}"
                }
                parameter[ p ] = value;
            }
            return parameter;
        },
        "select": function (statement, callback, options) {
            if (statement.name) {
                // var parameter = this.parseParameter(statement.parameter);
                // var filterList = this.parseFilter(statement.filter, parameter);
                var statementJson = {
                    "statementId": statement.name || "",
                    "isTitle": (statement.isTitle === false) ? "no" : "yes",
                    "select": (statement.isMulti === false) ? "single" : "multi",
                    "filter": statement.filter,
                    "parameter": statement.parameter
                };
                if (!options) options = {};
                options.width = statement.width;
                options.height = statement.height;
                options.title = statement.caption;

                var width = options.width || "700";
                var height = options.height || "400";

                if (layout.mobile) {
                    var size = document.body.getSize();
                    width = size.x;
                    height = size.y;
                    options.style = "viewmobile";
                }
                width = width.toInt();
                height = height.toInt();

                var size = _form.app.content.getSize();
                var x = (size.x - width) / 2;
                var y = (size.y - height) / 2;
                if (x < 0) x = 0;
                if (y < 0) y = 0;
                if (layout.mobile) {
                    x = 20;
                    y = 0;
                }

                var _self = this;
                MWF.require("MWF.xDesktop.Dialog", function () {
                    var dlg = new MWF.xDesktop.Dialog({
                        "title": options.title || "select statement view",
                        "style": options.style || "view",
                        "top": y,
                        "left": x - 20,
                        "fromTop": y,
                        "fromLeft": x - 20,
                        "width": width,
                        "height": height,
                        "html": "<div style='height: 100%;'></div>",
                        "maskNode": _form.app.content,
                        "container": _form.app.content,
                        "buttonList": [
                            {
                                "text": MWF.LP.process.button.ok,
                                "action": function () {
                                    //if (callback) callback(_self.view.selectedItems);
                                    if (callback) callback(_self.statement.getData());
                                    this.close();
                                }
                            },
                            {
                                "text": MWF.LP.process.button.cancel,
                                "action": function () { this.close(); }
                            }
                        ]
                    });
                    dlg.show();

                    if (layout.mobile) {
                        var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                        var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                        if (backAction) backAction.addEvent("click", function (e) {
                            dlg.close();
                        }.bind(this));
                        if (okAction) okAction.addEvent("click", function (e) {
                            //if (callback) callback(this.view.selectedItems);
                            if (callback) callback(this.statement.getData());
                            dlg.close();
                        }.bind(this));
                    }

                    MWF.xDesktop.requireApp("query.Query", "Statement", function () {
                        this.statement = new MWF.xApplication.query.Query.Statement(dlg.content.getFirst(), statementJson, { "style": "select" }, _form.app, _form.Macro);
                    }.bind(this));
                }.bind(this));
            }
        }
    };


    this.importer = {
        "upload": function (options, callback, async) {
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.load();
            }.bind(this));
        },
        "downloadTemplate": function(options, fileName){
            MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                importer.downloadTemplate(fileName);
            }.bind(this));
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

                //名称、别名、id
                json.data.importedList.each( function ( flag ) {
                    if( type === "portal" ){
                        includedScripts.push( type + "-" + json.data.portal + "-" + flag );
                        if( json.data.portalName )includedScripts.push( type + "-" + json.data.portalName + "-" + flag );
                        if( json.data.portalAlias )includedScripts.push( type + "-" + json.data.portalAlias + "-" + flag );
                    }else if( type === "cms" ){
                        includedScripts.push( type + "-" + json.data.appId + "-" + flag );
                        if( json.data.appName )includedScripts.push( type + "-" + json.data.appName + "-" + flag );
                        if( json.data.appAlias )includedScripts.push( type + "-" + json.data.appAlias + "-" + flag );
                    }else if( type === "process" ){
                        includedScripts.push( type + "-" + json.data.application + "-" + flag );
                        if( json.data.appName )includedScripts.push( type + "-" + json.data.appName + "-" + flag );
                        if( json.data.appAlias )includedScripts.push( type + "-" + json.data.appAlias + "-" + flag );
                    }
                });

                includedScripts = includedScripts.concat(json.data.importedList);
                MWF.CMSMacro.exec(json.data.text, this);
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
    //            MWF.CMSMacro.exec(json.data.text, this);
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
    //如果前端事件有异步调用，想要在异步调用结束后继续运行页面加载，
    //可在调用前执行 var resolve = this.wait();
    //在异步调用结束后 执行 resolve.cb()；
    //目前只有表单的queryload事件支持此方法。
    this.wait = function(){
        resolve = {};
        var setResolve = function(callback){
            resolve.cb = callback;
        }.bind(this);
        this.target.event_resolve = setResolve;
        return resolve;
    };
    //和this.wait配合使用，
    //如果没有异步，则resolve.cb方法不存在，
    //所以在回调中中使用this.goon();使表单继续加载
    this.goon = function(){
        this.target.event_resolve = null;
    };

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

        /**发布当前文档。<b>（仅内容管理表单中可用）</b>
         * @method publish
         * @memberOf module:form
         * @param {Function} callback - 发布后的回调方法
         * @o2syntax
         this.form.publish( callback );
         */
        "publish": function(callback){
            _form.publishDocument(callback)
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
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
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
                            var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
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
        "openPortal": function (name, page, par) {
            var action = MWF.Actions.get("x_portal_assemble_surface");
            action.getApplication(name, function (json) {
                if (json.data) {
                    if (page) {
                        action.getPageByName(page, json.data.id, function (pageJson) {
                            var pageId = (pageJson.data) ? pageJson.data.id : "";
                            layout.desktop.openApplication(null, "portal.Portal", {
                                "portalId": json.data.id,
                                "pageId": pageId,
                                "parameters": par,
                                "appId": (par && par.appId) || ("portal.Portal" + json.data.id + pageId)
                            })
                        });
                    } else {
                        layout.desktop.openApplication(null, "portal.Portal", {
                            "portalId": json.data.id,
                            "parameters": par,
                            "appId": (par && par.appId) || ("portal.Portal" + json.data.id)
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
        "createDocument": function (columnOrOptions, category, data, identity, callback, target, latest, selectColumnEnable, ignoreTitle) {
            var column = columnOrOptions;
            var onAfterPublish, onPostPublish;
            if (typeOf(columnOrOptions) == "object") {
                column = columnOrOptions.column;
                category = columnOrOptions.category;
                data = columnOrOptions.data;
                identity = columnOrOptions.identity;
                callback = columnOrOptions.callback;
                target = columnOrOptions.target;
                latest = columnOrOptions.latest;
                selectColumnEnable = columnOrOptions.selectColumnEnable;
                ignoreTitle = columnOrOptions.ignoreTitle;
                onAfterPublish = columnOrOptions.onAfterPublish;
                onPostPublish = columnOrOptions.onPostPublish;
            }
            if (target) {
                if (layout.app && layout.app.inBrowser) {
                    layout.app.content.empty();
                    layout.app = null;
                }
            }

            MWF.xDesktop.requireApp("cms.Index", "Newer", function () {
                var starter = new MWF.xApplication.cms.Index.Newer(null, null, _form.app, null, {
                    "documentData": data,
                    "identity": identity,

                    "ignoreTitle": ignoreTitle === true,
                    "ignoreDrafted": latest === false,
                    "selectColumnEnable": !category || selectColumnEnable === true,
                    "restrictToColumn": !!category && selectColumnEnable !== true,

                    "categoryFlag": category, //category id or name
                    "columnFlag": column, //column id or name,
                    "onStarted": function (documentId, data) {
                        if (callback) callback();
                    },
                    "onPostPublish": function () {
                        if(onPostPublish)onPostPublish();
                    },
                    "onAfterPublish": function () {
                        if(onAfterPublish)onAfterPublish();
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
                                if (data.work){
                                    var work = data.work;
                                    var options = {"draft": work, "appId": "process.Work"+(new o2.widget.UUID).toString(), "desktopReload": false};
                                    layout.desktop.openApplication(null, "process.Work", options);
                                }else{
                                    var currentTask = [];
                                    data.each(function(work){
                                        if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                    }.bind(this));

                                    if (currentTask.length==1){
                                        var options = {"workId": currentTask[0], "appId": currentTask[0]};
                                        layout.desktop.openApplication(null, "process.Work", options);
                                    }else{}
                                }

                                // var currentTask = [];
                                // data.each(function(work){
                                //     if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                // }.bind(this));
                                //
                                // if (currentTask.length==1){
                                //     var options = {"workId": currentTask[0], "appId": currentTask[0]};
                                //     layout.desktop.openApplication(null, "process.Work", options);
                                // }else{}

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
        o["some"] = {"get": function(){return data.some;}};
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

if( !MWF.xScript.dictLoaded )MWF.xScript.dictLoaded = {};

MWF.xScript.addDictToCache = function ( options, path, json ) {
    if( !path )path = "root";
    if( path.indexOf("root") !== 0 )path = "root." + path ;

    var type = options.appType || "process";
    var enableAnonymous = options.enableAnonymous || false;

    var appFlagList = [];
    if( options.application )appFlagList.push( options.application );
    if( options.appId )appFlagList.push( options.appId );
    if( options.appName )appFlagList.push( options.appName );
    if( options.appAlias )appFlagList.push( options.appAlias );

    var dictFlagList = [];
    if( options.id )dictFlagList.push( options.id );
    if( options.name )dictFlagList.push( options.name );
    if( options.alias )dictFlagList.push( options.alias );

    var cache = {};
    cache[path] = json;

    for( var i=0; i<appFlagList.length; i++ ){
        for( var j=0; j<dictFlagList.length; j++ ){
            var k = dictFlagList[j] + type + appFlagList[i] + enableAnonymous;
            if( !MWF.xScript.dictLoaded[k] ){
                MWF.xScript.dictLoaded[k] = cache; //指向同一个对象
                // MWF.xScript.dictLoaded[k][path] = json; //指向不同的对象
            }else if( i===0 && j===0 ){
                MWF.xScript.setDictToCache( k, path ,json );
                var arr = path.split(/\./g);
                var p;
                var cache = MWF.xScript.dictLoaded[k];
                for( var l=0 ; l<arr.length; l++ ){
                    p = l === 0 ? arr[0] : ( p + "." + arr[l] );
                    if( cache[ p ] )break;
                }
                if( p ){
                    var mathP = p+".";
                    Object.keys( cache ).each( function( path, idx){
                        if( path.indexOf( mathP ) === 0 )delete cache[path];
                    })
                }
            }
        }
    }
};

MWF.xScript.getMatchedDict = function(key, path){
    if( !path )path = "root";
    if( path.indexOf("root") !== 0 )path = "root." + path ;

    var arr = path.split(/\./g);
    if( MWF.xScript.dictLoaded[key] ){
        var dicts = MWF.xScript.dictLoaded[key];
        var list = Array.clone(arr);
        var p;
        var dict;
        for( var i=0 ; i<arr.length; i++ ){
            p = i === 0 ? arr[0] : ( p + "." + arr[i] );
            list.shift();
            if( dicts[ p ] ){
                dict = dicts[ p ];
                break;
            }
        }
        return {
            dict : dict,
            unmatchedPathList : list
        }
    }
    return {
        dict : null,
        unmatchedPathList : list
    }
};

MWF.xScript.insertDictToCache = function(key, path, json){
    if( MWF.xScript.dictLoaded[key] ){
        var matchedDict = MWF.xScript.getMatchedDict( key, path );
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;
        if( !dict ){
            MWF.xScript.dictLoaded[key][path] = json;
        }else{
            for( var j=0; j<list.length-1; j++ ){
                if( !dict[ list[j] ] ){
                    dict[ list[j] ] = {};
                }
                dict = dict[ list[j] ];
            }
            var lastPath = list[list.length-1];
            if( !dict[lastPath] ){
                dict[lastPath] = json;
            }else if( typeOf( dict[lastPath] ) === "array" ){
                dict[lastPath].push( json );
            }
        }
    }else{
        MWF.xScript.dictLoaded[key] = {};
        MWF.xScript.dictLoaded[key][path] = json;
    }
};


MWF.xScript.setDictToCache = function(key, path, json){
    if( MWF.xScript.dictLoaded[key] ){
        var matchedDict = MWF.xScript.getMatchedDict( key, path );
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;
        if( !dict ){
            MWF.xScript.dictLoaded[key][path] = json;
        }else{
            for( var j=0; j<list.length-1; j++ ){
                if( !dict[ list[j] ] ){
                    dict[ list[j] ] = {};
                }
                dict = dict[ list[j] ];
            }
            dict[list[list.length-1]] = json;
        }
    }else{
        MWF.xScript.dictLoaded[key] = {};
        MWF.xScript.dictLoaded[key][path] = json;
    }
};

MWF.xScript.getDictFromCache = function( key, path ){
    var matchedDict = MWF.xScript.getMatchedDict( key, path );
    var dict = matchedDict.dict;
    var list = matchedDict.unmatchedPathList;
    if( dict ){
        for( var j=0; j<list.length; j++ ){
            dict = dict[ list[j] ];
            if( !dict )return null;
        }
        return dict;
    }
    return null;
};

MWF.xScript.deleteDictToCache = function(key, path){
    var matchedDict = MWF.xScript.getMatchedDict( key, path );
    var dict = matchedDict.dict;
    var list = matchedDict.unmatchedPathList;

    if( dict){
        for( var j=0; j<list.length-1; j++ ){
            dict = dict[ list[j] ];
            if( !dict )return;
        }
        delete dict[list[list.length-1]];
    }
};


MWF.xScript.createCMSDict = function(application){
    //optionsOrName : {
    //  type : "", //默认为process, 可以为  process  cms
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

        var opt = {
            "appType" : type,
            "name" : name,
            "appId" : applicationId,
            "enableAnonymous" : enableAnonymous
        };

        var key = name+type+applicationId+enableAnonymous;
        // if (!dictLoaded[key]) dictLoaded[key] = {};
        // this.dictData = dictLoaded[key];

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

        this.get = function(path, success, failure, async, refresh){
            var value = null;

            if (success===true) async=true;
            if (failure===true) async=true;

            if (!refresh ){
                var data = MWF.xScript.getDictFromCache( key, path );
                if( data ){
                    if (success && o2.typeOf(success)=="function") success( data );
                    if( !!async ){
                        return Promise.resolve( data );
                    }else{
                        return data;
                    }
                }
            }


            // var cb = function(json){
            //     value = json.data;
            //     MWF.xScript.addDictToCache(opt, path, value);
            //     if (success && o2.typeOf(success)=="function") value = success(json.data);
            //     return value;
            // }.ag().catch(function(xhr, text, error){ if (failure && o2.typeOf(failure)=="function") return failure(xhr, text, error); });

            var cb = function(json){
                value = json.data;
                MWF.xScript.addDictToCache(opt, path, value);
                if (success && o2.typeOf(success)=="function") value = success(json.data);
                return value;
            };

            var promise;
            if (path){
                var p = encodePath( path );
                //var p = path.replace(/\./g, "/");
                promise = action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, cb, null, !!async, false);
            }else{
                promise = action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, cb, null, !!async, false);
            }
            return (!!async) ? promise : value;

            // if (path){
            //     var p = encodePath( path );
            //     //var p = path.replace(/\./g, "/");
            //     action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, function(json){
            //         value = json.data;
            //         // this.dictData[path] = value;
            //         MWF.xScript.addDictToCache(opt, path, value);
            //         if (success) success(json.data);
            //     }.bind(this), function(xhr, text, error){
            //         if (failure) failure(xhr, text, error);
            //     }, !!async);
            // }else{
            //     action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, function(json){
            //         value = json.data;
            //         // this.dictData["root"] = value;
            //         MWF.xScript.addDictToCache(opt, path, value);
            //         if (success) success(json.data);
            //     }.bind(this), function(xhr, text, error){
            //         if (failure) failure(xhr, text, error);
            //     }, !!async);
            // }

            //return value;
        };

        this.set = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            return action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                MWF.xScript.setDictToCache(key, path, value);
                if (success) return success(json.data);
            }, function(xhr, text, error){
                if (failure) return failure(xhr, text, error);
            }, false, false);
        };
        this.add = function(path, value, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            return action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                MWF.xScript.insertDictToCache(key, path, value);
                if (success) return success(json.data);
            }, function(xhr, text, error){
                if (failure) return failure(xhr, text, error);
            }, false, false);
        };
        this["delete"] = function(path, success, failure){
            var p = encodePath( path );
            //var p = path.replace(/\./g, "/");
            return action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
                MWF.xScript.deleteDictToCache(key, path);
                if (success) return success(json.data);
            }, function(xhr, text, error){
                if (failure) return failure(xhr, text, error);
            }, false, false);
        };
        this.destory = this["delete"];
    }
};
