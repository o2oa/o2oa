MWF.xAction.RestActions.Action["x_organization_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    //个人接口服务--
    savePerson: function(data, success, failure){
        if (data.id){
            this.updatePerson(data.id, data, success, failure);
        }else{
            this.addPerson(data, success, failure);
        }
    },
    listPersonByPinyin: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listPersonByPinyin","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listPersonByPinyininitial: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listPersonByPinyininitial","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listPersonByKey: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listPersonByKey","async": async, "data": data,	"success": success,	"failure": failure});
    },
    getPersonIcon: function(id){
        var uri = "/jaxrs/person/{flag}/icon";
        uri = uri.replace("{flag}", id);
        this.action.getAddress();
        return this.action.address+uri+"?"+(new Date()).getTime();
    },


    //个人属性接口服务---
    savePersonAttribute: function(data, success, failure){
        if (data.id){
            this.updatePersonAttribute(data.id, data, success, failure);
        }else{
            this.addPersonAttribute(data, success, failure);
        }
    },

    //身份接口服务---
    saveIdentity: function(data, success, failure){
        if (data.id){
            this.updateIdentity(data.id, data, success, failure);
        }else{
            this.addIdentity(data, success, failure);
        }
    },
    listIdentityByKey: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listIdentityByKey","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listIdentityByPinyin: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listIdentityByPinyin","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listIdentityByPinyininitial: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listIdentityByPinyininitial","async": async, "data": data,	"success": success,	"failure": failure});
    },

    //角色接口服务---
    saveRole: function(data, success, failure){
        if (data.id){
            this.updateRole(data.id, data, success, failure);
        }else{
            this.addRole(data, success, failure);
        }
    },
    listRoleByPinyin: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listRoleByKey","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listRoleByPinyininitial: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listRoleByPinyininitial","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listRoleByKey: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listRoleByKey","async": async, "data": data,	"success": success,	"failure": failure});
    },

    //群组接口服务---
    saveGroup: function(data, success, failure){
        if (data.id){
            this.updateGroup(data.id, data, success, failure);
        }else{
            this.addGroup(data, success, failure);
        }
    },
    listGroupByKey: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listGroupByKey","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listGroupByPinyin: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listGroupByPinyin","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listGroupByPinyininitial: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listGroupByPinyininitial","async": async, "data": data,	"success": success,	"failure": failure});
    },

    //组织接口服务---
    saveUnit: function(data, success, failure){
        if (data.id){
            this.updateUnit(data.id, data, success, failure);
        }else{
            this.addUnit(data, success, failure);
        }
    },
    listUnitByKey: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listUnitByKey","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listUnitByPinyin: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listUnitByPinyin","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listUnitByPinyininitial: function(success, failure, key, async){
        var data = {};
        if (typeOf(key)==="string"){ data.key = key; }else{data = key;}
        this.action.invoke({"name": "listUnitByPinyininitial","async": async, "data": data,	"success": success,	"failure": failure});
    },

    //组织属性接口服务---
    saveUnitattribute: function(data, success, failure){
        if (data.id){
            this.updateUnitattribute(data.id, data, success, failure);
        }else{
            this.addUnitattribute(data, success, failure);
        }
    },

    //职务接口服务---
    saveUnitduty: function(data, success, failure){
        if (data.id){
            this.updateUnitduty(data.id, data, success, failure);
        }else{
            this.addUnitduty(data, success, failure);
        }
    }
});