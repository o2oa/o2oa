MWF.xAction.RestActions.Action["x_report_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    listWorkTagWithUnit : function(unitName, success, failure, async){
        this.actionReport.invoke({"name": "listWorkTagWithUnit", data : { "unitName" : unitName }, "success": success,"failure": failure, "async": async});
    },
    statByKeyWork : function( year, success,failure, async){
        this.action.getActions(function(){
            var url = this.action.actions.statByKeyWork.uri;
            url = url.replace("{year}", encodeURIComponent(year));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    getExportFileStream: function(id){
        this.action.getActions(function(){
            var url = this.action.actions.exportStatResult.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(o2.filterUrl(this.action.address+url), "_blank");
        }.bind(this));
    }
    //statByUnit : function( year, success,failure, async){
    //    this.action.getActions(function(){
    //        var url = this.action.actions.statByUnit.uri;
    //        url = url.replace("{year}", encodeURIComponent(year));
    //        window.open(this.action.address+url);
    //    }.bind(this));
    //}
});