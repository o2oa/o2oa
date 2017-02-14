MWF.xApplication.Setting = MWF.xApplication.Setting || {};
MWF.xApplication.Setting.Actions = MWF.xApplication.Setting.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Setting.Actions.RestActions = new Class({
    initialize: function(){
        this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "", "x_component_Setting");
        this.collectAction = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "", "x_component_Setting");

        var _self = this;
        this.collectAction.getActions = function(callback){
            uri = "http://collect.xplatform.tech/o2_collect";
            this.address = uri;
            if (_self.action.actions){
                this.actions = _self.action.actions;
                if (callback) callback();
            }else{
                if (!this.actions){
                    var url = (this.root) ? "/"+this.root+this.actionPath : MWF.defaultPath+this.actionPath
                    MWF.getJSON(url, function(json){
                        this.actions = json;
                        if (callback) callback();
                    }.bind(this), false, false, false);
                }else{
                    if (callback) callback();
                }
            }
        }

    },

    listApplicationServer: function(success, failure, async){
        this.action.invoke({"name": "listApplicationServer", "async": async, "success": success, "failure": failure});
    },
    listDepolyable: function(success, failure, async){
        this.action.invoke({"name": "listDepolyable", "async": async, "success": success, "failure": failure});
    },
    updateAppServer: function(name, data, success, failure, async){
        this.action.invoke({"name": "updateAppServer", "async": async, "data": data, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    addAppServer: function(data, success, failure, async){
        this.action.invoke({"name": "addAppServer", "async": async, "data": data, "success": success, "failure": failure});
    },
    deploy: function(name, force, success, failure, async){
        this.action.invoke({"name": "updateAppServer", "async": async, "parameter": {"name": name, "forceRedeploy": force}, "success": success, "failure": failure});
    },

    getAppServer: function(name, success, failure, async){
        this.action.invoke({"name": "getAppServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    removeAppServer: function(name, success, failure, async){
        this.action.invoke({"name": "removeAppServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },

    listDataServer: function(success, failure, async){
        this.action.invoke({"name": "listDataServer", "async": async, "success": success, "failure": failure});
    },
    updateDataServer: function(name, data, success, failure, async){
        this.action.invoke({"name": "updateDataServer", "async": async, "data": data, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    addDataServer: function(data, success, failure, async){
        this.action.invoke({"name": "addDataServer", "async": async, "data": data, "success": success, "failure": failure});
    },
    removeDataServer: function(name, success, failure, async){
        this.action.invoke({"name": "removeDataServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    getDataServer: function(name, success, failure, async){
        this.action.invoke({"name": "getDataServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },

    listStorageServer: function(success, failure, async){
        this.action.invoke({"name": "listStorageServer", "async": async, "success": success, "failure": failure});
    },
    updateStorageServer: function(name, data, success, failure, async){
        this.action.invoke({"name": "updateStorageServer", "async": async, "data": data, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    addStorageServer: function(data, success, failure, async){
        this.action.invoke({"name": "addStorageServer", "async": async, "data": data, "success": success, "failure": failure});
    },
    removeStorageServer: function(name, success, failure, async){
        this.action.invoke({"name": "removeStorageServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    getStorageServer: function(name, success, failure, async){
        this.action.invoke({"name": "getStorageServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },

    listWebServer: function(success, failure, async){
        this.action.invoke({"name": "listWebServer", "async": async, "success": success, "failure": failure});
    },
    updateWebServer: function(name, data, success, failure, async){
        this.action.invoke({"name": "updateWebServer", "async": async, "data": data, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    addWebServer: function(data, success, failure, async){
        this.action.invoke({"name": "addWebServer", "async": async, "data": data, "success": success, "failure": failure});
    },
    removeWebServer: function(name, success, failure, async){
        this.action.invoke({"name": "removeWebServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },
    getWebServer: function(name, success, failure, async){
        this.action.invoke({"name": "getWebServer", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },

    updateCenterServer: function(data, success, failure, async){
        this.action.invoke({"name": "updateCenterServer", "async": async, "data": data, "success": success, "failure": failure});
    },
    getCenterServer: function(success, failure, async){
        this.action.invoke({"name": "getCenterServer", "async": async, "success": success, "failure": failure});
    },
    listApplications: function(success, failure, async){
        this.action.invoke({"name": "listApplications", "async": async, "success": success, "failure": failure});
    },
    listDatas: function(success, failure, async){
        this.action.invoke({"name": "listDatas", "async": async, "success": success, "failure": failure});
    },
    listDataMappings: function(success, failure, async){
        this.action.invoke({"name": "listDataMappings", "async": async, "success": success, "failure": failure});
    },

    listStorages: function(success, failure, async){
        this.action.invoke({"name": "listStorages", "async": async, "success": success, "failure": failure});
    },
    listStorageMappings: function(success, failure, async){
        this.action.invoke({"name": "listStorageMappings", "async": async, "success": success, "failure": failure});
    },

    addStorage: function(type, data, success, failure, async){
        this.action.invoke({"name": "addStorage", "async": async, "data": data, "parameter": {"storageType": type}, "success": success, "failure": failure});
    },
    removeStorage: function(type, server, success, failure, async){
        this.action.invoke({"name": "removeStorage", "async": async, "parameter": {"storageType": type, "storageServer": server}, "success": success, "failure": failure});
    },
    updateStorage: function(type, server, data, success, failure, async){
        this.action.invoke({"name": "updateStorage", "async": async, "data": data, "parameter": {"storageType": type, "storageServer": server}, "success": success, "failure": failure});
    },
    getStorage: function(type, server, success, failure, async){
        this.action.invoke({"name": "getStorage", "async": async, "parameter": {"storageType": type, "storageServer": server}, "success": success, "failure": failure});
    },

    getResAdministrator: function(success, failure, async){
        this.action.invoke({"name": "getResAdministrator", "async": async, "success": success, "failure": failure});
    },
    updateResAdministrator: function(data, success, failure, async){
        this.action.invoke({"name": "updateResAdministrator", "async": async, "data": data, "success": success, "failure": failure});
    },
    getResCollect: function(success, failure, async){
        this.action.invoke({"name": "getResCollect", "async": async, "success": success, "failure": failure});
    },
    updateResCollect: function(data, success, failure, async){
        this.action.invoke({"name": "updateResCollect", "async": async, "data": data, "success": success, "failure": failure});
    },

    getResOpenMeeting: function(success, failure, async){
        this.action.invoke({"name": "getResOpenMeeting", "async": async, "success": success, "failure": failure});
    },
    updateResOpenMeeting: function(data, success, failure, async){
        this.action.invoke({"name": "updateResOpenMeeting", "async": async, "data": data, "success": success, "failure": failure});
    },

    getResPassword: function(success, failure, async){
        this.action.invoke({"name": "getResPassword", "async": async, "success": success, "failure": failure});
    },
    updateResPassword: function(data, success, failure, async){
        this.action.invoke({"name": "updateResPassword", "async": async, "data": data, "success": success, "failure": failure});
    },

    getResPerson: function(success, failure, async){
        this.action.invoke({"name": "getResPerson", "async": async, "success": success, "failure": failure});
    },
    updateResPerson: function(data, success, failure, async){
        this.action.invoke({"name": "updateResPerson", "async": async, "data": data, "success": success, "failure": failure});
    },

    getResSSO: function(success, failure, async){
        this.action.invoke({"name": "getResSSO", "async": async, "success": success, "failure": failure});
    },
    updateResSSO: function(data, success, failure, async){
        this.action.invoke({"name": "updateResSSO", "async": async, "data": data, "success": success, "failure": failure});
    },

    getResWorktime: function(success, failure, async){
        this.action.invoke({"name": "getResWorktime", "async": async, "success": success, "failure": failure});
    },
    updateResWorktime: function(data, success, failure, async){
        this.action.invoke({"name": "updateResWorktime", "async": async, "data": data, "success": success, "failure": failure});
    },

    getResource: function(name, success, failure, async){
        this.action.invoke({"name": "getResource", "async": async, "parameter": {"name": name}, "success": success, "failure": failure});
    },

    checkConnect: function(success, failure, async){
        this.action.invoke({"name": "checkConnect", "async": async, "success": success, "failure": failure});
    },
    connectCollect: function(success, failure, async){
        this.collectAction.invoke({"name": "connectCollect", "async": async, "success": success, "failure": failure});
    },

    getCaptcha: function(success, failure, async){
        this.collectAction.invoke({"name": "getCaptcha", "async": async, "success": success, "failure": failure});
    },
    loginCollect: function(key, answer, data, success, failure, async){
        this.collectAction.invoke({"name": "loginCollect", "async": async, "data": data, "parameter": {"key": key, "answer": answer}, "success": success, "failure": failure});
    },
    logoutCollect: function(success, failure, async){
        this.collectAction.invoke({"name": "logoutCollect", "async": async, "success": success, "failure": failure});
    },
    getCode: function(data, success, failure, async){
        this.collectAction.invoke({"name": "getCode", "async": async, "data": data, "success": success, "failure": failure});
    },
    register: function(data, success, failure, async){
        this.collectAction.invoke({"name": "register", "async": async, "data": data, "success": success, "failure": failure});
    },


});