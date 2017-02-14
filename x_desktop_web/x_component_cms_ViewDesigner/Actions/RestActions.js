MWF.xApplication.cms.Explorer = MWF.xApplication.cms.Explorer || {};
MWF.xApplication.cms.Explorer.Actions = MWF.xApplication.cms.Explorer.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.cms.Explorer.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_cms_assemble_control", "x_component_cms_ViewDesign");
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

    listView: function(id, count, catagoryId, success, failure, async){
        this.action.invoke({"name": "listView","async": async, "parameter": {"id": id, "count": count, "catagoryId": catagoryId}, "success": success,	"failure": failure});
    },
    getView: function(docId, data, success, failure){
        this.action.invoke({"name": "getView", "parameter": {"id": docId },"success": success,"failure": failure});
    },
    saveView: function(viewData, success, failure){
        if (!viewData.isNew){
            this.updateView(viewData, success, failure);
        }else{
            this.addView(viewData, success, failure);
        }
    },
    addView: function(viewData, success, failure){
        this.action.invoke({"name": "addView","data": viewData,"success": success,"failure": failure});
    },
    updateView: function(viewData, success, failure){
        this.action.invoke({"name": "updateView","data": viewData,"parameter": {"id": viewData.id},"success": success,"failure": failure});
    },
    removeView: function(id, success, failure, async){
        this.action.invoke({"name": "removeView", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    }

	
});