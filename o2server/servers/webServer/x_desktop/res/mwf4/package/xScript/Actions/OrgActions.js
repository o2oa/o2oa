MWF.xScript = MWF.xScript || {};
MWF.xScript.Actions = MWF.xScript.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xScript.Actions.OrgActions = new Class({
	initialize: function(){
        this.actionPath = "/xScript/Actions/orgAction.json";
		this.action = new MWF.xDesktop.Actions.RestActions(this.actionPath, "x_organization_assemble_express");
    },

    getDepartmentDutyBydepartmentName: function(name, departmentName, success, failure, async){
        this.action.invoke({"name": "getDepartmentDutyBydepartmentName","async": async, "urlEncode": true, "parameter": {"name": name, "departmentName": departmentName},	"success": success,	"failure": failure});
    },
    getPersonInfo: function(name, success, failure, async){
        this.action.invoke({"name": "getPersonInfo","async": async, "urlEncode": true, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getPersonAttribute: function(name, personName, success, failure, async){
        this.action.invoke({"name": "getPersonAttribute","async": async, "urlEncode": true, "parameter": {"name": name, "personName": personName},	"success": success,	"failure": failure});
    },

    getDepartmentByIdentity: function(name, success, failure, async){
        this.action.invoke({"name": "getDepartmentByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    updatePersonAttribute: function(name, data, success, failure, async){
        this.action.invoke({"name": "updatePersonAttribute", "data":data, "async": async, "urlEncode": true, "parameter": {"name": name},"success": success,	"failure": failure});
    },
    setPersonAttribute: function(name, personName, data, success, failure, async){
        this.action.invoke({"name": "setPersonAttribute", "data":data, "async": async, "urlEncode": true, "parameter": {"name": name, "personName": personName},"success": success,	"failure": failure});
    },
    listSupDepartmentNested: function(name, success, failure, async){
        this.action.invoke({"name": "listSupDepartmentNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listPersonAttribute: function(name, success, failure, async){
        this.action.invoke({"name": "listPersonAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listCompanyByPerson: function(name, success, failure, async){
        this.action.invoke({"name": "listCompanyByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyByIdentity: function(name, success, failure, async){
        this.action.invoke({"name": "getCompanyByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listCompanyAttribute: function(name, success, failure, async){
        this.action.invoke({"name": "listCompanyAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyAttribute: function(name, companyName, success, failure, async){
        this.action.invoke({"name": "getCompanyAttribute","async": async, "parameter": {"name": name, "companyName": companyName},	"success": success,	"failure": failure});
    },
    listCompanyDuty: function(name, success, failure, async){
        this.action.invoke({"name": "listCompanyDuty","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyDuty: function(name, companyName, success, failure, async){
        this.action.invoke({"name": "getCompanyDuty","async": async, "parameter": {"name": name, "companyName": companyName},	"success": success,	"failure": failure});
    },
    listDepartmentAttribute: function(name, success, failure, async){
        this.action.invoke({"name": "listDepartmentAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getDepartmentAttribute: function(name, departmentName, success, failure, async){
        this.action.invoke({"name": "getDepartmentAttribute","async": async, "parameter": {"name": name, "departmentName": departmentName},	"success": success,	"failure": failure});
    },
    listDepartmentDuty: function(name, success, failure, async){
        this.action.invoke({"name": "listDepartmentDuty","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getDepartmentDuty: function(name, departmentName, success, failure, async){
        this.action.invoke({"name": "getDepartmentDuty","async": async, "parameter": {"name": name, "departmentName": departmentName},	"success": success,	"failure": failure});
    },
    listIdentity: function(name, success, failure, async){
        this.action.invoke({"name": "listIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listIdentityNested: function(name, success, failure, async){
        this.action.invoke({"name": "listIdentityNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getSupDepartmentDirect: function(name, success, failure, async){
        this.action.invoke({"name": "getSupDepartmentDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByPerson: function(name, success, failure, async){
        this.action.invoke({"name": "listDepartmentByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listTopCompanyByCompany:function(name, success, failure, async){
        this.action.invoke({"name": "listTopCompanyByCompany","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listCompanyByAttribute:function(name, success, failure, async){
        this.action.invoke({"name": "listCompanyByAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listTopCompany:function(success, failure, async){
        this.action.invoke({"name": "listTopCompany","async": async,	"success": success,	"failure": failure});
    },
    listSubCompanyNest:function(name, success, failure, async){
        this.action.invoke({"name": "listSubCompanyNest","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSubCompanyDirect:function(name, success, failure, async){
        this.action.invoke({"name": "listSubCompanyDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    }
});