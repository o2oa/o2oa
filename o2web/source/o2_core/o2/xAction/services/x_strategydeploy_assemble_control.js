MWF.xAction.RestActions.Action["x_strategydeploy_assemble_control"] = new Class({
    Extends: MWF.xAction.RestActions.Action,

    //////////////////////////组织/////////////////////////////////////////
    //getNewPerson:function(data,success,failure,async){
    //    this.orgAction.invoke({"name": "getNewPerson","data": data, "async": async, "success": success,"failure": failure});
    //},
    //listIdentityWithPerson:function(data,success,failure,async){
    //    this.orgAction.invoke({"name": "listIdentityWithPerson","data": data, "async": async, "success": success,"failure": failure});
    //},
    //listUnitWithIdentity:function(data,success,failure,async){
    //    this.orgAction.invoke({"name": "listUnitWithIdentity","data": data, "async": async, "success": success,"failure": failure});
    //},
    //listWithIdentityObject:function(data,success,failure,async){
    //    this.orgAction.invoke({"name": "listWithIdentityObject","data": data, "async": async, "success": success,"failure": failure});
    //},
    //getUnitDuty:function(data,success,failure,async){
    //    this.orgAction.invoke({"name": "getUnitDuty","data": data, "async": async, "success": success,"failure": failure});
    //},
    //获取顶层组织名称
    //getTopUnit:function(success,failure,async){
    //    this.orgActionAlpha.invoke({"name": "getTopUnit", "async": async, "success": success,	"failure": failure});
    //},
    ///////////////////////////组织///////////////////////////////////////////

    //获取用户头像
    //getPersonIcon: function( name, callback ){
    //    this.orgActionAlpha.getActions(function(){
    //        var url = this.orgActionAlpha.actions.getPersonIcon.uri;
    //        url = url.replace("{flag}", encodeURIComponent(name));
    //        if (callback) callback(this.orgActionAlpha.address+url);
    //    }.bind(this));
    //},

    ///////////////////////////公司工作重点///////////////////////////////
    //getKeyWorkListYear:function(success,failure,async){
    //    this.action.invoke({"name": "getKeyWorkListYear", "async": async, "success": success,	"failure": failure});
    //},
    //getKeyWorkDepartmentByYear:function(year,success,failure,async){
    //    this.action.invoke({"name": "getKeyWorkDepartmentByYear","parameter": {"year": year }, "async": async, "success": success,	"failure": failure});
    //},
    //getKeyWorkListNext : function(id, count, filterData, success,failure, async){
    //    this.action.invoke({"name": "getKeyWorkListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    //},
    //getKeyWorkListPage : function(page, count, filterData, success,failure, async){
    //    this.action.invoke({"name": "getKeyWorkListPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    //},
    //getKeyWorkById:function(id,success,failure,async){
    //    this.action.invoke({"name": "getKeyWorkById","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //saveKeyWork:function(data,success,failure,async){
    //    this.action.invoke({"name":"saveKeyWork","data":data,"success":success,"failure":failure,async:async})
    //},
    //deleteKeyWork:function(id,success,failure,async){
    //    this.action.invoke({"name": "deleteKeyWork","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //deleteKeyWorkAll:function(id,success,failure,async){
    //    this.action.invoke({"name": "deleteKeyWorkAll","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //changeKeyWorkPosition:function(data,success,failure,async){
    //    this.action.invoke({"name":"changeKeyWorkPosition","data":data,"success":success,"failure":failure,async:async})
    //},
    //getKeyWorkAddAuthorize:function(success,failure,async){
    //    this.action.invoke({"name": "getKeyWorkAddAuthorize", "async": async, "success": success,	"failure": failure});
    //},

    ///////////////////////////公司工作重点///////////////////////////////

    ///////////////////////////举措///////////////////////////////
    //getMeasureListYear:function(success,failure,async){
    //    this.action.invoke({"name": "getMeasureListYear", "async": async, "success": success,	"failure": failure});
    //},
    //getMeasureDepartmentByYear:function(year,success,failure,async){
    //    this.action.invoke({"name": "getMeasureDepartmentByYear","parameter": {"year": year }, "async": async, "success": success,	"failure": failure});
    //},
    //getMeasureListNext : function(id, count, filterData, success,failure, async){
    //    this.action.invoke({"name": "getMeasureListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    //},
    //getMeasureListPage : function(page, count, filterData, success,failure, async){
    //    this.action.invoke({"name": "getMeasureListPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    //},
    //getMeasureById:function(id,success,failure,async){
    //    this.action.invoke({"name": "getMeasureById","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //saveMeasure:function(data,success,failure,async){
    //    this.action.invoke({"name":"saveMeasure","data":data,"success":success,"failure":failure,async:async})
    //},
    //deleteMeasure:function(id,success,failure,async){
    //    this.action.invoke({"name": "deleteMeasure","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //getMeasureAddAuthorize:function(success,failure,async){
    //    this.action.invoke({"name": "getMeasureAddAuthorize", "async": async, "success": success,	"failure": failure});
    //},
    //getMeasureMaxNumber:function(parentid,success,failure,async){
    //    this.action.invoke({"name": "getMeasureMaxNumber","parameter": {"parentid": parentid }, "async": async, "success": success,	"failure": failure});
    //},
    //importMeasure: function(success, failure, formData, file){
    //    this.action.invoke({"name": "importMeasure",  "data": formData,"file": file,"success": success,"failure": failure});
    //},
    //getErrorImportExcel:function(id,success,failure,async){
    //    this.action.invoke({"name": "getErrorImportExcel","parameter": {"flag": id }, "async": async, "success": success,	"failure": failure});
    //},
    //changeMeasurePosition:function(data,success,failure,async){
    //    this.action.invoke({"name":"changeMeasurePosition","data":data,"success":success,"failure":failure,async:async})
    //},
    //getMeasureByParentId:function(parentid,success,failure,async){
    //    this.action.invoke({"name": "getMeasureByParentId","parameter": {"parentid": parentid }, "async": async, "success": success,	"failure": failure});
    //},
    //exportMeasure:function(year,success,failure,async){
    //    this.action.invoke({"name": "exportMeasure","parameter": {"year": year }, "async": async, "success": success,	"failure": failure});
    //},
    //downloadFile:function(flag,success,failure,async){
    //    this.action.invoke({"name": "downloadFile","parameter": {"flag": flag }, "async": async, "success": success,	"failure": failure});
    //},
    ///////////////////////////举措///////////////////////////////

    ///////////////////////////五项///////////////////////////////
    //getPriorityDepartments:function(success,failure,async){
    //    this.action.invoke({"name": "getPriorityDepartments", "async": async, "success": success,	"failure": failure});
    //},
    //getYearsByDepartment:function(data,success,failure,async){
    //    this.action.invoke({"name":"getYearsByDepartment","data":data,"success":success,"failure":failure,async:async})
    //},
    //getPriorityListNext : function(id, count, filterData, success,failure, async){
    //    this.action.invoke({"name": "getPriorityListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    //},
    //getPriorityById:function(id,success,failure,async){
    //    this.action.invoke({"name": "getPriorityById","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //savePriority:function(data,success,failure,async){
    //    this.action.invoke({"name":"savePriority","data":data,"success":success,"failure":failure,async:async})
    //},
    //deletePriority:function(id,success,failure,async){
    //    this.action.invoke({"name": "deletePriority","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
    //},
    //getPriorityAddAuthorize:function(success,failure,async){
    //    this.action.invoke({"name": "getPriorityAddAuthorize", "async": async, "success": success,	"failure": failure});
    //},
    //
    //listPriorityAttachment: function(workId, success, failure, async){
    //    this.action.invoke({"name": "listPriorityAttachment","async": async, "parameter": {"workId": workId},	"success": success,	"failure": failure});
    //},
    //uploadPriorityAttachment: function(workId, success, failure, formData, file){
    //    this.action.invoke({"name": "uploadPriorityAttachment", "parameter": {"workId": workId},"data": formData,"file": file,"success": success,"failure": failure});
    //},
    //getPriorityAttachment: function(id, workId, success, failure, async){
    //    this.action.invoke({"name": "getPriorityAttachment","async": async, "parameter": {"id": id, "workId": workId},	"success": success,	"failure": failure});
    //},
    //deletePriorityAttachment: function(id, workId, success, failure, async){
    //    this.action.invoke({"name": "deletePriorityAttachment","async": async, "parameter": {"id": id, "workId": workId},	"success": success,	"failure": failure});
    //},
    getPriorityAttachmentData: function(id, workId){
        this.action.getActions(function(){
            var url = this.action.actions.getPriorityAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workId}", encodeURIComponent(workId));
            window.open(o2.filterUrl(this.actionAttachment.address+url));
        }.bind(this));
    },
    getPriorityAttachmentStream: function(id, workId){
        this.action.getActions(function(){
            var url = this.action.actions.getPriorityAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{workId}", encodeURIComponent(workId));
            window.open(o2.filterUrl(this.action.address+url));
        }.bind(this));
    },
    ///////////////////////////五项///////////////////////////////

    //listDeptList2:function(level,success,failure,async){
    //    this.action.invoke({"name": "listDeptList2","parameter": {"level": level }, "async": async, "success": success,	"failure": failure});
    //},





    aa:function(){}
});