MWF.xAction.RestActions.Action["x_query_assemble_designer"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    getUUID: function(success){
        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0].id;
            if (success) success(id);
        },	"failure": null});
        return id;
    },
    listApplicationSummary: function(categoryName, success, failure, async){
        if (categoryName){
            this.action.invoke({"name": "listApplicationByCategorySummary","async": async, "parameter": {"queryCategory": categoryName}, "success": success,	"failure": failure});
        }else{
            this.action.invoke({"name": "listApplicationSummary","async": async, "success": success,	"failure": failure});
        }
    },
    saveApplication: function(applicationData, success, failure){
        if (applicationData.id){
            this.updateApplication(applicationData.id, applicationData, success, failure);
        }else{
            this.addApplication(applicationData, success, failure);
        }
    },
    saveView: function(viewData, success, failure){
        if (!viewData.isNewView){
            this.updateView(viewData, success, failure);
        }else{
            this.addView(viewData, success, failure);
        }
    },
    updateView: function(viewData, success, failure){
        var data =viewData.data;
        viewData.data = JSON.encode(viewData.data);
        viewData.query = viewData.application;
        viewData.queryName = viewData.applicationName;
        this.action.invoke({"name": "updateView","data": viewData,"parameter": {"id": viewData.id},"success": success,"failure": failure});
        viewData.data = data;
    },
    addView: function(viewData, success, failure){
        var data =viewData.data;
        viewData.data = JSON.encode(viewData.data);
        viewData.query = viewData.application;
        viewData.queryName = viewData.applicationName;
        if (!data.id){
            this.getUUID(function(id){
                viewData.id = id;
                this.action.invoke({"name": "addView","data": viewData, "success": function(json){
                    viewData.isNewView = false;
                    if (success) success(json);
                },"failure": failure});
                viewData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addView","data": viewData, "success": function(json){
                viewData.isNewView = false;
                if (success) success(json);
            },"failure": failure});
            viewData.data = data;
        }
    },
    saveStat: function(data, success, failure){
        if (!data.isNewView){
            this.updateStat(data, success, failure);
        }else{
            this.addStat(data, success, failure);
        }
    },
    updateStat: function(statData, success, failure){
        var data =statData.data;
        statData.data = JSON.encode(statData.data);
        statData.query = statData.application;
        statData.queryName = statData.applicationName;
        this.action.invoke({"name": "updateStat","data": statData,"parameter": {"id": statData.id},"success": success,"failure": failure});
        statData.data = data;
    },
    addStat: function(statData, success, failure){
        var data =statData.data;
        statData.data = JSON.encode(statData.data);
        statData.query = statData.application;
        statData.queryName = statData.applicationName;
        if (!data.id){
            this.getUUID(function(id){
                statData.id = id;
                this.action.invoke({"name": "addStat","data": statData, "success": function(json){
                    statData.isNewView = false;
                    if (success) success(json);
                },"failure": failure});
                statData.data = data;
            }.bind(this));
        }else{
            this.action.invoke({"name": "addStat","data": statData, "success": function(json){
                statData.isNewView = false;
                if (success) success(json);
            },"failure": failure});
            statData.data = data;
        }
    },
    deleteView: function(id, success, failure, async){
        this.action.invoke({"name": "removeView", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deleteStat: function(id, success, failure, async){
        this.action.invoke({"name": "removeStat", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    // saveStatement: function( data, success, failure, async){
    //     if ( data.id ){
    //         this.action.invoke({"name": "updateStatement", "async": async, "parameter": { "flag" : data.id  }, "data" : data, "success": success, "failure": failure});
    //     }else{
    //         this.action.invoke({"name": "createStatement", "async": async, "data" : data, "success": success, "failure": failure});
    //     }
    // },

    saveTable: function(data, success, failure){
        if (!data.isNewTable){
            this.updateTable(data, success, failure);
        }else{
            this.addTable(data, success, failure);
        }
    },
    updateTable: function(tableData, success, failure){
        var data = Object.clone(tableData);
        data.draftData = JSON.encode(tableData.draftData);
        data.query = tableData.application;
        data.queryName = tableData.applicationName;
        this.action.invoke({"name": "updateTable", "data": data, "parameter": {"flag": data.id},"success": success,"failure": failure});
    },
    addTable: function(tableData, success, failure){
        var data = Object.clone(tableData);
        data.draftData = JSON.encode(tableData.draftData);
        data.query = tableData.application;
        data.queryName = tableData.applicationName;

        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "createTable","data": data, "success": function(json){
                    tableData.isNewTable = false;
                    if (success) success(json);
                },"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "createTable","data": data, "success": function(json){
                tableData.isNewTable = false;
                if (success) success(json);
            },"failure": failure});
        }
    },
    saveStatement: function(data, success, failure){
        if (!data.isNewStatement){
            this.updateStatement(data, success, failure);
        }else{
            this.addStatement(data, success, failure);
        }
    },
    updateStatement: function(statementData, success, failure){
        var data = Object.clone(statementData);
        data.query = statementData.application;
        data.queryName = statementData.applicationName;
        delete data.tableObj;
        this.action.invoke({"name": "updateStatement", "data": data, "parameter": {"flag": data.id},"success": success,"failure": failure});
    },
    addStatement: function(statementData, success, failure){
        var data = Object.clone(statementData);
        data.query = statementData.application;
        data.queryName = statementData.applicationName;
        delete data.tableObj;
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "createStatement","data": data, "success": function(json){
                        statementData.isNewStatement = false;
                        if (success) success(json);
                    },"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "createStatement","data": data, "success": function(json){
                    statementData.isNewStatement = false;
                    if (success) success(json);
                },"failure": failure});
        }
    },

    saveImportModel: function(data, success, failure){
        if (!data.isNewImportModel){
            this.updateImportModel(data, success, failure);
        }else{
            this.addImportModel(data, success, failure);
        }
    },
    updateImportModel: function(importModelData, success, failure){
        var data = Object.clone(importModelData);
        data.data = JSON.encode(data.data);
        data.query = importModelData.application;
        data.queryName = importModelData.applicationName;
        delete data.tableObj;
        this.action.invoke({"name": "updateImportModel", "data": data, "parameter": {"flag": data.id},"success": success,"failure": failure});
    },
    addImportModel: function(importModelData, success, failure){
        var data = Object.clone(importModelData);
        data.data = JSON.encode(data.data);
        data.query = importModelData.application;
        data.queryName = importModelData.applicationName;
        delete data.tableObj;
        if (!data.id){
            this.getUUID(function(id){
                data.id = id;
                this.action.invoke({"name": "createImportModel","data": data, "success": function(json){
                        importModelData.isNewImportModel = false;
                        if (success) success(json);
                    },"failure": failure});
            }.bind(this));
        }else{
            this.action.invoke({"name": "createImportModel","data": data, "success": function(json){
                    importModelData.isNewImportModel = false;
                    if (success) success(json);
                },"failure": failure});
        }
    },

    saveRow: function(tableFlag, data, success, failure, async){
        if ( data.id ){
            this.action.invoke({"name": "updateRow", "async": async, "parameter": {"tableFlag": tableFlag, "id" : data.id  }, "data" : data, "success": success, "failure": failure});
        }else{
            this.action.invoke({"name": "insertRow", "async": async, "parameter": {"tableFlag": tableFlag  }, "data" : data, "success": success, "failure": failure});
        }
    }
});