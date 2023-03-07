MWF.xAction.RestActions.Action["x_portal_assemble_surface"] = new Class({
    Extends: MWF.xAction.RestActions.Action
    // saveDictionary: function(data, success, failure){
    //     if (data.id){
    //         this.updateDictionary(data, success, failure);
    //     }else{
    //         this.addDictionary(data, success, failure);
    //     }
    // },
    // updateDictionary: function(data, success, failure){
    //     this.action.invoke({"name": "updataDictionary","data": data,"parameter": {"applicationDictFlag": data.id, "applicationFlag": data.application},"success": success,"failure": failure});
    // },
    // addDictionary: function(data, success, failure){
    //     if (!data.id){
    //         this.getUUID(function(id){
    //             data.id = id;
    //             this.action.invoke({"name": "addDictionary","data": data,"success": success,"failure": failure});
    //         }.bind(this));
    //     }
    // }
});