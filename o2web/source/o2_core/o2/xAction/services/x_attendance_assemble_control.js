MWF.xAction.RestActions.Action["x_attendance_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,
    getUUID: function(success){
        var id = "";
        this.action.invoke({"name": "getId","async": false, "parameter": {"count": "1"}, "success": function(ids){
            id = ids.data[0];
            if (success) success(id);
        },	"failure": null});
        return id;
    },
    exportSelfHoliday: function(startdate, enddate, success, failure){
        this.action.getActions(function(){
            var url = this.action.actions.exportSelfHoliday.uri;
            url = url.replace("{startdate}", startdate);
            url = url.replace("{enddate}", enddate);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
    },
    uploadAttachment: function(success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "data": formData,"file": file,"success": success,"failure": failure});
    },
    getAttachmentStream: function(id){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(this.action.address+url);
        }.bind(this));
    },
    exportAbnormalAttachment: function(year, month, success, failure){
        this.action.getActions(function(){
            var url = this.action.actions.exportAbnormalAttachment.uri;
            url = url.replace("{year}", year);
            url = url.replace("{month}", month);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
        //this.action.invoke({"name": "exportAbnormalAttachment", "parameter": {"year": year, "month": month },"success": success,"failure": failure});
    },
    listHolidayByYearAndName: function(year,name,success, failure, async){
        this.action.invoke({"name": "listHolidayFilter","data": {"q_Year": year, "q_Name" : name },"async": async, "success": success,	"failure": failure});
    },
    listHolidayByYearAndMonth: function(year, month, success, failure, async){
        this.action.invoke({"name": "listHolidayFilter","data": {"q_Year": year, "q_Month" : month },"async": async, "success": success,	"failure": failure});
    },
    listHolidayByName: function(name,success, failure, async){
        this.action.invoke({"name": "listHolidayFilter","data": {"q_Name": name },"async": async, "success": success,	"failure": failure});
    }
});