MWF.xApplication.Profile = MWF.xApplication.Profile || {};
MWF.xApplication.Profile.Actions = MWF.xApplication.Profile.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Profile.Actions.RestActions = new Class({
    initialize: function(){
        this.actionPath = "/x_component_Profile/Actions/action.json";

        this.actionOrg = new MWF.xDesktop.Actions.RestActions("", "x_organization_assemble_personal", "x_component_Profile");
        this.actionOrg.getActions = function(callback){
            this.getActionActions(this.actionOrg, callback);
        }.bind(this);

    },
    getActionActions: function(action, callback){
        if (!action.actions){
            this.getActions(function(json){
                action.actions = this.actions;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    getActions: function(callback){
        if (!this.actions){
            MWF.getJSON(this.actionPath, function(json){
                this.actions = json;
                if (callback) callback();
            }.bind(this));
        }else{
            if (callback) callback();
        }
    },
    changePassword: function(oldPassword, password, morePassword, success, failure, async){
        var data = {
            "oldPassword": oldPassword,
            "newPassword": password,
            "confirmPassword": morePassword
        }
        this.actionOrg.invoke({"name": "changePassword", "async": async, "data": data, "success": success, "failure": failure});
    },
    getPerson: function(success, failure, async){
        this.actionOrg.invoke({"name": "getPerson", "async": async, "success": success, "failure": failure});
    },
    updatePerson: function(data, success, failure, async){
        this.actionOrg.invoke({"name": "updatePerson", "async": async, "data": data, "success": success, "failure": failure});
    },
    changeIcon: function(success, failure, formData, file){
        this.actionOrg.invoke({"name": "changeIcon", "data": formData,"file": file,"success": success,"failure": failure});
    },
    checkPassword:  function(password, success, failure, async){
        this.actionOrg.invoke({"name": "checkPassword", "parameter": {"password": password },"success": success,"failure": failure, "async": async});
    }
});