MWF.xApplication.CRM = MWF.xApplication.CRM || {};
MWF.xApplication.CRM.Actions = MWF.xApplication.CRM.Actions || {};
MWF.require("MWF.xDesktop.Actions.RestActions", null, false);
MWF.require("MWF.widget.UUID", null, false);
MWF.xApplication.CRM.Actions.RestActions = new Class({
	initialize: function(){
		this.action = new MWF.xDesktop.Actions.RestActions("/Actions/action.json", "x_wcrm_assemble_control", "x_component_CRM");
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

	//Attachment
	updateAttachment: function(preview, wcrmId, wcrmModule,  formData, file, success,failure){
		this.action.invoke({"name": "updateAttachment","parameter": {"preview": preview ,"wcrmId": wcrmId , "wcrmModule" : wcrmModule}, "data": formData, "file": file, "success": success,	"failure": failure});
	},
	getAttachment: function(wcrmId, success, failure, async){
		this.action.invoke({"name": "getAttachment", "parameter": {"wcrmId": wcrmId },"success": success,"failure": failure,"async": false});
	},
	delAttachment: function(id, success, failure, async){
		this.action.invoke({"name": "delAttachment", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	downloadAttachment: function(id, wcrmId, success, failure, async){
		this.action.invoke({"name": "downloadAttachment", "parameter": {"id": id, "wcrmId":wcrmId},"success": success,"failure": failure,"async": false});
	},

	//customer
	getCustomerInfo: function(customerid, success, failure, async){
		this.action.invoke({"name": "getCustomerInfo", "parameter": {"customerid": customerid },"success": success,"failure": failure,"async": false});
	},
	getCustomerListPage: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListPage","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListMyDuty_customer: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListMyDuty_customer","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListNestedSubPerson_customer: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListNestedSubPerson_customer","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListMyParticipate_customer: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListMyParticipate_customer","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListAllMy_customer: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListAllMy_customer","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getCustomerListNext: function(id, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getCustomerListPrev: function(id, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	updateCustomer: function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "updateCustomer","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	saveCustomer : function(data, success, failure, async){
		this.action.invoke({"name": "saveCustomer","data": data, "async": async,"success": success,"failure": failure});
	},
	customerDealStatus_completed : function(customerid, success, failure, async){
		this.action.invoke({"name": "customerDealStatus_completed", "parameter": {"customerid": customerid },"success": success,"failure": failure,"async": false});
	},
	customerDealStatus_processing : function(customerid, success, failure, async){
		this.action.invoke({"name": "customerDealStatus_processing", "parameter": {"customerid": customerid },"success": success,"failure": failure,"async": false});
	},
	lockCustomer : function(customerid, success, failure, async){
		this.action.invoke({"name": "lockCustomer", "parameter": {"customerid": customerid },"success": success,"failure": failure,"async": false});
	},
	unLockCustomer : function(customerid, success, failure, async){
		this.action.invoke({"name": "unLockCustomer", "parameter": {"customerid": customerid },"success": success,"failure": failure,"async": false});
	},
	customerTransfer : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "customerTransfer","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	putToCustomerPool : function(id, success, failure, async){
		this.action.invoke({"name": "putToCustomerPool", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	getTeamMemberListById : function(id, success, failure, async){
		this.action.invoke({"name": "getTeamMemberListById", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	setTeamReader : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "setTeamReader","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	setTeamWriter : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "setTeamWriter","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	removeTeamMember: function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "removeTeamMember","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	addRelevantPerson: function(id, writeReadSymbol,  filterData, success,failure, async){
		this.action.invoke({"name": "addRelevantPerson","parameter": {"id": id , "writeReadSymbol" : writeReadSymbol }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},


	//contacts
	getContacts: function(customerid, success, failure, async){
		this.action.invoke({"name": "getContacts", "parameter": {"CustomerId": customerid },"success": success,"failure": failure,"async": false});
	},
	getContactsById: function(contactsid, success, failure, async){
		this.action.invoke({"name": "getContactsById", "parameter": {"contactsid": contactsid },"success": success,"failure": failure,"async": false});
	},
	saveContacts : function(data, success, failure, async){
		this.action.invoke({"name": "saveContacts","data": data, "async": async,"success": success,"failure": failure});
	},
	getContactsListPage: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "getContactsListPage","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getContactsInfo: function(contactsid, success, failure, async){
		this.action.invoke({"name": "getContactsInfo", "parameter": {"contactsid": contactsid },"success": success,"failure": failure,"async": false});
	},
	listOpportunityByContactsId: function(id, success, failure, async){
		this.action.invoke({"name": "listOpportunityByContactsId", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	contactsTransfer : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "contactsTransfer","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	updateContacts: function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "updateContacts","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},

	//record
	createRecord : function(data, success, failure, async){
		this.action.invoke({"name": "createRecord","data": data, "async": async,"success": success,"failure": failure});
	},
	getRecord: function(crmId, success, failure, async){
		this.action.invoke({"name": "getRecord", "parameter": {"crmId": crmId },"success": success,"failure": failure,"async": false});
	},
	getOptionsRecord: function(crmid, success, failure, async){
		this.action.invoke({"name": "getOptionsRecord", "parameter": {"crmid": crmid },"success": success,"failure": failure,"async": false});
	},


	//clue
	getClueInfo: function(id, success, failure, async){
		this.action.invoke({"name": "getClueInfo", "parameter": {"leadsid": id },"success": success,"failure": failure,"async": false});
	},
	transformToCustomer: function(id, success, failure, async){
		this.action.invoke({"name": "transformToCustomer", "parameter": {"leadsid": id },"success": success,"failure": failure,"async": false});
	},
	updateClue: function(leadsid, isKeepOriginalData,  filterData, success,failure, async){
		this.action.invoke({"name": "updateClue","parameter": {"leadsid": leadsid , "isKeepOriginalData" : isKeepOriginalData }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getClueListNext: function(id, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListNext","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getClueListPrev: function(id, count,  filterData, success,failure, async){
		this.action.invoke({"name": "getCustomerListPrev","parameter": {"id": id , "count" : count }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getClueListPage: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "getClueListPage","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListNestedSubPerson: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListNestedSubPerson","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListMyDuty: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListMyDuty","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListTransfer: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListTransfer","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListAllMy: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListAllMy","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	saveClue : function(data, success, failure, async){
		this.action.invoke({"name": "saveClue","data": data, "async": async,"success": success,"failure": failure});
	},
	culeTransfer : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "culeTransfer","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	//StatisticAction---
	countLike : function(data, success, failure, async){
		this.action.invoke({"name": "countLike","data": data, "async": async,"success": success,"failure": failure});
	},
	countCustomerByProvince: function(success, failure, async){
		this.action.invoke({"name": "countCustomerByProvince","async": false, "success": success,	"failure": failure});
	},
	countGroupByTypes : function(data, success, failure, async){
		this.action.invoke({"name": "countGroupByTypes","data": data, "async": false,"success": success,"failure": failure});
	},
	listByTypesAndTimeRange: function(types, data, success, failure, async){
		this.action.invoke({"name": "listByTypesAndTimeRange","parameter": {"types": types },"data": data, "async": async,"success": success,"failure": failure});
	},
	countCustomerByMonth : function(data, success, failure, async){
		this.action.invoke({"name": "countCustomerByMonth","data": data, "async": false,"success": success,"failure": failure});
	},
	countCustomerByIndustry : function(data, success, failure, async){
		this.action.invoke({"name": "countCustomerByIndustry","data": data, "async": false,"success": success,"failure": failure});
	},
	listNextTimePaginLike: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "listNextTimePaginLike","parameter": {"page": page , "size" : size }, "data": filterData, "async": false, "success": success,	"failure": failure});
	},

	//chance
	getOpportunityListByCustomerId: function(customerid, success, failure, async){
		this.action.invoke({"name": "getOpportunityListByCustomerId", "parameter": {"CustomerId": customerid },"success": success,"failure": failure,"async": false});
	},
	getChanceByPage: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "getChanceByPage","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListMyDuty_chance: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListMyDuty_chance","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListNestedSubPerson_chance: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListNestedSubPerson_chance","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListMyParticipate_chance: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListMyParticipate_chance","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	ListAllMy_chance: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "ListAllMy_chance","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	createChance : function(data, success, failure, async){
		this.action.invoke({"name": "createChance","data": data, "async": async,"success": success,"failure": failure});
	},
	updateChance: function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "updateChance","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	chanceTransfer : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "chanceTransfer","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	getTeamMemberListByChanceId : function(id, success, failure, async){
		this.action.invoke({"name": "getTeamMemberListByChanceId", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	setTeamReaderChance : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "setTeamReaderChance","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	setTeamWriterChance : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "setTeamWriterChance","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	removeTeamMemberChance: function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "removeTeamMemberChance","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	addRelevantPersonChance: function(id, writeReadSymbol,  filterData, success,failure, async){
		this.action.invoke({"name": "addRelevantPersonChance","parameter": {"id": id , "writeReadSymbol" : writeReadSymbol }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	//ContactsOpportunityAction
	getContactsByChanceId: function(id, success, failure, async){
		this.action.invoke({"name": "getContactsByChanceId", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	relateChanceAndContact : function(data, success, failure, async){
		this.action.invoke({"name": "relateChanceAndContact","data": data, "async": async,"success": success,"failure": failure});
	},
	terminatedRelation: function(id, success, failure, async){
		this.action.invoke({"name": "terminatedRelation", "parameter": {"id": id },"success": success,"failure": failure,"async": false});
	},
	getContactsListPageByCustomerId: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "getContactsListPageByCustomerId","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},

	getChanceInfo: function(chanceid, success, failure, async){
		this.action.invoke({"name": "getChanceInfo", "parameter": {"opportunityid": chanceid },"success": success,"failure": failure,"async": false});
	},
	getTypes: function(success, failure, async){
		this.action.invoke({"name": "getTypes","success": success,"failure": failure,"async": false});
	},
	getStatusByTypeid: function(id, success, failure, async){
		this.action.invoke({"name": "getStatusByTypeid", "parameter": {"typeid": id },"success": success,"failure": failure,"async": false});
	},

	//Publicseas
	getPublicseasByPage: function(page, size,  filterData, success,failure, async){
		this.action.invoke({"name": "getPublicseasByPage","parameter": {"page": page , "size" : size }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	distributeCustomer : function(id,  filterData, success,failure, async){
		this.action.invoke({"name": "distributeCustomer","parameter": {"id": id }, "data": filterData, "async": async, "success": success,	"failure": failure});
	},
	receiveCustomer: function(id, success,failure, async){
		this.action.invoke({"name": "receiveCustomer","parameter": {"id": id }, "async": async, "success": success,	"failure": failure});
	}
});