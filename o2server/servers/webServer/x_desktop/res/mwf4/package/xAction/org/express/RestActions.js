MWF.xAction = MWF.xAction || {};
MWF.xAction.org = MWF.xAction.org || {};
MWF.xAction.org.express = MWF.xAction.org.express || {};

MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.xAction.org.express.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_Selector");
	},
    //公司---------------------------------------------------------------
    getSupCompanyDirect: function(success, failure, name, async){
        this.action.invoke({"name": "getSupCompanyDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSupCompanyNested: function(success, failure, name, async){
        this.action.invoke({"name": "listSupCompanyNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyByDepartment: function(success, failure, name, async){
        this.action.invoke({"name": "getCompanyByDepartment","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyByIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "getCompanyByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listCompanyByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listCompanyByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listCompanyByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listCompanyByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listCompanyByPinyininitial: function(success, failure, key, async){
        this.action.invoke({"name": "listCompanyByPinyininitial","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listCompanyByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listCompanyByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listTopCompany: function(success, failure, async){
        this.action.invoke({"name": "listTopCompany","async": async,	"success": success,	"failure": failure});
    },
    listSubCompany: function(success, failure, name, async){
        this.action.invoke({"name": "listSubCompanyDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSubCompanyNested: function(success, failure, name, async){
        this.action.invoke({"name": "listSubCompanyNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompany: function(success, failure, name, async){
        this.action.invoke({"name": "getCompany","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    //公司属性------------------------------------------------------------
    listCompanyAttribute: function(success, failure, name, async){
        this.action.invoke({"name": "listCompanyAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyAttribute: function(success, failure, name, companyName, async){
        this.action.invoke({"name": "getCompanyAttribute","async": async, "parameter": {"name": name, "companyName": companyName},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    //公司职务------------------------------------------------------------
    listCompanyDuty: function(success, failure, name, async){
        this.action.invoke({"name": "listCompanyDuty","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getCompanyDuty: function(success, failure, name, companyName, async){
        this.action.invoke({"name": "getCompanyDuty","async": async, "parameter": {"name": name, "companyName": companyName},	"success": success,	"failure": failure});
    },
    listCompanyDutyByIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "listCompanyDutyByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    listSubComplexDirect: function(success, failure, name, async){
        this.action.invoke({"name": "listSubComplexDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    //部门---------------------------------------------------------------
    getSupDepartmentDirect: function(success, failure, name, async){
        this.action.invoke({"name": "getSupDepartmentDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSupDepartmentNested: function(success, failure, name, async){
        this.action.invoke({"name": "listSupDepartmentNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getDepartmentByIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "getDepartmentByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listDepartmentByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    listDepartment: function(success, failure, name, async){
        this.action.invoke({"name": "listDepartmentDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSubDepartment: function(success, failure, name, async){
        this.action.invoke({"name": "listSubDepartmentDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSubDepartmentNested: function(success, failure, name, async){
        this.action.invoke({"name": "listSubDepartmentNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listDepartmentByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listDepartmentByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listDepartmentByPinyininitial: function(success, failure, key, async){
        this.action.invoke({"name": "listDepartmentByPinyininitial","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listDepartmentByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listDepartmentByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    getDepartment: function(success, failure, name, async){
        this.action.invoke({"name": "getDepartment","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    //部门属性------------------------------------------------------------
    listDepartmentAttribute: function(success, failure, name, async){
        this.action.invoke({"name": "listDepartmentAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getDepartmentAttribute: function(success, failure, name, departmentName, async){
        this.action.invoke({"name": "getDepartmentAttribute","async": async, "parameter": {"name": name, "departmentName": departmentName},	"success": success,	"failure": failure});
    },

    //-------------------------------------------------------------------

    //部门职务------------------------------------------------------------
    listDepartmentDuty: function(success, failure, name, async){
        this.action.invoke({"name": "listDepartmentDuty","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getDepartmentDuty: function(success, failure, name, departmentName, async){
        this.action.invoke({"name": "getDepartmentDuty","async": async, "parameter": {"name": name, "departmentName": departmentName},	"success": success,	"failure": failure});
    },
    listDepartmentDutyByIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "listDepartmentDutyByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    //身份---------------------------------------------------------------
    listIdentityByKeyFromRange: function(success, failure, name, async){
        this.action.invoke({"name": "listIdentityByKeyFromRange","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "listIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listIdentityNested: function(success, failure, name, async){
        this.action.invoke({"name": "listIdentityNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listIdentityByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listIdentityByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listIdentityByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listIdentityByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listIdentityByPinyininitial: function(success, failure, key, async){
        this.action.invoke({"name": "listIdentityByPinyininitial","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listIdentityByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listIdentityByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "getIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------


    //群组---------------------------------------------------------------
    listGroupByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listGroupByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listGroupByPinyininitial: function(success, failure, key, async){
        this.action.invoke({"name": "listGroupByPinyininitial","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listGroupByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listGroupByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listGroupDirectByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listGroupDirectByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listGroupNestedByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listGroupNestedByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    listSubGroupDirect: function(success, failure, name, async){
        this.action.invoke({"name": "listSubGroupDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSubGroupNested: function(success, failure, name, async){
        this.action.invoke({"name": "listSubGroupNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSupGroupDirect: function(success, failure, name, async){
        this.action.invoke({"name": "listSupGroupDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listSupGroupNested: function(success, failure, name, async){
        this.action.invoke({"name": "listSupGroupNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getGroup: function(success, failure, name, async){
        this.action.invoke({"name": "getGroup","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listGroupNext: function(last, count, success, failure, async){
        this.action.invoke({"name": "listGroupNext","async": async, "parameter": {"name": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    listGroupPrev: function(last, count, success, failure, async){
        this.action.invoke({"name": "listGroupPrev","async": async, "parameter": {"name": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------


    //人员---------------------------------------------------------------
    getPersonByIdentity: function(success, failure, name, async){
        this.action.invoke({"name": "getPersonByIdentity","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listPersonDirect: function(success, failure, name, async){
        this.action.invoke({"name": "listPersonDirect","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listPersonNested: function(success, failure, name, async){
        this.action.invoke({"name": "listPersonNested","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    listPersonByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listPersonByPinyin","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listPersonByPinyininitial: function(success, failure, key, async){
        this.action.invoke({"name": "listPersonByPinyininitial","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listPersonByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listPersonByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },

    getPerson: function(success, failure, name, async){
        this.action.invoke({"name": "getPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    listPersonNext: function(last, count, success, failure, async){
        this.action.invoke({"name": "listPersonNext","async": async, "parameter": {"name": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    listPersonPrev: function(last, count, success, failure, async){
        this.action.invoke({"name": "listPersonPrev","async": async, "parameter": {"name": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    //人员属性------------------------------------------------------------
    listPersonAttribute: function(success, failure, name, async){
        this.action.invoke({"name": "listPersonAttribute","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    getPersonAttribute: function(success, failure, name, personName, async){
        this.action.invoke({"name": "getPersonAttribute","async": async, "parameter": {"name": name, "personName": personName},	"success": success,	"failure": failure});
    },
    //-------------------------------------------------------------------

    //角色---------------------------------------------------------------
    listRoleByGroup: function(success, failure, name, async){
        this.action.invoke({"name": "listRoleByGroup","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listRoleByPerson: function(success, failure, name, async){
        this.action.invoke({"name": "listRoleByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },
    listRoleByPinyin: function(success, failure, key, async){
        this.action.invoke({"name": "listRoleByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listRoleByPinyininitial: function(success, failure, key, async){
        this.action.invoke({"name": "listRoleByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    listRoleByKey: function(success, failure, key, async){
        this.action.invoke({"name": "listRoleByKey","async": async, "parameter": {"key": key},	"success": success,	"failure": failure});
    },
    getRole: function(success, failure, name, async){
        this.action.invoke({"name": "getRole","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
    },

    listRoleNext: function(last, count, success, failure, async){
        this.action.invoke({"name": "listRoleNext","async": async, "parameter": {"name": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    },
    listRolePrev: function(last, count, success, failure, async){
        this.action.invoke({"name": "listRolePrev","async": async, "parameter": {"name": last || "(0)", "count": count || "20"},	"success": success,	"failure": failure});
    }
    //-------------------------------------------------------------------
});