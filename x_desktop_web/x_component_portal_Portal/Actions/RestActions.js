MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.Portal.Actions = MWF.xApplication.portal.Portal.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.portal.Portal.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_portal_assemble_surface", "x_component_portal_Portal");
	},
    listApplication: function(categoryName, success, failure, async){
        if (categoryName){
            this.action.invoke({"name": "listApplicationByCategory","async": async, "parameter": {"applicationCategory": categoryName}, "success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "listApplication","async": async, "success": success,	"failure": failure});
        }
    },
    getApplication: function(application, success, failure, async){
        this.action.invoke({"name": "getApplication","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
    },

    listPage: function(application, success, failure, async){
        this.action.invoke({"name": "listPage","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
    },
    listSource: function(application, success, failure, async){
        this.action.invoke({"name": "listSource","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listScript: function(application, success, failure, async){
        this.action.invoke({"name": "listScript","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listMenu: function(application, success, failure, async){
        this.action.invoke({"name": "listMenu","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },

    getPage: function(id, success, failure, async){
		this.action.invoke({"name": "getPage","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
	},
    getPageByName: function(name, id, success, failure, async){
        this.action.invoke({"name": "getPageByName","async": async, "parameter": {"name": name, "id": id},	"success": success,	"failure": failure});
    },
    getSource: function(id, success, failure, async){
        this.action.invoke({"name": "getSource","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getMenu: function(id, success, failure, async){
        this.action.invoke({"name": "getMenu","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getScript: function(id, success, failure, async){
        this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getScriptByName: function(name, application, success, failure, async){
        this.action.invoke({"name": "getScriptByName","async": async, "parameter": {"name": name, "applicationId": application},	"success": success,	"failure": failure});
    }
});