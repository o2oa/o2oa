MWF.xDesktop = MWF.xDesktop || {};
MWF.xApplication = MWF.xApplication || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

MWF.xDesktop.UserData = MWF.UD = {
    getAction: function(){
        this.action = new MWF.xDesktop.Actions.RestActions("/xDesktop/Actions/action.json", "x_organization_assemble_custom");
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
            if (callback) callback(json);
        }.bind(this)});
    },
    deletePublicData: function(name, callback, async){
        if (!this.action) this.getAction();
        this.action.invoke({"name": "deletePublicUserData", "async": async, "parameter": {"name": name}, "success": function(json){
            if (callback) callback(json);
        }.bind(this)});
    }
}