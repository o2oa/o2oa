MWF.xAction.RestActions.Action["x_organization_assemble_authentication"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    login: function(data, success, failure, async){
        this.action.invoke({"name": "login", data : data,
            "success": function(json, responseText){
                if (json.data.tokenType!="anonymous"){
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
            },"failure": failure, "async": async});
    },
    getAuthentication: function(success, failure, async){
        this.action.invoke({"name": "getAuthentication",
            "success": function(json, responseText){
                if (json.data.tokenType!="anonymous"){
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
            }, "failure": failure, "async": async});
    }
});