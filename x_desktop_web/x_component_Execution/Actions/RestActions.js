MWF.xApplication.Execution = MWF.xApplication.Execution || {};
MWF.xApplication.Execution.Actions = MWF.xApplication.Execution.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.Execution.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_okr_assemble_control", "x_component_Execution");

        //alert(JSON.stringify(this.action))
        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_Execution");
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
    login : function(data, success, failure, async){
        this.action.invoke({"name": "login","data": data, "async": async, "success": success,"failure": failure});
    },
    logout : function(data, success, failure, async){
        this.action.invoke({"name": "logout","data": data, "async": async, "success": success,"failure": failure});
    },
    getDepartmentGather : function(id, success, failure, async){
        this.action.invoke({"name": "getDepartmentGather", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    getTaskListNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getTaskListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getTaskListPrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getTaskListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkConditionListNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkConditionListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkConditionListPrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkConditionListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    readDone : function(id, success, failure, async){
        this.action.invoke({"name": "readDone", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },

    getProfileByCode: function(data, success, failure, async){
        this.action.invoke({"name": "getProfileByCode", "data":data,"success": success,"failure": failure,"async": async});
    },
    getMyStat: function(success, failure, async){
        this.action.invoke({"name": "getMyStat","success": success,"failure": failure,"async": async});
    },

    listCompanyByPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "listCompanyByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "listDepartmentByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByCompany: function(success, failure, companyName, async){
        this.actionOrg.invoke({"name": "listDepartmentByCompany","async": async, "parameter": {"companyName": companyName},	"success": success,	"failure": failure});
    },
    getDepartmentDuty: function(success, failure, name, departmentName, async){
        this.actionOrg.invoke({"name": "getDepartmentDuty","async": async, "parameter": {"name": name, "departmentName": departmentName},	"success": success,	"failure": failure});
    },
    getPersonByIdentity: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "getPersonByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listIdentityByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listIdentityByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getPerson: function(success, failure, name, async){
        this.actionOrg.invoke({"name": "getPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    listDetailFilterNext : function( id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listDetailFilterPrev : function(  id, count, filterData, success,failure, async){
        this.action.invoke({"name": "listDetailFilterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    workReportDrafter : function(id, success, failure, async){
        this.action.invoke({"name": "workReportDrafter", "parameter": {"workId": id },"success": success,"failure": failure,"async": async});
    },
    getWorkReport: function(id, success, failure, async){
        this.action.invoke({"name": "getWorkReport", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    saveWorkReport : function(data, success, failure, async){
        this.action.invoke({"name": "saveWorkReport","data": data, "async": async,"success": success,"failure": failure});
    }, 
    submitWorkReport : function(data, success, failure, async){
        this.action.invoke({"name": "submitWorkReport","data": data, "async": async,"success": success,"failure": failure});
    },
    getWorkReportList : function(id, success, failure, async){
        this.action.invoke({"name": "getWorkReportList", "parameter": {"workId": id },"success": success,"failure": failure,"async": async});
    },
    deleteWortReport: function(id, success, failure, async){
        this.action.invoke({"name": "deleteWortReport", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    getWorkReportOpinion : function(data, success, failure, async){
        this.action.invoke({"name": "getWorkReportOpinion","data": data, "async": async,"success": success,"failure": failure});
    },

    getWorkReportDrafterNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportDrafterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkReportDrafterPrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportDrafterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkReportTodoNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportTodoNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkReportTodoPrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportTodoPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkReportDoneNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportDoneNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkReportDonePrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportDonePrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getWorkReportArchiveNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getWorkReportArchiveNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    getCenterWorkDrafterListNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getCenterWorkDrafterListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getCenterWorkDrafterListPrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getCenterWorkDrafterListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    getCenterWorkDeployListNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getCenterWorkDeployListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getCenterWorkDeployListPrev : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getCenterWorkDeployListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    getCenterWorkListNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getCenterWorkListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getCenterWorkArchiveListNext : function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getCenterWorkArchiveListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    saveMainTask : function(data, success, failure, async){
        this.action.invoke({"name": "saveMainTask","data": data, "async": async,"success": success,"failure": failure});

    },
    getMainTask: function(id, success, failure, async){
        this.action.invoke({"name": "getMainTask", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    archiveMainTask: function(id, success, failure, async){
        this.action.invoke({"name": "archiveMainTask", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    deleteCenterWork: function(id, success, failure, async){
        this.action.invoke({"name": "deleteCenterWork", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    saveTask : function(data,success,failure,async){
        this.action.invoke({"name":"saveTask","data":data ,"async" : async, "success":success,"failure":failure});
    },
    getTask: function(id,success,failure, async){
        this.action.invoke({"name":"getTask", "parameter": {"id": id },"success": success,"failure":failure,"async" : async})
    },
    getUserBaseWork :function(id, success, failure, async){
        this.action.invoke({"name": "getUserBaseWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    getUserProcessBaseWork :function(id, success, failure, async){
        this.action.invoke({"name": "getUserProcessBaseWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    getUserDeployBaseWork :function(id, success, failure, async){
        this.action.invoke({"name": "getUserDeployBaseWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    getUserNestBaseWork :function(id, success, failure, async){
        this.action.invoke({"name": "getUserNestBaseWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    deleteBaseWork: function(id, success, failure, async){
        this.action.invoke({"name": "deleteBaseWork", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    deployBaseWork : function(data, success, failure, async){
        this.action.invoke({"name": "deployBaseWork","data": data, "async": async,"success": success,"failure": failure});
    },
    appointBaseWork : function(data, success, failure, async){
        this.action.invoke({"name": "appointBaseWork","data": data, "async": async,"success": success,"failure": failure});
    },
    unAppointBaseWork : function(data, success, failure, async){
        this.action.invoke({"name": "unAppointBaseWork","data": data, "async": async,"success": success,"failure": failure});
    },

    importBaseWork: function(id,success, failure, formData, file){
        this.action.invoke({"name": "importBaseWork", "parameter": {"centerId": id }, "data": formData,"file": file,"success": success,"failure": failure});
    },
    getBaseWorkDetails: function(id, success, failure, async){
        this.action.invoke({"name": "getBaseWorkDetails", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    getBaseWorksByParentId: function(id, success, failure, async){
        this.action.invoke({"name": "getBaseWorksByParentId", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    getBaseWorkListMyDrafterNext: function(id, count,  filterData, success,failure, async){   //具体工作我的草稿
        this.action.invoke({"name": "getBaseWorkListMyDrafterNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyDrafterPrev: function(id, count,  filterData, success,failure, async){ //具体工作我的草稿
        this.action.invoke({"name": "getBaseWorkListMyDrafterPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyDeployNext: function(id, count,  filterData, success,failure, async){ //具体工作我部署的
        this.action.invoke({"name": "getBaseWorkListMyDeployNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyDeployPrev: function(id, count,  filterData, success,failure, async){ //具体工作我部署的
        this.action.invoke({"name": "getBaseWorkListMyDeployPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyDoNext: function(id, count,  filterData, success,failure, async){ //具体工作我负责的
        this.action.invoke({"name": "getBaseWorkListMyDoNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyDoPrev: function(id, count,  filterData, success,failure, async){ //具体工作我负责的
        this.action.invoke({"name": "getBaseWorkListMyDoPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyAssistNext: function(id, count,  filterData, success,failure, async){ //具体工作我协助的的
        this.action.invoke({"name": "getBaseWorkListMyAssistNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyAssistPrev: function(id, count,  filterData, success,failure, async){ //具体工作我协助的
        this.action.invoke({"name": "getBaseWorkListMyAssistPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyReadNext: function(id, count,  filterData, success,failure, async){ //具体工作我阅知的
        this.action.invoke({"name": "getBaseWorkListMyReadNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyReadPrev: function(id, count,  filterData, success,failure, async){ //具体工作我阅知的
        this.action.invoke({"name": "getBaseWorkListMyReadPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyAppointNext: function(id, count,  filterData, success,failure, async){ //具体工作我委托的
        this.action.invoke({"name": "getBaseWorkListMyAppointNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyAppointPrev: function(id, count,  filterData, success,failure, async){ //具体工作我委托的
        this.action.invoke({"name": "getBaseWorkListMyAppointPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getBaseWorkListMyArchiveNext: function(id, count,  filterData, success,failure, async){ //具体工作我委托的
        this.action.invoke({"name": "getBaseWorkListMyArchiveNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    //chat
    submitChat : function(data, success, failure, async){
        this.action.invoke({"name": "submitChat","data": data, "async": async,"success": success,"failure": failure});
    },
    getChatListNext: function(id, count,  filterData, success,failure, async){ //具体工作我委托的
        this.action.invoke({"name": "getChatListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getChatListPrev: function(id, count,  filterData, success,failure, async){ //具体工作我委托的
        this.action.invoke({"name": "getChatListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    deleteConfig: function(id, success, failure, async){
        this.action.invoke({"name": "deleteConfig", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    saveConfig : function(data,success,failure,async){
        this.action.invoke({"name":"saveConfig","data":data ,"async" : async, "success":success,"failure":failure});
    },
    getConfig: function(id,success,failure,async){
        this.action.invoke({"name":"getConfig", "async": async, "parameter": {"id": id },"success": success,"failure":failure})
    },
    listConfigAll: function( success,failure, async){
        this.action.invoke({"name": "listConfigAll", "async": async, "success": success,	"failure": failure});
    },
    listMyRelief: function(success, failure, async){
        this.action.invoke({"name": "listMyRelief","async": async, "success": success,	"failure": failure});
    },
    deleteSecretary: function(id, success, failure, async){
        this.action.invoke({"name": "deleteSecretary", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    saveSecretary : function(data,success,failure,async){
        this.action.invoke({"name":"saveSecretary","data":data ,"async" : async, "success":success,"failure":failure});
    },
    getSecretary: function(id,success,failure){
        this.action.invoke({"name":"getSecretary", "parameter": {"id": id },"success": success,"failure":failure})
    },
    listSecretaryNext: function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listSecretaryNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    listSecretaryPrev: function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "listSecretaryPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
    },

    deleteCategory: function(id, success, failure, async){
        this.action.invoke({"name": "deleteCategory", "async": async, "parameter": {"id": id}, "success": success, "failure": failure});
    },
    saveCategory : function(data,success,failure,async){
        this.action.invoke({"name":"saveCategory","data":data ,"async" : async, "success":success,"failure":failure});
    },
    getCategory: function(id,success,failure,async){
        this.action.invoke({"name":"getCategory", "async": async, "parameter": {"id": id },"success": success,"failure":failure})
    },
    listCategoryAll: function( success,failure, async){
        this.action.invoke({"name": "listCategoryAll", "async": async, "success": success,	"failure": failure});
    },
    getCategoryCountAll: function( success,failure, async){
        this.action.invoke({"name": "getCategoryCountAll", "async": async, "success": success,	"failure": failure});
    },
    getCategoryMyCountAll: function( success,failure, async){
        this.action.invoke({"name": "getCategoryMyCountAll", "async": async, "success": success,	"failure": failure});
    },

    listAttachment: function(documentid, success, failure, async){
        this.action.invoke({"name": "listAttachment","async": async, "parameter": {"documentid": documentid},	"success": success,	"failure": failure});
    },
    uploadAttachment: function(documentid, success, failure, formData, file){
        this.action.invoke({"name": "uploadAttachment", "parameter": {"documentid": documentid},"data": formData,"file": file,"success": success,"failure": failure});
    },
    //replaceAttachment: function(id, documentid, success, failure, formData, file){
    //    this.action.invoke({"name": "replaceAttachment", "parameter": {"documentid": documentid, "id": id},"data": formData,"file": file,"success": success,"failure": failure});
    //},
    getAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "getAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    deleteAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "deleteAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    getAttachmentData: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getAttachmentStream: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },

    getAttachmentUrl: function(id, documentid, callback){
        this.action.getActions(function(){
            var url = this.action.actions.getAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            if (callback) callback(this.action.address+url);
        }.bind(this));
    },


    deleteReportAttachment: function(id, documentid, success, failure, async){
        this.action.invoke({"name": "deleteReportAttachment","async": async, "parameter": {"id": id, "documentid": documentid},	"success": success,	"failure": failure});
    },
    getReportAttachmentData: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getReportAttachmentData.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.actionAttachment.address+url);
        }.bind(this));
    },
    getReportAttachmentStream: function(id, documentid){
        this.action.getActions(function(){
            var url = this.action.actions.getReportAttachmentStream.uri;
            url = url.replace("{id}", encodeURIComponent(id));
            url = url.replace("{documentid}", encodeURIComponent(documentid));
            window.open(this.action.address+url);
        }.bind(this));
    },

    listReportAttachment: function(documentid, success, failure, async){
        this.action.invoke({"name": "listReportAttachment","async": async, "parameter": {"documentid": documentid},	"success": success,	"failure": failure});
    },
    uploadReportAttachment: function(documentid, success, failure, formData, file){
        this.action.invoke({"name": "uploadReportAttachment", "parameter": {"documentid": documentid},"data": formData,"file": file,"success": success,"failure": failure});
    },


    getStatListForCenterWork: function(data,success,failure,async){
        this.action.invoke({"name": "getStatListForCenterWork","data": data, "async": async,"success": success,"failure": failure});
    },
    getStatByWorkId: function(id,parentWorkId,success,failure,async){
        this.action.invoke({"name":"getStatByWorkId","parameter": {"id": id,"parentWorkId":parentWorkId },"async": async, "success": success,	"failure": failure})
    },


    //************************tidy*********************************
    //*******************中心工作*******************
    //中心工作创建权限
    createCenterWorkAuthorization: function(success,failure, async){
        this.action.invoke({"name":"createCenterWorkAuthorization", "success": success,"failure":failure,"async" : async})
    },
    //获取中心工作内容
    getCenterWorkInfo: function(id, success, failure, async){
        this.action.invoke({"name": "getCenterWorkInfo", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    //中心工作保存
    saveCenterWork : function(data, success, failure, async){
        this.action.invoke({"name": "saveCenterWork","data": data, "async": async,"success": success,"failure": failure});
    },
    //中心工作部署
    deployCenterWork : function(id, success, failure, async){
        this.action.invoke({"name": "deployCenterWork", "parameter": {"centerId": id },"success": success,"failure": failure,"async": async});
    },
    //*******************中心工作*******************
    //*******************具体工作*******************
    getBaseWorkInfo: function(id,success,failure, async){
        this.action.invoke({"name":"getBaseWorkInfo", "parameter": {"id": id },"success": success,"failure":failure,"async" : async})
    },
    //获取全部具体工作
    getBaseWorkListAllNext: function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getBaseWorkListAllNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,"failure": failure});
    },
    getBaseWorkListAllPrev: function(id, count,  filterData, success,failure, async){
        this.action.invoke({"name": "getBaseWorkListAllPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,"failure": failure});
    },
    //在部署界面获取全部自己部署的工作
    getMyDeployWork:function(id, success, failure, async){
        this.action.invoke({"name": "getMyDeployWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    //在部署界面获取全部自己参与的工作
    getMyRelativeWork :function(id, success, failure, async){
        this.action.invoke({"name": "getMyRelativeWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    //具体工作归档
    archiveBaseWork: function(id, success, failure, async){
        this.action.invoke({"name": "archiveBaseWork", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    //修改具体工作进展
    progressBaseWork: function(id,percent,success,failure,async){
        this.action.invoke({"name":"progressBaseWork","parameter": {"id": id,"percent":percent },"async": async, "success": success,	"failure": failure})
    },
    //*******************具体工作*******************
    //*******************工作汇报*******************

    //*******************工作汇报*******************
    //*******************主页*******************

    //*******************主页*******************
    //*******************统计*******************
    //获取时间段内的统计信息
    getStatType : function(filterData, success,failure, async){
        this.action.invoke({"name": "getStatType", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    //获取时间段内所有汇报周期列表
    getStatDateList : function(filterData, success,failure, async){
        this.action.invoke({"name": "getStatDateList", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    getStatDate : function(filterData, success,failure, async){
        this.action.invoke({"name": "getStatDate", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    //导出单个时间得EXCEL
    exportByCenterWork : function(filterData, success,failure, async){
        this.action.invoke({"name": "exportByCenterWork", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    //按部门导出
    exportByDeptWork : function(filterData, success,failure, async){
        this.action.invoke({"name": "exportByDeptWork", "data": filterData, "async": async, "success": success,	"failure": failure});
    },
    //*******************统计*******************
    //*******************脑图*******************
    getUserMind :function(id, success, failure, async){
        this.action.invoke({"name": "getUserMind", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
    },
    //*******************脑图*******************
    //***********************tidy*********************************

    test:function(){}
});