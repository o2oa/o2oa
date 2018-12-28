MWF.xApplication.MinderEditor = MWF.xApplication.MinderEditor || {};
MWF.xApplication.MinderEditor.Actions = MWF.xApplication.MinderEditor.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.MinderEditor.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_attendance_assemble_control", "x_component_Minder");
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
    }

});