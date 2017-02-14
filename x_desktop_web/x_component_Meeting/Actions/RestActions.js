MWF.xApplication.Meeting = MWF.xApplication.Meeting || {};
MWF.xApplication.Meeting.Actions = MWF.xApplication.Meeting.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.Meeting.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_meeting_assemble_control", "x_component_Meeting");
	},


    listBuilding: function(success, failure, async){
        this.action.invoke({"name": "listBuilding","async": async, "success": success,	"failure": failure});
    },
    listBuildingByRange: function(start, completed, success, failure, async){
        this.action.invoke({"name": "listBuildingByRange","async": async, "parameter": {"start": start, "completed": completed}, "success": success,	"failure": failure});
    },

    listBuildingByPinyin: function(key, success, failure, async){
        this.action.invoke({"name": "listBuildingByPinyin","async": async, "parameter": {"key": key}, "success": success,	"failure": failure});
    },
    listBuildingByKey: function(key, success, failure, async){
        if (!key){
            this.action.invoke({"name": "listBuilding","async": async, "success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "listBuildingByKey","async": async, "parameter": {"key": key}, "success": success,	"failure": failure});
        }
    },
    listBuildingByPinyininitial: function(key, success, failure, async){
        this.action.invoke({"name": "listBuildingByPinyininitial","async": async, "parameter": {"key": key}, "success": success,	"failure": failure});
    },

    getBuilding: function(id, success, failure, async){
        this.action.invoke({"name": "getBuilding","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    deleteBuilding: function(id, success, failure, async){
        this.action.invoke({"name": "removeBuilding", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    saveBuilding: function(data, success, failure, async){
        if (data.id){
            this.updateBuilding(data, success, failure, async);
        }else{
            this.addBuilding(data, success, failure, async);
        }
    },
    updateBuilding: function(data, success, failure, async){
        this.action.invoke({"name": "updateBuilding", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },
    addBuilding: function(data, success, failure, async){
        this.action.invoke({"name": "addBuilding", "async": async, "data": data, "success": success, "failure": failure});
    },

    listRoomByPinyin: function(key, success, failure, async){
        this.action.invoke({"name": "listRoomByPinyin","async": async, "parameter": {"key": key}, "success": success,	"failure": failure});
    },
    listRoomByKey: function(key, success, failure, async){
        this.action.invoke({"name": "listRoomByKey","async": async, "parameter": {"key": key}, "success": success,	"failure": failure});
    },
    listRoomByPinyininitial: function(key, success, failure, async){
        this.action.invoke({"name": "listRoomByPinyininitial","async": async, "parameter": {"key": key}, "success": success,	"failure": failure});
    },

    getRoom: function(id, success, failure, async){
        this.action.invoke({"name": "getRoom","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    deleteRoom: function(id, success, failure, async){
        this.action.invoke({"name": "removeRoom", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    saveRoom: function(data, success, failure, async){
        if (data.id){
            this.updateRoom(data, success, failure, async);
        }else{
            this.addRoom(data, success, failure, async);
        }
    },
    updateRoom: function(data, success, failure, async){
        this.action.invoke({"name": "updateRoom", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },
    addRoom: function(data, success, failure, async){
        this.action.invoke({"name": "addRoom", "async": async, "data": data, "success": success, "failure": failure});
    },

    listMeetingApplyCompleted: function(success, failure, async){
        this.action.invoke({"name": "listMeetingApplyCompleted","async": async, "success": success,	"failure": failure});
    },
    listMeetingApplyProcessing: function(success, failure, async){
        this.action.invoke({"name": "listMeetingApplyProcessing","async": async, "success": success,	"failure": failure});
    },
    listMeetingApplyWait: function(success, failure, async){
        this.action.invoke({"name": "listMeetingApplyWait","async": async, "success": success,	"failure": failure});
    },


    listMeetingInvitedWait: function(success, failure, async){
        this.action.invoke({"name": "listMeetingInvitedWait","async": async, "success": success,	"failure": failure});
    },
    listMeetingInvitedCompleted: function(success, failure, async){
        this.action.invoke({"name": "listMeetingInvitedCompleted","async": async, "success": success,	"failure": failure});
    },
    listMeetingInvitedProcessing: function(success, failure, async){
        this.action.invoke({"name": "listMeetingInvitedProcessing","async": async, "success": success,	"failure": failure});
    },
    listMeetingInvitedRejected: function(success, failure, async){
        this.action.invoke({"name": "listMeetingInvitedRejected","async": async, "success": success,	"failure": failure});
    },



    listMeetingDays: function(count, success, failure, async){
        this.action.invoke({"name": "listMeetingDays","async": async, "parameter": {"count": count}, "success": success,	"failure": failure});
    },
    listMeetingMonths: function(count, success, failure, async){
        this.action.invoke({"name": "listMeetingMonths","async": async, "parameter": {"count": count}, "success": success,	"failure": failure});
    },
    listMeetingWaitAccept: function(success, failure, async){
        this.action.invoke({"name": "listMeetingWaitAccept","async": async, "success": success,	"failure": failure});
    },
    listMeetingWaitConfirm: function(success, failure, async){
        this.action.invoke({"name": "listMeetingWaitConfirm","async": async, "success": success,	"failure": failure});
    },

    listMeetingMonth: function(year, month, success, failure, async){
        this.action.invoke({"name": "listMeetingMonth","async": async, "parameter": {"year": year, "month": month}, "success": success,	"failure": failure});
    },
    listMeetingDay: function(year, month, day, success, failure, async){
        this.action.invoke({"name": "listMeetingDay","async": async, "parameter": {"year": year, "month": month, "day": day}, "success": success,	"failure": failure});
    },

    listMeetingNext: function(id, count, success, failure, async){
        this.action.invoke({"name": "listMeetingNext","async": async, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },
    listMeetingPrev: function(id, count, success, failure, async){
        this.action.invoke({"name": "listMeetingPrev","async": async, "parameter": {"id": id, "count": count}, "success": success,	"failure": failure});
    },


    saveMeeting: function(data, success, failure, async){
        if (data.id){
            this.updateMeeting(data, success, failure, async);
        }else{
            this.addMeeting(data, success, failure, async);
        }
    },
    updateMeeting: function(data, success, failure, async){
        this.action.invoke({"name": "updateMeeting", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },
    addMeeting: function(data, success, failure, async){
        this.action.invoke({"name": "addMeeting", "async": async, "data": data, "success": success, "failure": failure});
    },

    getMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "getMeeting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    deleteMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "removeMeeting", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    acceptMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "acceptMeeting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    rejectMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "rejectMeeting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    addMeetingInvite: function(data, success, failure, async){
        this.action.invoke({"name": "addMeetingInvite", "async": async, "data": data, "parameter": {"id": data.id}, "success": success, "failure": failure});
    },

    allowMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "allowMeeting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    denyMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "denyMeeting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    completedMeeting: function(id, success, failure, async){
        this.action.invoke({"name": "completedMeeting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },

    addAttachment: function(success, failure, formData, meeting, file){
        this.action.invoke({"name": "addAttachment","data": formData, "parameter": {"id": meeting},"file": file,"success": success,"failure": failure});
    },
    getAttachment: function(id, success, failure, async){
        this.action.invoke({"name": "getAttachment","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },

    deleteFile: function(id, success, failure){
        this.action.invoke({
            "name": "removeAttachment",
            "parameter": {"id": id},
            "success": success,
            "failure": failure
        });
    },
    getFileUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },
    getFileDownloadUrl: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },
    getFile: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(this.action.address+url);
        }.bind(this));
    },
    getFileDownload: function(id, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(this.action.address+url);
        }.bind(this));
    }



});