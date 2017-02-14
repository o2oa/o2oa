MWF.xApplication.Template = MWF.xApplication.Template || {};
MWF.xApplication.Template.Actions = MWF.xApplication.Template.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.Template.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_attendance_assemble_control", "x_component_Template");

        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_Template");
	},
	getId: function(count, success, failure, async){
		this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
	},
    getUUID: function(success){

        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0];
            if (success) success(id);
        },	"failure": null});
        return id;
    },


    listCompanyByPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "listCompanyByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "listDepartmentByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByCompany: function(success, failure, companyName, async){
        this.actionOrg.invoke({"name": "listDepartmentByCompany","async": async, "parameter": {"companyName": companyName},	"success": success,	"failure": failure});
    },

    getHoliday: function(id, success, failure){
        this.action.invoke({"name": "getHoliday", "parameter": {"id": id },"success": success,"failure": failure});
    },
    listHolidayAll: function(success, failure, async){
        this.action.invoke({"name": "listHolidayAll","async": async, "success": success,	"failure": failure});
    },
    listHolidayFilter : function( filterData, success,failure, async){
        this.action.invoke({"name": "listHolidayFilter", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    saveHoliday: function(data, success, failure, async){
        this.action.invoke({"name": "saveHoliday","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteHoliday: function(id, success, failure, async){
        this.action.invoke({"name": "deleteHoliday", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    listAttachmentInfo: function(success, failure, async){
        this.action.invoke({"name": "listAttachmentInfo","async": async, "success": success,	"failure": failure});
    },
    uploadAttachment: function(success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "data": formData,"file": file,"success": success,"failure": failure});
    },
    deleteAttachment: function(id, success, failure, async){
        this.action.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    getAttachmentStream: function(id){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(this.action.address+url);
        }.bind(this));
    },

    listDetailFilterNext : function( id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailFilterPrev : function(  id, count, filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    deployBaseWork : function(data, success, failure, async){
        this.action.invoke({"name": "deployBaseWork","data": data, "async": async,"success": success,"failure": failure});
    },

    listAttachment: function(documentid, success, failure, async){
        this.action.invoke({"name": "listAttachment","async": async, "parameter": {"documentid": documentid},	"success": success,	"failure": failure});
    },
    uploadAttachment: function(documentid, success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "parameter": {"documentid": documentid},"data": formData,"file": file,"success": success,"failure": failure});
    },
    //replaceAttachment: function(id, documentid, success, failure, formData, file){
    //    this.action.invoke({"name": "replaceAttachment", "parameter": {"documentid": documentid, "id": id},"data": formData,"file": file,"success": success,"failure": failure});
    //},
    getAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    deleteAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    getAttachmentData: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getAttachmentStream: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },

    getAttachmentUrl: function(id, documentid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    }

});