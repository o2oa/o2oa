 MWF.xAction.RestActions.Action["x_processplatform_assemble_designer"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    getId: function(count, success, failure, async){
        this.action.invoke({"name": "getId","async": async, "parameter": {"count": count},	"success": success,	"failure": failure});
    },
    getUUID: function(success, async){
        var sync = (async !== false);
        var id = "";
        this.action.invoke({"name": "getId","async": sync, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0].id;
            if (success) success(id);
        },	"failure": null});

        //this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"},	"success": function(ids){
        //	id = ids.data[0].id;
        //   if (success) success(id);
        //},	"failure": null, "withCredentials": false});
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
        this.action.invoke({"name": "updateApplicationIcon", "parameter": {"id": applicationId},"data": formData,"file": file,"success": success,"failure": failure});
    },


    listProcess: function(application, success, failure, async){
        this.action.invoke({"name": "listProcess","async": async, "parameter": {"id": application},	"success": success,	"failure": failure});
    },
    listForm: function(application, success, failure, async){
        this.action.invoke({"name": "listForm","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listDictionary: function(application, success, failure, async){
        this.action.invoke({"name": "listDictionary","async": async, "parameter": {"application": application}, "success": success,	"failure": failure});
    },
    listScript: function(application, success, failure, async){
        this.action.invoke({"name": "listScript","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listView: function(application, success, failure, async){
        this.action.invoke({"name": "listView","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listStat: function(application, success, failure, async){
        this.action.invoke({"name": "listStat","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listFormField: function(application, success, failure, async){
        this.action.invoke({"name": "listFormField","async": async, "parameter": {"id": application}, "success": success,	"failure": failure});
    },
    listFormTemplate: function(success, failure, async){
        this.action.invoke({"name": "listFormTemplate","async": async, "success": success,	"failure": failure});
    },
    listFormTemplateCategory: function(success, failure, async){
        this.action.invoke({"name": "listFormTemplateCategory","async": async, "success": success,	"failure": failure});
    },
    listFormTemplatByCategory: function(success, failure, async){
        this.action.invoke({"name": "listFormTemplatByCategory","async": async, "success": success,	"failure": failure});
    },

    //listFormCategory: function(lastId, count, success, failure, async){
    //	this.action.invoke({"name": "listFormCategory","async": async, "parameter": {"id": lastId || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    //},
    //listProcessCategory: function(lastId, count, success, failure, async){
    //	this.action.invoke({"name": "listProcessCategory","async": async, "parameter": {"id": lastId || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    //},
    //listForm: function(category, last, count, success, failure, async){
    //	this.action.invoke({"name": "listForm","async": async, "parameter": {"cid": category, "id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    //},
    //listProcess: function(category, last, count, success, failure, async){
    //	this.action.invoke({"name": "listProcess","async": async, "parameter": {"cid": category, "id": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    //},
    getForm: function(form, success, failure, async){
        this.action.invoke({"name": "getForm","async": async, "parameter": {"id": form},	"success": success,	"failure": failure});
    },
    getView: function(id, success, failure, async){
        this.action.invoke({"name": "getView","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getStat: function(id, success, failure, async){
        this.action.invoke({"name": "getStat","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getProcess: function(process, success, failure, async){
        this.action.invoke({"name": "getProcess","async": async, "parameter": {"id": process},	"success": success,	"failure": failure});
    },
    getDictionary: function(id, success, failure, async){
        this.action.invoke({"name": "getDictionary","async": async, "parameter": {"applicationDict": id},	"success": success,	"failure": failure});
    },
    getScript: function(id, success, failure, async){
        this.action.invoke({"name": "getScript","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    getScriptByName: function(name, application, success, failure, async){
        this.action.invoke({"name": "getScriptByName","async": async, "parameter": {"name": name, "applicationId": application},	"success": success,	"failure": failure});
    },
    getFormTemplate: function(id, success, failure, async){
        this.action.invoke({"name": "getFormTemplate","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    //getProcessCategory: function(id, success, failure, async){
    //	this.action.invoke({"name": "getProcessCategory","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    //},
    //getFormCategory: function(id, success, failure, async){
    //	this.action.invoke({"name": "getFormCategory","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    //},

    //saveProcessCategory: function(categoryData, success, failure){
    //	if (categoryData.id){
    //		this.updateProcessCategory(categoryData, success, failure);
    //	}else{
    //		this.addProcessCategory(categoryData, success, failure);
    //	}
    //},
    //updateProcessCategory: function(categoryData, success, failure){
    //	this.action.invoke({"name": "updataProcessCategory","data": categoryData,"parameter": {"id": categoryData.id},"success": success,"failure": failure});
    //},
    //addProcessCategory: function(categoryData, success, failure){
    //	this.getId(1, function(json){
    //		categoryData.id = json.data[0].id;
    //		this.action.invoke({"name": "addProcessCategory","data": categoryData,"success": success,"failure": failure});
    //	}.bind(this));
    //},

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

    saveFile: function(data, success, failure, async){
        if (data.id){
            this.updataFile(data.id, data, success, failure, async);
        }else{
            this.getUUID(function(id){
                data.id = id;
                this.addFile(data, success, failure, async);
            }.bind(this), false);

        }
    },
    // updateFile: function(){
    //
    // },
    // addFile: function(){
    //
    // },


    saveForm: function(formData, mobileData, fieldList, success, failure){
        if (!formData.isNewForm){
            this.updateForm(formData, mobileData, fieldList, success, failure);
        }else{
            this.addForm(formData, mobileData, fieldList, success, failure);
        }
    },
    updateForm: function(formData, mobileData, fieldList, success, failure){
        var data, mobileDataStr;
        if (formData) data = MWF.encodeJsonString(JSON.encode(formData));
        if (mobileData) mobileDataStr = MWF.encodeJsonString(JSON.encode(mobileData));

        var relatedScriptMap = null;
        if (formData && formData.json.includeScripts && formData.json.includeScripts.length){
            relatedScriptMap = {};
            formData.json.includeScripts.each(function(s){
                relatedScriptMap[s.id] = ((s.appType==="process") ? "processPlatform" : s.appType);
            });
        };

        var mobileRelatedScriptMap = null;
        if (mobileData && mobileData.json.includeScripts && mobileData.json.includeScripts.length){
            mobileRelatedScriptMap = {};
            mobileData.json.includeScripts.each(function(s){
                mobileRelatedScriptMap[s.id] = ((s.appType==="process") ? "processPlatform" : s.appType);
            });
        };

        if (!fieldList){
            dataTypes = {
                "string": ["htmledit", "radio", "select", "textarea", "textfield"],
                "person": ["personfield","orgfield","org"],
                "date": ["calender"],
                "number": ["number"],
                "array": ["checkbox"]
            };
            fieldList = [];
            Object.keys(formData.json.moduleList).forEach(function(moduleKey){
                var moudle = formData.json.moduleList[moduleKey];
                var key = "";
                for (k in dataTypes){
                    if (dataTypes[k].indexOf(moudle.type.toLowerCase())!=-1){
                        key = k;
                        break;
                    }
                }
                if (key){
                    fieldList.push({
                        "name": moudle.id,
                        "dataType": key
                    });
                }
            }.bind(this));
        }

        var json = {
            "id": formData.json.id,
            "name": formData.json.name,
            "alias": formData.json.name,
            "hasMobile": false,
            "description": formData.json.description,
            "application": formData.json.application,
            "category": formData.json.category,
            "icon": formData.json.formIcon,
            "formFieldList": fieldList,
            "relatedScriptMap": relatedScriptMap,
            "relatedFormList": (formData && formData.json.subformList) ? formData.json.subformList : [],
            "mobileRelatedScriptMap": mobileRelatedScriptMap,
            "mobileRelatedFormList": (mobileData && mobileData.json.subformList) ? mobileData.json.subformList : []
        };
        if (mobileData && mobileData.json.moduleList){
            if (Object.keys(mobileData.json.moduleList).length){
                json.hasMobile = true;
            }else{
                json.hasMobile = false;
            }
        }
        if (formData) json.data = data;
        if (mobileData) json.mobileData = mobileDataStr;

        this.action.invoke({"name": "updataForm","data": json,"parameter": {"id": formData.json.id},"success": success,"failure": failure});
    },
    addForm: function(formData, mobileData, fieldList, success, failure){
        var data, mobileDataStr;
        if (!formData.json.id){
            this.getUUID(function(id){
                formData.json.id = id;
                //if (formData) formData.isNewForm = false;
                //if (mobileData) mobileData.isNewForm = false;

                if (formData) data = MWF.encodeJsonString(JSON.encode(formData));

                if (mobileData && !mobileData.json.id){
                    mobileData.json.id = id;
                }
                if (mobileData) mobileDataStr = MWF.encodeJsonString(JSON.encode(mobileData));

                var relatedScriptMap = null;
                if (formData && formData.json.includeScripts && formData.json.includeScripts.length){
                    relatedScriptMap = {};
                    formData.json.includeScripts.each(function(s){
                        relatedScriptMap[s.id] = ((s.appType==="process") ? "processPlatform" : s.appType);
                    });
                };

                var mobileRelatedScriptMap = null;
                if (mobileData && mobileData.json.includeScripts && mobileData.json.includeScripts.length){
                    mobileRelatedScriptMap = {};
                    mobileData.json.includeScripts.each(function(s){
                        mobileRelatedScriptMap[s.id] = ((s.appType==="process") ? "processPlatform" : s.appType);
                    });
                }


                var json = {
                    "id": formData.json.id,
                    "name": formData.json.name,
                    "alias": formData.json.name,
                    "hasMobile": false,
                    "description": formData.json.description,
                    "application": formData.json.application,
                    "category": formData.json.category,
                    "icon": formData.json.formIcon,
                    "formFieldList": fieldList,
                    "relatedScriptMap": relatedScriptMap,
                    "relatedFormList": (formData && formData.json.subformList) ? formData.json.subformList : [],
                    "mobileRelatedScriptMap": mobileRelatedScriptMap,
                    "mobileRelatedFormList": (mobileData && mobileData.json.subformList) ? mobileData.json.subformList : []
                };

                if (mobileData && mobileData.json.moduleList){
                    if (Object.keys(mobileData.json.moduleList).length){
                        json.hasMobile = true;
                    }else{
                        json.hasMobile = false;
                    }
                }

                if (formData) json.data = data;
                if (mobileData) json.mobileData = mobileDataStr;
                this.action.invoke({"name": "addForm","data": json, "parameter": {"id": formData.json.categoryId}, "success": success,"failure": failure});
            }.bind(this));
        }else{
            //if (formData) formData.isNewForm = false;
            //if (mobileData) mobileData.isNewForm = false;
            if (formData) data = MWF.encodeJsonString(JSON.encode(formData));

            if ( mobileData && !mobileData.json.id ){
                mobileData.json.id = formData.json.id;
            }
            if (mobileData) mobileDataStr = MWF.encodeJsonString(JSON.encode(mobileData));

            var json = {
                "id": formData.json.id,
                "name": formData.json.name,
                "alias": formData.json.name,
                "hasMobile": false,
                "description": formData.json.description,
                "application": formData.json.application,
                "category": formData.json.category
            };
            if (mobileData && mobileData.json.moduleList){
                if (Object.keys(mobileData.json.moduleList).length){
                    json.hasMobile = true;
                }else{
                    json.hasMobile = false;
                }
            }

            if (formData) json.data = data;
            if (mobileData) json.mobileData = mobileDataStr;
            this.action.invoke({"name": "addForm","data": json, "parameter": {"id": formData.json.categoryId}, "success": success,"failure": failure});
        }
    },
    deleteForm: function(id, success, failure, async){
        this.action.invoke({"name": "removeForm", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    addFormTemplate: function(formData, mobileData, templateData, success, failure){
        var data, mobileDataStr;
        this.getUUID(function(id){
            //formData.json.id = id;
            templateData.id = id;
            if (formData){
                var tFormData = Object.clone(formData);
                tFormData.json.id = id;
                tFormData.json.name = templateData.name;
                data = MWF.encodeJsonString(JSON.encode(tFormData));
            }
            if (mobileData){
                var tMobileData = Object.clone(mobileData);
                tMobileData.json.id = id;
                tMobileData.json.name = templateData.name;
                mobileDataStr = MWF.encodeJsonString(JSON.encode(tMobileData));
            }

            //templateData.name
            //templateData.category
            //templateData.description

            if (formData) templateData.data = data;
            if (mobileData) templateData.mobileData = mobileDataStr;
            this.action.invoke({"name": "addFormTemplate","data": templateData, "success": success,"failure": failure});
        }.bind(this));
    },

    deleteFormTemplate: function(id, success, failure, async){
        this.action.invoke({"name": "deleteFormTemplate", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    saveView: function(viewData, success, failure){
        if (!viewData.isNewView){
            this.updateView(viewData, success, failure);
        }else{
            viewData.isNewView = false;
            this.addView(viewData, success, failure);
        }
    },
    updateView: function(viewData, success, failure){
        var data =viewData.data;
        viewData.data = JSON.encode(viewData.data);
        this.action.invoke({"name": "updataView","data": viewData,"parameter": {"id": viewData.id},"success": success,"failure": failure});
        viewData.data = data;
    },
    addView: function(viewData, success, failure){
        var data =viewData.data;
        viewData.data = JSON.encode(viewData.data);
        if (!data.id){
            this.getUUID(function(id){
                viewData.id = id;
                this.action.invoke({"name": "addView","data": viewData, "success": success,"failure": failure});
                viewData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addView","data": viewData, "success": success,"failure": failure});
            viewData.data = data;
        }
    },
    saveStat: function(data, success, failure){
        if (!data.isNewView){
            this.updateStat(data, success, failure);
        }else{
            data.isNewView = false;
            this.addStat(data, success, failure);
        }
    },
    updateStat: function(statData, success, failure){
        var data =statData.data;
        statData.data = JSON.encode(statData.data);
        this.action.invoke({"name": "updataStat","data": statData,"parameter": {"id": statData.id},"success": success,"failure": failure});
        statData.data = data;
    },
    addStat: function(statData, success, failure){
        var data =statData.data;
        statData.data = JSON.encode(statData.data);
        if (!data.id){
            this.getUUID(function(id){
                statData.id = id;
                this.action.invoke({"name": "addStat","data": statData, "success": success,"failure": failure});
                statData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addView","data": statData, "success": success,"failure": failure});
            statData.data = data;
        }
    },
    deleteView: function(id, success, failure, async){
        this.action.invoke({"name": "removeView", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deleteStat: function(id, success, failure, async){
        this.action.invoke({"name": "removeStat", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
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
        if (!data.id){
            var dirData = Object.clone(data);
            this.getUUID(function(id){
                dirData.id = id;
                this.action.invoke({"name": "addDictionary","data": dirData,"success": success,"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "addDictionary","data": data,"success": success,"failure": failure});
        }
    },
    deleteDictionary: function(id, success, failure, async){
        this.action.invoke({"name": "removeDictionary", "async": async, "parameter": {"applicationDict": id}, "success": success, "failure": failure});
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
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "addScript","data": data,"success": success,"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "addScript","data": data,"success": success,"failure": failure});
        }
    },

    deleteScript: function(id, success, failure, async){
        this.action.invoke({"name": "removeScript", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    loadView: function(id, success, failure, async){
        this.action.invoke({"name": "loadView","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },
    loadStat: function(id, success, failure, async){
        this.action.invoke({"name": "loadStat","async": async, "parameter": {"id": id},	"success": success,	"failure": failure});
    },

    saveProjection: function(data, success, failure){
        if (!data.isNewProjection){
            this.updateProjection(data, success, failure);
        }else{
            this.addProjection(data, success, failure);
        }
    },
    updateProjection: function(data, success, failure){
        this.action.invoke({"name": "updataProjection","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    addProjection: function(data, success, failure){
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "addProjection","data": data,"success": success,"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "addProjection","data": data,"success": success,"failure": failure});
        }
    },
    deleteProjection: function(id, success, failure, async){
        this.action.invoke({"name": "removeProjection", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

});
