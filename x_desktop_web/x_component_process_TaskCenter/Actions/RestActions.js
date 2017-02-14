MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.TaskCenter = MWF.xApplication.process.TaskCenter || {};
MWF.xApplication.process.TaskCenter.Actions = MWF.xApplication.process.TaskCenter.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.process.TaskCenter.Actions.RestActions = new Class({
	initialize: function(){
        this.actionPath = "/x_component_process_TaskCenter/Actions/action.json";
		this.actionApplication = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionApplication.getActions = function(callback){
            this.getActionActions(this.actionApplication, callback);
        }.bind(this);

        this.actionProcess = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionProcess.getActions = function(callback){
            this.getActionActions(this.actionProcess,callback);
        }.bind(this);

        this.actionWork = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionWork.getActions = function(callback){
            this.getActionActions(this.actionWork,callback);
        }.bind(this);

        this.actionTask = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionTask.getActions = function(callback){
            this.getActionActions(this.actionTask,callback);
        }.bind(this);

        this.actionTaskCompleted = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionTaskCompleted.getActions = function(callback){
            this.getActionActions(this.actionTaskCompleted, callback);
        }.bind(this);

        this.actionRead = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionRead.getActions = function(callback){
            this.getActionActions(this.actionRead, callback);
        }.bind(this);

        this.actionReaded = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionReaded.getActions = function(callback){
            this.getActionActions(this.actionReaded, callback);
        }.bind(this);

        this.actionReview = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_TaskCenter");
        this.actionReview.getActions = function(callback){
            this.getActionActions(this.actionReview, callback);
        }.bind(this);

        //this.actionDesigner = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_designer");
        //this.actionDesigner.getActions = function(callback){
        //    this.getActionActions(this.actionDesigner, callback);
        //}.bind(this);
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
            }.bind(this), true, true, false);
        }else{
            if (callback) callback();
        }
    },
    getApplication: function(success, failure, id, async){
        this.actionApplication.invoke({"name": "getApplication","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listApplication: function(success, failure, async){
        this.actionApplication.invoke({"name": "listApplication","async": async, "success": success, "failure": failure});
    },
    listApplicationStartable: function(success, failure, async){
        this.actionApplication.invoke({"name": "listApplicationStartable","async": async, "success": success, "failure": failure});
    },
    getApplicationIcon: function(success, failure, id, async){
        this.actionApplication.invoke({"name": "getApplicationIcon","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    listProcess: function(success, failure, id, async){
        this.actionProcess.invoke({"name": "listProcess","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    startWork: function(success, failure, id, data, async){
        this.actionWork.invoke({"name": "startWork","data": data,"async": async, "parameter": {"processId": id},	"success": success,	"failure": failure});
    },

    listWorkByCreator: function(success, failure, async){
        this.actionWork.invoke({"name": "listWorkByCreator","async": async, "success": success, "failure": failure});
    },
    removeWork: function(id, success, failure, async){
        this.actionWork.invoke({"name": "removeWork", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    getWork: function(success, failure, id, async){
        this.actionWork.invoke({"name": "getWork","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getCount: function(success, failure, credential, async){
        this.actionWork.invoke({"name": "getCount","async": async, "parameter": {"credential": credential},	"success": success,	"failure": failure});
    },


    processWork: function(data, success, failure){
        this.action.invoke({"name": "processWork","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },

    listTask: function(success, failure, async){
        this.actionTask.invoke({"name": "listTask","async": async, "success": success, "failure": failure});
    },
    listTaskNext: function(success, failure, id, count, async){
        this.actionTask.invoke({"name": "listTaskNext", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },
    listTaskNextByApp: function(success, failure, id, count, app, async){
        this.actionTask.invoke({"name": "listTaskNextByApp", "parameter": {"id": id, "count": count, "application": app},"async": async, "success": success, "failure": failure});
    },



    listTaskPrev: function(success, failure, id, count, async){
        this.actionTask.invoke({"name": "listTaskPrev", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },
    listTaskCompletedNext: function(success, failure, id, count, async){
        this.actionTaskCompleted.invoke({"name": "listTaskCompletedNext", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },
    listTaskCompletedNextByApp: function(success, failure, id, count, app, async){
        this.actionTaskCompleted.invoke({"name": "listTaskCompletedNextByApp", "parameter": {"id": id, "count": count, "application": app},"async": async, "success": success, "failure": failure});
    },

    listTaskCompletedPrev: function(success, failure, id, count, async){
        this.actionTaskCompleted.invoke({"name": "listTaskCompletedPrev", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },

    getJobByTask: function(success, failure, id, async){
        this.actionWork.invoke({"name": "getJobByTask","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    getSimpleJobByTask: function(success, failure, id, async){
        this.actionTask.invoke({"name": "getSimpleJobByTask","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    processTask: function(success, failure, id, data, async){
        this.actionTask.invoke({"name": "processTask","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    listTaskApplication: function(success, failure, async){
        this.actionTask.invoke({"name": "listTaskApplication","async": async, "success": success, "failure": failure});
    },
    listTaskCompletedApplication: function(success, failure, async){
        this.actionTaskCompleted.invoke({"name": "listTaskCompletedApplication","async": async, "success": success, "failure": failure});
    },

    getSimpleJobByTaskCompleted: function(success, failure, id, async){
        this.actionTaskCompleted.invoke({"name": "getSimpleJobByTaskCompleted","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    listTaskCompletedFilterCount: function(success, failure, async){
        this.actionTaskCompleted.invoke({"name": "listTaskCompletedFilterCount","async": async, "success": success, "failure": failure});
    },

    listTaskCompletedFilter: function(success, failure, id, count, data, async){
        this.actionTaskCompleted.invoke({"name": "listTaskCompletedFilter","data": data,"async": async, "parameter": {"id": id, "count": count},"success": success,	"failure": failure});
    },

    listTaskFilterCount: function(success, failure, async){
        this.actionTaskCompleted.invoke({"name": "listTaskFilterCount","async": async, "success": success, "failure": failure});
    },

    listTaskFilter: function(success, failure, id, count, data, async){
        this.actionTaskCompleted.invoke({"name": "listTaskFilter","data": data,"async": async, "parameter": {"id": id, "count": count},"success": success,	"failure": failure});
    },

    listReadApplication: function(success, failure, async){
        this.actionRead.invoke({"name": "listReadApplication","async": async, "success": success, "failure": failure});
    },
    listReadFilter: function(success, failure, id, count, data, async){
        this.actionRead.invoke({"name": "listReadFilter","data": data,"async": async, "parameter": {"id": id, "count": count},"success": success,	"failure": failure});
    },
    listReadNext: function(success, failure, id, count, async){
        this.actionRead.invoke({"name": "listReadNext", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },
    getSimpleJobByRead: function(success, failure, id, async){
        this.actionRead.invoke({"name": "getSimpleJobByRead","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listReadFilterCount: function(success, failure, async){
        this.actionRead.invoke({"name": "listReadFilterCount","async": async, "success": success, "failure": failure});
    },
    setReaded: function(success, failure, id, data, async){
        this.actionRead.invoke({"name": "setReaded","data": data,"async": async, "parameter": {"id": id},"success": success,	"failure": failure});
    },

    listReadedApplication: function(success, failure, async){
        this.actionReaded.invoke({"name": "listReadedApplication","async": async, "success": success, "failure": failure});
    },
    listReadedFilter: function(success, failure, id, count, data, async){
        this.actionReaded.invoke({"name": "listReadedFilter","data": data,"async": async, "parameter": {"id": id, "count": count},"success": success,	"failure": failure});
    },
    listReadedNext: function(success, failure, id, count, async){
        this.actionReaded.invoke({"name": "listReadedNext", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },
    getSimpleJobByReaded: function(success, failure, id, async){
        this.actionReaded.invoke({"name": "getSimpleJobByReaded","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listReadedFilterCount: function(success, failure, async){
        this.actionReaded.invoke({"name": "listReadedFilterCount","async": async, "success": success, "failure": failure});
    },

    listReviewApplication: function(success, failure, async){
        this.actionReview.invoke({"name": "listReviewApplication","async": async, "success": success, "failure": failure});
    },
    listReviewFilter: function(success, failure, id, count, data, async){
        this.actionReview.invoke({"name": "listReviewFilter","data": data,"async": async, "parameter": {"id": id, "count": count},"success": success,	"failure": failure});
    },
    listReviewNext: function(success, failure, id, count, async){
        this.actionReview.invoke({"name": "listReviewNext", "parameter": {"id": id, "count": count},"async": async, "success": success, "failure": failure});
    },
    getSimpleJobByReview: function(success, failure, id, async){
        this.actionReview.invoke({"name": "getSimpleJobByReview","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    listReviewFilterCount: function(success, failure, async){
        this.actionReview.invoke({"name": "listReviewFilterCount","async": async, "success": success, "failure": failure});
    }

});