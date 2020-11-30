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

    loginByCaptcha: function(data, success, failure, async){
        if (layout.config.publicKey){
            o2.load("../o2_lib/jsencrypt/jsencrypt.js", function(){
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+layout.config.publicKey+"-----END PUBLIC KEY-----");
                data.password = encrypt.encrypt(data.password);
                data.isEncrypted = "y";
                this.action.invoke({"name": "loginByCaptcha", data : data,
                    "success": function(json, responseText){
                        if (json.data.tokenType!="anonymous"){
                            if (success) success(json);
                        }else{
                            if (failure) failure(null, responseText, json.message);
                        }
                    },"failure": failure, "async": async});
            }.bind(this));
        }else{
            this.action.invoke({"name": "loginByCaptcha", data : data,
                "success": function(json, responseText){
                    if (json.data.tokenType!="anonymous"){
                        if (success) success(json);
                    }else{
                        if (failure) failure(null, responseText, json.message);
                    }
                },"failure": failure, "async": async});
        }
    },

    getAuthentication: function(success, failure, async){
        this.action.invoke({"name": "getAuthentication",
            "success": function(json, responseText){
                if (json.data.tokenType!=="anonymous" || layout.anonymous){
                    if (json.data && !json.data.roleList) json.data.roleList = [];
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
            }, "failure": failure, "async": async});
    }
});
