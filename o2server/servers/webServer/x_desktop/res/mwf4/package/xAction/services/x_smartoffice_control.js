MWF.xAction.RestActions.Action["x_smartoffice_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    initialize: function(root, actions){
        this.action = new MWF.xAction.RestActions.Action["x_smartoffice_control"].RestActions("/xAction/services/"+root+".json", root, "");
        this.action.actions = actions;

        Object.each(this.action.actions, function(service, key){
            if (service.uri) if (!this[key]) this.createMethod(service, key);
        }.bind(this));
    },
    saveDevice: function(data, success, failure){
        if (data.id){
            this.updateDevice(data, success, failure);
        }else{
            this.addDevice(data, success, failure);
        }
    }
    // updatePage: function(pageData, mobileData, fieldList, success, failure){
    //     this.action.invoke({"name": "updatePage","data": json,"parameter": {"id": pageData.json.id},"success": success,"failure": failure});
    // },
    // addDevice: function(data, success, failure){
    //     this.action.invoke({"name": "addDevice","data": data, "success": success,"failure": failure});
    // },

});
MWF.xAction.RestActions.Action["x_smartoffice_control"].RestActions = new Class({
    Extends: MWF.xDesktop.Actions.RestActions,
    getAddress: function(success, failure){
        this.address = "http://127.0.0.1:8000/"+this.serviceName;
        if (success) success.apply();
    }
});