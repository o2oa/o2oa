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
        return this.action.address+uri+"?"+(new Date()).getTime();
    },
    getIcon: function(person){
        var uri = "/jaxrs/icon/"+person;
        this.action.getAddress();
        return this.action.address+uri+"?"+(new Date()).getTime();
    }
});