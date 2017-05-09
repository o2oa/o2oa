MWF.xApplication.portal.PortalManager = MWF.xApplication.portal.PortalManager || {};
MWF.xApplication.portal.PortalManager.Actions = MWF.xApplication.portal.PortalManager.Actions || {};
MWF.xDesktop.requireApp("portal.PortalManager", "package", null, false);
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.portal.PortalManager.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_portal_assemble_designer", "x_component_portal_PortalManager");
	},
	getId: function(count, success, failure, async){
		this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
	},
    getUUID: function(success){
        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0].id;
            if (success) success(id);
        },	"failure": null});
        return id;
    },
    listApplication: function(success, failure, async){
            this.action.invoke({"name": "listApplication","async": async, "success": success,	"failure": failure});
    },
    getApplication: function(application, success, failure, async){
        this.action.invoke({"name": "getApplication","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
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

    changeApplicationIcon: function(applicationId, success, failure, formData, file){
        this.action.invoke({"name": "updataApplicationIcon", "parameter": {"id": applicationId},"data": formData,"file": file,"success": success,"failure": failure});
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

    listPageTemplate: function(success, failure, async){
        this.action.invoke({"name": "listPageTemplate","async": async, "success": success,	"failure": failure});
    },
    listPageTemplateCategory: function(success, failure, async){
        this.action.invoke({"name": "listPageTemplateCategory","async": async, "success": success,	"failure": failure});
    },
    listPageTemplateByCategory: function(success, failure, async){
        this.action.invoke({"name": "listPageTemplateByCategory","async": async, "success": success,	"failure": failure});
    },
	

    getPage: function(id, success, failure, async){
		this.action.invoke({"name": "getPage","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
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
    },
    getPageTemplate: function(id, success, failure, async){
        this.action.invoke({"name": "getPageTemplate","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
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

    addPageTemplate: function(pageData, mobileData, templateData, success, failure){
        var data, mobileDataStr;
        debugger;
        this.getUUID(function(id){
            templateData.id = id;
            if (pageData){
                var tPageData = Object.clone(pageData);
                tPageData.json.id = id;
                tPageData.json.name = templateData.name;
                data = MWF.encodeJsonString(JSON.encode(tPageData));
            }
            if (mobileData){
                var tMobileData = Object.clone(mobileData);
                tMobileData.json.id = id;
                tMobileData.json.name = templateData.name;
                mobileDataStr = MWF.encodeJsonString(JSON.encode(tMobileData));
            }

            if (pageData) templateData.data = data;
            if (mobileData) templateData.mobileData = mobileDataStr;
            this.action.invoke({"name": "addPageTemplate","data": templateData, "success": success,"failure": failure});
        }.bind(this));
    },

    deletePageTemplate: function(id, success, failure, async){
        this.action.invoke({"name": "deletePageTemplate", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    saveSource: function(sourceData, success, failure){
        if (!sourceData.isNewSource){
            this.updateSource(sourceData, success, failure);
        }else{
            this.addSource(sourceData, success, failure);
        }
    },
    updateSource: function(sourceData, success, failure){
        var data =sourceData.data;
        sourceData.data = JSON.encode(sourceData.data);
        this.action.invoke({"name": "updataSource","data": sourceData,"parameter": {"id": sourceData.id},"success": success,"failure": failure});
        sourceData.data = data;
    },
    addSource: function(sourceData, success, failure){
        var data =sourceData.data;
        sourceData.data = JSON.encode(sourceData.data);
        if (!data.id){
            this.getUUID(function(id){
                sourceData.id = id;
                this.action.invoke({"name": "addSource","data": sourceData, "success": success,"failure": failure});
                sourceData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addSource","data": sourceData, "success": success,"failure": failure});
            sourceData.data = data;
        }
    },
    deleteSource: function(id, success, failure, async){
        this.action.invoke({"name": "removeSource", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },


    saveMenu: function(data, success, failure){
        if (!data.isNewMenu){
            this.updateMenu(data, success, failure);
        }else{
            this.addStat(data, success, failure);
        }
    },
    updateMenu: function(menuData, success, failure){
        var data =menuData.data;
        menuData.data = JSON.encode(menuData.data);
        this.action.invoke({"name": "updataMenu","data": menuData,"parameter": {"id": statData.id},"success": success,"failure": failure});
        menuData.data = data;
    },
    addMenu: function(menuData, success, failure){
        var data =menuData.data;
        menuData.data = JSON.encode(menuData.data);
        if (!data.id){
            this.getUUID(function(id){
                menuData.id = id;
                this.action.invoke({"name": "addMenu","data": menuData, "success": success,"failure": failure});
                menuData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addMenu","data": menuData, "success": success,"failure": failure});
            menuData.data = data;
        }
    },
    deleteMenu: function(id, success, failure, async){
        this.action.invoke({"name": "removeMenu", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
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