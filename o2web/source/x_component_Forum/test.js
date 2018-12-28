this.org = {
    //群组***************

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

    //人员属性************
    //添加人员属性值(在属性中添加values值，如果没有此属性，则创建一个)
    appendPersonAttribute: function(person, attr, values){
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
    setPersonAttribute: function(person, attr, values){
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
                orgActions.f(data, function(json){ v = json.data; }, null, false);
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
    //列出人员所在组织的所有上级组织
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
        var data = {"name":name,"identity":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
        var v = null;
        orgActions.listUnitWithDuty(data, function(json){v = json.data;}, null, false);
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
    //列出顶层组织
    listTopUnit: function(){
        var action = MWF.Actions.get("x_organization_assemble_control");
        var v = null;
        action.listTopUnit(function(json){
            v = json.data;
        }, null, false);
        return v;
    },

    //组织属性**************
    //添加组织属性值(在属性中添加values值，如果没有此属性，则创建一个)
    appendUnitAttribute: function(unit, attr, values){
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
    setUnitAttribute: function(unit, attr, values){
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