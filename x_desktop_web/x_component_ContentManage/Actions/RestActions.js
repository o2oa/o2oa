MWF.xApplication.ContentManage.Actions = MWF.xApplication.ContentManage.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.ContentManage.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_contentmanage_assemble_control", "x_component_ContentManage");
	},
	addNote: function(data, success, failure, async){
		this.action.invoke({"name": "addNote","async": async, "data": data, "success": success,	"failure": failure});
	},
    deleteNote: function(id, success, failure, async){
        this.action.invoke({"name": "deleteNote","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    updateNote: function(id, data, success, failure, async){
        this.action.invoke({"name": "updateNote","async": async, "data": data, "parameter": {"id": id}, "success": success,	"failure": failure});
    }
});