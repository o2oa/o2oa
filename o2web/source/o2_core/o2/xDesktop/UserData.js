MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xDesktop.UserData = MWF.UD = {
    getAction: function(){
        this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_organization_assemble_personal");
    },
    getData: function(name, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "getUserData", "async": async, "parameter": {"name": name}, "success": function(json){
            if (callback) callback(json);
        }.bind(this)});
    },
    getDataJson: function(name, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "getUserData", "async": async, "parameter": {"name": name}, "success": function(json){
            var returnJson = null;
            if (json.data) returnJson = JSON.decode(json.data);
            if (callback) callback(returnJson);
        }.bind(this)});
    },

    putData: function(name, data, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "putUserData", "async": async, "data": data, "parameter": {"name": name}, "success": function(json){
            if (callback) callback(json);
        }.bind(this)});
    },
    deleteData: function(name, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "deleteUserData", "async": async, "parameter": {"name": name}, "success": function(json){
            if (callback) callback(json);
        }.bind(this)});
    },

    getPublicData: function(name, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "getPublicUserData", "async": async, "parameter": {"name": name}, "success": function(json){
            var returnJson = null;
            if (json.data) returnJson = JSON.decode(json.data);
            if (callback) callback(returnJson);
        }.bind(this)});
    },
    putPublicData: function(name, data, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "putPublicUserData", "async": async, "data": data, "parameter": {"name": name}, "success": function(json){
            if (callback){
                if (callback.success){
                    callback.success(json);
                }else{
                    callback(json);
                }
            }
        }.bind(this), "failure": function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
            if (callback) if (callback.failure) callback.failure(xhr, text, error);
        }});
    },
    deletePublicData: function(name, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "deletePublicUserData", "async": async, "parameter": {"name": name}, "success": function(json){
            if (callback) callback(json);
        }.bind(this)});
    }
};