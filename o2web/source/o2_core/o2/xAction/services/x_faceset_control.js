MWF.xAction.RestActions.Action["x_faceset_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    initialize: function(root, actions){
        this.action = new MWF.xAction.RestActions.Action["x_faceset_control"].RestActions("/xAction/services/"+root+".json", root, "");
        this.action.actions = actions;

        o2.UD.getPublicData("faceKeys", function(json){
            this.faceKeys = (json) ? json.api : null;
        }.bind(this), false);
        Object.each(this.action.actions, function(service, key){
            if (service.uri) if (!this[key]) this.createMethod(service, key);
        }.bind(this));
    },
    invoke: function(service, options){

        if (this.faceKeys){
            if(service.enctype && (service.enctype.toLowerCase()==="formdata")){
                options.data.append("api_key", this.faceKeys.api_key);
                options.data.append("api_secret", this.faceKeys.api_secret);
            }else{
                if (!options.parameter) options.parameter = {};
                options.parameter.api_key = this.faceKeys.api_key;
                options.parameter.api_secret = this.faceKeys.api_secret
            }
        }
        return this.action.invoke(options);
    }
    // saveDevice: function(data, success, failure){
    //     if (data.id){
    //         this.updateDevice(data, success, failure);
    //     }else{
    //         this.addDevice(data, success, failure);
    //     }
    // }
    // updatePage: function(pageData, mobileData, fieldList, success, failure){
    //     this.action.invoke({"name": "updatePage","data": json,"parameter": {"id": pageData.json.id},"success": success,"failure": failure});
    // },
    // addDevice: function(data, success, failure){
    //     this.action.invoke({"name": "addDevice","data": data, "success": success,"failure": failure});
    // },

});
MWF.xAction.RestActions.Action["x_faceset_control"].RestActions = new Class({
    Extends: MWF.xDesktop.Actions.RestActions,
    getAddress: function(success, failure){
        //this.address = "http://dev.o2oa.net:8888/"+this.serviceName;
        this.address = "https://git.o2oa.net:8888/"+this.serviceName;
        //this.address = "http://127.0.0.1:8888/"+this.serviceName;
        if (success) success.apply();
    }
});
