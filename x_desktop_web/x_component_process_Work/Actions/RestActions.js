MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.Work = MWF.xApplication.process.Work || {};
MWF.xApplication.process.Work.Actions = MWF.xApplication.process.Work.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.process.Work.Actions.RestActions = new Class({
    initialize: function(){
        this.actionPath = "/x_component_process_Work/Actions/action.json";

        this.actionWork = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_Work");
        this.actionWork.getActions = function(callback){
            this.getActionActions(this.actionWork, callback);
        }.bind(this);

        this.actionProcess = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_Work");
        this.actionProcess.getActions = function(callback){
            this.getActionActions(this.actionProcess, callback);
        }.bind(this);

        this.actionWorkCompleted = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_Work");
        this.actionWorkCompleted.getActions = function(callback){
            this.getActionActions(this.actionWorkCompleted, callback);
        }.bind(this);

        this.actionData = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_Work");
        this.actionData.getActions = function(callback){
            this.getActionActions(this.actionData, callback);
        }.bind(this);

        this.actionTask = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_Work");
        this.actionTask.getActions = function(callback){
            this.getActionActions(this.actionTask, callback);
        }.bind(this);

        this.actionAttachment = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "x_component_process_Work");
        this.actionAttachment.getActions = function(callback){
            this.getActionActions(this.actionAttachment, callback);
        }.bind(this);

        this.actionDesigner = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_designer", "x_component_process_Work");
        this.actionDesigner.getActions = function(callback){
            this.getActionActions(this.actionDesigner, callback);
        }.bind(this);

        this.actionView = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface_lookup", "x_component_process_Work");
        this.actionView.getActions = function(callback){
            this.getActionActions(this.actionView, callback);
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
    getJobByTask: function(success, failure, id, async){
        this.actionWork.invoke({"name": "getJobByTask","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getJobByWork: function(success, failure, id, async){
        this.actionWork.invoke({"name": "getJobByWork","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getJobByWorkAssignForm: function(success, failure, id, application, form, async){
        this.actionWork.invoke({"name": "getJobByWorkAssignForm","async": async, "parameter": {"id": id, "applicationTag": application, "formTag": form}, "success": success, "failure": failure});
    },
    getJobByWorkCompletedAssignForm: function(success, failure, id, application, form, async){
        this.actionWork.invoke({"name": "getJobByWorkCompletedAssignForm","async": async, "parameter": {"id": id, "applicationTag": application, "formTag": form}, "success": success, "failure": failure});
    },

    getJobByWorkMobile: function(success, failure, id, async){
        this.actionWork.invoke({"name": "getJobByWorkMobile","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getJobByWorkAssignFormMobile: function(success, failure, id, application, form, async){
        this.actionWork.invoke({"name": "getJobByWorkAssignFormMobile","async": async, "parameter": {"id": id, "applicationTag": application, "formTag": form}, "success": success, "failure": failure});
    },
    getJobByWorkCompletedAssignFormMobile: function(success, failure, id, application, form, async){
        this.actionWork.invoke({"name": "getJobByWorkCompletedAssignFormMobile","async": async, "parameter": {"id": id, "applicationTag": application, "formTag": form}, "success": success, "failure": failure});
    },

    rerouteWork: function(success, failure, id, activityId, type, async){
        this.actionWork.invoke({"name": "rerouteWork","async": async, "data": null, "parameter": {"id": id, "activityId": activityId, "type": type},	"success": success,	"failure": failure});
    },
    retractWork: function(success, failure, id, async){
        this.actionWork.invoke({"name": "retractWork","async": async, "data": null, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    deleteWork: function(success, failure, id, async){
        this.actionWork.invoke({"name": "deleteWork","async": async, "data": null, "parameter": {"id": id},	"success": success,	"failure": failure});
    },


    getJobByWorkCompleted: function(success, failure, id, async){
        this.actionWorkCompleted.invoke({"name": "getJobByWorkCompleted","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getJobByWorkCompletedMobile: function(success, failure, id, async){
        this.actionWorkCompleted.invoke({"name": "getJobByWorkCompletedMobile","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    saveData: function(success, failure, id, data, async){
        this.actionData.invoke({"name": "saveData","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    processTask: function(success, failure, id, data, async){
        this.actionTask.invoke({"name": "processTask","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    resetWork: function(success, failure, id, data, async){
        this.actionTask.invoke({"name": "resetWork","async": async, "data": data, "parameter": {"id": id},	"success": success,	"failure": failure});
    },


    uploadAttachment: function(id, success, failure, formData, file){
        this.actionAttachment.invoke({"name": "uploadAttachment", "parameter": {"id": id},"data": formData,"file": file,"success": success,"failure": failure});
    },
    replaceAttachment: function(id, workid, success, failure, formData, file){
        this.actionAttachment.invoke({"name": "replaceAttachment", "parameter": {"workid": workid, "id": id},"data": formData,"file": file,"success": success,"failure": failure});
    },
    getAttachment: function(id, workid, success, failure, async){
        this.actionAttachment.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "workid": workid},	"success": success,	"failure": failure});
    },
    getAttachmentWorkcompleted: function(id, workCompletedId, success, failure, async){
        this.actionAttachment.invoke({"name": "getAttachmentWorkcompleted","async": async, "parameter": {"id": id, "workCompletedId": workCompletedId},	"success": success,	"failure": failure});
    },
    deleteAttachment: function(id, workid, success, failure, async){
        this.actionAttachment.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id, "workid": workid},	"success": success,	"failure": failure});
    },

    getView: function(id, workid, success, failure, async){
        this.actionDesigner.invoke({"name": "getView","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getForm: function(id, success, failure, async){
        this.actionDesigner.invoke({"name": "getForm","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    lookupView: function(id, workid, success, failure, async){
        this.actionView.invoke({"name": "lookupView","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    getRerouteTo: function(flag, success, failure, async){
        this.actionProcess.invoke({"name": "getRerouteTo","async": async, "parameter": {"flag": flag},	"success": success,	"failure": failure});
    },

    getAttachmentData: function(id, workid){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workid}", encodeURIComponent(workid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getWorkcompletedAttachmentData: function(id, workid){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getWorkcompletedAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workCompletedId}", encodeURIComponent(workid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getAttachmentStream: function(id, workid){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workid}", encodeURIComponent(workid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getWorkcompletedAttachmentStream: function(id, workid){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getWorkcompletedAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workCompletedId}", encodeURIComponent(workid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },

    getAttachmentUrl: function(id, workid, callback){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workid}", encodeURIComponent(workid));
            if (callback) callback(this.actionAttachment.address+url);
        }.bind(this));
    },
    getAttachmentWorkcompletedUrl: function(id, workid, callback){
        this.actionAttachment.getActions(function(){
            var url = this.actionAttachment.actions.getWorkcompletedAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workCompletedId}", encodeURIComponent(workid));
            if (callback) callback(this.actionAttachment.address+url);
        }.bind(this));
    }

});