MWF.xApplication.cms = MWF.xApplication.cms || {};
MWF.xApplication.cms.Column = MWF.xApplication.cms.Column || {};
MWF.xApplication.cms.Column.Actions = MWF.xApplication.cms.Column.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.cms.Column.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_cms_assemble_control", "x_component_cms_Column");
	},
    getId: function(count, success, failure, async){
        this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
    },
    getUUID: function(){
        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"},	"success": function(ids){
            id = ids.data[0];
        },	"failure": null});
        return id;
    },

    listColumn: function( success, failure, async){
        this.action.invoke({"name": "listColumn","async": async, "success": success,	"failure": failure});
    },
    getColumn: function(columnData, success, failure){
        this.action.invoke({"name": "getColumn","parameter": {"id": columnData.id},"success": success,"failure": failure});
    },
    saveColumn: function(columnData, success, failure){
        if (!columnData.isNewColumn){
            this.updateColumn(columnData, success, failure);
        }else{
            this.addColumn(columnData, success, failure);
        }
    },
    addColumn: function(columnData, success, failure){
        this.action.invoke({"name": "addColumn","data": columnData,"success": success,"failure": failure});
    },
    updateColumn: function(columnData, success, failure){
        this.action.invoke({"name": "updateColumn","data": columnData,"parameter": {"id": columnData.id},"success": success,"failure": failure});
    },
    removeColumn: function(id, success, failure, async){
        this.action.invoke({"name": "removeColumn", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    getColumnIcon: function(columnId, success, failure){
        this.action.invoke({"name": "getColumnIcon", "parameter": {"id": columnId },"success": success,"failure": failure});
    },
    updataColumnIcon: function(columnId, success, failure, formData, file){
        this.action.invoke({"name": "updataColumnIcon", "parameter": {"id": columnId},"data": formData,"file": file,"success": success,"failure": failure});
    },

    listControllerByPerson: function(person, success, failure, async){
        this.action.invoke({"name": "listControllerByPerson","async": async, "parameter": {"person": person}, "success": success,	"failure": failure});
    },
    addController: function(data, success, failure,async){
        if (!data.id){
            var id = this.getUUID();
            data.id = id;
            this.action.invoke({"name": "addController","async": async,"data": data, "parameter": {"id": data.id },"success": success,"failure": failure});
        }else{
            this.action.invoke({"name": "addController","async": async,"data": data, "parameter": {"id": data.id },"success": success,"failure": failure});
        }
    }
});