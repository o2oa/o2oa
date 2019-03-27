MWF.xAction.RestActions.Action["x_query_assemble_surface"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    saveRow: function(tableFlag, data, success, failure, async){
        if ( data.id ){
            this.action.invoke({"name": "updateRow", "async": async, "parameter": {"tableFlag": tableFlag, "id" : data.id  }, "data" : data, "success": success, "failure": failure});
        }else{
            this.action.invoke({"name": "insertRow", "async": async, "parameter": {"tableFlag": tableFlag  }, "data" : data, "success": success, "failure": failure});
        }
    }
});