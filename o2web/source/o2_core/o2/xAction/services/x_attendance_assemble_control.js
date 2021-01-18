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
    exportAbnormalAttachment: function(year, month, stream){
        this.action.getActions(function(){
            var url = this.action.actions.exportAbnormalAttachment.uri;
            url = url.replace("{year}", year);
            url = url.replace("{month}", month);
            url = url.replace("{stream}", stream);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
        //this.action.invoke({"name": "exportAbnormalAttachment", "parameter": {"year": year, "month": month },"success": success,"failure": failure});
    },
    //统计导出
    detailsExportStream: function(q_topUnitName, q_unitName,q_empName,cycleYear,cycleMonth,q_date,isAbsent,isLackOfTime,isLate, stream){
        this.action.getActions(function(){
            var url = this.action.actions.detailsExportStream.uri;
            url = url.replace("{q_topUnitName}", q_topUnitName);
            url = url.replace("{q_unitName}", q_unitName);
            url = url.replace("{q_empName}", q_empName);
            url = url.replace("{cycleYear}", cycleYear);
            url = url.replace("{cycleMonth}", cycleMonth);
            url = url.replace("{q_date}", q_date);
            url = url.replace("{isAbsent}", isAbsent);
            url = url.replace("{isLackOfTime}", isLackOfTime);
            url = url.replace("{isLate}", isLate);
            url = url.replace("{stream}", stream);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
    },
    //个人出勤率导出
    exportPersonStatisticAttachment: function(name,year, month, stream){
        this.action.getActions(function(){
            var url = this.action.actions.exportPersonStatisticAttachment.uri;
            url = url.replace("{name}", name);
            url = url.replace("{year}", year);
            url = url.replace("{month}", month);
            url = url.replace("{stream}", stream);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
    },
    //部门出勤率导出
    exportUnitStatisticAttachment: function(name,year, month, stream){
        this.action.getActions(function(){
            var url = this.action.actions.exportUnitStatisticAttachment.uri;
            url = url.replace("{name}", name);
            url = url.replace("{year}", year);
            url = url.replace("{month}", month);
            url = url.replace("{stream}", stream);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
    },
    //公司出勤率导出
    exportTopUnitStatisticAttachment: function(name,year, month, stream){
        this.action.getActions(function(){
            var url = this.action.actions.exportTopUnitStatisticAttachment.uri;
            url = url.replace("{name}", name);
            url = url.replace("{year}", year);
            url = url.replace("{month}", month);
            url = url.replace("{stream}", stream);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
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