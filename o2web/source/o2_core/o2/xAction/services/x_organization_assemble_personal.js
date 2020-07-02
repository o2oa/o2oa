MWF.xAction.RestActions.Action["x_organization_assemble_personal"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    changePassword: function(oldPassword, password, morePassword, success, failure, async){
        var data = {
            "oldPassword": oldPassword,
            "newPassword": password,
            "confirmPassword": morePassword
        };
        this.action.invoke({"name": "changePassword", "async": async, "data": data, "success": success, "failure": failure});
    },
    getPersonIcon: function(id){
        var uri = "/jaxrs/person/icon";
        this.action.getAddress();
        return o2.filterUrl(this.action.address+uri+"?"+(new Date()).getTime());
    },
    getIcon: function(person){
        var uri = "/jaxrs/icon/"+person;
        this.action.getAddress();
        return this.action.address+uri+"?"+(new Date()).getTime();
    },
    createEmPower: function(data, success, failure, async){
        this.action.invoke({"name": "createEmPower", "async": async, "data": data, "success": success, "failure": failure});
    },
    editEmPower: function(id,data, success, failure, async){
        this.action.getAddress();

        this.action.invoke({"name": "editEmPower","parameter":{"id":id}, "async": async, "data": data, "success": success, "failure": failure});
    },
    deleteEmPower: function(id, success, failure, async){
        this.action.invoke({"name": "deleteEmPower","parameter":{"id":id}, "async": async, "success": success, "failure": failure});
    },

    listToCurrentPersonPaging: function( page, size, key,success, failure, async){
        this.action.invoke({"name": "listToCurrentPersonPaging", "parameter":{ "page":page, "size":size }, "data": { "key": key}, "async": async, "success": success, "failure": failure});
    },
    listWithCurrentPersonPaging: function( page, size, key,success, failure, async){
        this.action.invoke({"name": "listWithCurrentPersonPaging", "parameter":{ "page":page, "size":size }, "data": { "key": key}, "async": async, "success": success, "failure": failure});
    }
});