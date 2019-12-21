MWF.xScript = MWF.xScript || {};
MWF.xScript.Actions = MWF.xScript.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
var invoke = function(serviceName){
    return function(data, success, failure, async){
        this.action.invoke({"name": serviceName,"data": data, "async": async, "success": success,"failure": failure});
    }
};

MWF.xScript.Actions.UnitActions = new Class({
	initialize: function(){
        this.actionPath = "/xScript/Actions/unitAction.json";
		this.action = new MWF.xDesktop.Actions.RestActions(this.actionPath, "x_organization_assemble_express");
    },
    //群组--------
    listGroup: invoke("listGroup"),
    listSubGroupDirect: invoke("listSubGroupDirect"),
    listSubGroupNested: invoke("listSubGroupNested"),
    listSupGroupDirect: invoke("listSupGroupDirect"),
    listSupGroupNested: invoke("listSupGroupNested"),
    listGroupWithPerson: invoke("listGroupWithPerson"),
    groupHasRole: invoke("groupHasRole"),

    //角色--------
    listRole: invoke("listRole"),
    listRoleWithPerson: invoke("listRoleWithPerson"),

    //人员--------
    personHasRole: invoke("personHasRole"),
    listPerson: invoke("listPerson"),
    listPersonSubDirect: invoke("listPersonSubDirect"),
    listPersonSubNested: invoke("listPersonSubested"),
    listPersonSupDirect: invoke("listPersonSupDirect"),
    listPersonSupNested: invoke("listPersonSupNested"),
    listPersonWithGroup: invoke("listPersonWithGroup"),
    listPersonWithRole: invoke("listPersonWithRole"),
    listPersonWithIdentity: invoke("listPersonWithIdentity"),
    listPersonWithUnitDirect: invoke("listPersonWithUnitDirect"),
    listPersonWithUnitNested: invoke("listPersonWithUnitNested"),
    listPersonAllAttribute: invoke("listPersonAllAttribute"),
    listPersonWithAttributeValue: invoke("listPersonWithAttributeValue"),
    listPersonWithAttribute: invoke("listPersonWithAttribute"),

    //人员属性-------
    appendPersonAttribute: invoke("appendPersonAttribute"),
    setPersonAttribute: invoke("setPersonAttribute"),
    getPersonAttribute: invoke("getPersonAttribute"),
    listPersonAttributeName: invoke("listPersonAttributeName"),

    //身份----------
    listIdentity: invoke("listIdentity"),
    listIdentityWithPerson: invoke("listIdentityWithPerson"),
    listIdentityWithUnitDirect: invoke("listIdentityWithUnitDirect"),
    listIdentityWithUnitNested: invoke("listIdentityWithUnitNested"),

    //组织----------
    listUnit: invoke("listUnit"),
    listUnitSubDirect: invoke("listUnitSubDirect"),
    listUnitSubNested: invoke("listUnitSubNested"),
    listUnitSupDirect: invoke("listUnitSupDirect"),
    listUnitSupNested: invoke("listUnitSupNested"),
    getUnitWithIdentityAndLevel: invoke("getUnitWithIdentityAndLevel"),
    getUnitWithIdentityAndType: invoke("getUnitWithIdentityAndType"),
    listUnitSupNestedWithIdentity: invoke("listUnitSupNestedWithIdentity"),
    listUnitWithIdentity: invoke("listUnitWithIdentity"),
    listUnitWithPerson: invoke("listUnitWithPerson"),
    listUnitSupNestedWithPerson: invoke("listUnitSupNestedWithPerson"),
    listUnitWithAttribute: invoke("listUnitWithAttribute"),
    listUnitWithDuty: invoke("listUnitWithDuty"),

    //组织职务-------
    getDuty: invoke("getDuty"),
    listDutyNameWithIdentity: invoke("listDutyNameWithIdentity"),
    listDutyNameWithUnit: invoke("listDutyNameWithUnit"),
    listUnitAllDuty: invoke("listUnitAllDuty"),

    //组织属性-------
    appendUnitAttribute: invoke("appendUnitAttribute"),
    setUnitAttribute: invoke("setUnitAttribute"),
    getUnitAttribute: invoke("getUnitAttribute"),
    listUnitAttributeName: invoke("listUnitAttributeName"),
    listUnitAllAttribute: invoke("listUnitAllAttribute")
});
