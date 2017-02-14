MWF.xApplication.process.Application = MWF.xApplication.process.Application || {};
MWF.xApplication.process.Application.Actions = MWF.xApplication.process.Application.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.process.Application.Actions.RestActions = new Class({
    initialize: function(){
        this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_processplatform_assemble_surface", "x_component_process_Application");
        this.actiondict = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_processplatform_assemble_surface", "x_component_process_Application");
        this.actionSerial = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_processplatform_assemble_surface", "x_component_process_Application");
    },

    getApplication: function(id, success, failure, async){
        this.action.invoke({"name": "getApplication","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listApplication: function(success, failure, async){
        this.action.invoke({"name": "listApplication","async": async, "success": success, "failure": failure});
    },
    getApplicationIcon: function(success, failure, id, async){
        this.action.invoke({"name": "getApplicationIcon","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listWorkNext: function(id, count, application, success, failure, async){
        this.action.invoke({"name": "listWorkNext","async": async, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },
    listWorkNextManage: function(id, count, application, success, failure, async){
        this.action.invoke({"name": "listWorkNextManage","async": async, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },
    listAssignments: function(id, application, success, failure, async){
        this.action.invoke({"name": "listAssignments","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    listRelatives: function(id, application, success, failure, async){
        this.action.invoke({"name": "listRelatives","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },

    listProcess: function(application, success, failure, async){
        this.action.invoke({"name": "listProcess","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },
    listProcessManage: function(application, success, failure, async){
        this.action.invoke({"name": "listProcessManage","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },

    listWorkFilter: function(id, count, application, data, success, failure, async){
        this.action.invoke({"name": "listWorkFilter","async": async, "data": data, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },
    listWorkFilterManage: function(id, count, application, data, success, failure, async){
        this.action.invoke({"name": "listWorkFilterManage","async": async, "data": data, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },

    listFilterAttribute: function(application, success, failure, async){
        this.action.invoke({"name": "listFilterAttribute","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },
    listFilterAttributeManage: function(application, success, failure, async){
        this.action.invoke({"name": "listFilterAttributeManage","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },

    listWorkCompletedNext: function(id, count, application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedNext","async": async, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },
    listWorkCompletedNextManage: function(id, count, application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedNextManage","async": async, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },

    listWorkCompletedFilter: function(id, count, application, data, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedFilter","async": async, "data": data, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },
    listWorkCompletedFilterManage: function(id, count, application, data, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedFilterManage","async": async, "data": data, "parameter": {"id": id, "count": count, "applicationId": application}, "success": success,	"failure": failure});
    },

    listWorkCompletedFilterAttribute: function(application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedFilterAttribute","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },
    listWorkCompletedFilterAttributeManage: function(application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedFilterAttributeManage","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },


    listWorkCompletedProcess: function(application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedProcess","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },
    listWorkCompletedProcessManage: function(application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedProcessManage","async": async, "parameter": {"applicationId": application},	"success": success,	"failure": failure});
    },

    listWorkCompletedAssignments: function(id, application, success, failure, async){
        this.action.invoke({"name": "listWorkCompletedAssignments","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },


    removeWork: function(id, application, all, success, failure, async){
        if (all){
            this.action.invoke({"name": "removeAllWork","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "removeWork","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
        }
    },
    removeWorkCompleted: function(id, application, success, failure, async){
        this.action.invoke({"name": "removeWorkCompleted","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    resetTask: function(id, application, data, success, failure, async){
        this.action.invoke({"name": "resetTask","async": async, "data": data, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    flowTask: function(id, application, data, success, failure, async){
        this.action.invoke({"name": "flowTask","async": async, "data": data, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    flowWork: function(id, application, data, success, failure, async){
        this.action.invoke({"name": "flowWork","async": async, "data": data, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },

    resetRead: function(id, application, data, success, failure, async){
        this.action.invoke({"name": "resetRead","async": async, "data": data, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    flagRead: function(id, application, data, success, failure, async){
        this.action.invoke({"name": "flagRead","async": async, "data": data, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },

    getWork: function(id, application, success, failure, async){
        this.action.invoke({"name": "getWork","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    getWorkCompleted: function(id, application, success, failure, async){
        this.action.invoke({"name": "getWorkCompleted","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },

    removeTask: function(id, application, success, failure, async){
        this.action.invoke({"name": "removeTask","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    removeDone: function(id, application, success, failure, async){
        this.action.invoke({"name": "removeDone","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    removeRead: function(id, application, success, failure, async){
        this.action.invoke({"name": "removeRead","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },
    removeReaded: function(id, application, success, failure, async){
        this.action.invoke({"name": "removeReaded","async": async, "parameter": {"id": id, "applicationId": application},	"success": success,	"failure": failure});
    },

    listDictionary: function(application, success, failure, async){
        this.actiondict.invoke({"name": "listDictionary","async": async, "parameter": {"application": application}, "success": success,	"failure": failure});
    },
    getDictionary: function(id, success, failure, async){
        this.actiondict.invoke({"name": "getDictionary","async": async, "parameter": {"applicationDict": id, "applicationFlag": this.application.id || this.application},	"success": success,	"failure": failure});
    },
    saveDictionary: function(data, success, failure){
        if (data.id){
            this.updateDictionary(data, success, failure);
        }else{
            this.addDictionary(data, success, failure);
        }
    },
    updateDictionary: function(data, success, failure){
        this.actiondict.invoke({"name": "updataDictionary","data": data,"parameter": {"applicationDictFlag": data.id, "applicationFlag": data.application},"success": success,"failure": failure});
    },
    addDictionary: function(data, success, failure){
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.actiondict.invoke({"name": "addDictionary","data": data,"success": success,"failure": failure});
            }.bind(this));
        }
    },
    deleteDictionary: function(id, success, failure, async){
        this.actiondict.invoke({"name": "removeDictionary", "async": async, "parameter": {"applicationDict": id}, "success": success, "failure": failure});
    },

    listSerialNumber: function(application, success, failure, async){
        this.actionSerial.invoke({"name": "listSerialNumber","async": async, "parameter": {"application": application}, "success": success,	"failure": failure});
    },
    getSerialNumber: function(id, success, failure, async){
        this.actionSerial.invoke({"name": "getSerialNumber","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    deleteSerialNumber: function(id, success, failure, async){
        this.actionSerial.invoke({"name": "deleteSerialNumber","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    updateSerialNumber: function(id, data, success, failure, async){
        this.actionSerial.invoke({"name": "updateSerialNumber", "data": data, "async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },

    getRerouteTo: function(flag, success, failure, async){
        this.action.invoke({"name": "getRerouteTo","async": async, "parameter": {"flag": flag},	"success": success,	"failure": failure});
    },
    rerouteWork: function(success, failure, id, activityId, type, async){
        this.action.invoke({"name": "rerouteWork","async": async, "data": null, "parameter": {"id": id, "activityId": activityId, "type": type},	"success": success,	"failure": failure});
    },

    listView: function(application, success, failure, async){
        this.action.invoke({"name": "listView","async": async, "parameter": {"application": application},	"success": success,	"failure": failure});
    },
    loadView: function(success, failure, id, application, type, async){
        this.action.invoke({"name": "loadView","async": async, "data": null, "parameter": {"flag": id, "application": application},	"success": success,	"failure": failure});
    },

    listStat: function(application, success, failure, async){
        this.action.invoke({"name": "listStat","async": async, "parameter": {"application": application},	"success": success,	"failure": failure});
    },
    loadStat: function(success, failure, id, application, type, async){
        this.action.invoke({"name": "loadStat","async": async, "data": null, "parameter": {"flag": id, "application": application},	"success": success,	"failure": failure});
    }

});