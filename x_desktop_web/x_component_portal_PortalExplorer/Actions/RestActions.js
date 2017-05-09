MWF.xApplication.portal = MWF.xApplication.portal || {};
MWF.xApplication.portal.PortalExplorer = MWF.xApplication.portal.PortalExplorer || {};
MWF.xApplication.portal.PortalExplorer.Actions = MWF.xApplication.portal.PortalExplorer.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.portal.PortalExplorer.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_portal_assemble_designer", "x_component_portal_PortalExplorer");
	},
	getId: function(count, success, failure, async){
		this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
	},
	getUUID: function(){
		var id = "";
		this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"},	"success": function(ids){
			id = ids.data[0].id;
		},	"failure": null});
		return id;
	},

    listApplication: function(categoryName, success, failure, async){
        if (categoryName){
            this.action.invoke({"name": "listApplicationByCategory","async": async, "parameter": {"applicationCategory": categoryName}, "success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "listApplication","async": async, "success": success,	"failure": failure});
        }
    },
    listApplicationSummary: function(categoryName, success, failure, async){
        if (categoryName){
            this.action.invoke({"name": "listApplicationByCategorySummary","async": async, "parameter": {"applicationCategory": categoryName}, "success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "listApplicationSummary","async": async, "success": success,	"failure": failure});
        }
    },
    listApplicationCategory: function(success, failure, async){
        this.action.invoke({"name": "listApplicationCategory","async": async, "success": success,	"failure": failure});
    },

    saveApplication: function(applicationData, success, failure){
        if (applicationData.id){
            this.updateApplication(applicationData, success, failure);
        }else{
            this.addApplication(applicationData, success, failure);
        }
    },
    updateApplication: function(applicationData, success, failure){
        this.action.invoke({"name": "updataApplication","data": applicationData,"parameter": {"id": applicationData.id},"success": success,"failure": failure});
    },
    addApplication: function(applicationData, success, failure){
        this.getId(1, function(json){
            applicationData.id = json.data[0].id;
            this.action.invoke({"name": "addApplication","data": applicationData,"success": success,"failure": failure});
        }.bind(this));
    },

    getApplication: function(application, success, failure, async){
        this.action.invoke({"name": "getApplication","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
    },

    deleteApplication: function(id, onlyRemoveNotCompleted, success, failure, async){
        this.action.invoke({"name": "removeApplication", "async": async, "parameter": {"id": id, "onlyRemoveNotCompleted": onlyRemoveNotCompleted}, "success": success, "failure": failure});
    },
    listPage: function(application, success, failure, async){
        this.action.invoke({"name": "listPage","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
    },
    listScript: function(application, success, failure, async){
        this.action.invoke({"name": "listScript","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },

    getPage: function(id, success, failure, async){
        this.action.invoke({"name": "getPage","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getScript: function(id, success, failure, async){
        this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    savePage: function(pageData, mobileData, fieldList, success, failure){
        if (!pageData.isNewPage){
            this.updatePage(pageData, mobileData, fieldList, success, failure);
        }else{
            this.addPage(pageData, mobileData, fieldList, success, failure);
        }
    },
    updatePage: function(pageData, mobileData, fieldList, success, failure){
        var data, mobileDataStr;
        if (pageData) data = MWF.encodeJsonString(JSON.encode(pageData));
        if (mobileData) mobileDataStr = MWF.encodeJsonString(JSON.encode(mobileData));
        var json = {
            "id": pageData.json.id,
            "name": pageData.json.name,
            "alias": pageData.json.name,
            "description": pageData.json.description,
            "portal": pageData.json.application,
            "icon": pageData.json.formIcon,
            "formFieldList": fieldList
        };
        if (pageData) json.data = data;
        if (mobileData) json.mobileData = mobileDataStr;

        this.action.invoke({"name": "updatePage","data": json,"parameter": {"id": pageData.json.id},"success": success,"failure": failure});
    },
    addPage: function(pageData, mobileData, fieldList, success, failure){
        var data, mobileDataStr;
        if (!pageData.json.id){
            this.getUUID(function(id){
                pageData.json.id = id;

                if (pageData) data = MWF.encodeJsonString(JSON.encode(pageData));
                if (mobileData) mobileDataStr = MWF.encodeJsonString(JSON.encode(mobileData));

                var json = {
                    "id": pageData.json.id,
                    "name": pageData.json.name,
                    "alias": pageData.json.name,
                    "description": pageData.json.description,
                    "portal": pageData.json.application,
                    "icon": pageData.json.formIcon,
                    "formFieldList": fieldList
                };

                if (pageData) json.data = data;
                if (mobileData) json.mobileData = mobileDataStr;
                this.action.invoke({"name": "addPage","data": json, "parameter": {"id": pageData.json.categoryId}, "success": success,"failure": failure});
            }.bind(this));
        }else{
            if (pageData) data = MWF.encodeJsonString(JSON.encode(pageData));
            if (mobileData) mobileData = MWF.encodeJsonString(JSON.encode(mobileData));

            var json = {
                "id": pageData.json.id,
                "name": pageData.json.name,
                "alias": pageData.json.name,
                "description": pageData.json.description,
                "portal": pageData.json.application
            };
            if (pageData) json.data = data;
            if (mobileData) json.mobileData = mobileData;
            this.action.invoke({"name": "addPage","data": json, "parameter": {"id": pageData.json.categoryId}, "success": success,"failure": failure});
        }
    },
    deletePage: function(id, success, failure, async){
        this.action.invoke({"name": "removePage", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    saveScript: function(data, success, failure){
        if (!data.isNewScript){
            this.updateScript(data, success, failure);
        }else{
            this.addScript(data, success, failure);
        }
    },
    updateScript: function(data, success, failure){
        this.action.invoke({"name": "updataScript","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    addScript: function(data, success, failure){
        //if (!data.id){
        //    thi.getUUID(function(id){
        //        data.id = id;
        data.portal = data.application;
        this.action.invoke({"name": "addScript","data": data,"success": success,"failure": failure});
        //    }.bind(this));
        //}
    },
    deleteScript: function(id, success, failure, async){
        this.action.invoke({"name": "removeScript", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    }
});