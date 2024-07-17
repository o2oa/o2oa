MWF.xAction.RestActions.Action["x_organization_assemble_personal"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    changePassword: function(oldPassword, password, morePassword, success, failure, async){
        if (layout.config.publicKey){
            o2.load("../o2_lib/jsencrypt/jsencrypt.js", function(){
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+layout.config.publicKey+"-----END PUBLIC KEY-----");
                var data = {
                    "oldPassword": encrypt.encrypt(oldPassword),
                    "newPassword": encrypt.encrypt(password),
                    "confirmPassword": encrypt.encrypt(morePassword),
                    "isEncrypted": "y"
                };
                this.action.invoke({"name": "changePassword", "async": async, "data": data, "success": success, "failure": failure});
            }.bind(this));
        }else{
            var data = {
                "oldPassword": oldPassword,
                "newPassword": password,
                "confirmPassword": morePassword
            };
            this.action.invoke({"name": "changePassword", "async": async, "data": data, "success": success, "failure": failure});
        }
    },
    resetPassword: function( data, success, failure, async ){
        if (layout.config.publicKey){
            o2.load("../o2_lib/jsencrypt/jsencrypt.js", function(){
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+layout.config.publicKey+"-----END PUBLIC KEY-----");
                var d = {
                    "codeAnswer": data.codeAnswer,
                    "credential": encrypt.encrypt(data.credential),
                    "password": encrypt.encrypt(data.password)
                };
                this.action.invoke({"name": "resetPassword", "async": async, "data": d, "success": success, "failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "resetPassword", "async": async, "data": data, "success": success, "failure": failure});
        }
    },
    setPasswordAnonymous: function( data, success, failure, async ){
        if (layout.config.publicKey){
            o2.load("../o2_lib/jsencrypt/jsencrypt.js", function(){
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+layout.config.publicKey+"-----END PUBLIC KEY-----");
                var d = {
                    userName : data.userName,
                    oldPassword : encrypt.encrypt(data.oldPassword),
                    newPassword : encrypt.encrypt(data.newPassword),
                    confirmPassword : encrypt.encrypt(data.confirmPassword),
                    isEncrypted : "y" //是否启用加密,默认不加密,启用(y)。注意:使用加密先要在服务器运行 create encrypt key"
                };
                this.action.invoke({"name": "setPasswordAnonymous", "async": async, "data": d, "success": success, "failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "setPasswordAnonymous", "async": async, "data": data, "success": success, "failure": failure});
        }
    },
    createCodeOnResetPassword: function ( credential, success, failure, async ){
        if (layout.config.publicKey){
            o2.load("../o2_lib/jsencrypt/jsencrypt.js", function(){
                var encrypt = new JSEncrypt();
                encrypt.setPublicKey("-----BEGIN PUBLIC KEY-----"+layout.config.publicKey+"-----END PUBLIC KEY-----");
                var c = encrypt.encrypt(credential);
                this.action.invoke({"name": "createCodeOnResetPassword", "async": async, "parameter":{ "credential": encodeURIComponent(c) }, "success": success, "failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "createCodeOnResetPassword", "async": async, "parameter":{ "credential": credential }, "success": success, "failure": failure});
        }
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