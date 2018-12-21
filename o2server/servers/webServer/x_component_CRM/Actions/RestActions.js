MWF.xApplication.CRM = MWF.xApplication.CRM || {};
MWF.xApplication.CRM.Actions = MWF.xApplication.CRM.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.CRM.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_crm_assemble_control", "x_component_CRM");

        this.actionOrg = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_organization_assemble_express", "x_component_CRM");
	},
	listIdentityByPerson: function(success, failure, name, async){
		this.action.invoke({"name": "listIdentityByPerson","async": async, "parameter": {"name": name},	"success": success,	"failure": failure});
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
	listMyRelief: function(success, failure, async){
		this.action.invoke({"name": "listMyRelief","async": async, "success": success,	"failure": failure});
	},
	//获取配置信息
	getProfiles: function(type, success,failure, async){
		this.action.invoke({"name": "getProfiles","parameter": {"type": type}, "async": async, "success": success,	"failure": failure});
	},
	getProvinceList : function(success, failure, async){
		this.action.invoke({"name": "getProvinceList","async": async,"success": success,"failure": failure});
	},
	getCityList : function(data, success, failure, async){
		this.action.invoke({"name": "getCityList","data": data, "async": async,"success": success,"failure": failure});
	},
	getCityListByName : function(data, success, failure, async){
		this.action.invoke({"name": "getCityListByName","data": data, "async": async,"success": success,"failure": failure});
	},
	getAreaList : function(data, success, failure, async){
		this.action.invoke({"name": "getAreaList","data": data, "async": async,"success": success,"failure": failure});
	},
	getAreaListByName : function(data, success, failure, async){
		this.action.invoke({"name": "getAreaListByName","data": data, "async": async,"success": success,"failure": failure});
	},

	//customer
	getCustomerInfo: function(id, success, failure, async){
		this.action.invoke({"name": "getCustomerInfo", "parameter": {"id": id },"success": success,"failure": failure,"async": async});
	},

	getCustomerListNext: function(id, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getCustomerListPrev: function(id, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getCustomerListPage: function(page, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListPage","parameter": {"page": page , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	saveCustomer : function(data, success, failure, async){
		this.action.invoke({"name": "saveCustomer","data": data, "async": async,"success": success,"failure": failure});
	}
});