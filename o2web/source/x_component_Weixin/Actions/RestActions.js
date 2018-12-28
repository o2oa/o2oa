MWF.xApplication.File.Actions = MWF.xApplication.File.Actions || {};
MWF.xDesktop.requireApp("Common", "Actions.RestActions", null, false);
MWF.xApplication.File.Actions.RestActions = new Class({
	Extends: MWF.xApplication.Common.Actions.RestActions,
	initialize: function(){
		this.parent();

		MWF.getJSON("/x_component_File/Actions/action.json", function(json){
			this.fileActions = json;
		}.bind(this), false);
	},
	getDesignAddress: function(success, failure){
		this.designAddress = "http://xa01.zoneland.net:9080/x_file_service_control";
		if (success) success.apply();
	},
	_listTopFolder: function(success, failure){
		var url = this.designAddress+this.fileActions.listFolderTop;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listTopFolder: function(success, failure){
		this.request(success, failure, "listTopFolder");
	},
	
	
	
	
	
	
	
	
	
	
	_listSubCompany: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getSubCompanyDirect;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listSubCompany: function(success, failure, id){
		this.request(success, failure, "listSubCompany", id);
	},
	_listDepartment: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listDepartmentDirect;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listDepartment: function(success, failure, id){
		this.request(success, failure, "listDepartment", id);
	},
	_listSubDepartment: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getSubDepartmentDirect;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listSubDepartment: function(success, failure, id){
		this.request(success, failure, "listSubDepartment", id);
	},
	
	_listCompanyDuty: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listCompanyDuty;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listCompanyDuty: function(success, failure, id){
		this.request(success, failure, "listCompanyDuty", id);
	},
	_listDepartmentDuty: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listDepartmentDuty;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listDepartmentDuty: function(success, failure, id){
		this.request(success, failure, "listDepartmentDuty", id);
	},

	_listCompanyDutyByIdentity: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listCompanyDutyByIdentity;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listCompanyDutyByIdentity: function(success, failure, id){
		this.request(success, failure, "listCompanyDutyByIdentity", id);
	},
	
	_listDepartmentDutyByIdentity: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listDepartmentDutyByIdentity;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listDepartmentDutyByIdentity: function(success, failure, id){
		this.request(success, failure, "listDepartmentDutyByIdentity", id);
	},
	
	
	_listCompanyAttribute: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listCompanyAttribute;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listCompanyAttribute: function(success, failure, id){
		this.request(success, failure, "listCompanyAttribute", id);
	},
	_listDepartmentAttribute: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listDepartmentAttribute;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listDepartmentAttribute: function(success, failure, id){
		this.request(success, failure, "listDepartmentAttribute", id);
	},
	_listIdentity: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listIdentity;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listIdentity: function(success, failure, id){
		this.request(success, failure, "listIdentity", id);
	},
	
	_listIdentityByPerson: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listIdentityByPerson;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listIdentityByPerson: function(success, failure, id){
		this.request(success, failure, "listIdentityByPerson", id);
	},
	
	_listGroupNext: function(success, failure, arg){
		var url = this.designAddress+this.organizationActions.listGroupNext;
		url = url.replace(/{id}/g, arg.last ? arg.last : "(0)");
		url = url.replace(/{count}/g, arg.count ? arg.count : "20");

		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listGroupNext: function(last, count, success, failure){
		this.request(success, failure, "listGroupNext", {"last": last, "count": count});
	},
	
	_listRoleNext: function(success, failure, arg){
		var url = this.designAddress+this.organizationActions.listRoleNext;
		url = url.replace(/{id}/g, arg.last ? arg.last : "(0)");
		url = url.replace(/{count}/g, arg.count ? arg.count : "20");

		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listRoleNext: function(last, count, success, failure){
		this.request(success, failure, "listRoleNext", {"last": last, "count": count});
	},
	
	_listPersonNext: function(success, failure, arg){
		var url = this.designAddress+this.organizationActions.listPersonNext;
		url = url.replace(/{id}/g, arg.last ? arg.last : "(0)");
		url = url.replace(/{count}/g, arg.count ? arg.count : "20");

		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listPersonNext: function(last, count, success, failure){
		this.request(success, failure, "listPersonNext", {"last": last, "count": count});
	},
	
	_listPersonAttribute: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.listPersonAttribute;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	listPersonAttribute: function(success, failure, id){
		this.request(success, failure, "listPersonAttribute", id);
	},
	
	
	
	_getIdentity: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getIdentity;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getIdentityNotAsync: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getIdentity;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getIdentity: function(success, failure, id, async){
		if (async!=false){
			this.request(success, failure, "getIdentity", id);
		}else{
			this.request(success, failure, "getIdentityNotAsync", id);
		}
	},
	
	
	_getPersonByKey: function(success, failure, key){
		var url = this.designAddress+this.organizationActions.getPersonByKey;
		url = url.replace(/{key}/g, key);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getPersonByKeyNotAsync: function(success, failure, key){
		var url = this.designAddress+this.organizationActions.getPersonByKey;
		url = url.replace(/{key}/g, key); 
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getPersonByKey: function(success, failure, key, async){
		if (async!=false){
			this.request(success, failure, "getPersonByKey", key);
		}else{
			this.request(success, failure, "getPersonByKeyNotAsync", key);
		}
	},
	
	_getRoleByKey: function(success, failure, key){
		var url = this.designAddress+this.organizationActions.getRoleByKey;
		url = url.replace(/{key}/g, key);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getRoleByKeyNotAsync: function(success, failure, key){
		var url = this.designAddress+this.organizationActions.getRoleByKey;
		url = url.replace(/{key}/g, key); 
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getRoleByKey: function(success, failure, key, async){
		if (async!=false){
			this.request(success, failure, "getRoleByKey", key);
		}else{
			this.request(success, failure, "getRoleByKeyNotAsync", key);
		}
	},
	
	
	_getPerson: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getPerson;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getPersonNotAsync: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getPerson;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getPerson: function(success, failure, id, async){
		if (async!=false){
			this.request(success, failure, "getPerson", id);
		}else{
			this.request(success, failure, "getPersonNotAsync", id);
		}
	},
	
	_getDepartment: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getDepartment;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getDepartmentNotAsync: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getDepartment;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getDepartment: function(success, failure, id, async){
		if (async!=false){
			this.request(success, failure, "getDepartment", id);
		}else{
			this.request(success, failure, "getDepartmentNotAsync", id);
		}
	},
	_getCompany: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getCompany;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getCompanyNotAsync: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getCompany;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getCompany: function(success, failure, id, async){
		if (async!=false){
			this.request(success, failure, "getCompany", id);
		}else{
			this.request(success, failure, "getCompanyNotAsync", id);
		}
	},
	
	_getGroup: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getGroup;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getGroupNotAsync: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.getGroup;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getGroup: function(success, failure, id, async){
		if (async!=false){
			this.request(success, failure, "getGroup", id);
		}else{
			this.request(success, failure, "getGroupNotAsync", id);
		}
	},
	
	_getGroupByKey: function(success, failure, key){
		var url = this.designAddress+this.organizationActions.getGroupByKey;
		url = url.replace(/{key}/g, key);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback);
	},
	_getGroupByKeyNotAsync: function(success, failure, key){
		var url = this.designAddress+this.organizationActions.getGroupByKey;
		url = url.replace(/{key}/g, key); 
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.getJSON(url, callback, false);
	},
	getGroupByKey: function(success, failure, key, async){
		if (async!=false){
			this.request(success, failure, "getGroupByKey", key);
		}else{
			this.request(success, failure, "getGroupByKeyNotAsync", key);
		}
	},
	
	saveCompany: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateCompany(data, success, failure);
			}else{
				this.addCompany(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateCompany(data, success, failure);
				}else{
					this.addCompany(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateCompany: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateCompany;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addCompany: function(data, success, failure){
		this.getId(1, function(json){
			data.id = json.data[0].id;
			var address = this.designAddress+this.organizationActions.addCompany;
			var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
			MWF.restful("POST", address, JSON.encode(data), callback);
		}.bind(this));
	},
	saveCompanyDuty: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateCompanyDuty(data, success, failure);
			}else{
				this.addCompanyDuty(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateCompanyDuty(data, success, failure);
				}else{
					this.addCompanyDuty(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateCompanyDuty: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateCompanyDuty;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addCompanyDuty: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addCompanyDuty;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	
	saveDepartmentDuty: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateDepartmentDuty(data, success, failure);
			}else{
				this.addDepartmentDuty(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateDepartmentDuty(data, success, failure);
				}else{
					this.addDepartmentDuty(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateDepartmentDuty: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateDepartmentDuty;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addDepartmentDuty: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addDepartmentDuty;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},

	saveCompanyAttribute: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateCompanyAttribute(data, success, failure);
			}else{
				this.addCompanyAttribute(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateCompanyAttribute(data, success, failure);
				}else{
					this.addCompanyAttribute(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateCompanyAttribute: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateCompanyAttribute;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addCompanyAttribute: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addCompanyAttribute;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	saveDepartmentAttribute: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateDepartmentAttribute(data, success, failure);
			}else{
				this.addDepartmentAttribute(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateDepartmentAttribute(data, success, failure);
				}else{
					this.addDepartmentAttribute(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateDepartmentAttribute: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateDepartmentAttribute;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addDepartmentAttribute: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addDepartmentAttribute;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	
	saveGroup: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateGroup(data, success, failure);
			}else{
				this.addGroup(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateGroup(data, success, failure);
				}else{
					this.addGroup(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateGroup: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateGroup;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addGroup: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addGroup;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	
	saveRole: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateRole(data, success, failure);
			}else{
				this.addRole(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateRole(data, success, failure);
				}else{
					this.addRole(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateRole: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateRole;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addRole: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addRole;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	
	savePerson: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updatePerson(data, success, failure);
			}else{
				this.addPerson(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updatePerson(data, success, failure);
				}else{
					this.addPerson(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updatePerson: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updatePerson;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addPerson: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addPerson;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},

	savePersonAttribute: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updatePersonAttribute(data, success, failure);
			}else{
				this.addPersonAttribute(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updatePersonAttribute(data, success, failure);
				}else{
					this.addPersonAttribute(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updatePersonAttribute: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updatePersonAttribute;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addPersonAttribute: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addPersonAttribute;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	
	saveIdentity: function(data, success, failure){
		if (this.designAddress){
			if (data.id){
				this.updateIdentity(data, success, failure);
			}else{
				this.addIdentity(data, success, failure);
			}
		}else{
			this.getDesignAddress(function(){
				if (data.id){
					this.updateIdentity(data, success, failure);
				}else{
					this.addIdentity(data, success, failure);
				}
			}.bind(this), failure);
		}
	},
	updateIdentity: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.updateIdentity;
		address = address.replace(/{id}/g, data.id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("PUT", address, JSON.encode(data), callback);
	},
	addIdentity: function(data, success, failure){
		var address = this.designAddress+this.organizationActions.addIdentity;
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("POST", address, JSON.encode(data), callback);
	},
	
	
	_deleteCompanyDuty: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeCompanyDuty;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteCompanyDuty: function(id, success, failure){
		this.request(success, failure, "deleteCompanyDuty", id);
	},
	_deleteDepartmentDuty: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeDepartmentDuty;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteDepartmentDuty: function(id, success, failure){
		this.request(success, failure, "deleteDepartmentDuty", id);
	},
	
	_deleteCompanyAttribute: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeCompanyAttribute;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteCompanyAttribute: function(id, success, failure){
		this.request(success, failure, "deleteCompanyAttribute", id);
	},
	
	_deleteDepartmentAttribute: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeDepartmentAttribute;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteDepartmentAttribute: function(id, success, failure){
		this.request(success, failure, "deleteDepartmentAttribute", id);
	},
	_deletePersonAttribute: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removePersonAttribute;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deletePersonAttribute: function(id, success, failure){
		this.request(success, failure, "deletePersonAttribute", id);
	},
	
	_deleteIdentity: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeIdentity;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteIdentity: function(id, success, failure){
		this.request(success, failure, "deleteIdentity", id);
	},
	
	_deleteGroup: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeGroup;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteGroup: function(id, success, failure){
		this.request(success, failure, "deleteGroup", id);
	},
	_deleteRole: function(success, failure, id){
		var url = this.designAddress+this.organizationActions.removeRole;
		url = url.replace(/{id}/g, id);
		var callback = new MWF.xApplication.Common.Actions.RestActions.Callback(success, failure);
		MWF.restful("DELETE", url, "", callback);
	},
	deleteRole: function(id, success, failure){
		this.request(success, failure, "deleteRole", id);
	}
	
});