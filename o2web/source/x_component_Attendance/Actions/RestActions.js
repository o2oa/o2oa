MWF.xApplication.Attendance = MWF.xApplication.Attendance || {};
MWF.xApplication.Attendance.Actions = MWF.xApplication.Attendance.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.Attendance.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_attendance_assemble_control", "x_component_Attendance");

        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_Attendance");

        this.actionPerson = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_personal", "x_component_Attendance");
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

    getCurrentPerson : function(success, failure, async){
        this.actionPerson.invoke({"name": "getCurrentPerson","async": async, "success": success,	"failure": failure});
    },
    listUnitWithPerson: function(success, failure, data, async){
        this.actionOrg.invoke({"name": "listUnitWithPerson","async": async, "data": data,	"success": success,	"failure": failure});
    },
    listUnitSubDirect: function(success, failure, data, async){
        this.actionOrg.invoke({"name": "listUnitSubDirect","data": data, "async": async, "success": success,"failure": failure});
        //this.actionOrg.invoke({"name": "listUnitByTopUnit","async": async, "parameter": {"topUnitName": topUnitName},	"success": success,	"failure": failure});
    },
    getPersonAttribute: function(data, success, failure, async){
        this.actionOrg.invoke({"name": "getPersonAttribute","async": async, "urlEncode": true, "data": data,	"success": success,	"failure": failure});
    },
    listPersonWithIdentity: function(data, success, failure, async){
        this.actionOrg.invoke({"name": "listPersonWithIdentity","async": async, "urlEncode": true, "data": data,	"success": success,	"failure": failure});
    },
    listIdentityWithUnitWithName: function(data, success, failure, async){
        this.actionOrg.invoke({"name": "listIdentityWithUnitWithName","async": async, "urlEncode": true, "data": data,	"success": success,	"failure": failure});
    },
    listUnitWithPersonSupNested: function(data, success, failure, async){
        this.actionOrg.invoke({"name": "listUnitWithPersonSupNested","async": async, "urlEncode": true, "data": data,	"success": success,	"failure": failure});
    },
    listPersonWithPersonSupDirect: function(data, success, failure, async){
        this.actionOrg.invoke({"name": "listPersonWithPersonSupDirect","async": async, "urlEncode": true, "data": data,	"success": success,	"failure": failure});
    },

    getPermission: function(id, success, failure){
        this.action.invoke({"name": "getPermission", "parameter": {"id": id },"success": success,"failure": failure});
    },
    listPermission: function(success, failure, async){
        this.action.invoke({"name": "listPermission","async": async, "success": success,	"failure": failure});
    },
    savePermission: function(data, success, failure){
        this.action.invoke({"name": "savePermission","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deletePermission: function(id, success, failure, async){
        this.action.invoke({"name": "deletePermission", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    getHoliday: function(id, success, failure){
        this.action.invoke({"name": "getHoliday", "parameter": {"id": id },"success": success,"failure": failure});
    },
    listHolidayAll: function(success, failure, async){
        this.action.invoke({"name": "listHolidayAll","async": async, "success": success,	"failure": failure});
    },
    listHolidayFilter : function( filterData, success,failure, async){
        this.action.invoke({"name": "listHolidayFilter", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listHolidayByYearAndName: function(year,name,success, failure, async){
        this.action.invoke({"name": "listHolidayFilter","data": {"q_Year": year, "q_Name" : name },"async": async, "success": success,	"failure": failure});
    },
    listHolidayByYearAndMonth: function(year, month, success, failure, async){
        this.action.invoke({"name": "listHolidayFilter","data": {"q_Year": year, "q_Month" : month },"async": async, "success": success,	"failure": failure});
    },
    listHolidayByName: function(name,success, failure, async){
        this.action.invoke({"name": "listHolidayFilter","data": {"q_Name": name },"async": async, "success": success,	"failure": failure});
    },
    saveHoliday: function(data, success, failure, async){
        this.action.invoke({"name": "saveHoliday","data": data, "async": async,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteHoliday: function(id, success, failure, async){
        this.action.invoke({"name": "deleteHoliday", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },

    getSelfHoliday: function(id, success, failure){
        this.action.invoke({"name": "getSelfHoliday", "parameter": {"id": id },"success": success,"failure": failure});
    },
    listSelfHoliday: function(success, failure, async){
        this.action.invoke({"name": "listSelfHoliday","async": async, "success": success,	"failure": failure});
    },
    saveSelfHoliday: function(data, success, failure){
        this.action.invoke({"name": "saveSelfHoliday","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteSelfHoliday: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSelfHoliday", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    exportSelfHoliday: function(startdate, enddate, success, failure){
        this.action.getActions(function(){
            var url = this.action.actions.exportSelfHoliday.uri;
            url = url.replace("{startdate}", startdate);
            url = url.replace("{enddate}", enddate);
            window.open(this.action.address+url , "_blank");
        }.bind(this));
    },
    listSelfHolidayFilterNext : function( id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listSelfHolidayFilterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    getSchedule: function(id, success, failure){
        this.action.invoke({"name": "getSchedule", "parameter": {"id": id },"success": success,"failure": failure});
    },
    listSchedule: function(success, failure, async){
        this.action.invoke({"name": "listSchedule","async": async, "success": success,	"failure": failure});
    },
    saveSchedule: function(data, success, failure){
        this.action.invoke({"name": "saveSchedule","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteSchedule: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSchedule", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    listScheduleByUnit: function(unit, success, failure, async){
        this.action.invoke({"name": "listScheduleByUnit", "parameter": {"name": unit },"success": success,"failure": failure, "async": async});
    },
    listScheduleByTopUnit: function(topUnit, success, failure, async){
        this.action.invoke({"name": "listScheduleByTopUnit", "parameter": {"name": topUnit },"success": success,"failure": failure, "async": async});
    },

    getCycle: function(id, success, failure){
        this.action.invoke({"name": "getCycle", "parameter": {"id": id },"success": success,"failure": failure});
    },
    deleteCycle: function(id, success, failure, async){
        this.action.invoke({"name": "deleteCycle", "parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    },
    saveCycle: function(data, success, failure){
        this.action.invoke({"name": "saveCycle","data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    listCycle: function( success, failure, async){
        this.action.invoke({"name": "listCycle", "async": async, "success": success, "failure": failure});
    },
    getCyclePerson: function(year, month, success, failure, async){
        this.action.invoke({"name": "getCyclePerson", "parameter": {"year": year, "month": month }, "async": async,"success": success,"failure": failure});
    },




    listSetting: function(success, failure, async){
        this.action.invoke({"name": "listSetting","async": async, "success": success,	"failure": failure});
    },
    getSetting: function(id, success, failure, async){
        this.action.invoke({"name": "getSetting","async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    saveSetting: function(data, success, failure,async){
        this.action.invoke({"name": "saveSetting","async": async,"data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteSetting: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSetting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    getSettingCode: function(code, success, failure, async){
        this.action.invoke({"name": "getSettingCode","async": async, "parameter": {"code": code },"success": success,"failure": failure});
    },

    listPersonSetting: function(success, failure, async){
        this.action.invoke({"name": "listPersonSetting","async": async, "success": success,	"failure": failure});
    },
    getPersonSetting: function(id, success, failure, async){
        this.action.invoke({"name": "getPersonSetting","async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    savePersonSetting: function(data, success, failure,async){
        this.action.invoke({"name": "savePersonSetting","async": async,"data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deletePersonSetting: function(id, success, failure, async){
        this.action.invoke({"name": "deletePersonSetting","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },


    listAttachmentInfo: function(success, failure, async){
        this.action.invoke({"name": "listAttachmentInfo","async": async, "success": success,	"failure": failure});
    },
    getAttachmentInfo : function(id, success, failure, async){
        this.action.invoke({"name": "getAttachmentInfo","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    uploadAttachment: function(success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "data": formData,"file": file,"success": success,"failure": failure});
    },
    //getAttachment: function(id, documentid, success, failure, async){
    //    this.actionAttachment.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    //},
    deleteAttachment: function(id, success, failure, async){
        this.action.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    getAttachmentStream: function(id){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            window.open(this.action.address+url);
        }.bind(this));
    },
    //importAttachment: function(file_id, success, failure){
    //    this.action.invoke({"name": "importAttachment", "parameter": {"file_id": file_id },"success": success,"failure": failure});
    //},
    //checkAttachment: function(file_id, success, failure){
    //    this.action.invoke({"name": "checkAttachment", "parameter": {"file_id": file_id },"success": success,"failure": failure});
    //},
    getImportStatus: function(file_id, success, failure){
        this.action.invoke({"name": "getImportStatus", "parameter": {"file_id": file_id },"success": success,"failure": failure});
    },
    getImportStatusAll: function(success, failure){
        this.action.invoke({"name": "getImportStatusAll", "success": success,"failure": failure});
    },
    getImportStatusDetail : function(file_id, success, failure){
        this.action.invoke({"name": "getImportStatusDetail", "parameter": {"file_id": file_id },"success": success,"failure": failure});
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

    getDetail: function(id, success, failure, async){
        this.action.invoke({"name": "getDetail","async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    deleteDetail: function(id, success, failure, async){
        this.action.invoke({"name": "deleteDetail","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    },
    listDetail: function(success, failure, async){
        this.action.invoke({"name": "listDetail","async": async, "success": success,	"failure": failure});
    },
    listDetailFilter : function( filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilter", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailFilterNext : function( id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailFilterPrev : function(  id, count, filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailFilterUser : function( filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterUser", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listUnitDetailFilter : function( filterData, success,failure, async){
        this.action.invoke({"name": "listUnitDetailFilter", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listTopUnitDetailFilter : function( filterData, success,failure, async){
        this.action.invoke({"name": "listTopUnitDetailFilter", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailByAttachment: function(file_id, success, failure){
        this.action.invoke({"name": "listDetailByAttachment", "parameter": {"file_id": file_id },"success": success,"failure": failure});
    },

    checkDetail: function(cycleYear, cycleMonth, success, failure){
        this.action.invoke({"name": "checkDetail", "parameter": {"cycleYear": cycleYear , "cycleMonth" : cycleMonth },"success": success,"failure": failure});
    },
    analyseDetail: function(startDate, endDate, success, failure){
        this.action.invoke({"name": "analyseDetail", "parameter": {"startDate": startDate , "endDate" : endDate },"success": success,"failure": failure});
    },
    staticDetail : function(year, month, success, failure){
        this.action.invoke({"name": "staticDetail", "parameter": {"year": year , "month" : month },"success": success,"failure": failure});
    },
    staticAllDetail : function(success, failure){
        this.action.invoke({"name": "staticAllDetail", "success": success,"failure": failure});
    },

    listStaticMonthPerson: function(person,year, month, success, failure){
        this.action.invoke({"name": "listStaticMonthPerson", "parameter": {"name":person,"year": year,"month":month },"success": success,"failure": failure});
    },
    listStaticDayUnit: function(unit, year, month, success, failure){
        this.action.invoke({"name": "listStaticDayUnit", "parameter": {"name":unit,"year": year,"month":month },"success": success,"failure": failure});
    },
    listStaticDateUnit: function(unit, date, success, failure){
        this.action.invoke({"name": "listStaticDateUnit", "parameter": {"name":unit,"date": date },"success": success,"failure": failure});
    },
    listStaticMonthUnit: function(unit, year, month, success, failure, async ){
        this.action.invoke({"name": "listStaticMonthUnit", "parameter": {"name":unit,"year": year,"month":month  },"success": success,"failure": failure,"async": async });
    },
    listStaticMonthUnitSum: function(unit, year, month, success, failure, async ){
        this.action.invoke({"name": "listStaticMonthUnitSum", "parameter": {"name":unit,"year": year,"month":month  },"success": success,"failure": failure,"async": async });
    },
    listStaticDayTopUnit: function(topUnit, year, month, success, failure){
        this.action.invoke({"name": "listStaticDayTopUnit", "parameter":{"name":topUnit,"year": year,"month":month },"success": success,"failure": failure});
    },
    listStaticMonthTopUnit: function(topUnit, year, month, success, failure){
        this.action.invoke({"name": "listStaticMonthTopUnit", "parameter": {"name":topUnit,"year": year,"month":month  },"success": success,"failure": failure});
    },
    listStaticMonthPersonByUnit: function(unit,year, month, success, failure){
        this.action.invoke({"name": "listStaticMonthPersonByUnit", "parameter": {"name":unit, "year": year,"month":month },"success": success,"failure": failure});
    },
    listStaticMonthPersonByUnitNested : function(unit,year, month, success, failure){
        this.action.invoke({"name": "listStaticMonthPersonByUnitNested", "parameter": {"name":unit, "year": year,"month":month },"success": success,"failure": failure});
    },

    //根据公司查询公司下所有部门的统计数据信息：
    //"listUnitDateStaticByTopUnit" : {"uri":"/jaxrs/statisticshow/unit/day/topUnit/{name}/{date}"},
    listUnitDateStaticByTopUnit: function(topUnit, date, success, failure){
        this.action.invoke({"name": "listUnitDateStaticByTopUnit", "parameter": {"name":topUnit,"date": date },"success": success,"failure": failure});
    },
    //根据公司查询公司下所有部门的统计数据信息：
    //"listUnitMonthStaticByTopUnit" : {"uri":"/jaxrs/statisticshow/unit/topUnit/{name}/{year}/{month}"},
    listUnitMonthStaticByTopUnit: function(topUnit, year, month, success, failure, async ){
        this.action.invoke({"name": "listUnitMonthStaticByTopUnit", "parameter": {"name":topUnit,"year": year,"month":month  },"success": success,"failure": failure,"async": async });
    },
    //根据部门查询部门下所有员工的统计数据信息：
    //"listPersonMonthStaticByUnit" : {"uri":"/jaxrs/statisticshow/persons/unit/subnested/{name}/{year}/{month}"},
    listPersonMonthStaticByUnit: function(unit, year, month, success, failure, async ){
        this.action.invoke({"name": "listPersonMonthStaticByUnit", "parameter": {"name":unit,"year": year,"month":month  },"success": success,"failure": failure,"async": async });
    },

    createAppeal: function(detailId, data, success, failure){
        this.action.invoke({"name": "createAppeal", "parameter": {"detailId": detailId }, "data": data, "success": success,"failure": failure});
    },
    processAppeal: function( data, success, failure, async){
        this.action.invoke({"name": "processAppeal", "async": async, "data": data, "success": success,	"failure": failure});
    },
    process2Appeal: function(data, success, failure, async){
        this.action.invoke({"name": "process2Appeal", "async": async, "data": data, "success": success,	"failure": failure});
    },
    getAppeal: function(id, success, failure,async){
        this.action.invoke({"name": "getAppeal", "parameter": {"id": id}, "async": async, "success": success,"failure": failure});
    },
    deleteAppeal: function(id, success, failure, async){
        this.action.invoke({"name": "deleteAppeal", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    listAppealFilterNext : function( id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listAppealFilterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listAppealFilterPrev : function(  id, count, filterData, success,failure, async){
        this.action.invoke({"name": "listAppealFilterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    listWorkplace: function(success, failure, async){
        this.action.invoke({"name": "listWorkplace","async": async, "success": success,	"failure": failure});
    },
    getWorkplace: function(id, success, failure, async){
        this.action.invoke({"name": "getWorkplace","async": async, "parameter": {"id": id },"success": success,"failure": failure});
    },
    saveWorkplace: function(data, success, failure,async){
        this.action.invoke({"name": "saveWorkplace","async": async,"data": data,"parameter": {"id": data.id},"success": success,"failure": failure});
    },
    deleteWorkplace: function(id, success, failure, async){
        this.action.invoke({"name": "deleteWorkplace","async": async, "parameter": {"id": id}, "success": success,	"failure": failure});
    }

});