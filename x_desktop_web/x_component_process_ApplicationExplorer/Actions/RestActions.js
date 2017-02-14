MWF.xApplication.process = MWF.xApplication.process || {};
MWF.xApplication.process.ApplicationExplorer = MWF.xApplication.process.ApplicationExplorer || {};
MWF.xApplication.process.ApplicationExplorer.Actions = MWF.xApplication.process.ApplicationExplorer.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xApplication.process.ApplicationExplorer.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_processplatform_assemble_designer", "x_component_process_ApplicationExplorer");
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

    listProcess: function(application, success, failure, async){
        this.action.invoke({"name": "listProcess","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
    },
    listForm: function(application, success, failure, async){
        this.action.invoke({"name": "listForm","async": async, "parameter": {"id": application}, "success": success, "failure": failure});
    },
    listDictionary: function(application, success, failure, async){
        this.action.invoke({"name": "listDictionary","async": async, "parameter": {"id": application}, "success": success, "failure": failure});
    },
    listScript: function(application, success, failure, async){
        this.action.invoke({"name": "listScript","async": async, "parameter": {"id": application}, "success": success, "failure": failure});
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



    listFormCategory: function(lastId, count, success, failure, async){
        this.action.invoke({"name": "listFormCategory","async": async, "parameter": {"id": lastId || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    listProcessCategory: function(lastId, count, success, failure, async){
        this.action.invoke({"name": "listProcessCategory","async": async, "parameter": {"id": lastId || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    //listForm: function(category, last, count, success, failure, async){
    //	this.action.invoke({"name": "listForm","async": async, "parameter": {"cid": category, "id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    //},
    //listProcess: function(category, last, count, success, failure, async){
    //	this.action.invoke({"name": "listProcess","async": async, "parameter": {"cid": category, "id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    //},
    getDictionary: function(id, success, failure, async){
        this.action.invoke({"name": "getDictionary","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getScript: function(id, success, failure, async){
        this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    getForm: function(form, success, failure, async){
        this.action.invoke({"name": "getForm","async": async, "parameter": {"id": form},	"success": success,	"failure": failure});
    },
    getProcess: function(process, success, failure, async){
        this.action.invoke({"name": "getProcess","async": async, "parameter": {"id": process},	"success": success,	"failure": failure});
    },
    getProcessCategory: function(id, success, failure, async){
        this.action.invoke({"name": "getProcessCategory","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getFormCategory: function(id, success, failure, async){
        this.action.invoke({"name": "getFormCategory","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    saveProcessCategory: function(categoryData, success, failure){
        if (categoryData.id){
            this.updateProcessCategory(categoryData, success, failure);
        }else{
            this.addProcessCategory(categoryData, success, failure);
        }
    },
    updateProcessCategory: function(categoryData, success, failure){
        this.action.invoke({"name": "updataProcessCategory","data": categoryData,"parameter": {"id": categoryData.id},"success": success,"failure": failure});
    },
    addProcessCategory: function(categoryData, success, failure){
        this.getId(1, function(json){
            categoryData.id = json.data[0].id;
            this.action.invoke({"name": "addProcessCategory","data": categoryData,"success": success,"failure": failure});
        }.bind(this));
    },

    saveProcess: function(processData, success, failure){
        if (!processData.isNewProcess){
            this.updateProcess(processData, success, failure);
        }else{
            this.addProcess(processData, success, failure);
        }
    },
    addProcess: function(processData, success, failure){
        this.action.invoke({"name": "addProcess","data": processData,"parameter": {"id": processData.categoryId},"success": success,"failure": failure});
    },
    updateProcess: function(processData, success, failure){
        this.action.invoke({"name": "updataProcess","data": processData,"parameter": {"id": processData.id},"success": success,"failure": failure});
    },

    deleteProcess: function(id, success, failure, async){
        this.action.invoke({"name": "removeProcess", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },



    saveForm: function(formData, success, failure){
        if (!formData.isNewForm){
            this.updateForm(formData, success, failure);
        }else{
            this.addForm(formData, success, failure);
        }
    },
    updateForm: function(formData, success, failure){
        var data = MWF.encodeJsonString(JSON.encode(formData));
        var json = {
            "id": formData.json.id,
            "data": data,
            "name": formData.json.name,
            "alias": formData.json.name,
            "description": formData.json.description,
            "application": formData.json.application
        };
        this.action.invoke({"name": "updataForm","data": json,"parameter": {"id": formData.json.id},"success": success,"failure": failure});
    },
    addForm: function(formData, success, failure){
        var data = MWF.encodeJsonString(JSON.encode(formData));
        var json = {
            "id": formData.json.id,
            "data": data,
            "name": formData.json.name,
            "alias": formData.json.name,
            "description": formData.json.description,
            "application": formData.json.application
        };
        this.action.invoke({"name": "addForm","data": json, "parameter": {"id": formData.json.categoryId}, "success": success,"failure": failure});
    },
    deleteForm: function(id, success, failure, async){
        this.action.invoke({"name": "removeForm", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    saveDictionary: function(data, success, failure){
        if (data.id){
            this.updateDictionary(data, success, failure);
        }else{
            this.addDictionary(data, success, failure);
        }
    },
    updateDictionary: function(data, success, failure){
        this.action.invoke({"name": "updataDictionary","data": data,"parameter": {"applicationDict": data.id},"success": success,"failure": failure});
    },
    addDictionary: function(data, success, failure){
    //    if (!data.id){
    //        this.getUUID(function(id){
    //            data.id = id;
                this.action.invoke({"name": "addDictionary","data": data,"success": success,"failure": failure});
    //        }.bind(this));
    //    }
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
        //    this.getUUID(function(id){
        //        data.id = id;
        this.action.invoke({"name": "addScript","data": data,"success": success,"failure": failure});
        //    }.bind(this));
        //}
    }
	
});