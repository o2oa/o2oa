MWF.xApplication.HotArticle = MWF.xApplication.HotArticle || {};
MWF.xApplication.HotArticle.Actions = MWF.xApplication.HotArticle.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.HotArticle.Actions.RestActions = new Class({
	initialize: function(){
        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_HotArticle");

        this.actionHotPic = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_hotpic_assemble_control", "x_component_HotArticle");
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

    getPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "getPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    getHotPic: function(application, infoId , success, failure, async){
        this.actionHotPic.invoke({"name": "getHotPic", "parameter": {"application": application, "infoId" : infoId },"success": success,"failure": failure, "async": async});
    },
    saveHotPic: function(data, success, failure, async){
        this.actionHotPic.invoke({"name": "saveHotPic", data : data, "success": success,"failure": failure, "async": async});
    },
    removeHotPic: function(id, success, failure, async){
        this.actionHotPic.invoke({"name": "removeHotPic", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    removeHotPicByInfor: function(application, infoId , success, failure){
        this.actionHotPic.invoke({"name": "removeHotPicByInfor", "parameter": {"application": application, "infoId" : infoId },"success": success,"failure": failure});
    },
    listHotPicFilterPage : function(page, count,  filterData, success,failure, async){
        this.actionHotPic.invoke({"name": "listHotPicFilterPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    }

});