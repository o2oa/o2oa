MWF.xApplication.Authentication = MWF.xApplication.Authentication || {};
MWF.xApplication.Authentication.Actions = MWF.xApplication.Authentication.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Authentication.Actions.RestActions = new Class({
	initialize: function(){
        this.actionPerson =  new MWF.xDesktop.Actions.RestActions("/xAction/Authentication/action.json", "x_organization_assemble_personal");

        this.actionAuthentication = new MWF.xDesktop.Actions.RestActions("/xAction/Authentication/action.json", "x_organization_assemble_authentication");
	},

    getRegisterMode: function(success, failure, async){
        this.actionPerson.invoke({"name": "getRegisterMode", "success": success,"failure": failure, "async": async});
    },
    getRegisterCaptcha: function(width, height, success, failure, async){
        this.actionPerson.invoke({"name": "getRegisterCaptcha", "parameter": {"width": width, "height" : height },"success": success,"failure": failure, "async": async});
    },
    createRegisterCode: function( mobile, success, failure, async){
        this.actionPerson.invoke({"name": "createRegisterCode", "parameter": {"mobile": mobile },"success": success,"failure": failure, "async": async});
    },
    checkRegisterName : function(name, success, failure, async){
        this.actionPerson.invoke({"name": "checkRegisterName", "parameter": {"name": name },"success": success,"failure": failure, "async": async});
    },
    checkRegisterPassword:  function(password, success, failure, async){
        this.actionPerson.invoke({"name": "checkRegisterPassword", "parameter": {"password": password },"success": success,"failure": failure, "async": async});
    },
    checkRegisterMobile:  function(mobile, success, failure, async){
        this.actionPerson.invoke({"name": "checkRegisterMobile", "parameter": {"mobile": mobile },"success": success,"failure": failure, "async": async});
    },
    register: function(data, success, failure, async){
        this.actionPerson.invoke({"name": "register", data : data, "success": success,"failure": failure, "async": async});
    },

    resetPassword: function(data, success, failure, async){
        this.actionPerson.invoke({"name": "resetPassword", data : data, "success": success,"failure": failure, "async": async});
    },
    checkCredentialOnResetPassword : function(credential, success, failure, async){
        this.actionPerson.invoke({"name": "checkCredentialOnResetPassword", "parameter": {"credential": credential },"success": success,"failure": failure, "async": async});
    },
    checkPasswordOnResetPassword:  function(password, success, failure, async){
        this.actionPerson.invoke({"name": "checkPasswordOnResetPassword", "parameter": {"password": password },"success": success,"failure": failure, "async": async});
    },
    createCodeOnResetPassword : function(credential, success, failure, async){
        this.actionPerson.invoke({"name": "createCodeOnResetPassword", "parameter": {"credential": credential },"success": success,"failure": failure, "async": async});
    },

    authentication: function( success, failure, async){
        this.actionAuthentication.invoke({"name": "authentication", "success": success,"failure": failure, "async": async});
    },
    login: function(data, success, failure, async){
        this.actionAuthentication.invoke({"name": "login", data : data,
            "success": function(json, responseText){
                if (json.data.tokenType!="anonymous"){
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
        },"failure": failure, "async": async});
    },
    loginAdmin: function(data, success, failure, async){
        this.actionAuthentication.invoke({"name": "loginAdmin", data : data, "success": success,"failure": failure, "async": async});
    },
    logout: function(success, failure, async){
        this.actionAuthentication.invoke({"name": "logout", "success": success,"failure": failure, "async": async});
    },
    getLoginMode: function(success, failure, async){
        this.actionAuthentication.invoke({"name": "getLoginMode", "success": success,"failure": failure, "async": async});
    },
    getAuthentication: function(success, failure, async){
        this.actionAuthentication.invoke({"name": "getAuthentication",
            "success": function(json, responseText){
                if (json.data.tokenType!="anonymous"){
                    if (success) success(json);
                }else{
                    if (failure) failure(null, responseText, json.message);
                }
        }, "failure": failure, "async": async});
    },
    loginByPassword: function(data, success, failure, async){
        this.actionAuthentication.invoke({"name": "loginByPassword", data : data, "success": success,"failure": failure, "async": async});
    },
    getLoginCaptcha: function(width, height, success, failure, async){
        this.actionAuthentication.invoke({"name": "getLoginCaptcha", "parameter": {"width": width, "height" : height },"success": success,"failure": failure, "async": async});
    },
    loginByCaptcha: function(data, success, failure, async){
        this.actionAuthentication.invoke({"name": "loginByCaptcha", data : data, "success": success,"failure": failure, "async": async});
    },
    createCredentialCode: function( credential, success, failure, async){
        this.actionAuthentication.invoke({"name": "createCredentialCode", "parameter": {"credential": credential },"success": success,"failure": failure, "async": async});
    },
    checkCredential: function( credential, success, failure, async){
        this.actionAuthentication.invoke({"name": "checkCredential", "parameter": {"credential": credential },"success": success,"failure": failure, "async": async});
    },
    loginByCode: function(data, success, failure, async){
        this.actionAuthentication.invoke({"name": "loginByCode", data : data, "success": success,"failure": failure, "async": async});
    },
    getLoginBind: function(success, failure, async){
        this.actionAuthentication.invoke({"name": "getLoginBind","success": success,"failure": failure, "async": async});
    },
    checkBindStatus: function(meta, success, failure, async){
        this.actionAuthentication.invoke({"name": "checkBindStatus", "parameter": {"meta": meta }, "success": success,"failure": failure, "async": async});
    }

});